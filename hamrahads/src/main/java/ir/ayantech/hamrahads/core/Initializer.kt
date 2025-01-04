package ir.ayantech.hamrahads.core

import android.content.Context
import ir.ayantech.hamrahads.di.NetworkModule
import ir.ayantech.hamrahads.di.NetworkResult
import ir.ayantech.hamrahads.listener.HamrahAdsInitListener
import ir.ayantech.hamrahads.repository.InitializerRepository
import ir.ayantech.hamrahads.utils.LocationTracker
import ir.ayantech.hamrahads.utils.preferenceDataStore.PreferenceDataStoreConstants
import ir.ayantech.hamrahads.utils.preferenceDataStore.PreferenceDataStoreHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Initializer(
    private val context: Context,
    private val hamrahAdsId: String,
    private val listener: HamrahAdsInitListener
) {
    init {
        LocationTracker(context).startTrackingLocation { latitude, longitude ->
            PreferenceDataStoreHelper(context).putPreference(
                PreferenceDataStoreConstants.HamrahLatitude,
                latitude
            )

            PreferenceDataStoreHelper(context).putPreference(
                PreferenceDataStoreConstants.HamrahLongitude,
                longitude
            )
        }
        CoroutineScope(Dispatchers.IO).launch {
            val cachedInitKey = PreferenceDataStoreHelper(context).getPreference(
                PreferenceDataStoreConstants.HamrahInitializer,
                ""
            )
            if (cachedInitKey.isEmpty() || cachedInitKey != hamrahAdsId) {
                when (val result =
                    InitializerRepository(NetworkModule(context)).fetchProfileInfo(hamrahAdsId)) {
                    is NetworkResult.Success -> {
                        PreferenceDataStoreHelper(context).putPreference(
                            PreferenceDataStoreConstants.HamrahInitializer,
                            hamrahAdsId
                        )
                        listener.onSuccess()
                    }

                    is NetworkResult.Error -> {
                        val error = result.errorResponse
                        listener.onError(error)
                    }
                }
            } else {
                listener.onSuccess()
            }
        }
    }
}