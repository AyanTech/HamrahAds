package ir.ayantech.hamrahads.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

private val errorCodes = listOf(
    /*0*/  NetworkError(code = "G00010", description = "The information entered is not complete"),
    /*1*/  NetworkError(code = "G00011", description = "Response body is null"),
    /*2*/  NetworkError(code = "G00012", description = "Error body is null"),
    /*3*/  NetworkError(code = "G00013", description = "Failed to deserialize error response"),
    /*4*/  NetworkError(code = "G00014", description = "Network request failed"),
    /*5*/  NetworkError(code = "G00015", description = "The ad image has not been downloaded"),
    /*6*/  NetworkError(code = "G00017", description = "There is no advertising information"),
    /*7*/  NetworkError(code = "G00018", description = "The web display encountered a problem"),
    /*8*/  NetworkError(code = "G00019", description = "AppKey is empty"),
)

@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class NetworkError(

    @SerialName("code")
    var code: String? = null,

    @SerialName("description")
    var description: String? = null,

    @Transient
    var type: ErrorType = ErrorType.Remote
) {
    fun getError(id: Int, type: ErrorType): NetworkError {
        val error = errorCodes[id]
        error.type = type
        return error
    }
}

@Serializable
enum class ErrorType {
    Remote, Local
}