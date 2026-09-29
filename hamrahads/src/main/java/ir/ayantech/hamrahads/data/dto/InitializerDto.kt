package ir.ayantech.hamrahads.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class InitializerDto(

    @SerialName("code")
    val code: String? = null,

    @SerialName("description")
    val description: String? = null,
)


