package ir.ayantech.hamrahads.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class InterstitialAdDto(

    @SerialName("caption")
    val caption: String? = null,

    @SerialName("description")
    val description: String? = null,

    @SerialName("cta")
    val cta: String? = null,

    @SerialName("logo")
    val logo: String? = null,

    @SerialName("interstitial_label")
    val interstitialLabel: String? = null,

    @SerialName("interstitial_banner")
    val interstitialBanner: String? = null,

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


