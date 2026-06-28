package com.sigmob.android.demo;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.sigmob.windad.WindAds;

public class PrivacySettingActivity extends Activity {

    private CheckBox cbAdult;
    private CheckBox cbPersonalized;
    private CheckBox cbProgrammatic;
    private Button btnSave;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_setting);
        setTitle("隐私设置");

        sharedPreferences = getSharedPreferences(Constants.PRIVACY_PREFS, 0);

        initView();
        loadSettings();
    }

    private void initView() {
        cbAdult = findViewById(R.id.cb_adult);
        cbPersonalized = findViewById(R.id.cb_personalized);
        cbProgrammatic = findViewById(R.id.cb_programmatic);
        btnSave = findViewById(R.id.btn_save);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
            }
        });
    }

    private void loadSettings() {
        cbAdult.setChecked(sharedPreferences.getBoolean(Constants.CONF_ADULT, true));
        cbPersonalized.setChecked(sharedPreferences.getBoolean(Constants.CONF_PERSONALIZED, true));
        cbProgrammatic.setChecked(sharedPreferences.getBoolean(Constants.CONF_PROGRAMMATIC, true));
    }

    private void saveSettings() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.CONF_ADULT, cbAdult.isChecked());
        editor.putBoolean(Constants.CONF_PERSONALIZED, cbPersonalized.isChecked());
        editor.putBoolean(Constants.CONF_PROGRAMMATIC, cbProgrammatic.isChecked());
        editor.apply();

        // 实时应用开关设置（SDK 已初始化时生效）
        applySettingsToSDK();

        Toast.makeText(this, "设置已保存", Toast.LENGTH_SHORT).show();
        finish();
    }

    private void applySettingsToSDK() {
        WindAds ads = WindAds.sharedAds();
        if (!ads.isInit()) {
            return;
        }
        ads.setAdult(cbAdult.isChecked());
        ads.setPersonalizedAdvertisingOn(cbPersonalized.isChecked());
        ads.setProgrammaticRecommend(cbProgrammatic.isChecked());
    }
}
