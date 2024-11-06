package ir.ayantech.hamrahads.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkTracker(
    @SerialName("impression")
    var impression: String? = null,

    @SerialName("click")
    var click: String? = null,
)
