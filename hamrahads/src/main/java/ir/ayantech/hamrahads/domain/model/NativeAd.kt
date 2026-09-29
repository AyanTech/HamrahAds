package ir.ayantech.hamrahads.domain.model

data class NativeAd(
    val caption: String? = null,
    val description: String? = null,
    val cta: String? = null,
    val logo: String? = null,
    val banner320x50: String? = null,
    val banner1136x640: String? = null,
    val banner640x1136: String? = null,
    val video: String? = null,
    val landingType: Int? = null,
    val landingLink: String? = null,
    val interstitialTemplate: Int? = null,
    val webTemplateUrl: String? = null,
    val timeToSkip: Int? = null,
    val timeOut: Int? = null,
    val trackers: Tracker? = null,
)
