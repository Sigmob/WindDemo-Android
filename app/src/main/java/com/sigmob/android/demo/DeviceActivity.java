package com.sigmob.android.demo;

import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.os.Bundle;
import android.os.Process;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.czhj.sdk.common.ClientMetadata;
import com.sigmob.windad.WindAds;

public class DeviceActivity extends Activity {

    private static final String TAG = DeviceActivity.class.getSimpleName();
    private static String ANDROID_ID = "";
    private TextView tvDeviceModel;
    private TextView tvOsVersion;
    private TextView tvSdkVersion;
    private TextView tvPackageName;
    private TextView tvInitStatus;
    private TextView tvAppId;
    private TextView tvManufacturer;
    private TextView tvImei;
    private TextView tvOaid;
    private TextView tvAndroidId;
    private TextView tvNetworkType;
    private TextView tvNetworkState;
    private TextView tvWifiState;
    private TextView tvGpsLocation;
    private TextView tvNetworkLocation;
    private TextView tvReadPhoneState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);

        setTitle("设备信息");

        initViews();
        loadDeviceInfo();
    }

    private void initViews() {
        // 基础信息
        tvManufacturer = findViewById(R.id.tv_manufacturer);
        tvDeviceModel = findViewById(R.id.tv_device_model);
        tvOsVersion = findViewById(R.id.tv_os_version);
        tvSdkVersion = findViewById(R.id.tv_sdk_version);
        tvPackageName = findViewById(R.id.tv_package_name);
        tvInitStatus = findViewById(R.id.tv_init_status);
        tvAppId = findViewById(R.id.tv_app_id);
        tvImei = findViewById(R.id.tv_imei);
        tvOaid = findViewById(R.id.tv_oaid);
        tvAndroidId = findViewById(R.id.tv_android_id);

        // 网络配置
        tvNetworkType = findViewById(R.id.tv_network_type);

        // 权限状态
        tvNetworkState = findViewById(R.id.tv_network_state);
        tvWifiState = findViewById(R.id.tv_wifi_state);
        tvGpsLocation = findViewById(R.id.tv_gps_location);
        tvNetworkLocation = findViewById(R.id.tv_network_location);
        tvReadPhoneState = findViewById(R.id.tv_read_phone_state);
    }

    private void loadDeviceInfo() {
        // 基础信息
        tvManufacturer.setText(Build.MANUFACTURER);
        tvDeviceModel.setText(Build.MODEL);
        tvOsVersion.setText(Build.VERSION.RELEASE);
        tvSdkVersion.setText(WindAds.getVersion());
        tvPackageName.setText(getPackageName());
        tvInitStatus.setText(WindAds.sharedAds().isInit() ? "√" : "×");
        tvAppId.setText(Constants.app_id);
        getOAID();
        tvImei.setText(getIMEI());
        tvAndroidId.setText(getAndroidId());
        copyText(tvOaid, tvAndroidId);

        // 网络配置
        tvNetworkType.setText(getNetworkType());

        // 权限状态
        tvNetworkState.setText(checkPermission(Manifest.permission.ACCESS_NETWORK_STATE) ? "√" : "×");
        tvWifiState.setText(checkPermission(Manifest.permission.ACCESS_WIFI_STATE) ? "√" : "×");
        tvGpsLocation.setText(checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) ? "√" : "×");
        tvNetworkLocation.setText(checkPermission(Manifest.permission.ACCESS_COARSE_LOCATION) ? "√" : "×");
        tvReadPhoneState.setText(checkPermission(Manifest.permission.READ_PHONE_STATE) ? "√" : "×");
    }

    private void copyText(TextView... textViews) {
        if (textViews == null) return;

        for (TextView textView : textViews) {
            if (textView == null) continue;

            textView.setOnClickListener(v -> {
                // 复制到剪贴板
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    String text = textView.getText().toString().trim();
                    ClipData clipData = ClipData.newPlainText("", text);
                    getSystemService(ClipboardManager.class).setPrimaryClip(clipData);
                    Toast.makeText(this, "信息已复制到剪贴板", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void getOAID() {
        try {
            String oaid = ClientMetadata.getInstance().getOAID();
            tvOaid.setText(TextUtils.isEmpty(oaid) ? "null" : oaid);
        } catch (Exception e) {
            Log.e(TAG, "getOAID: error = ", e);
        }
    }

    private String getIMEI() {
        String defResult = "null";
        if (checkPermission(Manifest.permission.READ_PHONE_STATE)) {
            try {
                TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                if (tm == null) return defResult;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    return tm.getImei();
                } else {
                    return tm.getDeviceId();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            return "权限未授予";
        }

        return defResult;
    }

    public String getAndroidId() {
        if (TextUtils.isEmpty(ANDROID_ID)) {
            ANDROID_ID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        }
        return ANDROID_ID;
    }

    private String getNetworkType() {
        String defResult = "未知";
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return defResult;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            NetworkCapabilities capabilities = cm.getNetworkCapabilities(cm.getActiveNetwork());
            if (capabilities == null) return defResult;

            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return "WiFi";
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return "移动网络";
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                return "以太网";
            }
        } else {
            android.net.NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
                int type = activeNetworkInfo.getType();
                if (type == ConnectivityManager.TYPE_WIFI) {
                    return "WiFi";
                } else if (type == ConnectivityManager.TYPE_MOBILE) {
                    return "移动网络";
                }
            }
        }

        return defResult;
    }

    private boolean checkPermission(String permission) {
        return checkPermission(permission, Process.myPid(), Process.myUid()) == PackageManager.PERMISSION_GRANTED;
    }
}