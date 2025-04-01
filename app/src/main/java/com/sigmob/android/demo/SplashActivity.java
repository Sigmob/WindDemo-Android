package com.sigmob.android.demo;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        // 展示隐私条款弹窗
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
                Toast.makeText(SplashActivity.this, "您需要同意隐私政策才能使用本应用",
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
     * 不可点击的开屏，使用该 jump 方法，而不是用 jumpWhenCanClick
     */
    private void jumpMainActivity() {
        Intent intent = new Intent(this, MainActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }
}