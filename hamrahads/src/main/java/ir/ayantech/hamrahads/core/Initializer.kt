package ir.ayantech.hamrahads.core

import android.content.Context
import ir.ayantech.hamrahads.di.NetworkModule
import ir.ayantech.hamrahads.di.NetworkResult
import ir.ayantech.hamrahads.listener.InitListener
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
    private val listener: InitListener
) {
    init {
        LocationTracker(context).startTrackingLocation { latitude, longitude ->
            CoroutineScope(Dispatchers.IO).launch {
                val helper = PreferenceDataStoreHelper(context)
                helper.putPreferenceCoroutine(
                    PreferenceDataStoreConstants.HamrahLatitude,
                    latitude
                )
                helper.putPreferenceCoroutine(
                    PreferenceDataStoreConstants.HamrahLongitude,
                    longitude
                )
            }
        }
        CoroutineScope(Dispatchers.IO).launch {
            val helper = PreferenceDataStoreHelper(context)
            val cachedInitKey = helper.getPreferenceCoroutine(
                PreferenceDataStoreConstants.HamrahInitializer,
                ""
            )
            if (cachedInitKey.isNotBlank() && cachedInitKey == hamrahAdsId) {
                listener.onSuccess()
            } else {
                when (val result =
                    InitializerRepository(NetworkModule(context)).fetchProfileInfo(hamrahAdsId)) {
                    is NetworkResult.Success -> {
                        helper.putPreferenceCoroutine(
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
            }
        }
    }
}
