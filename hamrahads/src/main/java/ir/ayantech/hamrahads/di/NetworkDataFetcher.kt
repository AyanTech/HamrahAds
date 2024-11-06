package ir.ayantech.hamrahads.di

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
                } ?: NetworkResult.Error(NetworkError().getError(1))
            } else {
                val errorBody = response.errorBody()?.string()
                    ?: return NetworkResult.Error(NetworkError().getError(2))
                try {
                    NetworkResult.Error(Json.decodeFromString<NetworkError>(errorBody))
                } catch (e: Exception) {
                    NetworkResult.Error(NetworkError().getError(3))
                }
            }
        } catch (e: Exception) {
            NetworkResult.Error(NetworkError().getError(4))
        }
    }
}