package ir.ayantech.hamrahads.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkInterstitialAd(

    @SerialName("caption")
    var caption: String? = null,

    @SerialName("description")
    var description: String? = null,

    @SerialName("cta")
    var cta: String? = null,

    @SerialName("logo")
    var logo: String? = null,

    @SerialName("interstitial_label")
    var interstitialLabel: String? = null,

    @SerialName("interstitial_banner")
    var interstitialBanner: String? = null,

    @SerialName("landing_type")
    var landingType: Int? = null,

    @SerialName("landing_link")
    var landingLink: String? = null,

    @SerialName("interstitial_template")
    var interstitialTemplate: Int? = null,

    @SerialName("web_template_url")
    var webTemplateUrl: String? = null,

    @SerialName("time_to_skip")
    var timeToSkip: Int? = null,

    @SerialName("time_out")
    var timeOut: Int? = null,

    @SerialName("trackers")
    var trackers: NetworkTracker? = null,
)


