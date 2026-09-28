package ir.ayantech.hamrahads.internal.presentation

import android.content.Context
import ir.ayantech.hamrahads.data.mapper.toAdRequest
import ir.ayantech.hamrahads.domain.model.AdRequest
import ir.ayantech.hamrahads.domain.model.AdResult
import ir.ayantech.hamrahads.internal.device.DeviceInfo
import ir.ayantech.hamrahads.internal.diagnostics.AdDiagnostics
import ir.ayantech.hamrahads.listener.AdLoadListener
import ir.ayantech.hamrahads.model.error.AdError
import ir.ayantech.hamrahads.model.error.ErrorType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/** Shared request lifecycle used by the three public loader facades. */
internal class AdRequestRunner(listener: AdLoadListener) {
    private val tasks = AdTaskScope(listener::onError)
    private val callback = listener

    fun <T> start(context: Context, zoneId: String, operation: String, load: suspend (AdRequest) -> AdResult<T>) {
        val application = context.applicationContext
        tasks.launch(operation) {
            if (zoneId.isBlank()) {
                callback.onError(AdError.INVALID_REQUEST.toError(ErrorType.Local))
                return@launch
            }
            val request = withContext(Dispatchers.IO) { DeviceInfo().fetchDeviceInfo(application).toAdRequest(zoneId) }
            when (val result = load(request)) {
                is AdResult.Success -> { AdDiagnostics.event(operation, "succeeded"); callback.onSuccess() }
                is AdResult.Error -> {
                    AdDiagnostics.event(operation, "failed", result.errorResponse.code)
                    callback.onError(result.errorResponse)
                }
            }
        }
    }

    fun cancel() = tasks.cancel()
}
