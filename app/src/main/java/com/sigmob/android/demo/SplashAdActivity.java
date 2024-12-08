package com.sigmob.android.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Spinner;

import com.sigmob.android.demo.callbackinfo.CallBackInfo;
import com.sigmob.android.demo.callbackinfo.CallBackItem;
import com.sigmob.android.demo.callbackinfo.ExpandAdapter;
import com.sigmob.windad.Splash.WindSplashAD;
import com.sigmob.windad.Splash.WindSplashADListener;
import com.sigmob.windad.Splash.WindSplashAdRequest;
import com.sigmob.windad.WindAdError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SplashAdActivity extends Activity {
    private Spinner spinner;
    private ArrayAdapter<String> arrayAdapter;
    private CheckBox fullScreen;
    private boolean isFullScreen;
    private String placementId;

    private ListView listView;
    private ExpandAdapter adapter;
    private List<CallBackItem> callBackDataList = new ArrayList<>();
    private String userId = "123456789";
    private WindSplashAD splashAd;


    private void initCallBack() {
        resetCallBackData();
        listView = findViewById(R.id.callback_lv);
        adapter = new ExpandAdapter(this, callBackDataList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("windSDK", "------onItemClick------" + position);
                CallBackItem callItem = callBackDataList.get(position);
                if (callItem != null) {
                    if (callItem.is_expand()) {
                        callItem.set_expand(false);
                    } else {
                        callItem.set_expand(true);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });


    }

    private void loadAd(boolean show){

        Map<String, Object> options = new HashMap<>();
        options.put("user_id", userId);

        WindSplashAdRequest splashAdRequest = new WindSplashAdRequest(placementId, userId, options);
        splashAd = new WindSplashAD(splashAdRequest, new WindSplashADListener() {
            @Override
            public void onSplashAdShow(String placementId) {
                logCallBack("onSplashAdShow", "");

            }

            @Override
            public void onSplashAdLoadSuccess(String placementId) {
                logCallBack("onSplashAdLoadSuccess", "");

            }

            @Override
            public void onSplashAdLoadFail(WindAdError error, String placementId) {
                logCallBack("onSplashAdLoadFail", error.toString());

            }

            @Override
            public void onSplashAdShowError(WindAdError error, String placementId) {
                logCallBack("onSplashAdShowError", error.toString());

            }

            @Override
            public void onSplashAdClick(String placementId) {
                logCallBack("onSplashAdClick", "");

            }

            @Override
            public void onSplashAdClose(String placementId) {
                logCallBack("onSplashAdClose", "");
                splashAd.destroy();
            }

            @Override
            public void onSplashAdSkip(String s) {
                logCallBack("onSplashAdSkip", "");

            }
        });

        if (show){
            splashAd.loadAndShow((ViewGroup)getWindow().getDecorView());
        }else{
            splashAd.loadAd();

        }

    }

    private void showAd(){
        splashAd.show((ViewGroup)getWindow().getDecorView());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_ad);

        fullScreen = findViewById(R.id.cb_fullscreen);

        fullScreen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                isFullScreen = isChecked;
            }
        });

        initCallBack();
    }

    public void ButtonClick(View view) {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }

        placementId = Constants.splash_placement_id;

        Intent intent = new Intent(this, SplashActivity.class);
        intent.putExtra("isFullScreen", isFullScreen);
        intent.putExtra("placementId", placementId);
        intent.putExtra("need_start_main_activity", false);
        switch (view.getId()) {
            case R.id.bt_load_show:
                resetCallBackData();
                loadAd(true);
                break;
            case R.id.bt_load_only:
                resetCallBackData();
                loadAd(false);
                break;
            case R.id.bt_show_only:
                showAd();
                break;
        }
    }



    private void resetCallBackData() {
        callBackDataList.clear();
        for (int i = 0; i < CallBackInfo.SPLASH_CALLBACK.length; i++) {
            callBackDataList.add(new CallBackItem(CallBackInfo.SPLASH_CALLBACK[i], "", false, false));
        }
    }

    private void logCallBack(String call, String child) {
        for (int i = 0; i < callBackDataList.size(); i++) {
            CallBackItem callItem = callBackDataList.get(i);
            if (callItem.getText().equals(call)) {
                callItem.set_callback(true);
                if (!TextUtils.isEmpty(child)) {
                    callItem.setChild_text(child);
                }
                break;
            }
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null){
            HashMap<String, String> result = (HashMap<String, String>) data.getExtras().getSerializable("result");
            Log.d("windSDK", "------onActivityResult------" + resultCode + ":" + resultCode + ":" + result.size());
            for (Map.Entry<String, String> entry : result.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                logCallBack(key, value);
            }
        }
    }
}