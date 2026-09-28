package ir.ayantech.hamrahads.domain.usecase

import ir.ayantech.hamrahads.domain.model.AdRequest
import ir.ayantech.hamrahads.domain.model.AdResult
import ir.ayantech.hamrahads.domain.model.NativeAd

interface LoadNativeAdUseCase {
    suspend operator fun invoke(request: AdRequest): AdResult<NativeAd>
}
