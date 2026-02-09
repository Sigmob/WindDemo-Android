package com.sigmob.android.demo;

import android.app.Application;
import android.content.Context;

/**
 * created by lance on   2021/12/7 : 1:26 下午
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
    }
}