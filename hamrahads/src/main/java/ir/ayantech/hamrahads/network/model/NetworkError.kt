package ir.ayantech.hamrahads.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

private val errorCodes = listOf(
    NetworkError("G00010", "The information entered is not complete"),
    NetworkError("G00011", "Response body is null"),
    NetworkError("G00012", "Error body is null"),
    NetworkError("G00013", "Failed to deserialize error response"),
    NetworkError("G00014", "Network request failed"),
    NetworkError("G00015", "The desired advertisement photo has not been downloaded correctly"),
    NetworkError("G00017", "There is no advertising information"),
)

@Serializable
data class NetworkError(

    @SerialName("code")
    var code: String? = null,

    @SerialName("description")
    var description: String? = null,
) {
    fun getError(id: Int): NetworkError {
        return errorCodes[id]
    }
}