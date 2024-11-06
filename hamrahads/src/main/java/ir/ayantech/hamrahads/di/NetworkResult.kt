package ir.ayantech.hamrahads.di

import ir.ayantech.hamrahads.network.model.NetworkError

sealed class NetworkResult<out T> {
    data class Success<out T>(val data: T) : NetworkResult<T>()
    data class Error(val errorResponse: NetworkError) : NetworkResult<Nothing>()
}