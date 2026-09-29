package ir.ayantech.hamrahads.data.network

import ir.ayantech.hamrahads.model.error.HamrahAdsError

sealed class NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>()
    data class Error(val errorResponse: HamrahAdsError) : NetworkResult<Nothing>()
}
