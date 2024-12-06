package com.sigmob.android.demo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Toast;

import com.sigmob.windad.Splash.WindSplashAD;
import com.sigmob.windad.Splash.WindSplashADListener;
import com.sigmob.windad.Splash.WindSplashAdRequest;
import com.sigmob.windad.WindAdError;

import java.util.HashMap;
import java.util.Map;


public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        showPrivacyPolicyDialog();

    }

    private void showPrivacyPolicyDialog() {
        PrivacyPolicyDialog dialog = new PrivacyPolicyDialog(this);
        dialog.setOnPrivacyResultListener(new PrivacyPolicyDialog.OnPrivacyResultListener() {
            @Override
            public void onAccept() {
                // 用户同意隐私政策
                savePrivacyPolicyAccepted();
                // 继续应用的初始化流程
                proceedWithAppInitialization();
            }

            @Override
            public void onReject() {
                // 用户拒绝隐私政策
                Toast.makeText(SplashActivity.this,
                        "您需要同意隐私政策才能使用本应用",
                        Toast.LENGTH_LONG).show();
                finish();
            }
        });
        dialog.show();
    }

    private boolean shouldShowPrivacyPolicy() {
        SharedPreferences prefs = getSharedPreferences("app_settings", MODE_PRIVATE);
        return !prefs.getBoolean("privacy_policy_accepted", false);
    }

    private void savePrivacyPolicyAccepted() {
        SharedPreferences prefs = getSharedPreferences("app_settings", MODE_PRIVATE);
        prefs.edit().putBoolean("privacy_policy_accepted", true).apply();
    }

    private void proceedWithAppInitialization() {
        // 继续应用的初始化流程
        jumpMainActivity();
    }


    /**
     * 不可点击的开屏，使用该jump方法，而不是用jumpWhenCanClick
     */
    private void jumpMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        this.startActivity(intent);
        this.finish();
    }
}
