package com.demo.myservicedemo.demo;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by ${momoThree} on 2017/9/25.
 * Title:
 */

public class DownloadTask extends AsyncTask<String,Integer,Integer> {
    public static  final  int  TYPE_SUCCESS =0;
    public static  final  int  TYPE_FAILED =1;
    public static  final  int  TYPE_PAUSED =2;
    public static  final  int  TYPE_CANCELED =3;

    private DownloadLisenter  lisenter;
    private boolean  isCanceled = false;
    private  boolean  isPaused = false ;
    private  int  lastProgress;

    public DownloadTask(DownloadLisenter lisenter) {
        this.lisenter = lisenter;
    }

    @Override
    protected Integer doInBackground(String... params) {
        InputStream inputStream =null;
        RandomAccessFile savedFiled = null;
        File file =null;
        long downloadedLength= 0;//记录下载文件的长度
        String downloadUrl= params[0];
        String  fileName= downloadUrl.substring(downloadUrl.lastIndexOf("/"));
        String  directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
        file = new File(directory+fileName);
        if(file.exists()){
            downloadedLength = file.length();
        }
        long  contentLength= getContentLength(downloadUrl);
        if(contentLength == 0){
            return  TYPE_FAILED;
        }else if(contentLength ==downloadedLength){
          //已下载字节和字数总字节数相等，说明下载完成了
            return  TYPE_SUCCESS;
        }
        OkHttpClient client = new OkHttpClient();
        Request request= new Request.Builder()
                //断点下载，指定那个字节开始下载
                .addHeader("RANGE","bytes="+downloadedLength+".")
                .url(downloadUrl)
                .build();
        try {
            Response response= client.newCall(request).execute();
            if(response!=null){
                inputStream = response.body().byteStream();
                savedFiled = new RandomAccessFile(file,"rw");
                savedFiled.seek(downloadedLength);//跳过已经下载好的字节
                byte[] b=  new byte[1024];
                int  total =0 ;
                int  len;
                while ((len=  inputStream.read(b))!=-1){
                    if(isCanceled){
                        return  TYPE_CANCELED;
                    }else if(isPaused){
                        return  TYPE_PAUSED;
                    }else {
                        total+=len;
                        savedFiled.write(b,0,len);
                        //计算已经下载的百分比
                        int progress = (int)((total+downloadedLength)*100/contentLength);
                        publishProgress(progress);
                    }
                }
                response.body().close();
                return  TYPE_SUCCESS;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
            if(inputStream!=null){
                    inputStream.close();
                }
                if(savedFiled!= null){
                    savedFiled.close();
                }
                if(isCanceled&& file !=null){
                    file.delete();
                }
                } catch (Exception e) {
                    e.printStackTrace();
            }
        }
        return TYPE_FAILED;
    }


    @Override
    protected void onProgressUpdate(Integer... values) {
        int progress =values[0];
        if(progress>lastProgress){
            lisenter.onProgress(progress);
            lastProgress = progress;
        }
    }

    @Override
    protected void onPostExecute(Integer integer) {
        switch (integer){
            case TYPE_SUCCESS:
                lisenter.onSuccess();
                break;
            case TYPE_PAUSED:
                lisenter.onPaused();
                break;

            case TYPE_CANCELED:
                lisenter.onCanceled();
                break;
            case TYPE_FAILED:
                lisenter.onFailed();
                break;
            default:
                break;
        }
    }
    public  void  pauseDownload(){
        isPaused = true;
    }

    public void  cancelDownload(){
        isCanceled = true;
    }


    private  long getContentLength(String downloadUrl){
        OkHttpClient client =  new OkHttpClient();
        Request request = new Request.Builder()
                .url(downloadUrl)
                .build();
        try {
            Response response= client.newCall(request).execute();
            if(response !=null && response.isSuccessful()){
                long  contentLength= response.body().contentLength();
                response.body().close();
                return  contentLength;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return  0;
    }
}
