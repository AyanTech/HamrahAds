package ir.ayantech.hamrahads.internal.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class InitializerDto(

    @SerialName("code")
    var code: String? = null,

    @SerialName("description")
    var description: String? = null,
)


