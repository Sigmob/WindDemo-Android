package com.sigmob.android.demo.natives;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sigmob.android.demo.R;
import com.sigmob.windad.natives.NativeADEventListener;
import com.sigmob.windad.natives.NativeAdPatternType;
import com.sigmob.windad.natives.WindNativeAdData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NativeAdDemoRender {

    private static final String TAG = "windSDK";
    /**
     * 多布局根据 adPatternType 复用不同的根视图
     */
    private final Map<Integer, View> developViewMap = new HashMap<>();
    private ImageView img_logo;
    private ImageView ad_logo;
    private ImageView img_dislike;
    private TextView text_desc;
    private View mButtonsContainer;
    private Button mPlayButton, mPauseButton, mStopButton, mVoiceButton;
    private FrameLayout mMediaViewLayout;
    private ImageView mImagePoster;
    private LinearLayout native_3img_ad_container;
    private ImageView img_1;
    private ImageView img_2;
    private ImageView img_3;
    private TextView text_title;
    private Button mCTAButton;
    private ViewGroup mvideoProgressContainer;
    private ProgressBar mVideoProgressBar;
    private TextView mVideoProgressText;

    private boolean mMute = false;

    // 视频播放进度定时器
    private final Handler mProgressHandler = new Handler(Looper.getMainLooper());
    private Runnable mProgressRunnable;
    private WindNativeAdData mCurrentAdData;

    public View getNativeAdView(Context context, WindNativeAdData adData,
                                NativeADEventListener nativeADEventListener,
                                WindNativeAdData.NativeADMediaListener nativeADMediaListener) {
        int adPatternType = adData.getAdPatternType();
        Log.d("windSDK", "getNativeAdView: adPatternType = " + adPatternType);
        View view = developViewMap.get(adPatternType);
        View nativeAdView;
        if (view == null) {
            nativeAdView = LayoutInflater.from(context).inflate(R.layout.native_ad_item_normal, null);
            developViewMap.put(adPatternType, nativeAdView);
        } else {
            nativeAdView = view;
        }

        ViewParent parent = nativeAdView.getParent();
        if (parent != null) {
            ((ViewGroup) parent).removeView(nativeAdView);
        }

        img_logo = nativeAdView.findViewById(R.id.img_logo);
        ad_logo = nativeAdView.findViewById(R.id.channel_ad_logo);
        img_dislike = nativeAdView.findViewById(R.id.iv_dislike);

        text_desc = nativeAdView.findViewById(R.id.text_desc);

        mButtonsContainer = nativeAdView.findViewById(R.id.video_btn_container);
        mPlayButton = nativeAdView.findViewById(R.id.btn_play);
        mPauseButton = nativeAdView.findViewById(R.id.btn_pause);
        mStopButton = nativeAdView.findViewById(R.id.btn_stop);
        mVoiceButton = nativeAdView.findViewById(R.id.btn_voice);

        mMediaViewLayout = nativeAdView.findViewById(R.id.media_layout);
        mImagePoster = nativeAdView.findViewById(R.id.img_poster);
        mvideoProgressContainer = nativeAdView.findViewById(R.id.video_progress_container);
        mVideoProgressBar = nativeAdView.findViewById(R.id.video_progress_bar);
        mVideoProgressText = nativeAdView.findViewById(R.id.tv_video_progress_text);

        native_3img_ad_container = nativeAdView.findViewById(R.id.native_3img_ad_container);
        img_1 = nativeAdView.findViewById(R.id.img_1);
        img_2 = nativeAdView.findViewById(R.id.img_2);
        img_3 = nativeAdView.findViewById(R.id.img_3);

        text_title = nativeAdView.findViewById(R.id.text_title);
        mCTAButton = nativeAdView.findViewById(R.id.btn_cta);

        // 渲染 UI
        String iconUrl = adData.getIconUrl();
        if (TextUtils.isEmpty(iconUrl)) {
            img_logo.setVisibility(View.GONE);
        } else {
            img_logo.setVisibility(View.VISIBLE);
            Glide.with(context).load(iconUrl).into(img_logo);
        }

        String title = adData.getTitle();
        Log.d("windSDK", "getNativeAdView: title = " + title);
        text_title.setText(TextUtils.isEmpty(title) ? "点开有惊喜" : title);

        String desc = adData.getDesc();
        text_desc.setText(TextUtils.isEmpty(desc) ? "听说点开它的人都交了好运" : desc);

        Bitmap adLogo = adData.getAdLogo();
        if (adLogo == null) {
            ad_logo.setVisibility(View.GONE);
        } else {
            ad_logo.setVisibility(View.VISIBLE);
            ad_logo.setImageBitmap(adLogo);
        }

        // clickViews 数量必须大于等于 1
        List<View> clickableViews = new ArrayList<>();
        // 可以被点击的 View，也可以把 convertView 放进来意味 item 可被点击
        // 全区域可点
        //clickableViews.add(nativeAdView);
        // 仅按键可点
        clickableViews.add(mCTAButton);
        // 触发创意广告的 View（点击下载或拨打电话）
        List<View> creativeViewList = new ArrayList<>();
        // 所有广告类型，注册 DownloadButton 的点击事件
        creativeViewList.add(mCTAButton);

        List<ImageView> imageViews = new ArrayList<>();
        int patternType = adData.getAdPatternType();
        Log.d("windSDK", "getNativeAdView: patternType = " + patternType);

        // 重要！这个涉及到广告计费，必须正确调用。convertView 必须使用 ViewGroup
        // 作为 creativeViewList 传入，点击不进入详情页，直接下载或进入落地页，视频和图文广告均生效
        adData.bindViewForInteraction(nativeAdView, clickableViews, creativeViewList, img_dislike, nativeADEventListener);

        // 需要等到 bindViewForInteraction 后再去添加 media
        if (patternType == NativeAdPatternType.NATIVE_BIG_IMAGE_AD) {
            // 双图双文、单图双文：注册 mImagePoster 的点击事件
            mImagePoster.setVisibility(View.VISIBLE);
            mButtonsContainer.setVisibility(View.GONE);
            mvideoProgressContainer.setVisibility(View.GONE);
            native_3img_ad_container.setVisibility(View.GONE);
            mMediaViewLayout.setVisibility(View.GONE);
            imageViews.add(mImagePoster);
            adData.bindImageViews(imageViews, 0);
        } else if (patternType == NativeAdPatternType.NATIVE_VIDEO_AD) {
            // 视频广告，注册 MediaView 的点击事件
            mImagePoster.setVisibility(View.GONE);
            native_3img_ad_container.setVisibility(View.GONE);
            mMediaViewLayout.setVisibility(View.VISIBLE);
            adData.bindMediaView(mMediaViewLayout, nativeADMediaListener);
            mButtonsContainer.setVisibility(View.VISIBLE);
            mvideoProgressContainer.setVisibility(View.VISIBLE);

            // 保存当前广告数据，供定时器使用
            mCurrentAdData = adData;

            View.OnClickListener listener = v -> {
                if (v == mPlayButton) {
                    adData.startVideo();
                    startProgressTimer();
                } else if (v == mPauseButton) {
                    adData.pauseVideo();
                    stopProgressTimer();
                } else if (v == mStopButton) {
                    adData.stopVideo();
                    stopProgressTimer();
                    resetProgressBar();
                } else if (v == mVoiceButton) {
                    if (mMute) {
                        adData.setVideoMute(false);
                        mVoiceButton.setText("有声");
                        mMute = false;
                    } else {
                        adData.setVideoMute(true);
                        mVoiceButton.setText("静音");
                        mMute = true;
                    }
                }
            };
            mPlayButton.setOnClickListener(listener);
            mPauseButton.setOnClickListener(listener);
            mStopButton.setOnClickListener(listener);
            mVoiceButton.setOnClickListener(listener);
        }

        /*
         * 营销组件
         * 支持项目：智能电话（点击跳转拨号盘），外显表单
         * bindCTAViews 绑定营销组件监听视图，注意：bindCTAViews 的视图不可调用 setOnClickListener，否则 SDK 功能可能受到影响
         * ad.getCTAText 判断拉取广告是否包含营销组件，如果包含组件，展示组件按钮，否则展示 download 按钮
         */
        // 获取组件文案
        String ctaText = adData.getCTAText();
        Log.d("windSDK", "getNativeAdView: ctaText = " + ctaText);
        updateAdAction(ctaText);
        return nativeAdView;
    }

    private void updateAdAction(String ctaText) {
        if (TextUtils.isEmpty(ctaText)) {
            mCTAButton.setVisibility(View.INVISIBLE);
        } else {
            // 如果拉取广告包含 CTA 组件，则渲染该组件
            mCTAButton.setText(ctaText);
            mCTAButton.setVisibility(View.VISIBLE);
        }
    }

    public boolean isMute() {
        return mMute;
    }

    /**
     * 启动播放进度定时器，每隔 500ms 更新一次进度条
     */
    public void startProgressTimer() {
        stopProgressTimer();
        mProgressRunnable = new Runnable() {
            @Override
            public void run() {
                if (mCurrentAdData == null) {
                    stopProgressTimer();
                    return;
                }
                int duration = mCurrentAdData.getVideoDuration();
                int position = mCurrentAdData.getVideoCurrentPosition();
                if (duration > 0 && isNotNull(mVideoProgressBar)) {
                    int progress = (int) ((position * 100L) / duration);
                    mVideoProgressBar.setProgress(progress);
                    if (isNotNull(mVideoProgressText)) {
                        mVideoProgressText.setText(progress + "%");
                    }
                    Log.d(TAG, "startProgressTimer: position = " + position + ", duration = " + duration + " progress = " + progress + "%");
                }
                // 继续调度下一次更新
                mProgressHandler.postDelayed(this, 500);
            }
        };
        mProgressHandler.post(mProgressRunnable);
    }

    /**
     * 停止播放进度定时器
     */
    public void stopProgressTimer() {
        if (isNotNull(mProgressRunnable)) {
            mProgressHandler.removeCallbacks(mProgressRunnable);
            mProgressRunnable = null;
        }
    }

    /**
     * 播放完成时调用：停止定时器并将进度条重置为满格后隐藏
     */
    public void onVideoCompleted() {
        stopProgressTimer();
        if (isNotNull(mVideoProgressBar)) {
            mVideoProgressBar.setProgress(100);
        }
        if (isNotNull(mVideoProgressText)) {
            mVideoProgressText.setText("100%");
        }
    }

    /**
     * 重置进度条（停止播放时使用）
     */
    private void resetProgressBar() {
        if (isNotNull(mVideoProgressBar)) {
            mVideoProgressBar.setProgress(0);
        }
        if (isNotNull(mVideoProgressText)) {
            mVideoProgressText.setText("0%");
        }
    }

    private boolean isNotNull(Object obj) {
        return obj != null;
    }
}