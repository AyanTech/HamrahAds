package ir.ayantech.hamrahads.data.network

import ir.ayantech.hamrahads.model.error.AdError
import ir.ayantech.hamrahads.model.error.ErrorType
import ir.ayantech.hamrahads.model.error.HamrahAdsError
import ir.ayantech.hamrahads.internal.diagnostics.AdDiagnostics
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
                } ?: NetworkResult.Error(AdError.EMPTY_RESPONSE.toError(ErrorType.Remote))
            } else {
                val errorBody = response.errorBody()?.string()
                    ?: return NetworkResult.Error(AdError.EMPTY_ERROR_RESPONSE.toError(ErrorType.Remote))
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
            AdDiagnostics.failure("http", e)
            if (!e.message.isNullOrBlank()) {
                NetworkResult.Error(
                    HamrahAdsError(
                        description = e.message,
                        code = "G00014",
                        type = ErrorType.Remote
                    )
                )
            } else {
                NetworkResult.Error(AdError.REQUEST_FAILED.toError(ErrorType.Remote))
            }
        }
    }
}
