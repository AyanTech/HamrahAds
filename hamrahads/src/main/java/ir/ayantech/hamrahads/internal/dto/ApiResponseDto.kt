package ir.ayantech.hamrahads.internal.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class ApiResponseDto(

    @SerialName("code")
    var code: String? = null,
)
