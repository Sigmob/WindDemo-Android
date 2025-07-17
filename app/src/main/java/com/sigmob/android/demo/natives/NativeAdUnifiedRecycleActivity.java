package com.sigmob.android.demo.natives;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sigmob.android.demo.Constants;
import com.sigmob.android.demo.R;
import com.sigmob.android.demo.view.LoadMoreRecyclerView;
import com.sigmob.android.demo.view.LoadMoreView;
import com.sigmob.windad.WindAdError;
import com.sigmob.windad.natives.NativeADEventListener;
import com.sigmob.windad.natives.WindNativeAdData;
import com.sigmob.windad.natives.WindNativeAdRequest;
import com.sigmob.windad.natives.WindNativeUnifiedAd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NativeAdUnifiedRecycleActivity extends Activity {

    private static final int LIST_ITEM_COUNT = 10;

    private LoadMoreRecyclerView mListView;

    private MyAdapter myAdapter;

    private WindNativeUnifiedAd windNativeUnifiedAd;

    private int userID = 0;

    private String placementId;

    private List<WindNativeAdData> mData;

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    // 广告宽高
    private int adWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_ad_unified_recycle);
        getExtraInfo();
        initListView();
        // 减 20 因为容器有个 margin 10dp//340
        adWidth = screenWidthAsIntDips(this) - 20;
    }

    private void getExtraInfo() {
        Intent intent = getIntent();
        placementId = intent.getStringExtra(Constants.CONF_PLACEMENT_ID);
    }

    public static int screenWidthAsIntDips(Context context) {
        int pixels = context.getResources().getDisplayMetrics().widthPixels;
        float density = context.getResources().getDisplayMetrics().density;
        return (int) ((pixels / density) + 0.5f);
    }

    private void initListView() {
        mListView = findViewById(R.id.unified_native_ad_recycle);
        mListView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mData = new ArrayList<>();
        myAdapter = new MyAdapter(this, mData);
        mListView.setAdapter(myAdapter);
        mListView.setLoadMoreListener(this::loadListAd);

        mHandler.postDelayed(this::loadListAd, 500);
    }

    /**
     * 加载 feed 广告
     */
    private void loadListAd() {
        Log.d("windSDK", adWidth + "-----------loadListAd-----------" + placementId);
        userID++;
        Map<String, Object> options = new HashMap<>();

        options.put("user_id", String.valueOf(userID));
        if (windNativeUnifiedAd == null) {
            windNativeUnifiedAd = new WindNativeUnifiedAd(new WindNativeAdRequest(placementId, String.valueOf(userID), 3, options));
        }

        windNativeUnifiedAd.setNativeAdLoadListener(new WindNativeUnifiedAd.WindNativeAdLoadListener() {
            @Override
            public void onAdError(WindAdError error, String placementId) {
                Log.d("windSDK", "onAdError:" + error.toString() + ":" + placementId);
                Toast.makeText(NativeAdUnifiedRecycleActivity.this, "onAdError" + error, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdLoad(List<WindNativeAdData> list, String s) {
                if (mListView != null) {
                    mListView.setLoadingFinish();
                }

                Toast.makeText(NativeAdUnifiedRecycleActivity.this, "onAdLoad", Toast.LENGTH_SHORT).show();
                if (list != null && !list.isEmpty()) {
                    Log.d("windSDK", "onFeedAdLoad:" + list.size());
                    for (WindNativeAdData adData : list) {
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

    private static class MyAdapter extends RecyclerView.Adapter {

        private static final int FOOTER_VIEW_COUNT = 1;
        private static final int ITEM_VIEW_TYPE_LOAD_MORE = -1;
        private static final int ITEM_VIEW_TYPE_NORMAL = 0;
        private static final int ITEM_VIEW_TYPE_AD = 1;

        private final List<WindNativeAdData> mData;

        private final Activity mActivity;

        public MyAdapter(Activity activity, List<WindNativeAdData> data) {
            this.mActivity = activity;
            this.mData = data;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            switch (viewType) {
                case ITEM_VIEW_TYPE_LOAD_MORE:
                    return new LoadMoreViewHolder(new LoadMoreView(mActivity));
                case ITEM_VIEW_TYPE_AD:
                    return new AdViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.listitem_ad_native, parent, false));
                default:
                    return new NormalViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.listitem_normal, parent, false));
            }
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof AdViewHolder) {
                AdViewHolder adViewHolder = (AdViewHolder) holder;
                bindData(adViewHolder, mData.get(position));
            } else if (holder instanceof NormalViewHolder) {
                NormalViewHolder normalViewHolder = (NormalViewHolder) holder;
                normalViewHolder.idle.setText("Recycler item " + position);
                holder.itemView.setBackgroundColor(getColorRandom());
            } else if (holder instanceof LoadMoreViewHolder) {
                LoadMoreViewHolder loadMoreViewHolder = (LoadMoreViewHolder) holder;
                loadMoreViewHolder.itemView.setBackgroundColor(Color.TRANSPARENT);
            }
        }

        private int getColorRandom() {
            int a = Double.valueOf(Math.random() * 255).intValue();
            int r = Double.valueOf(Math.random() * 255).intValue();
            int g = Double.valueOf(Math.random() * 255).intValue();
            int b = Double.valueOf(Math.random() * 255).intValue();
            return Color.argb(a, r, g, b);
        }

        private void bindData(AdViewHolder adViewHolder, WindNativeAdData adData) {
            View nativeAdView = adViewHolder.adRender.getNativeAdView(mActivity, adData, new NativeADEventListener() {
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
                    Log.d("WindSDK", "onAdDetailDismiss");
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
                    Toast.makeText(mActivity, "onVideoError:" + error.toString(), Toast.LENGTH_SHORT).show();
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

            // 设置 Dislike 弹窗
            adData.setDislikeInteractionCallback(mActivity, new WindNativeAdData.DislikeInteractionCallback() {
                @Override
                public void onShow() {
                    Toast.makeText(mActivity, "dislike onShow:", Toast.LENGTH_SHORT).show();
                    Log.d("windSDK", "onShow: ");
                }

                @Override
                public void onSelected(int position, String value, boolean enforce) {
                    Toast.makeText(mActivity, "dislike onSelected:" + position + ":" + value + ":" + enforce, Toast.LENGTH_SHORT).show();

                    Log.d("windSDK", "onSelected: " + position + ":" + value + ":" + enforce);
                    // 用户选择不喜欢原因后，移除广告展示
                    mData.remove(adData);
                    notifyDataSetChanged();
                }

                @Override
                public void onCancel() {
                    Toast.makeText(mActivity, "dislike onCancel:", Toast.LENGTH_SHORT).show();

                    Log.d("windSDK", "onAdExposed: ");
                }
            });
            // 添加进容器
            if (adViewHolder.adContainer != null) {
                adViewHolder.adContainer.removeAllViews();
                ViewGroup.LayoutParams lp = new ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                adViewHolder.adContainer.addView(nativeAdView, lp);
            }
        }

        @Override
        public int getItemCount() {
            int count = mData == null ? 0 : mData.size();
            return count + FOOTER_VIEW_COUNT;
        }

        @Override
        public int getItemViewType(int position) {
            if (mData != null) {
                int count = mData.size();
                if (position >= count) {
                    return ITEM_VIEW_TYPE_LOAD_MORE;
                } else {
                    WindNativeAdData ad = mData.get(position);
                    if (ad == null) {
                        return ITEM_VIEW_TYPE_NORMAL;
                    } else {
                        return ITEM_VIEW_TYPE_AD;
                    }
                }
            }
            return super.getItemViewType(position);
        }

        private static class AdViewHolder extends RecyclerView.ViewHolder {
            FrameLayout adContainer;
            // 媒体自渲染的 View
            NativeAdDemoRender adRender;

            public AdViewHolder(View itemView) {
                super(itemView);
                adContainer = itemView.findViewById(R.id.iv_list_item_container);
                adRender = new NativeAdDemoRender();
            }
        }

        private static class NormalViewHolder extends RecyclerView.ViewHolder {
            TextView idle;

            public NormalViewHolder(View itemView) {
                super(itemView);
                idle = itemView.findViewById(R.id.text_idle);
            }
        }

        private static class LoadMoreViewHolder extends RecyclerView.ViewHolder {
            TextView mTextView;
            ProgressBar mProgressBar;

            public LoadMoreViewHolder(View itemView) {
                super(itemView);
                itemView.setLayoutParams(new RecyclerView.LayoutParams(
                        RecyclerView.LayoutParams.MATCH_PARENT, RecyclerView.LayoutParams.WRAP_CONTENT));
                mTextView = itemView.findViewById(R.id.tv_load_more_tip);
                mProgressBar = itemView.findViewById(R.id.pb_load_more_progress);
            }
        }
    }
}