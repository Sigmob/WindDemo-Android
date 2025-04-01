package com.sigmob.android.demo.callbackinfo;

/**
 * created by lance on 2021/12/9 : 9:22 上午
 */
public class CallBackInfo {

    public static String[] REWARD_CALLBACK = {
            "onRewardAdLoadSuccess",
            "onRewardAdLoadError",
            "onRewardAdPlayStart",
            "onRewardAdPlayError",
            "onRewardAdPlayEnd",
            "onRewardAdClicked",
            "onRewardAdClosed",
            "onRewardAdRewarded"};
    public static String[] INTERSTITIAL_CALLBACK = {
            "onInterstitialAdLoadSuccess",
            "onInterstitialAdLoadError",
            "onInterstitialAdPlayStart",
            "onInterstitialAdPlayError",
            "onInterstitialAdPlayEnd",
            "onInterstitialAdClicked",
            "onInterstitialAdClosed",};
    public static String[] NEW_INTERSTITIAL_CALLBACK = {
            "onInterstitialAdLoadSuccess",
            "onInterstitialAdLoadError",
            "onInterstitialAdShow",
            "onInterstitialAdShowError",
            "onInterstitialAdClicked",
            "onInterstitialAdClosed",};
    public static String[] SPLASH_CALLBACK = {
            "onSplashAdLoadSuccess",
            "onSplashAdLoadFail",
            "onSplashAdShow",
            "onSplashAdShowError",
            "onSplashAdSkip",
            "onSplashAdClick",
            "onSplashAdClose"};
    public static String[] NATIVE_CALLBACK = {
            "onAdLoad",
            "onAdError",
            "onAdExposed",
            "onAdClicked",
            "onAdDetailShow",
            "onAdDetailDismiss",
            "onShow",
            "onSelected",
            "onCancel"};
}