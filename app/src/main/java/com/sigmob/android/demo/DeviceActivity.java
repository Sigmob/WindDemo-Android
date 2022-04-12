package com.sigmob.android.demo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.sigmob.android.demo.deviceutils.AdvertisingIdClient;
import com.sigmob.android.demo.deviceutils.OaidHelper;
import com.sigmob.windad.WindAds;

import java.util.LinkedHashMap;
import java.util.Map;

public class DeviceActivity extends Activity {

    private TableLayout tl;

    private Map<String, String> mAdVersions = new LinkedHashMap<>();

    private String[] mAdNames = {"SigId", "GAID", "OAID", "IMEI"};
    private String imei;
    private String oaid;
    private String gaid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device);
        tl = findViewById(R.id.tl_version);
        loadId();
    }

    private void updateDeviceIDList(){
        initDeviceIDList();
        updateView();
    }

    public int dipsToIntPixels(int dips) {
        float density = this.getResources().getDisplayMetrics().density;
        return (int) ((dips * density) + 0.5f);
    }


    private void updateView() {
        tl.removeAllViews();
        if (mAdVersions.size() > 0) {

            for (Map.Entry<String, String> entry : mAdVersions.entrySet()) {

                String key = entry.getKey();
                String value = entry.getValue();

                TableRow tb = new TableRow(this);
                tb.setBackgroundColor(Color.GRAY);
                tb.setPadding(1, 1, 1, 1);
                tb.setGravity(Gravity.CENTER_VERTICAL);

                final TextView tv3 = new TextView(this);
                TableRow.LayoutParams params3 = new TableRow.LayoutParams(0, dipsToIntPixels(50));
                params3.weight = 1;
                params3.setMargins(0, 0, 1, 0);
                tv3.setLayoutParams(params3);
                tv3.setGravity(Gravity.CENTER);
                tv3.setBackgroundColor(Color.WHITE);
                tv3.setText(key);
                tb.addView(tv3);

                final TextView tv4 = new TextView(this);
                TableRow.LayoutParams params4 = new TableRow.LayoutParams(0, dipsToIntPixels(50));
                params4.weight = 4;
                tv4.setLayoutParams(params4);
                tv4.setGravity(Gravity.CENTER);
                tv4.setBackgroundColor(Color.WHITE);
                tv4.post(new Runnable() {
                    @Override
                    public void run() {
                        tv4.setTextIsSelectable(true);
                    }
                });
                tv4.setText(value);
                tb.addView(tv4);

                tl.addView(tb);
            }
        }
    }

    @SuppressLint("MissingPermission")
    public static String getIMEI(Context context) {
        try {
            String deviceUniqueIdentifier = null;
            TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm == null) return null;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                deviceUniqueIdentifier = tm.getImei(0);

                if (TextUtils.isEmpty(deviceUniqueIdentifier)) {
                    try {
                        return tm.getDeviceId(0);
                    } catch (Throwable ignored) {
                        ignored.printStackTrace();
                    }

                    return tm.getMeid(0);
                }
            } else {
                deviceUniqueIdentifier = tm.getDeviceId();
            }
            if (deviceUniqueIdentifier != null) {
                return deviceUniqueIdentifier;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    private void loadId() {

        /**
         * 获取imei
         */

        final Context context = getApplicationContext();
        final Activity activity = this;
        imei = getIMEI(context);

        /**
         * 获取oaid
         */
        try {
            int i = new OaidHelper(new OaidHelper.AppIdsUpdater() {

                @Override
                public void OnIdsAvalid(@NonNull final String ids) {
                    if (!TextUtils.isEmpty(ids)) {
                        oaid = ids;
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateDeviceIDList();

                            }
                        });
                    }
                }
            }).CallFromReflect(context);
            Log.d("sigmob", "load oaid return value: " + i);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /**
         * 获取gaid
         */
        new Thread(new Runnable() {

            @Override
            public void run() {
                gaid = AdvertisingIdClient.getGAID(context);
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        updateDeviceIDList();

                    }
                });
            }
        }).start();

        /**
         * 获取uid
         */

    }


    private void initDeviceIDList() {
        mAdVersions.clear();
        for (int i = 0; i < mAdNames.length; i++) {
            String mAdName = mAdNames[i];
            switch (mAdName) {
                case "SigId":
                    try {
                        String uid = WindAds.sharedAds().getWindUid();
                        if (TextUtils.isEmpty(uid)) {
                            mAdVersions.put(mAdName, "Null");
                        } else {
                            mAdVersions.put(mAdName, uid);
                        }
                    } catch (Exception e) {
                        mAdVersions.put(mAdName, "Null");
                        e.printStackTrace();
                    }
                    break;
                case "IMEI":
                    try {


                        if (TextUtils.isEmpty(imei)) {
                            mAdVersions.put(mAdName, "Null");
                        } else {
                            mAdVersions.put(mAdName, imei);
                        }
                    } catch (Exception e) {
                        mAdVersions.put(mAdName, "Null");
                        e.printStackTrace();
                    }
                    break;
                case "OAID":
                    try {

                        if (TextUtils.isEmpty(oaid)) {
                            mAdVersions.put(mAdName, null);
                        } else {
                            mAdVersions.put(mAdName, oaid);
                        }
                    } catch (Exception e) {
                        mAdVersions.put(mAdName, "Null");
                        e.printStackTrace();
                    }
                    break;
                case "GAID":
                    try {

                        if (TextUtils.isEmpty(gaid)) {
                            mAdVersions.put(mAdName, "Null");
                        } else {
                            mAdVersions.put(mAdName, gaid);
                        }
                    } catch (Exception e) {
                        mAdVersions.put(mAdName, "Null");
                        e.printStackTrace();
                    }
                    break;

            }
        }
    }

}