package ir.ayantech.hamrahads.model.error

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

private val errorCodes = listOf(
    /*0*/  HamrahAdsError(code = "G00010", description = "The information entered is not complete"),
    /*1*/  HamrahAdsError(code = "G00011", description = "Response body is null"),
    /*2*/  HamrahAdsError(code = "G00012", description = "Error body is null"),
    /*3*/  HamrahAdsError(code = "G00013", description = "Failed to deserialize error response"),
    /*4*/  HamrahAdsError(code = "G00014", description = "Network request failed"),
    /*5*/  HamrahAdsError(code = "G00015", description = "The ad image has not been downloaded"),
    /*6*/  HamrahAdsError(code = "G00017", description = "There is no advertising information"),
    /*7*/  HamrahAdsError(code = "G00018", description = "The web display encountered a problem"),
    /*8*/  HamrahAdsError(code = "G00019", description = "AppKey is empty"),
)

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
        val error = errorCodes[id]
        error.type = type
        return error
    }
}
