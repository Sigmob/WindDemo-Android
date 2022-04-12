package com.sigmob.android.demo;

import android.content.Context;
import android.content.res.AssetManager;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.sigmob.windad.WindAdOptions;
import com.sigmob.windad.WindAds;
import com.sigmob.windad.WindAgeRestrictedUserStatus;
import com.sigmob.windad.WindConsentStatus;
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

        CrashReport.initCrashReport(getApplicationContext(), "4c41e5eed0", true);//4c41e5eed0//4ee13aff7b

        Constants.loadDefualtAdSetting(this);
        initSDK();
    }

    private String loadOaidCertPem(Context context){
        try {

            InputStream is;
            String defaultPemCert = context.getPackageName() + ".cert.pem";
            AssetManager assetManager = context.getAssets();

            is = assetManager.open(defaultPemCert);

            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                builder.append(line);
                builder.append('\n');
            }
            return builder.toString();

        }catch (Throwable th){

        }
        return "";
    }

    private void initSDK() {

        WindAds ads = WindAds.sharedAds();
        WindAds.setOAIDCertPem(loadOaidCertPem(this));

        ads.setUserAge(18);
        ads.setAdult(true);//是否成年
        ads.setPersonalizedAdvertisingOn(true);//是否开启个性化推荐接口
        ads.setIsAgeRestrictedUser(WindAgeRestrictedUserStatus.NO);//coppa//是否年龄限制
        ads.setUserGDPRConsentStatus(WindConsentStatus.ACCEPT);//是否接受gdpr协议

        ads.startWithOptions(this, new WindAdOptions(Constants.app_id, Constants.app_key));
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        MultiDex.install(this);

    }
}
