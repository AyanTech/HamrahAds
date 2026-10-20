package ir.ayantech.hamrahads.internal.presentation

import ir.ayantech.hamrahads.ads.AdEvents
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
    private val listener: AdDisplayListener,
    private val events: AdEvents = AdEvents(),
    private val deliveryTasks: AdTaskScope = tasks,
) {
    fun click(url: String?) {
        if (events.clicked || !tasks.isActive) {
            return
        }
        events.clicked = true
        if (!url.isNullOrBlank()) deliver(deliveryTasks, "click", url, trackClick::invoke)
        listener.onClick()
    }

    fun impression(url: String?) {
        if (events.displayed || !tasks.isActive) {
            return
        }
        events.displayed = true
        if (!url.isNullOrBlank()) deliver(deliveryTasks, "impression", url, trackImpression::invoke)
        listener.onDisplayed()
    }
}

// Captures only event data and a use case, never the view session or its listener.
private fun deliver(
    tasks: AdTaskScope,
    operation: String,
    url: String,
    send: suspend (String) -> AdResult<Unit>
) {
    tasks.launch(operation) {
        when (val result = send(url)) {
            is AdResult.Success -> AdDiagnostics.event(operation, "succeeded")
            is AdResult.Error -> AdDiagnostics.event(operation, "failed", result.errorResponse.code)
        }
    }
}
