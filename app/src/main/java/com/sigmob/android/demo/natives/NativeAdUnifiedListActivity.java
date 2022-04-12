package com.sigmob.android.demo.natives;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.sigmob.android.demo.R;
import com.sigmob.android.demo.view.ILoadMoreListener;
import com.sigmob.android.demo.view.LoadMoreListView;
import com.sigmob.windad.WindAdError;
import com.sigmob.windad.natives.NativeADEventListener;
import com.sigmob.windad.natives.WindNativeAdData;
import com.sigmob.windad.natives.WindNativeAdRequest;
import com.sigmob.windad.natives.WindNativeUnifiedAd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NativeAdUnifiedListActivity extends Activity {

    private static final int LIST_ITEM_COUNT = 10;
    private LoadMoreListView mListView;
    private MyAdapter myAdapter;
    private WindNativeUnifiedAd windNativeUnifiedAd;
    private int userID = 0;
    private String placementId;

    private List<WindNativeAdData> mData;

    private Handler mHandler = new Handler(Looper.getMainLooper());

    private int adWidth; // 广告宽高


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_ad_unified_list);
        getExtraInfo();
        initListView();
        adWidth = screenWidthAsIntDips(this) - 20;//减20因为容器有个margin 10dp//340
    }

    private void getExtraInfo() {
        Intent intent = getIntent();
        placementId = intent.getStringExtra("placementId");
    }

    public static int screenWidthAsIntDips(Context context) {
        int pixels = context.getResources().getDisplayMetrics().widthPixels;
        float density = context.getResources().getDisplayMetrics().density;
        return (int) ((pixels / density) + 0.5f);
    }


    private void initListView() {
        mListView = (LoadMoreListView) findViewById(R.id.unified_native_ad_list);
        mData = new ArrayList<>();
        myAdapter = new MyAdapter(this, mData);
        mListView.setAdapter(myAdapter);
        mListView.setLoadMoreListener(new ILoadMoreListener() {
            @Override
            public void onLoadMore() {
                loadListAd();
            }
        });

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                loadListAd();
            }
        }, 500);
    }

    /**
     * 加载feed广告
     */
    private void loadListAd() {
        Log.d("windSDK", adWidth + "-----------loadListAd-----------" + placementId);
        userID++;
        Map<String, Object> options = new HashMap<>();
        options.put("user_id", String.valueOf(userID));
        if (windNativeUnifiedAd == null) {
            windNativeUnifiedAd = new WindNativeUnifiedAd( new WindNativeAdRequest(placementId, String.valueOf(userID),3,  options));
        }

        windNativeUnifiedAd.setNativeAdLoadListener(new WindNativeUnifiedAd.WindNativeAdLoadListener() {
            @Override
            public void onAdError(WindAdError error, String placementId) {
                Log.d("windSDK", "onAdError:" + error.toString() + ":" + placementId);
                Toast.makeText(NativeAdUnifiedListActivity.this, "onAdError:" + error.toString(), Toast.LENGTH_SHORT).show();
                if (mListView != null) {
                    mListView.setLoadingFinish();
                }
            }

            @Override
            public void onAdLoad(List<WindNativeAdData> unifiedADData,String placementId) {
                if (mListView != null) {
                    mListView.setLoadingFinish();
                }

                if (unifiedADData != null && unifiedADData.size() > 0) {
                    Log.d("windSDK", "onFeedAdLoad:" + unifiedADData.size());
                    for (final WindNativeAdData adData : unifiedADData) {

                        for (int i = 0; i < LIST_ITEM_COUNT; i++) {
                            mData.add(null);
                        }

                        int count = mData.size();
                        mData.set(count - 1, adData);
                    }

                    myAdapter.notifyDataSetChanged();
                }
            }
        });
        windNativeUnifiedAd.loadAd();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mData != null) {
            for (WindNativeAdData ad : mData) {
                if (ad != null) {
                    ad.destroy();
                }
            }
        }
        mData = null;
    }

    private static class MyAdapter extends BaseAdapter {

        private static final int ITEM_VIEW_TYPE_NORMAL = 0;
        private static final int ITEM_VIEW_TYPE_UNIFIED_AD = 1;
        private static final int ITEM_VIEW_TYPE_EXPRESS_AD = 2;
        private List<WindNativeAdData> mData;
        private Activity mActivity;

        public MyAdapter(Activity activity, List<WindNativeAdData> data) {
            this.mActivity = activity;
            this.mData = data;
        }

        @Override
        public int getCount() {
            return mData.size(); // for test
        }

        @Override
        public WindNativeAdData getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        //信息流广告的样式，有大图、小图、组图和视频，通过ad.getImageMode()来判断
        @Override
        public int getItemViewType(int position) {
            WindNativeAdData ad = getItem(position);
            if (ad == null) {
                return ITEM_VIEW_TYPE_NORMAL;
            } else {
                return ITEM_VIEW_TYPE_UNIFIED_AD;
            }
        }

        @Override
        public int getViewTypeCount() {
            return 3;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            WindNativeAdData ad = getItem(position);
            switch (getItemViewType(position)) {
                case ITEM_VIEW_TYPE_UNIFIED_AD:
                    return getUnifiedADView(convertView, parent, ad);
                default:
                    return getNormalView(convertView, parent, position);
            }
        }

        //渲染视频广告，以视频广告为例，以下说明
        @SuppressWarnings("RedundantCast")
        private View getUnifiedADView(View convertView, ViewGroup viewGroup, @NonNull final WindNativeAdData ad) {
            final UnifiedAdViewHolder adViewHolder;
            try {
                if (convertView == null) {
                    convertView = LayoutInflater.from(mActivity).inflate(R.layout.listitem_ad_native, viewGroup, false);
                    adViewHolder = new UnifiedAdViewHolder(convertView);
                    convertView.setTag(adViewHolder);
                } else {
                    adViewHolder = (UnifiedAdViewHolder) convertView.getTag();
                }

                View nativeAdView = adViewHolder.adRender.getNativeAdView(mActivity, ad, new NativeADEventListener() {
                    @Override
                    public void onAdExposed() {
                        Toast.makeText(mActivity, "onAdExposed:", Toast.LENGTH_SHORT).show();

                        Log.d("windSDK", "onAdExposed: ");

                    }

                    @Override
                    public void onAdClicked() {
                        Toast.makeText(mActivity, "onAdClicked:", Toast.LENGTH_SHORT).show();

                        Log.d("windSDK", "onAdClicked: ");

                    }
                    @Override
                    public void onAdDetailShow() {
                        Toast.makeText(mActivity, "onAdDetailShow:", Toast.LENGTH_SHORT).show();

                        Log.d("windSDK", "onAdDetailShow: ");
                    }

                    @Override
                    public void onAdDetailDismiss() {
                        Toast.makeText(mActivity, "onAdDetailDismiss:", Toast.LENGTH_SHORT).show();

                        Log.d("windSDK", "onAdDetailDismiss: ");
                    }

                    @Override
                    public void onAdError(WindAdError error) {
                        Toast.makeText(mActivity, "onAdError: " + error.toString(), Toast.LENGTH_SHORT).show();

                        Log.d("windSDK", "onAdError error code :" + error.toString());
                    }
                }, new WindNativeAdData.NativeADMediaListener() {
                    @Override
                    public void onVideoLoad() {
                        Toast.makeText(mActivity, "onVideoLoad:", Toast.LENGTH_SHORT).show();

                        Log.d("windSDK", "----------onVideoLoad----------");
                    }

                    @Override
                    public void onVideoError(WindAdError error) {
                        Toast.makeText(mActivity, "onVideoError:", Toast.LENGTH_SHORT).show();

                        Log.d("windSDK", "----------onVideoError----------:" + error.toString());
                    }

                    @Override
                    public void onVideoStart() {
                        Toast.makeText(mActivity, "onVideoStart:", Toast.LENGTH_SHORT).show();

                        Log.d("windSDK", "----------onVideoStart----------");
                    }

                    @Override
                    public void onVideoPause() {
                        Toast.makeText(mActivity, "onVideoPause:", Toast.LENGTH_SHORT).show();

                        Log.d("windSDK", "----------onVideoPause----------");
                    }

                    @Override
                    public void onVideoResume() {
                        Toast.makeText(mActivity, "onVideoResume:", Toast.LENGTH_SHORT).show();

                        Log.d("windSDK", "----------onVideoResume----------");
                    }

                    @Override
                    public void onVideoCompleted() {
                        Toast.makeText(mActivity, "onVideoCompleted:", Toast.LENGTH_SHORT).show();

                        Log.d("windSDK", "----------onVideoCompleted----------");
                    }
                });


                //设置dislike弹窗
                ad.setDislikeInteractionCallback(mActivity, new WindNativeAdData.DislikeInteractionCallback() {
                    @Override
                    public void onShow() {
                        Log.d("windSDK", "onShow");
                    }

                    @Override
                    public void onSelected(int position, String value, boolean enforce) {
                        Log.d("windSDK", "onSelected: " + position + ":" + value + ":" + enforce);
                        //用户选择不喜欢原因后，移除广告展示
                        mData.remove(ad);
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onCancel() {
                        Log.d("windSDK", "onCancel");
                    }
                });

                //设置dislike弹窗
                ad.setDislikeInteractionCallback(mActivity, new WindNativeAdData.DislikeInteractionCallback() {
                    @Override
                    public void onShow() {
                        Log.d("windSDK", "----------onShow----------");
                    }

                    @Override
                    public void onSelected(int position, String value, boolean enforce) {
                        Log.d("windSDK", "----------onSelected----------:" + position + ":" + value + ":" + enforce);
                        //用户选择不喜欢原因后，移除广告展示
                        mData.remove(ad);
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onCancel() {
                        Log.d("windSDK", "----------onCancel----------");
                    }
                });
                //添加进容器
                if (adViewHolder.adContainer != null) {
                    adViewHolder.adContainer.removeAllViews();
                    adViewHolder.adContainer.addView(nativeAdView);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return convertView;
        }

        /**
         * 非广告list
         *
         * @param convertView
         * @param parent
         * @param position
         * @return
         */
        @SuppressWarnings("RedundantCast")
        @SuppressLint("SetTextI18n")
        private View getNormalView(View convertView, ViewGroup parent, int position) {
            NormalViewHolder normalViewHolder;
            if (convertView == null) {
                normalViewHolder = new NormalViewHolder();
                convertView = LayoutInflater.from(mActivity).inflate(R.layout.listitem_normal, parent, false);
                normalViewHolder.idle = (TextView) convertView.findViewById(R.id.text_idle);
                convertView.setTag(normalViewHolder);
            } else {
                normalViewHolder = (NormalViewHolder) convertView.getTag();
            }
            normalViewHolder.idle.setText("ListView item " + position);
            return convertView;
        }




        private static class UnifiedAdViewHolder extends AdViewHolder {
            //媒体自渲染的View
            NativeAdDemoRender adRender;

            public UnifiedAdViewHolder(View convertView) {
                super(convertView);
                adRender = new NativeAdDemoRender();
            }
        }

        private static class AdViewHolder {

            FrameLayout adContainer;

            public AdViewHolder(View convertView) {
                adContainer = (FrameLayout) convertView.findViewById(R.id.iv_list_item_container);

            }
        }

        private static class NormalViewHolder {
            TextView idle;
        }
    }
}