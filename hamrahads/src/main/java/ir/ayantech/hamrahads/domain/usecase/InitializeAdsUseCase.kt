package ir.ayantech.hamrahads.domain.usecase

import ir.ayantech.hamrahads.domain.model.AdResult

interface InitializeAdsUseCase {
    suspend operator fun invoke(appKey: String): AdResult<Unit>
}
