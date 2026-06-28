package com.sigmob.android.demo;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;

import com.sigmob.android.demo.natives.NativeAdActivity;
import com.sigmob.windad.WindAds;

public class AdEntryActivity extends androidx.appcompat.app.AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_entry);
        setTitle("Sigmob SDK Version : " + WindAds.getVersion());
        findViewById(R.id.bt_splash).setOnClickListener(v -> startActivity(new Intent(this, SplashAdActivity.class)));
        findViewById(R.id.bt_reward).setOnClickListener(v -> startActivity(new Intent(this, RewardVideoActivity.class)));
        findViewById(R.id.bt_new_interstitial).setOnClickListener(v -> startActivity(new Intent(this, NewInterstitialActivity.class)));
        findViewById(R.id.bt_native).setOnClickListener(v -> startActivity(new Intent(this, NativeAdActivity.class)));
        findViewById(R.id.bt_device).setOnClickListener(v -> startActivity(new Intent(this, DeviceActivity.class)));
    }
}
