package ir.ayantech.hamrahads.init

import android.content.Context
import ir.ayantech.hamrahads.di.AdDependencies
import ir.ayantech.hamrahads.domain.model.AdResult
import ir.ayantech.hamrahads.data.storage.PreferenceDataStoreConstants
import ir.ayantech.hamrahads.data.storage.PreferenceDataStoreHelper
import ir.ayantech.hamrahads.internal.device.LocationTracker
import ir.ayantech.hamrahads.internal.diagnostics.AdDiagnostics
import ir.ayantech.hamrahads.internal.presentation.AdTaskScope
import ir.ayantech.hamrahads.listener.InitializationListener
import kotlinx.coroutines.withTimeoutOrNull

class HamrahAdsInitializer(context: Context, appKey: String, listener: InitializationListener) {
    private val tasks = AdTaskScope(listener::onError)
    private val locationTasks = AdTaskScope(onError = { AdDiagnostics.event("location", "unavailable") })

    init {
        val application = context.applicationContext
        tasks.launch("initialize") {
            when (val result = AdDependencies.get(application).initialize(appKey)) {
                is AdResult.Success -> { AdDiagnostics.event("initialize", "succeeded"); listener.onSuccess() }
                is AdResult.Error -> {
                    AdDiagnostics.event("initialize", "failed", result.errorResponse.code)
                    listener.onError(result.errorResponse)
                }
            }
        }
        // Location is optional and must neither block initialization nor leave an unbounded listener.
        locationTasks.launch("location") {
            val location = withTimeoutOrNull(10_000) { LocationTracker(application).awaitLocation() }
            if (location != null) {
                val preferences = PreferenceDataStoreHelper(application)
                preferences.putPreferenceCoroutine(PreferenceDataStoreConstants.HamrahLatitude, location.first)
                preferences.putPreferenceCoroutine(PreferenceDataStoreConstants.HamrahLongitude, location.second)
            }
        }
    }

    fun cancelRequest() { tasks.cancel(); locationTasks.cancel() }
}
