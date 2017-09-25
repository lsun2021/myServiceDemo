package com.demo.myservicedemo.bindservice;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by ${momoThree} on 2017/9/25.
 * Title:
 */

public class MyIntentService extends IntentService {
    public MyIntentService() {
        super("MyIntentService");            //调用父类的有参构造函数
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
     //打印当前线程的id
        Log.d("MyIntentService","当前线程的id="+Thread.currentThread().getId());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("MyIntentService","onDestroy");

    }
}
