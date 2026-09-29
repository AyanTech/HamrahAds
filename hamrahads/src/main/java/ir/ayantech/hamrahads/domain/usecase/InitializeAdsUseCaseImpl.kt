package ir.ayantech.hamrahads.domain.usecase

import ir.ayantech.hamrahads.domain.model.AdResult
import ir.ayantech.hamrahads.model.error.AdError
import ir.ayantech.hamrahads.model.error.ErrorType

class InitializeAdsUseCaseImpl(
    private val cachedAppKey: suspend () -> String,
    private val initialize: suspend (String) -> AdResult<Unit>,
    private val saveAppKey: suspend (String) -> Unit,
) : InitializeAdsUseCase {
    override suspend operator fun invoke(appKey: String): AdResult<Unit> {
        if (appKey.isBlank()) {
            return AdResult.Error(AdError.MISSING_APP_KEY.toError(ErrorType.Local))
        }
        if (cachedAppKey() == appKey) {
            return AdResult.Success(Unit)
        }
        return initialize(appKey).also { result ->
            if (result is AdResult.Success) {
                saveAppKey(appKey)
            }
        }
    }
}
