package ir.ayantech.hamrahads.internal.presentation

import ir.ayantech.hamrahads.domain.model.AdResult
import ir.ayantech.hamrahads.domain.usecase.TrackClickUseCase
import ir.ayantech.hamrahads.domain.usecase.TrackImpressionUseCase
import ir.ayantech.hamrahads.internal.diagnostics.AdDiagnostics
import ir.ayantech.hamrahads.listener.AdDisplayListener

/** A display owns one click and one impression attempt. Tracking failures are non-fatal. */
internal class AdTracking(
    private val tasks: AdTaskScope,
    private val trackClick: TrackClickUseCase,
    private val trackImpression: TrackImpressionUseCase,
    private val consumeAd: suspend () -> Unit,
    private val listener: AdDisplayListener,
) {
    private var clicked = false
    private var displayed = false

    fun click(url: String?) {
        if (clicked || !tasks.isActive || url.isNullOrBlank()) {
            return
        }
        clicked = true
        tasks.launch("click") {
            when (val result = trackClick(url)) {
                is AdResult.Success -> {
                    AdDiagnostics.event("click", "succeeded")
                    listener.onClick()
                }
                is AdResult.Error -> AdDiagnostics.event("click", "failed", result.errorResponse.code)
            }
        }
    }

    fun impression(url: String?) {
        if (displayed || !tasks.isActive || url.isNullOrBlank()) {
            return
        }
        displayed = true
        tasks.launch("impression") {
            val result = trackImpression(url)
            // Consume even a failed tracking attempt: the creative has already been displayed.
            consumeAd()
            when (result) {
                is AdResult.Success -> {
                    AdDiagnostics.event("impression", "succeeded")
                    listener.onDisplayed()
                }
                is AdResult.Error -> AdDiagnostics.event("impression", "failed", result.errorResponse.code)
            }
        }
    }
}
