package com.sigmob.android.demo;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

public class DeviceSettingActivity extends Activity {

    private CheckBox cbCanUseLocation;
    private CheckBox cbCanUsePhoneState;
    private CheckBox cbCanUseOaid;
    private CheckBox cbCanUseAndroidId;
    private CheckBox cbCanUseAppList;
    private CheckBox cbCanUseSimOperator;
    private CheckBox cbCanUseSpaceSize;
    private Button btnSave;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_setting);
        setTitle("设备信息控制");

        sharedPreferences = getSharedPreferences(Constants.DEVICE_PREFS, 0);

        initView();
        loadSettings();
    }

    private void initView() {
        cbCanUseLocation = findViewById(R.id.cb_can_use_location);
        cbCanUsePhoneState = findViewById(R.id.cb_can_use_phone_state);
        cbCanUseOaid = findViewById(R.id.cb_can_use_oaid);
        cbCanUseAndroidId = findViewById(R.id.cb_can_use_android_id);
        cbCanUseAppList = findViewById(R.id.cb_can_use_app_list);
        cbCanUseSimOperator = findViewById(R.id.cb_can_use_sim_operator);
        cbCanUseSpaceSize = findViewById(R.id.cb_can_use_space_size);
        btnSave = findViewById(R.id.btn_save);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
            }
        });
    }

    private void loadSettings() {
        cbCanUseLocation.setChecked(sharedPreferences.getBoolean(Constants.DEV_CAN_USE_LOCATION, true));
        cbCanUsePhoneState.setChecked(sharedPreferences.getBoolean(Constants.DEV_CAN_USE_PHONE_STATE, true));
        cbCanUseOaid.setChecked(sharedPreferences.getBoolean(Constants.DEV_CAN_USE_OAID, true));
        cbCanUseAndroidId.setChecked(sharedPreferences.getBoolean(Constants.DEV_CAN_USE_ANDROID_ID, true));
        cbCanUseAppList.setChecked(sharedPreferences.getBoolean(Constants.DEV_CAN_USE_APP_LIST, true));
        cbCanUseSimOperator.setChecked(sharedPreferences.getBoolean(Constants.DEV_CAN_USE_SIM_OPERATOR, true));
        cbCanUseSpaceSize.setChecked(sharedPreferences.getBoolean(Constants.DEV_CAN_USE_SPACE_SIZE, true));
    }

    private void saveSettings() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(Constants.DEV_CAN_USE_LOCATION, cbCanUseLocation.isChecked());
        editor.putBoolean(Constants.DEV_CAN_USE_PHONE_STATE, cbCanUsePhoneState.isChecked());
        editor.putBoolean(Constants.DEV_CAN_USE_OAID, cbCanUseOaid.isChecked());
        editor.putBoolean(Constants.DEV_CAN_USE_ANDROID_ID, cbCanUseAndroidId.isChecked());
        editor.putBoolean(Constants.DEV_CAN_USE_APP_LIST, cbCanUseAppList.isChecked());
        editor.putBoolean(Constants.DEV_CAN_USE_SIM_OPERATOR, cbCanUseSimOperator.isChecked());
        editor.putBoolean(Constants.DEV_CAN_USE_SPACE_SIZE, cbCanUseSpaceSize.isChecked());
        editor.apply();

        Toast.makeText(this, "设置已保存，下次初始化 SDK 时生效", Toast.LENGTH_SHORT).show();
        finish();
    }
}
