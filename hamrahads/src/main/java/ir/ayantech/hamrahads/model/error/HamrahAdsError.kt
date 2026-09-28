package ir.ayantech.hamrahads.model.error

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class HamrahAdsError(

    @SerialName("code")
    var code: String? = null,

    @SerialName("description")
    var description: String? = null,

    @Transient
    var type: ErrorType = ErrorType.Remote
) {
    fun getError(id: Int, type: ErrorType): HamrahAdsError {
        return AdError.entries.first { it.legacyId == id }.toError(type)
    }
}
