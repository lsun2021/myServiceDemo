package com.demo.myservicedemo.demo;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.demo.myservicedemo.R;

public class Main3Activity extends AppCompatActivity  implements View.OnClickListener{

    private DownloadService.DownloadBinder  downloadBinder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            downloadBinder= (DownloadService.DownloadBinder) iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

        Button  btn_start_download = (Button) findViewById(R.id.btn_start_download);
        Button  btn_stop_download = (Button) findViewById(R.id.btn_stop_download);
        Button  btn_cancel_download = (Button) findViewById(R.id.btn_cancel_download);
        btn_start_download.setOnClickListener(this);
        btn_stop_download.setOnClickListener(this);
        btn_cancel_download.setOnClickListener(this);

        //启动服务
        Intent intent =  new Intent(this,DownloadService.class);
        startService(intent);//启动服务
        if(ContextCompat.checkSelfPermission(Main3Activity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(Main3Activity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
    }



    @Override
    public void onClick(View view) {
        if (downloadBinder == null){
            return;
        }
        switch (view.getId()){
            case R.id.btn_start_download:
               String  url="https://raw.githubusercontent.com/guolindev/eclipse/master/eclipse-inst-win64.exe";
                downloadBinder.startDownload(url);
                break;
            case R.id.btn_stop_download:
               downloadBinder.pauseDownload();
                break;
            case R.id.btn_cancel_download:
               downloadBinder.cancelDownload();
                break;
            default:
                break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                  if(grantResults.length>0 && grantResults[0]!= PackageManager.PERMISSION_GRANTED){
                      Toast.makeText(this, "权限拒绝无法使用程序", Toast.LENGTH_SHORT).show();
                  }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }
}
