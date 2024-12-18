package com.sigmob.android.demo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.res.AssetManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.appcompat.app.AppCompatActivity;

import com.sigmob.android.demo.natives.NativeAdActivity;
import com.sigmob.windad.OnInitializationListener;
import com.sigmob.windad.WindAdOptions;
import com.sigmob.windad.WindAds;
import com.sigmob.windad.WindAgeRestrictedUserStatus;
import com.sigmob.windad.WindConsentStatus;
import com.sigmob.windad.WindCustomController;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Sigmob SDK Version : "+ WindAds.getVersion());

        this.findViewById(R.id.bt_sdk_init).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                initSDK();
            }
        });
        bindButton(R.id.bt_reward, RewardVideoActivity.class);
        bindButton(R.id.bt_splash, SplashAdActivity.class);
        bindButton(R.id.bt_native, NativeAdActivity.class);
        bindButton(R.id.bt_new_interstitial, NewInterstitialActivity.class);
    }
    private void bindButton(@IdRes int id, final Class clz) {
        this.findViewById(id).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (WindAds.sharedAds().isInit()){
                    Intent intent = new Intent(MainActivity.this, clz);
                    startActivity(intent);
                }else{
                    Toast.makeText(MainActivity.this, "请先进行SDK初始化", Toast.LENGTH_SHORT).show();

                }

            }
        });
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
        /*
         * 是否成年
         * true 成年, false 未成年，默认值为true
         */
        ads.setAdult(true);
        /*
         * 是否开启个性化推荐接口
         * true 开启, false 关闭,默认值为true
         */
        ads.setPersonalizedAdvertisingOn(true);
        /*
         * 是否允许使用传感器
         * true 开启, false 关闭,默认值为true
         */
        ads.setSensorStatus(true);
        ads.setIsAgeRestrictedUser(WindAgeRestrictedUserStatus.NO);//coppa//是否年龄限制
        ads.setUserGDPRConsentStatus(WindConsentStatus.ACCEPT);//是否接受gdpr协议

        WindAdOptions windAdOptions = new WindAdOptions(Constants.app_id, Constants.app_key);

        //设置自定义设备信息，允许开发者自行传入设备及用户信息
        windAdOptions.setCustomController(new WindCustomController() {

            /**
             * 是否允许SDK主动获取地理位置信息
             *
             * @return true可以获取，false禁止获取。默认为true
             */
            @Override
            public boolean isCanUseLocation() {
                return false;
            }
            /**
             * 当isCanUseLocation=false时，可传入地理位置信息，sdk使用您传入的地理位置信息
             *
             * @return 地理位置参数 或者 null
             */
            @Override
            public Location getLocation() {
                return null;
            }
            /**
             * 是否允许SDK主动获取手机设备信息，如：imei，运营商信息
             *
             * @return true允许获取，false禁止获取。默认为true
             */
            @Override
            public boolean isCanUsePhoneState() {
                return false;
            }
            /**
             * 当isCanUsePhoneState=false时，可传入imei信息，sdk使用您传入的imei信息
             *
             * @return imei信息 或者 null
             */

            @Override
            public String getDevImei() {
                 return null;
            }
            /**
             * 是否允许SDK主动获取OAID
             *
             * @return true允许获取，false禁止获取。默认为true
             */
            @Override
            public boolean isCanUseOaid() {
                return true;
            }
            /**
             * 当isCanUseOaid = false，开发者可以传入oaid
             *
             * @return oaid 或者 null
             */
            @Override
            public String getDevOaid() {
                return null;
            }
            /**
             * 是否允许SDK主动获取Android id
             *
             * @return true允许获取，false禁止获取。默认为true
             */
            @Override
            public boolean isCanUseAndroidId() {
                return true;
            }
            /**
             * isCanUseAndroidId=false时，可传入android id信息，SDK使用您传入的android id信息
             *
             * @return android id信息 或者 null
             */
            @Override
            public String getAndroidId() {
                return null;
            }
            /**
             * 是否允许SDK主动收集上传应用列表
             *
             * @return true 允许SDK收集，false 开发者传入
             */
            @Override
            public boolean isCanUseAppList() {
                return false;
            }
            /**
             *  当isCanUseAppList = false，那么调用开发者传入的应用列表
             *
             * @return  开发者收集应用列表信息或者null
             */
            @Override
            public List<PackageInfo> getInstallPackageInfoList() {
                return null;
            }
        });

        //SDK初始化
        ads.startWithOptions(this, windAdOptions, new OnInitializationListener() {
            @Override
            public void OnInitializationSuccess() {
                Toast.makeText(MainActivity.this, "SDK 初始化成功", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnInitializationFail(String error) {
                Toast.makeText(MainActivity.this, "SDK 初始化失败 : " +error, Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    //调用菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;

        try {
            Toast.makeText(this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}