package ir.ayantech.hamrahads.di

import ir.ayantech.hamrahads.network.model.ErrorType
import ir.ayantech.hamrahads.network.model.NetworkError
import kotlinx.serialization.json.Json
import retrofit2.Response

class NetworkDataFetcher {
    suspend fun <T> fetchData(request: suspend () -> Response<T>): NetworkResult<T> {
        return try {
            val response = request()
            if (response.isSuccessful) {
                response.body()?.let {
                    NetworkResult.Success(it)
                } ?: NetworkResult.Error(NetworkError().getError(1, ErrorType.Remote))
            } else {
                val errorBody = response.errorBody()?.string()
                    ?: return NetworkResult.Error(NetworkError().getError(2, ErrorType.Remote))
                try {
                    NetworkResult.Error(Json.decodeFromString<NetworkError>(errorBody))
                } catch (_: Exception) {
                    NetworkResult.Error(
                        NetworkError(
                            code = "G00013",
                            description = errorBody
                        )
                    )
                }
            }
        } catch (e: Exception) {
            if (!e.message.isNullOrBlank()) {
                NetworkResult.Error(NetworkError(description = e.message, code = "G00014"))
            } else {
                NetworkResult.Error(NetworkError().getError(4, ErrorType.Remote))
            }
        }
    }
}