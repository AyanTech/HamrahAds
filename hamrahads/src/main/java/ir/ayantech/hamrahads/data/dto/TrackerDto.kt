package ir.ayantech.hamrahads.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class TrackerDto(
    @SerialName("impression")
    val impression: String? = null,

    @SerialName("click")
    val click: String? = null,
)
