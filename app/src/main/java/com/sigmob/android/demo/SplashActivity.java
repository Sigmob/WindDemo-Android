package com.sigmob.android.demo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ViewGroup;

import com.sigmob.windad.Splash.WindSplashAD;
import com.sigmob.windad.Splash.WindSplashADListener;
import com.sigmob.windad.Splash.WindSplashAdRequest;
import com.sigmob.windad.WindAdError;

import java.util.HashMap;
import java.util.Map;


public class SplashActivity extends Activity implements WindSplashADListener {

    /**
     * 设置一个变量来控制当前开屏页面是否可以跳转，当开屏广告为普链类广告时，点击会打开一个广告落地页，此时开发者还不能打开自己的App主页。当从广告落地页返回以后，
     * 才可以跳转到开发者自己的App主页；当开屏广告是App类广告时只会下载App。
     */
    public boolean canJumpImmediately = false;
    private WindSplashAD splashAd;
    private ViewGroup adContainer;

    private boolean isLoadAndShow = true;//是否实时加载并显示开屏广告
    private boolean isFullScreen = false;
    private boolean needStartMainActivity = true;

    private String placementId ;
    private String userId = "123456789";

    private HashMap<String, String> callBack;

    private void getExtraInfo() {
        Intent intent = getIntent();
        isLoadAndShow = intent.getBooleanExtra("isLoadAndShow", true);
        isFullScreen = intent.getBooleanExtra("isFullScreen", false);
        placementId = intent.getStringExtra("placementId");
        if(TextUtils.isEmpty(placementId)){
            placementId = "ea1f8f21300";
        }
        needStartMainActivity = intent.getBooleanExtra("need_start_main_activity", true);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        adContainer = findViewById(R.id.splash_container);
        callBack = new HashMap<>();

        getExtraInfo();

        Map<String, Object> options = new HashMap<>();
        options.put("user_id", userId);
        WindSplashAdRequest splashAdRequest = new WindSplashAdRequest(placementId, userId, options);

        splashAd = new WindSplashAD( splashAdRequest, this);

        if (isFullScreen){
            adContainer = (ViewGroup) getWindow().getDecorView();
        }
        if (isLoadAndShow) {
            splashAd.loadAndShow(adContainer);

        } else {
            splashAd.loadAd();
        }
    }

    private void jumpWhenCanClick() {
        if (canJumpImmediately) {
            jumpMainActivity();
        } else {
            canJumpImmediately = true;
        }
    }

    /**
     * 不可点击的开屏，使用该jump方法，而不是用jumpWhenCanClick
     */
    private void jumpMainActivity() {
        if (needStartMainActivity) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            this.startActivity(intent);
            this.finish();
        } else {
            Intent intent = new Intent();
            intent.putExtra("result", callBack);
            //设置返回数据
            this.setResult(RESULT_OK, intent);
            this.finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        canJumpImmediately = false;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (canJumpImmediately) {
            jumpWhenCanClick();
        }
        canJumpImmediately = true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        splashAd.destroy();
    }

    @Override
    public void onSplashAdShow(String s) {
        Log.d("windSDK", "------onSplashAdShow------");
        callBack.put("onSplashAdShow", "");
    }

    @Override
    public void onSplashAdLoadSuccess(String s) {
        Log.d("windSDK", "------onSplashAdSuccessLoad------" + splashAd.isReady());
        callBack.put("onSplashAdLoadSuccess", "");
        if (!isLoadAndShow && splashAd.isReady()) {
            splashAd.show(adContainer);
        }
    }

    @Override
    public void onSplashAdLoadFail(WindAdError error, String placementId) {
        callBack.put("onSplashAdLoadFail", error.toString());
        Log.d("windSDK", "------onSplashAdLoadFail------" + error.toString() + ":" + placementId);
        jumpMainActivity();

    }

    @Override
    public void onSplashAdShowError(WindAdError error, String s) {
        callBack.put("onSplashAdShowError", error.toString());
        Log.d("windSDK", "------onSplashAdShowError------" + error.toString() + ":" + placementId);
        jumpMainActivity();
    }

    @Override
    public void onSplashAdClick(String placementId) {
        callBack.put("onSplashAdClick", "");
        Log.d("windSDK", "------onSplashAdClick------");
    }

    @Override
    public void onSplashAdClose(String s) {
        callBack.put("onSplashClose", "");
        Log.d("windSDK", "------onSplashAdClose------");
        jumpWhenCanClick();
    }

    @Override
    public void onSplashAdSkip(String placementId) {
        callBack.put("onSplashAdSkip","");
        Log.d("windSDK", "------onSplashAdSkip------");

    }
}
