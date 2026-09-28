package ir.ayantech.hamrahads.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class NativeAdDto(

    @SerialName("caption")
    val caption: String? = null,

    @SerialName("description")
    val description: String? = null,

    @SerialName("cta")
    val cta: String? = null,

    @SerialName("logo")
    val logo: String? = null,

    @SerialName("banner_320x50")
    val banner320x50: String? = null,

    @SerialName("banner_1136x640")
    val banner1136x640: String? = null,

    @SerialName("banner_640x1136")
    val banner640x1136: String? = null,

    @SerialName("video")
    val video: String? = null,

    @SerialName("landing_type")
    val landingType: Int? = null,

    @SerialName("landing_link")
    val landingLink: String? = null,

    @SerialName("interstitial_template")
    val interstitialTemplate: Int? = null,

    @SerialName("web_template_url")
    val webTemplateUrl: String? = null,

    @SerialName("time_to_skip")
    val timeToSkip: Int? = null,

    @SerialName("time_out")
    val timeOut: Int? = null,

    @SerialName("trackers")
    val trackers: TrackerDto? = null,
)


