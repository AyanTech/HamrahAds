package ir.ayantech.hamrahads.di

import ir.ayantech.hamrahads.network.model.ErrorType
import ir.ayantech.hamrahads.network.model.NetworkError
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.json.Json
import retrofit2.Response

class NetworkDataFetcher {
    private val json = Json {
        ignoreUnknownKeys = true
    }

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
                    val parsed = json.decodeFromString<NetworkError>(errorBody)
                    if (parsed.code.isNullOrBlank() && parsed.description.isNullOrBlank()) {
                        NetworkResult.Error(
                            NetworkError(
                                code = "G00013",
                                description = errorBody,
                                type = ErrorType.Remote
                            )
                        )
                    } else {
                        parsed.type = ErrorType.Remote
                        NetworkResult.Error(parsed)
                    }
                } catch (_: Exception) {
                    NetworkResult.Error(
                        NetworkError(
                            code = "G00013",
                            description = errorBody,
                            type = ErrorType.Remote
                        )
                    )
                }
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            if (!e.message.isNullOrBlank()) {
                NetworkResult.Error(
                    NetworkError(
                        description = e.message,
                        code = "G00014",
                        type = ErrorType.Remote
                    )
                )
            } else {
                NetworkResult.Error(NetworkError().getError(4, ErrorType.Remote))
            }
        }
    }
}
