package com.sigmob.android.demo;

import android.content.Context;
import android.content.res.AssetManager;
import android.location.Location;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.sigmob.windad.WindAdOptions;
import com.sigmob.windad.WindAds;
import com.sigmob.windad.WindAgeRestrictedUserStatus;
import com.sigmob.windad.WindConsentStatus;
import com.sigmob.windad.WindCustomController;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * created by lance on   2021/12/7 : 1:26 下午
 */
public class MyApplication extends MultiDexApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        Constants.loadDefualtAdSetting(this);
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        MultiDex.install(this);

    }
}
