package ir.ayantech.hamrahads.internal.network

import ir.ayantech.hamrahads.model.error.ErrorType
import ir.ayantech.hamrahads.model.error.HamrahAdsError
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
                } ?: NetworkResult.Error(HamrahAdsError().getError(1, ErrorType.Remote))
            } else {
                val errorBody = response.errorBody()?.string()
                    ?: return NetworkResult.Error(HamrahAdsError().getError(2, ErrorType.Remote))
                try {
                    val parsed = json.decodeFromString<HamrahAdsError>(errorBody)
                    if (parsed.code.isNullOrBlank() && parsed.description.isNullOrBlank()) {
                        NetworkResult.Error(
                            HamrahAdsError(
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
                        HamrahAdsError(
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
                    HamrahAdsError(
                        description = e.message,
                        code = "G00014",
                        type = ErrorType.Remote
                    )
                )
            } else {
                NetworkResult.Error(HamrahAdsError().getError(4, ErrorType.Remote))
            }
        }
    }
}
