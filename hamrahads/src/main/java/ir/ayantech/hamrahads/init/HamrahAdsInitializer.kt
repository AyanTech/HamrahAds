package ir.ayantech.hamrahads.init

import android.content.Context
import ir.ayantech.hamrahads.internal.device.LocationTracker
import ir.ayantech.hamrahads.internal.network.NetworkClient
import ir.ayantech.hamrahads.internal.network.NetworkResult
import ir.ayantech.hamrahads.internal.repository.InitializerRepository
import ir.ayantech.hamrahads.internal.storage.PreferenceDataStoreConstants
import ir.ayantech.hamrahads.internal.storage.PreferenceDataStoreHelper
import ir.ayantech.hamrahads.listener.InitializationListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class HamrahAdsInitializer(
    private val context: Context,
    private val appKey: String,
    private val listener: InitializationListener
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
            if (cachedInitKey.isNotBlank() && cachedInitKey == appKey) {
                listener.onSuccess()
            } else {
                when (val result =
                    InitializerRepository(NetworkClient(context)).fetchProfileInfo(appKey)) {
                    is NetworkResult.Success -> {
                        helper.putPreferenceCoroutine(
                            PreferenceDataStoreConstants.HamrahInitializer,
                            appKey
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
