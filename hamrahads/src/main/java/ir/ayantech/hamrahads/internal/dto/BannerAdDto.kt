package ir.ayantech.hamrahads.internal.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class BannerAdDto(

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
    var trackers: TrackerDto? = null,
)


