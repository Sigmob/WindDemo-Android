package com.sigmob.android.demo.natives;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
     * 多布局根据adPatternType复用不同的根视图
     */
    private Map<Integer, View> developViewMap = new HashMap<>();
    private ImageView img_logo;
    private ImageView ad_logo;
    private ImageView img_dislike;
    private TextView text_desc;
    private View mButtonsContainer;
    private Button mPlayButton;
    private Button mPauseButton;
    private Button mStopButton;
    private FrameLayout mMediaViewLayout;
    private ImageView mImagePoster;
    private LinearLayout native_3img_ad_container;
    private ImageView img_1;
    private ImageView img_2;
    private ImageView img_3;
    private TextView text_title;
    private Button mCTAButton;



    public View getNativeAdView(Context context, final WindNativeAdData adData,
                                final NativeADEventListener nativeADEventListener,
                                final WindNativeAdData.NativeADMediaListener nativeADMediaListener) {


        int adPatternType = adData.getAdPatternType();

        Log.d("windSDK", "---------createView----------" + adPatternType);
        View nativeAdView = developViewMap.get(adPatternType);
        if (nativeAdView == null) {
            nativeAdView = LayoutInflater.from(context).inflate(R.layout.native_ad_item_normal, null);
            developViewMap.put(adPatternType, nativeAdView);

        }

        if (nativeAdView.getParent() != null) {
            ((ViewGroup) nativeAdView.getParent()).removeView(nativeAdView);
        }
        

        Log.d("windSDK", "renderAdView:" + adData.getTitle());
        img_logo =nativeAdView.findViewById(R.id.img_logo);
        ad_logo =nativeAdView.findViewById(R.id.channel_ad_logo);
        img_dislike =nativeAdView.findViewById(R.id.iv_dislike);

        text_desc =nativeAdView.findViewById(R.id.text_desc);

        mButtonsContainer =nativeAdView.findViewById(R.id.video_btn_container);
        mPlayButton =nativeAdView.findViewById(R.id.btn_play);
        mPauseButton =nativeAdView.findViewById(R.id.btn_pause);
        mStopButton =nativeAdView.findViewById(R.id.btn_stop);

        mMediaViewLayout =nativeAdView.findViewById(R.id.media_layout);
        mImagePoster =nativeAdView.findViewById(R.id.img_poster);

        native_3img_ad_container =nativeAdView.findViewById(R.id.native_3img_ad_container);
        img_1 =nativeAdView.findViewById(R.id.img_1);
        img_2 =nativeAdView.findViewById(R.id.img_2);
        img_3 =nativeAdView.findViewById(R.id.img_3);

        text_title =nativeAdView.findViewById(R.id.text_title);
        mCTAButton =nativeAdView.findViewById(R.id.btn_cta);

        //渲染UI
        if (!TextUtils.isEmpty(adData.getIconUrl())) {
            img_logo.setVisibility(View.VISIBLE);
            Glide.with(context).load(adData.getIconUrl()).into(img_logo);
        } else {
            img_logo.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(adData.getTitle())) {
            text_title.setText(adData.getTitle());
        } else {
            text_title.setText("点开有惊喜");
        }

        if (!TextUtils.isEmpty(adData.getDesc())) {
            text_desc.setText(adData.getDesc());
        } else {
            text_desc.setText("听说点开它的人都交了好运!");
        }

        if (adData.getAdLogo() != null) {
            ad_logo.setVisibility(View.VISIBLE);
            ad_logo.setImageBitmap(adData.getAdLogo());
        } else {
            ad_logo.setVisibility(View.GONE);
        }

        //clickViews数量必须大于等于1
        List<View> clickableViews = new ArrayList<>();
        //可以被点击的view, 也可以把convertView放进来意味item可被点击
        clickableViews.add(nativeAdView);
        ////触发创意广告的view（点击下载或拨打电话）
        List<View> creativeViewList = new ArrayList<>();
        // 所有广告类型，注册mDownloadButton的点击事件
        creativeViewList.add(mCTAButton);
//        clickableViews.add(mDownloadButton);

        List<ImageView> imageViews = new ArrayList<>();
        int patternType = adData.getAdPatternType();
        Log.d("windSDK", "patternType:" + patternType);


        //重要! 这个涉及到广告计费，必须正确调用。convertView必须使用ViewGroup。
        //作为creativeViewList传入，点击不进入详情页，直接下载或进入落地页，视频和图文广告均生效
        adData.bindViewForInteraction(nativeAdView, clickableViews, creativeViewList, img_dislike, nativeADEventListener);

        //需要等到bindViewForInteraction后再去添加media
        if (patternType == NativeAdPatternType.NATIVE_BIG_IMAGE_AD) {
            // 双图双文、单图双文：注册mImagePoster的点击事件
            mImagePoster.setVisibility(View.VISIBLE);
            mButtonsContainer.setVisibility(View.GONE);
            native_3img_ad_container.setVisibility(View.GONE);
            mMediaViewLayout.setVisibility(View.GONE);
            clickableViews.add(mImagePoster);
            imageViews.add(mImagePoster);
            adData.bindImageViews(imageViews, 0);
        } else if (patternType == NativeAdPatternType.NATIVE_VIDEO_AD) {
            // 视频广告，注册mMediaView的点击事件
            mImagePoster.setVisibility(View.GONE);
            native_3img_ad_container.setVisibility(View.GONE);
            mMediaViewLayout.setVisibility(View.VISIBLE);
            adData.bindMediaView(mMediaViewLayout, nativeADMediaListener);

            mButtonsContainer.setVisibility(View.VISIBLE);

           View.OnClickListener listener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v == mPlayButton) {
                        adData.startVideo();
                    } else if (v == mPauseButton) {
                        adData.pauseVideo();
                    } else if (v == mStopButton) {
                        adData.stopVideo();
                    }
                }
            };
            mPlayButton.setOnClickListener(listener);
            mPauseButton.setOnClickListener(listener);
            mStopButton.setOnClickListener(listener);
        }

        /**
         * 营销组件
         * 支持项目：智能电话（点击跳转拨号盘），外显表单
         *  bindCTAViews 绑定营销组件监听视图，注意：bindCTAViews的视图不可调用setOnClickListener，否则SDK功能可能受到影响
         *  ad.getCTAText 判断拉取广告是否包含营销组件，如果包含组件，展示组件按钮，否则展示download按钮
         */
        String ctaText = adData.getCTAText(); //获取组件文案
        Log.d("windSDK", "ctaText:" + ctaText);
        updateAdAction(ctaText);

        return nativeAdView;
    }

    private void updateAdAction(String ctaText) {
        if (!TextUtils.isEmpty(ctaText)) {
            //如果拉取广告包含CTA组件，则渲染该组件
            mCTAButton.setText(ctaText);
            mCTAButton.setVisibility(View.VISIBLE);
        } else {
            mCTAButton.setVisibility(View.INVISIBLE);
        }
    }
}