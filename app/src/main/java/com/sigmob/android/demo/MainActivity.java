package com.sigmob.android.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.sigmob.android.demo.natives.NativeAdActivity;
import com.sigmob.windad.OnInitializationListener;
import com.sigmob.windad.OnStartListener;
import com.sigmob.windad.WindAdOptions;
import com.sigmob.windad.WindAds;
import com.sigmob.windad.WindAgeRestrictedUserStatus;
import com.sigmob.windad.WindConsentStatus;
import com.sigmob.windad.WindCustomController;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("Sigmob SDK Version : " + WindAds.getVersion());

        findViewById(R.id.bt_sdk_init).setOnClickListener(view -> initSDK());
        bindButton(R.id.bt_reward, RewardVideoActivity.class);
        bindButton(R.id.bt_splash, SplashAdActivity.class);
        bindButton(R.id.bt_native, NativeAdActivity.class);
        bindButton(R.id.bt_new_interstitial, NewInterstitialActivity.class);
        bindButton(R.id.bt_device, DeviceActivity.class);
    }

    private void bindButton(int id, Class<?> clz) {
        View viewById = findViewById(id);
        if (viewById == null) return;

        viewById.setOnClickListener(v -> {
            if (WindAds.sharedAds().isInit() || id == R.id.bt_device) {
                Intent intent = new Intent(MainActivity.this, clz);
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "请先进行 SDK 初始化", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String loadOaidCertPem(Context context) {
        try {
            String defaultPemCert = context.getPackageName() + ".cert.pem";
            AssetManager assetManager = context.getAssets();
            InputStream is = assetManager.open(defaultPemCert);

            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                builder.append(line);
                builder.append('\n');
            }
            return builder.toString();
        } catch (Throwable th) {
            Log.e(TAG, "loadOaidCertPem: error = " + th.getMessage());
        }
        return "";
    }

    private void initSDK() {
        WindAds ads = WindAds.sharedAds();
        WindAds.setOAIDCertPem(loadOaidCertPem(this));

        ads.setUserAge(18);
        /*
         * 是否成年
         * true-成年；false-未成年；默认值为 true
         */
        ads.setAdult(true);
        /*
         * 是否开启个性化推荐接口
         * true-开启；false-关闭；默认值为 true
         */
        ads.setPersonalizedAdvertisingOn(true);
        /*
         * 是否允许使用传感器
         * true-开启；false-关闭；默认值为 true
         */
        ads.setSensorStatus(true);
        // coppa，是否年龄限制
        ads.setIsAgeRestrictedUser(WindAgeRestrictedUserStatus.NO);
        // 是否接受 gdpr 协议
        ads.setUserGDPRConsentStatus(WindConsentStatus.ACCEPT);

        String[] opArray = getNetworkOperator();
        WindAdOptions windAdOptions = new WindAdOptions(Constants.app_id, Constants.app_key);
        // 设置自定义设备信息，允许开发者自行传入设备及用户信息
        windAdOptions.setCustomController(new WindCustomController() {
            /**
             * 是否允许 SDK 主动获取地理位置信息
             *
             * @return true 可以获取，false 禁止获取。默认为 true
             */
            @Override
            public boolean isCanUseLocation() {
                return false;
            }

            /**
             * 当 isCanUseLocation=false 时，Sigmob 使用开发者传入的地理位置信息
             *
             * @return 地理位置参数或者 null
             */
            @Override
            public Location getLocation() {
                return null;
            }

            /**
             * 是否允许 SDK 主动获取 IMEI
             *
             * @return true 可以使用，false 禁止使用。默认为 true
             */
            @Override
            public boolean isCanUsePhoneState() {
                return false;
            }

            /**
             * 当 isCanUsePhoneState=false 时，Sigmob 使用开发者传入的 IMEI 信息
             *
             * @return IMEI 或者 null
             */
            @Override
            public String getDevImei() {
                return null;
            }

            /**
             * 是否允许 SDK 主动获取 OAID
             *
             * @return true 可以使用，false 禁止使用。默认为 true
             */
            @Override
            public boolean isCanUseOaid() {
                return true;
            }

            /**
             * 当 isCanUseOaid=false 时，Sigmob 使用开发者传入的 OAID 信息
             *
             * @return OAID 或者 null
             */
            @Override
            public String getDevOaid() {
                return null;
            }

            /**
             * 是否允许 SDK 主动获取 AndroidId
             *
             * @return true 可以使用，false 禁止使用。默认为 true
             */
            @Override
            public boolean isCanUseAndroidId() {
                return false;
            }

            /**
             * isCanUseAndroidId=false 时，Sigmob 使用开发者传入的 AndroidId 信息
             *
             * @return AndroidId 或者 null
             */
            @Override
            public String getAndroidId() {
                return null;
            }

            /**
             * 是否允许 SDK 查询已安装应用列表
             *
             * @return true 可以使用，false 禁止使用
             */
            @Override
            public boolean isCanUseAppList() {
                return false;
            }

            /**
             * isCanUseAppList=false 时，Sigmob 使用开发者传入的已安装应用列表信息
             *
             * @return 应用列表或者 null
             */
            @Override
            public List<PackageInfo> getInstallPackageInfoList() {
                //Context applicationContext = getApplicationContext();
                //List<PackageInfo> result = MainActivity.this.getInstallPackageInfoList(applicationContext);
                //Log.d(TAG, "getInstallPackageInfoList: result = " + result);
                return new ArrayList<>();
            }

            /**
             * 是否允许 SDK 查询运营商编码（4.22.0 版本新增）
             *
             * @return true 可以使用，false 禁止使用
             */
            @Override
            public boolean isCanUseSimOperator() {
                return false;
            }

            /**
             * isCanUseSimOperator=false 时，Sigmob 使用开发者传入的运营商编码，例如：46000（4.22.0 版本新增）
             */
            @Override
            public String getDevSimOperatorCode() {
                String result = (opArray == null || opArray.length == 0) ? null : opArray[0];
                return result;
            }

            /**
             * isCanUseSimOperator=false 时，Sigmob 使用开发者传入的运营商名称，例如：中国移动（4.22.0 版本新增）
             */
            @Override
            public String getDevSimOperatorName() {
                String result = (opArray == null || opArray.length == 0) ? null : opArray[1];
                return result;
            }
        });

        // SDK 初始化
        ads.init(getApplicationContext(), windAdOptions, new OnInitializationListener() {
            @Override
            public void onInitializationSuccess() {
                Log.i(TAG, "initSDK#onInitializationSuccess");
            }

            @Override
            public void onInitializationFail(String error) {
                Log.e(TAG, "initSDK#onInitializationFail: error =" + error);
            }
        });

        // SDK 启动
        ads.start(new OnStartListener() {
            @Override
            public void onStartSuccess() {
                Log.i(TAG, "initSDK#OnStartSuccess");
            }

            @Override
            public void onStartFail(String error) {
                Log.e(TAG, "initSDK#OnStartSuccess: error =" + error);
            }
        });
    }

    public List<PackageInfo> getInstallPackageInfoList(Context ctx) {
        PackageManager pm = ctx.getPackageManager();
        return pm.getInstalledPackages(0);
    }

    public String[] getNetworkOperator() {
        Context context = getApplicationContext();
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (tm == null) return null;

        // 运营商编码号，例如：46000
        String operatorCode;
        int phoneType = tm.getPhoneType();
        int simState = tm.getSimState();
        Log.d(TAG, "getNetworkOperator: phoneType = " + phoneType + ", simState = " + simState);
        if (phoneType == TelephonyManager.PHONE_TYPE_CDMA && simState == TelephonyManager.SIM_STATE_READY) {
            operatorCode = tm.getSimOperator();
            Log.d(TAG, "getNetworkOperator: simOperator = " + operatorCode);
        } else {
            operatorCode = tm.getNetworkOperator();
            Log.d(TAG, "getNetworkOperator: networkOperator = " + operatorCode);
        }

        // 运营商名称，例如：中国移动
        String operatorName = tm.getNetworkOperatorName();
        return new String[]{operatorCode, operatorName};
    }

    public void log(String method, Object result) {
        Log.d(TAG, method + ": result = " + result + ", thread = " + Thread.currentThread().getName());
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

        doubleBackToExitPressedOnce = true;

        try {
            Toast.makeText(this, "再按一次退出应用", Toast.LENGTH_SHORT).show();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}