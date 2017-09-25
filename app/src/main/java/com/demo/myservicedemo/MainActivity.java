package com.demo.myservicedemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn_start_service= (Button) findViewById(R.id.btn_start_service);
        Button btn_stop_service= (Button) findViewById(R.id.btn_stop_service);

        btn_start_service.setOnClickListener(this);
        btn_stop_service.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_start_service:
                Intent intent=new Intent(this,MyService.class);
                startService(intent);
                break;
            case R.id.btn_stop_service:
                Intent intent1=new Intent(this,MyService.class);
                stopService(intent1);
                break;
        }
    }
}
