package ir.ayantech.hamrahads.core

import android.content.Context
import ir.ayantech.hamrahads.di.NetworkModule
import ir.ayantech.hamrahads.di.NetworkResult
import ir.ayantech.hamrahads.listener.RequestListener
import ir.ayantech.hamrahads.network.model.NetworkDeviceInfo
import ir.ayantech.hamrahads.repository.InterstitialAdsRepository
import ir.ayantech.hamrahads.utils.preferenceDataStore.PreferenceDataStoreConstants
import ir.ayantech.hamrahads.utils.preferenceDataStore.PreferenceDataStoreHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class RequestInterstitialAds(
    private val context: Context,
    private val zoneId: String,
    private val listener: RequestListener
) {
    private val job = SupervisorJob()
    private val ioScope = CoroutineScope(Dispatchers.IO + job)
    private val mainScope = CoroutineScope(Dispatchers.Main + job)

    init {
        NetworkDeviceInfo().fetchNetworkDeviceInfo(context) {
            ioScope.launch {
                val appKey = PreferenceDataStoreHelper(context).getPreference(
                    PreferenceDataStoreConstants.HamrahInitializer,
                    ""
                )
                if (appKey.isBlank() || zoneId.isBlank()) {
                    return@launch
                }
                when (val result =
                    InterstitialAdsRepository(NetworkModule(context)).fetchInterstitialAds(zoneId, it)) {
                    is NetworkResult.Success -> {
                        result.data.let { data ->
                            PreferenceDataStoreHelper(context).putPreferenceInterstitial(
                                zoneId,
                                data
                            )
                            mainScope.launch {
                                listener.onSuccess()
                            }
                        }
                    }

                    is NetworkResult.Error -> {
                        val error = result.errorResponse
                        mainScope.launch {
                            listener.onError(error)
                        }
                    }
                }
            }
        }
    }

    fun cancelRequest() {
        job.cancel()
    }
}