package com.demo.myservicedemo.bindservice;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.demo.myservicedemo.MyService;
import com.demo.myservicedemo.R;

public class Main2Activity extends AppCompatActivity implements View.OnClickListener {

    private MyBindService.DownloadBinder downloadBinder;
    private ServiceConnection connection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            downloadBinder = (MyBindService.DownloadBinder) iBinder;
            downloadBinder.startDownload();
            downloadBinder.getProgress();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Button btn_start_service= (Button) findViewById(R.id.btn_start_service);
        Button btn_stop_service= (Button) findViewById(R.id.btn_stop_service);
        Button btn_log_service_id= (Button) findViewById(R.id.btn_log_service_id);

        btn_start_service.setOnClickListener(this);
        btn_stop_service.setOnClickListener(this);
        btn_log_service_id.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start_service:
                Intent intent = new Intent(this, MyBindService.class);
              bindService(intent,connection,BIND_AUTO_CREATE);//绑定服务
                break;
            case R.id.btn_stop_service:
              unbindService(connection); //解绑服务
                break;
            case R.id.btn_log_service_id:
                //打印主线程的id
                Log.d("Main2Activity", "主线程的id= "+Thread.currentThread().getId());
                Intent intentService = new Intent(this, MyIntentService.class);
                startService(intentService);
                break;
        }
    }
}
