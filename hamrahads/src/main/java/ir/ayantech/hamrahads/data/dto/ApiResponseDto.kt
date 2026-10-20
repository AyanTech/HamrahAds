package ir.ayantech.hamrahads.data.dto

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@OptIn(InternalSerializationApi::class)
@Serializable
data class ApiResponseDto(

    @SerialName("code")
    val code: String? = null,
)
