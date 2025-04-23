package ir.ayantech.hamrahads.core

import android.content.Context
import ir.ayantech.hamrahads.di.NetworkModule
import ir.ayantech.hamrahads.di.NetworkResult
import ir.ayantech.hamrahads.listener.HamrahAdsInitListener
import ir.ayantech.hamrahads.network.model.NetworkDeviceInfo
import ir.ayantech.hamrahads.repository.NativeAdsRepository
import ir.ayantech.hamrahads.utils.preferenceDataStore.PreferenceDataStoreConstants
import ir.ayantech.hamrahads.utils.preferenceDataStore.PreferenceDataStoreHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class RequestNativeAds(
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
                when (val result =
                    NativeAdsRepository(NetworkModule(context)).fetchNativeAds(zoneId, it)) {
                    is NetworkResult.Success -> {
                        val data = result.data
                        PreferenceDataStoreHelper(context).putPreferenceNative(
                            PreferenceDataStoreConstants.HamrahAdsNative,
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