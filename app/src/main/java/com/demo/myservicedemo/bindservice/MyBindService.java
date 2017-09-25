package com.demo.myservicedemo.bindservice;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.IntDef;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.demo.myservicedemo.R;

public class MyBindService extends Service {
    private static final String TAG = "MyBindService";
    public MyBindService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return  mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e(TAG, "onCreate: " );
        Intent intent= new Intent(this,Main2Activity.class);
        PendingIntent pi=PendingIntent.getActivity(this,0,intent,0);
        Notification notification= new NotificationCompat.Builder(this)
                .setContentTitle("标题")
                .setContentText("内容")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher))
                .setContentIntent(pi)
                .build();
        startForeground(1,notification);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand: " );
        new Thread(new Runnable() {
            @Override
            public void run() {
                //处理具体的逻辑
                stopSelf();//停止服务
            }
        }).start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: " );
    }
    private  DownloadBinder mBinder=new DownloadBinder();

    class DownloadBinder extends Binder{
        public  void  startDownload(){
            Log.e(TAG, "startDownload: 开始下载");
        }
        public  int  getProgress(){
            Log.e(TAG, "getProgress: 当前下载量" );
            return 0;
        }

    }
}
