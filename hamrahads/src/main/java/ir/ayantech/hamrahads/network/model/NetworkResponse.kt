package ir.ayantech.hamrahads.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkResponse(

    @SerialName("code")
    var code: String? = null,
)