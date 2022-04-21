package com.sigmob.android.demo;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.sigmob.android.demo.callbackinfo.CallBackInfo;
import com.sigmob.android.demo.callbackinfo.CallBackItem;
import com.sigmob.android.demo.callbackinfo.ExpandAdapter;
import com.sigmob.windad.WindAdError;
import com.sigmob.windad.WindAds;
import com.sigmob.windad.rewardVideo.WindRewardAdRequest;
import com.sigmob.windad.rewardVideo.WindRewardInfo;
import com.sigmob.windad.rewardVideo.WindRewardVideoAd;
import com.sigmob.windad.rewardVideo.WindRewardVideoAdListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RewardVideoActivity extends Activity {

    private Spinner spinner;
    private ArrayAdapter<String> arrayAdapter;
    private WindRewardVideoAd windRewardedVideoAd;
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
        setContentView(R.layout.activity_reward_video);

        placementId = Constants.reward_placement_id;

        initCallBack();

        Map<String, Object> options = new HashMap<>();
        options.put("user_id", String.valueOf(userID));
        windRewardedVideoAd = new WindRewardVideoAd( new WindRewardAdRequest(placementId, userID, options));
        windRewardedVideoAd.setWindRewardVideoAdListener(new WindRewardVideoAdListener() {
            @Override
            public void onRewardAdLoadSuccess(final String placementId) {
                Log.d("windSDK", "------onRewardAdLoadSuccess------" + placementId);
                logCallBack("onRewardAdLoadSuccess", "");
            }

            @Override
            public void onRewardAdPreLoadSuccess(String s) {
                Log.d("windSDK", "------onRewardAdPreLoadSuccess------" + placementId);
                logCallBack("onRewardAdPreLoadSuccess", "");
            }

            @Override
            public void onRewardAdPreLoadFail(String s) {
                Log.d("windSDK", "------onRewardAdPreLoadFail------" + placementId);
                logCallBack("onRewardAdPreLoadFail", "");
            }

            @Override
            public void onRewardAdPlayEnd(final String placementId) {
                Log.d("windSDK", "------onRewardAdPlayEnd------" + placementId);
                logCallBack("onRewardAdPlayEnd", "");
            }

            @Override
            public void onRewardAdPlayStart(final String placementId) {
                Log.d("windSDK", "------onRewardAdPlayStart------" + placementId);
                logCallBack("onRewardAdPlayStart", "");
            }

            @Override
            public void onRewardAdClicked(final String placementId) {
                Log.d("windSDK", "------onRewardAdClicked------" + placementId);
                logCallBack("onRewardAdClicked", "");
            }

            @Override
            public void onRewardAdClosed(final String placementId) {
                Log.d("windSDK", "------onRewardAdClosed------" + placementId);
                logCallBack("onRewardAdClosed", "");
            }

            @Override
            public void onRewardAdRewarded(WindRewardInfo rewardInfo, String placementId) {
                Log.d("windSDK", "------onRewardAdRewarded------" + rewardInfo.toString() + ":" + placementId);
                logCallBack("onRewardAdRewarded", rewardInfo.toString());

            }

            @Override
            public void onRewardAdLoadError(WindAdError error, String placementId) {
                Log.d("windSDK", "------onRewardAdLoadError------" + error.toString() + ":" + placementId);
                logCallBack("onRewardAdLoadError", error.toString());
            }

            @Override
            public void onRewardAdPlayError(WindAdError error, String placementId) {
                Log.d("windSDK", "------onRewardAdPlayError------" + error.toString() + ":" + placementId);
                logCallBack("onRewardAdPlayError", error.toString());
            }


        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (windRewardedVideoAd != null) {
            windRewardedVideoAd.destroy();
            windRewardedVideoAd = null;
        }
    }

    public void ButtonClick(View view) {
        switch (view.getId()) {
            case R.id.bt_load_ad:
                resetCallBackData();
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                }
                if (windRewardedVideoAd != null) {
                    windRewardedVideoAd.loadAd();
                }
                break;
            case R.id.bt_show_ad:
                HashMap option = new HashMap();
                option.put(WindAds.AD_SCENE_ID, "567");
                option.put(WindAds.AD_SCENE_DESC, "转盘抽奖");
                if (windRewardedVideoAd != null && windRewardedVideoAd.isReady()) {
                    windRewardedVideoAd.show( option);
                } else {
                    Log.d("windSDK", "------Ad is not Ready------");
                }
                break;
        }
    }


    private void resetCallBackData() {
        callBackDataList.clear();
        for (int i = 0; i < CallBackInfo.REWARD_CALLBACK.length; i++) {
            callBackDataList.add(new CallBackItem(CallBackInfo.REWARD_CALLBACK[i], "", false, false));
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