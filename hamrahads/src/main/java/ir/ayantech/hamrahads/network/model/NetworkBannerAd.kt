package ir.ayantech.hamrahads.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkBannerAd(

    @SerialName("banner_320x50")
    var banner320x50: String? = null,

    @SerialName("banner_1136x640")
    var banner1136x640: String? = null,

    @SerialName("banner_640x1136")
    var banner640x1136: String? = null,

    @SerialName("landing_type")
    var landingType: Int? = null,

    @SerialName("landing_link")
    var landingLink: String? = null,

    @SerialName("trackers")
    var trackers: NetworkTracker? = null,
)


