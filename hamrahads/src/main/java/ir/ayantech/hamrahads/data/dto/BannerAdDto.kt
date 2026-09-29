package ir.ayantech.hamrahads.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class BannerAdDto(

    @SerialName("banner_320x50")
    val banner320x50: String? = null,

    @SerialName("banner_1136x640")
    val banner1136x640: String? = null,

    @SerialName("banner_640x1136")
    val banner640x1136: String? = null,

    @SerialName("landing_type")
    val landingType: Int? = null,

    @SerialName("landing_link")
    val landingLink: String? = null,

    @SerialName("trackers")
    val trackers: TrackerDto? = null,
)


