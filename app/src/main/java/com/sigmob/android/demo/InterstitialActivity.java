package com.sigmob.android.demo;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sigmob.android.demo.callbackinfo.CallBackInfo;
import com.sigmob.android.demo.callbackinfo.CallBackItem;
import com.sigmob.android.demo.callbackinfo.ExpandAdapter;
import com.sigmob.windad.WindAdError;
import com.sigmob.windad.WindAds;
import com.sigmob.windad.interstitial.WindInterstitialAd;
import com.sigmob.windad.interstitial.WindInterstitialAdListener;
import com.sigmob.windad.interstitial.WindInterstitialAdRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InterstitialActivity extends Activity implements WindInterstitialAdListener {

    private WindInterstitialAd windInterstitialAd;
    private String placementId;
    private String userID = "123456789";

    private ListView listView;
    private ExpandAdapter adapter;
    private List<CallBackItem> callBackDataList = new ArrayList<>();

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interstitial);

        placementId = Constants.fullScreen_placement_id;


        WebView.setWebContentsDebuggingEnabled(true);

        initCallBack();

    }

    public void ButtonClick(View view) {
        switch (view.getId()) {
            case R.id.bt_load_ad:
                resetCallBackData();
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                if (windInterstitialAd == null) {
                    Map<String, Object> options = new HashMap<>();
                    options.put("user_id", String.valueOf(userID));
                    windInterstitialAd = new WindInterstitialAd( new WindInterstitialAdRequest(placementId, userID, options));
                    windInterstitialAd.setWindInterstitialAdListener(this);
                }
                windInterstitialAd.loadAd();
                break;
            case R.id.bt_show_ad:
                HashMap option = new HashMap();
                option.put(WindAds.AD_SCENE_ID, "567");
                option.put(WindAds.AD_SCENE_DESC, "转盘抽奖");
                if (windInterstitialAd != null && windInterstitialAd.isReady()) {
                    windInterstitialAd.show( option);
                } else {
                    Log.d("windSDK", "------Ad is not Ready------");
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (windInterstitialAd != null) {
            windInterstitialAd.destroy();
            windInterstitialAd = null;
        }
    }

    @Override
    public void onInterstitialAdLoadSuccess(final String placementId) {
        Log.d("windSDK", "------onInterstitialAdLoadSuccess------" + placementId);
//        Toast.makeText(InterstitialActivity.this, "onInterstitialAdLoadSuccess", Toast.LENGTH_SHORT).show();
        logCallBack("onInterstitialAdLoadSuccess", "");
    }

    @Override
    public void onInterstitialAdPreLoadSuccess(String s) {
        Log.d("windSDK", "------onInterstitialAdPreLoadSuccess------" + placementId);
//        Toast.makeText(InterstitialActivity.this, "onInterstitialAdLoadSuccess", Toast.LENGTH_SHORT).show();
        logCallBack("onInterstitialAdPreLoadSuccess", "");
    }

    @Override
    public void onInterstitialAdPreLoadFail(String s) {
        Log.d("windSDK", "------onInterstitialAdPreLoadFail------" + placementId);
//        Toast.makeText(InterstitialActivity.this, "onInterstitialAdLoadSuccess", Toast.LENGTH_SHORT).show();
        logCallBack("onInterstitialAdPreLoadFail", "");
    }

    @Override
    public void onInterstitialAdPlayStart(final String placementId) {
        Log.d("windSDK", "------onInterstitialAdPlayStart------" + placementId);
        logCallBack("onInterstitialAdPlayStart", "");
    }

    @Override
    public void onInterstitialAdPlayEnd(final String placementId) {
        Log.d("windSDK", "------onInterstitialAdPlayEnd------" + placementId);
        logCallBack("onInterstitialAdPlayEnd", "");
    }

    @Override
    public void onInterstitialAdClicked(final String placementId) {
        Log.d("windSDK", "------onInterstitialAdClicked------" + placementId);
        logCallBack("onInterstitialAdClicked", "");
    }

    @Override
    public void onInterstitialAdClosed(final String placementId) {
        Log.d("windSDK", "------onInterstitialAdClosed------" + placementId);
        logCallBack("onInterstitialAdClosed", "");
    }

    @Override
    public void onInterstitialAdLoadError(final WindAdError error, final String placementId) {
        Log.d("windSDK", "------onInterstitialAdLoadError------" + error.toString() + ":" + placementId);
        logCallBack("onInterstitialAdLoadError", error.toString());
    }

    @Override
    public void onInterstitialAdPlayError(final WindAdError error, final String placementId) {
        Log.d("windSDK", "------onInterstitialAdPlayError------" + error.toString() + ":" + placementId);
        logCallBack("onInterstitialAdPlayError", error.toString());
    }

    private void resetCallBackData() {
        callBackDataList.clear();
        for (int i = 0; i < CallBackInfo.INTERSTITIAL_CALLBACK.length; i++) {
            callBackDataList.add(new CallBackItem(CallBackInfo.INTERSTITIAL_CALLBACK[i], "", false, false));
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
}