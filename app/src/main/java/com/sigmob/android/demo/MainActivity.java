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
        WindAds.requestPermission(this);

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
        bindButton(R.id.bt_device, DeviceActivity.class);
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
        ads.setAdult(true);//是否成年
        ads.setPersonalizedAdvertisingOn(true);//是否开启个性化推荐接口
        ads.setSensorStatus(true); //允许使用传感器
        ads.setIsAgeRestrictedUser(WindAgeRestrictedUserStatus.NO);//coppa//是否年龄限制
        ads.setUserGDPRConsentStatus(WindConsentStatus.ACCEPT);//是否接受gdpr协议


        WindAdOptions windAdOptions = new WindAdOptions(Constants.app_id, Constants.app_key);

        //设置自定义设备信息，允许开发者自行传入设备及用户信息
        windAdOptions.setCustomController(new WindCustomController() {
            @Override
            public boolean isCanUseLocation() {
                return false;
            }

            @Override
            public Location getLocation() {
                return null;
            }

            @Override
            public boolean isCanUsePhoneState() {
                return false;
            }

            @Override
            public String getDevImei() {
                 return null;
            }

            @Override
            public boolean isCanUseOaid() {
                return true;
            }

            @Override
            public String getDevOaid() {
                return null;
            }

            @Override
            public boolean isCanUseAndroidId() {
                return true;
            }

            @Override
            public String getAndroidId() {
                return null;
            }

            @Override
            public boolean isCanUseAppList() {
                return false;
            }

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