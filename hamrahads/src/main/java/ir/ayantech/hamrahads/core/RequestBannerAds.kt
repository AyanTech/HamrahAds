package ir.ayantech.hamrahads.core

import android.content.Context
import ir.ayantech.hamrahads.di.NetworkModule
import ir.ayantech.hamrahads.di.NetworkResult
import ir.ayantech.hamrahads.listener.HamrahAdsInitListener
import ir.ayantech.hamrahads.network.model.NetworkDeviceInfo
import ir.ayantech.hamrahads.network.model.NetworkError
import ir.ayantech.hamrahads.repository.BannerAdsRepository
import ir.ayantech.hamrahads.utils.preferenceDataStore.PreferenceDataStoreConstants
import ir.ayantech.hamrahads.utils.preferenceDataStore.PreferenceDataStoreHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


class RequestBannerAds(
    private val context: Context,
    private val zoneId: String,
    private val listener: HamrahAdsInitListener
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
                if (appKey.isBlank()) {
                    mainScope.launch {
                        listener.onError(NetworkError().getError(8))
                    }
                    return@launch
                }
                when (val result =
                    BannerAdsRepository(NetworkModule(context)).fetchBannerAds(zoneId, it)) {
                    is NetworkResult.Success -> {
                        val data = result.data
                        PreferenceDataStoreHelper(context).putPreferenceBanner(
                            PreferenceDataStoreConstants.HamrahAdsBanner,
                            data
                        )
                        mainScope.launch {
                            listener.onSuccess()
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