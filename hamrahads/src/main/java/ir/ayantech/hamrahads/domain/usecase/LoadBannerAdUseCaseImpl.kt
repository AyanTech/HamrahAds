package ir.ayantech.hamrahads.domain.usecase

import ir.ayantech.hamrahads.domain.model.AdRequest
import ir.ayantech.hamrahads.domain.model.AdResult
import ir.ayantech.hamrahads.domain.model.BannerAd
import ir.ayantech.hamrahads.model.error.AdError
import ir.ayantech.hamrahads.model.error.ErrorType

class LoadBannerAdUseCaseImpl(
    private val appKey: suspend () -> String,
    private val fetch: suspend (AdRequest) -> AdResult<BannerAd>,
    private val cache: suspend (String, BannerAd) -> Unit,
) : LoadBannerAdUseCase {
    override suspend operator fun invoke(request: AdRequest): AdResult<BannerAd> {
        val zoneId = request.zoneId
        if (zoneId.isNullOrBlank()) return AdResult.Error(AdError.INVALID_REQUEST.toError(ErrorType.Local))
        if (appKey().isBlank()) return AdResult.Error(AdError.MISSING_APP_KEY.toError(ErrorType.Local))
        return fetch(request).also { result ->
            if (result is AdResult.Success) cache(zoneId, result.data)
        }
    }
}
