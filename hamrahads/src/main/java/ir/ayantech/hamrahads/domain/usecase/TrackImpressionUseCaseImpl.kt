package ir.ayantech.hamrahads.domain.usecase

import ir.ayantech.hamrahads.domain.model.AdResult
import ir.ayantech.hamrahads.model.error.AdError
import ir.ayantech.hamrahads.model.error.ErrorType

class TrackImpressionUseCaseImpl(
    private val track: suspend (String) -> AdResult<Unit>,
) : TrackImpressionUseCase {
    override suspend operator fun invoke(url: String): AdResult<Unit> {
        if (url.isBlank()) return AdResult.Error(AdError.INVALID_REQUEST.toError(ErrorType.Local))
        return track(url)
    }
}
