package ir.ayantech.hamrahads.listener

import ir.ayantech.hamrahads.ads.LoadedAd
import ir.ayantech.hamrahads.domain.model.BannerAd
import ir.ayantech.hamrahads.domain.model.InterstitialAd
import ir.ayantech.hamrahads.domain.model.NativeAd
import ir.ayantech.hamrahads.model.error.HamrahAdsError

interface AdLoadListener {
    fun onSuccess() {}
    fun onNativeAdLoaded(ad: LoadedAd<NativeAd>) {
        onSuccess()
    }

    fun onBannerAdLoaded(ad: LoadedAd<BannerAd>) {
        onSuccess()
    }

    fun onInterstitialAdLoaded(ad: LoadedAd<InterstitialAd>) {
        onSuccess()
    }

    fun onError(error: HamrahAdsError) {}
}
