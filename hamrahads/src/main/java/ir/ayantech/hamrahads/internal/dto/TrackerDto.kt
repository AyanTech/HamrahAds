package ir.ayantech.hamrahads.internal.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class TrackerDto(
    @SerialName("impression")
    var impression: String? = null,

    @SerialName("click")
    var click: String? = null,
)
