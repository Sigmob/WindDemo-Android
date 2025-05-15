package com.sigmob.android.demo;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ListView;

import com.sigmob.android.demo.callbackinfo.CallBackInfo;
import com.sigmob.android.demo.callbackinfo.CallBackItem;
import com.sigmob.android.demo.callbackinfo.ExpandAdapter;
import com.sigmob.windad.WindAdError;
import com.sigmob.windad.WindAds;
import com.sigmob.windad.newInterstitial.WindNewInterstitialAd;
import com.sigmob.windad.newInterstitial.WindNewInterstitialAdListener;
import com.sigmob.windad.newInterstitial.WindNewInterstitialAdRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewInterstitialActivity extends Activity implements WindNewInterstitialAdListener {

    private WindNewInterstitialAd WindNewInterstitialAd;
    private String placementId;
    private String userID = "123456789";

    private ListView listView;
    private ExpandAdapter adapter;
    private final List<CallBackItem> callBackDataList = new ArrayList<>();

    private void initCallBack() {
        resetCallBackData();
        listView = findViewById(R.id.callback_lv);
        adapter = new ExpandAdapter(this, callBackDataList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Log.d("windSDK", "------onItemClick------" + position);
            CallBackItem callItem = callBackDataList.get(position);
            if (callItem == null) return;

            if (callItem.is_expand()) {
                callItem.set_expand(false);
            } else {
                callItem.set_expand(true);
            }
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_interstitial);

        placementId = Constants.newInterstitial_placement_id;
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
                if (WindNewInterstitialAd == null) {
                    Map<String, Object> options = new HashMap<>();
                    options.put("user_id", String.valueOf(userID));
                    WindNewInterstitialAd = new WindNewInterstitialAd(new WindNewInterstitialAdRequest(placementId, userID, options));
                    WindNewInterstitialAd.setWindNewInterstitialAdListener(this);
                }
                WindNewInterstitialAd.loadAd();
                break;
            case R.id.bt_show_ad:
                HashMap<String, String> option = new HashMap<>();
                option.put(WindAds.AD_SCENE_ID, "567");
                option.put(WindAds.AD_SCENE_DESC, "转盘抽奖");
                if (WindNewInterstitialAd != null && WindNewInterstitialAd.isReady()) {
                    WindNewInterstitialAd.show(option);
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
        if (WindNewInterstitialAd != null) {
            WindNewInterstitialAd.destroy();
            WindNewInterstitialAd = null;
        }
    }

    @Override
    public void onInterstitialAdLoadSuccess(final String placementId) {
        Log.d("windSDK", "------onInterstitialAdLoadSuccess------" + placementId);
        //Toast.makeText(InterstitialActivity.this, "onInterstitialAdLoadSuccess", Toast.LENGTH_SHORT).show();
        logCallBack("onInterstitialAdLoadSuccess", "");
    }

    @Override
    public void onInterstitialAdPreLoadSuccess(String s) {
        Log.d("windSDK", "------onInterstitialAdPreLoadSuccess------" + placementId);
        //Toast.makeText(InterstitialActivity.this, "onInterstitialAdLoadSuccess", Toast.LENGTH_SHORT).show();
        logCallBack("onInterstitialAdPreLoadSuccess", "");
    }

    @Override
    public void onInterstitialAdPreLoadFail(String s) {
        Log.d("windSDK", "------onInterstitialAdPreLoadFail------" + placementId);
        //Toast.makeText(InterstitialActivity.this, "onInterstitialAdLoadSuccess", Toast.LENGTH_SHORT).show();
        logCallBack("onInterstitialAdPreLoadFail", "");
    }

    @Override
    public void onInterstitialAdShow(final String placementId) {
        Log.d("windSDK", "------onInterstitialAdShow------" + placementId);
        logCallBack("onInterstitialAdShow", "");
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
    public void onInterstitialAdShowError(final WindAdError error, final String placementId) {
        Log.d("windSDK", "------onInterstitialAdShowError------" + error.toString() + ":" + placementId);
        logCallBack("onInterstitialAdShowError", error.toString());
    }

    private void resetCallBackData() {
        callBackDataList.clear();
        for (int i = 0; i < CallBackInfo.NEW_INTERSTITIAL_CALLBACK.length; i++) {
            callBackDataList.add(new CallBackItem(CallBackInfo.NEW_INTERSTITIAL_CALLBACK[i], "", false, false));
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