package ir.ayantech.hamrahads.domain.model

data class BannerAd(
    val banner320x50: String? = null,
    val banner1136x640: String? = null,
    val banner640x1136: String? = null,
    val landingType: Int? = null,
    val landingLink: String? = null,
    val trackers: Tracker? = null,
)
