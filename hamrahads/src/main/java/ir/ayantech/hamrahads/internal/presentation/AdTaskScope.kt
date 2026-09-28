package ir.ayantech.hamrahads.internal.presentation

import ir.ayantech.hamrahads.internal.diagnostics.AdDiagnostics
import ir.ayantech.hamrahads.model.error.AdError
import ir.ayantech.hamrahads.model.error.ErrorType
import ir.ayantech.hamrahads.model.error.HamrahAdsError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

/** Owns SDK work and delivers results on the supplied UI dispatcher. */
internal class AdTaskScope(
    private val onError: (HamrahAdsError) -> Unit,
    dispatcher: CoroutineDispatcher = Dispatchers.Main,
) {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(dispatcher + job)
    val isActive: Boolean get() = job.isActive

    fun launch(operation: String, block: suspend () -> Unit) {
        scope.launch {
            AdDiagnostics.event(operation, "started")
            try {
                block()
            } catch (cancelled: CancellationException) {
                AdDiagnostics.event(operation, "cancelled")
                throw cancelled
            } catch (exception: Exception) {
                AdDiagnostics.failure(operation, exception)
                onError(AdError.REQUEST_FAILED.toError(ErrorType.Local))
            }
        }
    }

    fun cancel() = job.cancel()
}
