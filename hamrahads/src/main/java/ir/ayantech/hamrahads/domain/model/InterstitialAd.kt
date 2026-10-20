package ir.ayantech.hamrahads.domain.model

import kotlin.time.Duration

data class InterstitialAd(
    val caption: String? = null,
    val description: String? = null,
    val cta: String? = null,
    val logo: String? = null,
    val interstitialLabel: String? = null,
    val interstitialBanner: String? = null,
    val landingType: Int? = null,
    val landingLink: String? = null,
    val interstitialTemplate: Int? = null,
    val webTemplateUrl: String? = null,
    val timeToSkip: Duration? = null,
    val timeOut: Duration? = null,
    val trackers: Tracker? = null,
)
