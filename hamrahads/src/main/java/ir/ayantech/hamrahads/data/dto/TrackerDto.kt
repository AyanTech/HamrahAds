package ir.ayantech.hamrahads.data.dto

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@OptIn(InternalSerializationApi::class)
@Serializable
data class TrackerDto(
    @SerialName("impression")
    val impression: String? = null,

    @SerialName("click")
    val click: String? = null,
)
