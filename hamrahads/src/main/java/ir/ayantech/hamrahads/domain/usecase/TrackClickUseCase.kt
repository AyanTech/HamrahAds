package ir.ayantech.hamrahads.domain.usecase

import ir.ayantech.hamrahads.domain.model.AdResult

interface TrackClickUseCase {
    suspend operator fun invoke(url: String): AdResult<Unit>
}
