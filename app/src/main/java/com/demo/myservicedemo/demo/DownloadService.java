package com.demo.myservicedemo.demo;

import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Environment;
import android.os.IBinder;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import com.demo.myservicedemo.R;

import java.io.File;

public class DownloadService extends Service {

    private  DownloadTask downloadTask;
    private  String  downloadUrl;
    private  DownloadLisenter lisenter = new DownloadLisenter() {
        @Override
        public void onProgress(int progress) {
              getNotificationManager().notify(1,getNotification("下载....",progress));
        }

        @Override
        public void onSuccess() {
            downloadTask = null;
            //下载成功之前将前台服务通知关闭，并创建一个下载成功的通知
            stopForeground(true);
            getNotificationManager().notify(1,getNotification("下载成功",-1));
            Toast.makeText(DownloadService.this, "下载成功", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onFailed() {
            downloadTask = null;
            //下载失败时将前台服务通知关闭，并创建一个下载失败的通知
            stopForeground(true);
            getNotificationManager().notify(1,getNotification("下载失败",-1));
            Toast.makeText(DownloadService.this, "下载失败", Toast.LENGTH_SHORT).show();

        }

        @Override
        public void onPaused() {
      downloadTask=null;
            Toast.makeText(DownloadService.this, "下载暂停", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCanceled() {
            downloadTask = null;
            stopForeground(true);
            Toast.makeText(DownloadService.this, "下载失败", Toast.LENGTH_SHORT).show();
        }
    };


    public DownloadService() {
    }
   private  DownloadBinder mBinder = new DownloadBinder();

    @Override
    public IBinder onBind(Intent intent) {
     return  mBinder;
    }

    class DownloadBinder extends Binder {
        public  void  startDownload(String url){
          if(downloadTask == null){
              downloadUrl =url;
              downloadTask = new DownloadTask(lisenter);
              downloadTask.execute(downloadUrl);
              startForeground(1,getNotification("下载中....",0));

          }
        }
        public  void  pauseDownload(){
            if(downloadTask !=null){
                downloadTask.pauseDownload();
            }
        }
        public  void  cancelDownload(){
            if(downloadTask!=null){
                downloadTask.cancelDownload();
            }else {
                if (downloadUrl !=null){
                    //取消下载时需要将文件删除，并且通知关闭
                    String fileName= downloadUrl.substring(downloadUrl.lastIndexOf("/"));
                    String  directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();
                    File file = new File(directory + fileName);
                    if(file.exists()){
                        file.delete();
                    }
                    getNotificationManager().cancel(1);
                    stopForeground(true);

                }
            }
        }

    }


    private NotificationManager  getNotificationManager(){
        return  (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
    }

    private Notification  getNotification(String title,int progress){
        Intent intent =  new Intent(this,Main3Activity.class);
        PendingIntent pi = PendingIntent.getActivity(this,0,intent,0);
        NotificationCompat.Builder builder= new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher_round);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher));
        builder.setContentIntent(pi);
        builder.setContentTitle(title);
        if(progress>=0){
            //当progress 大于或者等于0的时候，才显示下载进度
            builder.setContentText(progress+"%");
            builder.setProgress(100,progress,false);
        }
        return  builder.build();
    }
}
