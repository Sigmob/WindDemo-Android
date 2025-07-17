package com.sigmob.android.demo.natives;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.Toast;

import com.sigmob.android.demo.Constants;
import com.sigmob.android.demo.R;
import com.sigmob.android.demo.callbackinfo.CallBackInfo;
import com.sigmob.android.demo.callbackinfo.CallBackItem;
import com.sigmob.android.demo.callbackinfo.ExpandAdapter;
import com.sigmob.android.demo.callbackinfo.MyListView;
import com.sigmob.windad.WindAdError;
import com.sigmob.windad.natives.NativeADEventListener;
import com.sigmob.windad.natives.WindNativeAdData;
import com.sigmob.windad.natives.WindNativeAdRequest;
import com.sigmob.windad.natives.WindNativeUnifiedAd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NativeAdUnifiedActivity extends Activity {

    private ViewGroup adContainer;
    private WindNativeUnifiedAd windNativeAd;
    private int userID = 0;
    private String placementId;
    private List<WindNativeAdData> nativeAdDataList;

    // 广告宽高
    private int adWidth, adHeight;
    private CheckBox checkBoxFullWidth, checkBoxAutoHeight;

    private MyListView listView;
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

    private void getExtraInfo() {
        Intent intent = getIntent();
        placementId = intent.getStringExtra(Constants.CONF_PLACEMENT_ID);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_ad_unified);
        adContainer = findViewById(R.id.native_ad_container);
        getExtraInfo();
        initCallBack();
    }

    public void ButtonClick(View view) {
        switch (view.getId()) {
            case R.id.load_native_button:
                resetCallBackData();
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                // 加载原生广告
                loadNativeAd();
                break;
            case R.id.show_native_button:
                // 展示原生广告
                showNativeAd();
                break;
        }
    }

    public static int screenWidthAsIntDips(Context context) {
        int pixels = context.getResources().getDisplayMetrics().widthPixels;
        float density = context.getResources().getDisplayMetrics().density;
        return (int) ((pixels / density) + 0.5f);
    }

    private void loadNativeAd() {
        Log.d("windSDK", "-----------loadNativeAd-----------");

        userID++;
        Map<String, Object> options = new HashMap<>();

        options.put("user_id", String.valueOf(userID));
        if (windNativeAd == null) {
            windNativeAd = new WindNativeUnifiedAd(new WindNativeAdRequest(placementId, String.valueOf(userID), 1, options));
        }

        windNativeAd.setNativeAdLoadListener(new WindNativeUnifiedAd.WindNativeAdLoadListener() {
            @Override
            public void onAdError(WindAdError error, String placementId) {
                Log.d("windSDK", "onAdError:" + error.toString() + ":" + placementId);
                logCallBack("onError", error.toString());
                Toast.makeText(NativeAdUnifiedActivity.this, "onAdError", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLoad(List<WindNativeAdData> list, String s) {
                Toast.makeText(NativeAdUnifiedActivity.this, "onAdLoad", Toast.LENGTH_SHORT).show();
                logCallBack("onAdLoad", "");
                nativeAdDataList = list;
            }
        });
        windNativeAd.loadAd();
    }

    private void showNativeAd() {
        Log.d("windSDK", "-----------showNativeAd-----------: " + placementId);
        if (nativeAdDataList != null && !nativeAdDataList.isEmpty()) {
            WindNativeAdData nativeADData = nativeAdDataList.get(0);
            //媒体自渲染的View
            NativeAdDemoRender adRender = new NativeAdDemoRender();

            View nativeAdView = adRender.getNativeAdView(this, nativeADData, new NativeADEventListener() {
                @Override
                public void onAdExposed() {
                    Log.d("windSDK", "onAdExposed: ");
                    logCallBack("onAdExposed", "");
                }

                @Override
                public void onAdClicked() {
                    Log.d("windSDK", "onAdClicked: ");
                    logCallBack("onAdClicked", "");
                }

                @Override
                public void onAdDetailShow() {
                    Log.d("windSDK", "onAdDetailShow: ");
                    logCallBack("onAdDetailShow", "");
                }

                @Override
                public void onAdDetailDismiss() {
                    Log.d("windSDK", "onAdDetailDismiss: ");
                    logCallBack("onAdDetailDismiss", "");
                }

                @Override
                public void onAdError(WindAdError error) {
                    logCallBack("onAdError", error.toString());
                    Log.d("windSDK", "onAdError error code :" + error.toString());
                }
            }, new WindNativeAdData.NativeADMediaListener() {
                @Override
                public void onVideoLoad() {
                    Log.d("windSDK", "----------onVideoLoad----------");
                }

                @Override
                public void onVideoError(WindAdError error) {
                    Log.d("windSDK", "----------onVideoError----------:" + error.toString());
                }

                @Override
                public void onVideoStart() {
                    Log.d("windSDK", "----------onVideoStart----------");
                }

                @Override
                public void onVideoPause() {
                    Log.d("windSDK", "----------onVideoPause----------");
                }

                @Override
                public void onVideoResume() {
                    Log.d("windSDK", "----------onVideoResume----------");
                }

                @Override
                public void onVideoCompleted() {
                    Log.d("windSDK", "----------onVideoCompleted----------");
                }
            });
            // 设置 Dislike 弹窗
            nativeADData.setDislikeInteractionCallback(this, new WindNativeAdData.DislikeInteractionCallback() {
                @Override
                public void onShow() {
                    logCallBack("onShow", "");
                    Log.d("windSDK", "onShow");
                }

                @Override
                public void onSelected(int position, String value, boolean enforce) {
                    logCallBack("onSelected", "");
                    Log.d("windSDK", "onSelected: " + position + ":" + value + ":" + enforce);
                    if (adContainer != null) {
                        adContainer.removeAllViews();
                    }
                }

                @Override
                public void onCancel() {
                    logCallBack("onCancel", "");
                    Log.d("windSDK", "onCancel");
                }
            });

            ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            // 媒体最终将要展示广告的容器
            if (adContainer != null) {
                adContainer.removeAllViews();
                adContainer.addView(nativeAdView, lp);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (nativeAdDataList != null && !nativeAdDataList.isEmpty()) {
            for (WindNativeAdData ad : nativeAdDataList) {
                if (ad != null) {
                    ad.destroy();
                }
            }
        }
        if (windNativeAd != null) {
            windNativeAd.destroy();
        }
    }

    private void resetCallBackData() {
        callBackDataList.clear();
        for (int i = 0; i < CallBackInfo.NATIVE_CALLBACK.length; i++) {
            callBackDataList.add(new CallBackItem(CallBackInfo.NATIVE_CALLBACK[i], "", false, false));
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