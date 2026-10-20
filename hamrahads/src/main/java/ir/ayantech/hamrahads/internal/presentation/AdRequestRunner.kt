package ir.ayantech.hamrahads.internal.presentation

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import ir.ayantech.hamrahads.ads.LoadedAd
import ir.ayantech.hamrahads.di.AdDependencies
import ir.ayantech.hamrahads.domain.model.AdRequest
import ir.ayantech.hamrahads.domain.model.AdResult
import ir.ayantech.hamrahads.internal.diagnostics.AdDiagnostics
import ir.ayantech.hamrahads.listener.AdLoadListener
import ir.ayantech.hamrahads.model.error.AdError
import ir.ayantech.hamrahads.model.error.ErrorType

/** Shared request lifecycle used by the three public loader facades. */
internal class AdRequestRunner(listener: AdLoadListener, owner: LifecycleOwner? = null) {
    private val tasks = AdTaskScope(listener::onError)
    private val callback = listener
    private var lifecycleOwner = owner
    private val observer = object : DefaultLifecycleObserver {
        override fun onDestroy(owner: LifecycleOwner) = cancel()
    }

    init {
        if (owner?.lifecycle?.currentState == Lifecycle.State.DESTROYED) {
            tasks.cancel()
            lifecycleOwner = null
        } else owner?.lifecycle?.addObserver(observer)
    }

    fun <T> start(
        context: Context,
        zoneId: String,
        operation: String,
        load: suspend (AdRequest) -> AdResult<T>,
        onLoaded: (LoadedAd<T>) -> Unit
    ) {
        val application = context.applicationContext
        tasks.launch(operation) {
            try {
                if (zoneId.isBlank()) {
                    callback.onError(AdError.INVALID_REQUEST.toError(ErrorType.Local))
                    return@launch
                }
                val dependencies = AdDependencies.get(application)
                when (val result = dependencies.load(zoneId, load)) {
                    is AdResult.Success -> {
                        AdDiagnostics.event(operation, "succeeded")
                        val ad = result.data
                        dependencies.cache.put(ad)
                        onLoaded(ad)
                    }

                    is AdResult.Error -> {
                        AdDiagnostics.event(operation, "failed", result.errorResponse.code)
                        callback.onError(result.errorResponse)
                    }
                }
            } finally {
                lifecycleOwner?.lifecycle?.removeObserver(observer)
                lifecycleOwner = null
            }
        }
    }

    fun cancel() {
        tasks.cancel()
        lifecycleOwner?.lifecycle?.removeObserver(observer)
        lifecycleOwner = null
    }
}
