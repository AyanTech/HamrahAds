package ir.ayantech.hamrahads.domain.model

import ir.ayantech.hamrahads.model.error.HamrahAdsError

sealed interface AdResult<out T> {
    data class Success<T>(val data: T) : AdResult<T>
    data class Error(val errorResponse: HamrahAdsError) : AdResult<Nothing>
}
