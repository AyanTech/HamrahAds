package ir.ayantech.hamrahads.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

private val errorCodes = listOf(
  /*0*/  NetworkError("G00010", "The information entered is not complete"),
  /*1*/  NetworkError("G00011", "Response body is null"),
  /*2*/  NetworkError("G00012", "Error body is null"),
  /*3*/  NetworkError("G00013", "Failed to deserialize error response"),
  /*4*/  NetworkError("G00014", "Network request failed"),
  /*5*/  NetworkError("G00015", "The desired advertisement photo has not been downloaded correctly"),
  /*6*/  NetworkError("G00017", "There is no advertising information"),
  /*7*/  NetworkError("G00018", "The web display encountered a problem"),
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