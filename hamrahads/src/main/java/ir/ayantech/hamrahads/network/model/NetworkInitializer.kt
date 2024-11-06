package ir.ayantech.hamrahads.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkInitializer(

    @SerialName("caption")
    var caption: String? = null,

    @SerialName("description")
    var description: String? = null,

    @SerialName("cta")
    var cta: String? = null,

    @SerialName("logo")
    var logo: String? = null,

    @SerialName("banner_320x50")
    var banner320x50: String? = null,

    @SerialName("banner_1136x640")
    var banner1136x640: String? = null,

    @SerialName("banner_640x1136")
    var banner640x1136: String? = null,

    @SerialName("video")
    var video: String? = null,

    @SerialName("landing_type")
    var landingType: String? = null,

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
)


