package ir.ayantech.hamrahads.domain.model

import ir.ayantech.hamrahads.model.enums.BannerSize

internal fun BannerAd.imageUrl(size: BannerSize): String? = when (size) {
    BannerSize.BANNER_320x50 -> banner320x50
    BannerSize.BANNER_640x1136 -> banner640x1136
    BannerSize.BANNER_1136x640 -> banner1136x640
}

private fun Tracker?.isComplete(): Boolean = this != null && !click.isNullOrBlank() && !impression.isNullOrBlank()

internal fun BannerAd.isDisplayable(size: BannerSize): Boolean =
    landingType != null && !landingLink.isNullOrBlank() && trackers.isComplete() && !imageUrl(size).isNullOrBlank()

internal fun NativeAd.isDisplayable(): Boolean =
    landingType != null && !landingLink.isNullOrBlank() && trackers.isComplete() &&
        !caption.isNullOrBlank() && !cta.isNullOrBlank()

internal fun InterstitialAd.isDisplayable(): Boolean =
    interstitialTemplate in 1..3 && landingType != null && !landingLink.isNullOrBlank() &&
        trackers.isComplete() && !caption.isNullOrBlank() && !cta.isNullOrBlank() &&
        !interstitialBanner.isNullOrBlank() && !logo.isNullOrBlank()
