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
import kotlinx.coroutines.launch

class RequestNativeAds constructor(
    val context: Context,
    val zoneId: String,
    val listener: HamrahAdsInitListener
) {
    init {
        NetworkDeviceInfo().fetchNetworkDeviceInfo(context) {
            CoroutineScope(Dispatchers.IO).launch {
                when (val result =
                    NativeAdsRepository(NetworkModule(context)).fetchNativeAds(zoneId, it)) {
                    is NetworkResult.Success -> {
                        val data = result.data
                        PreferenceDataStoreHelper(context).putPreferenceNative(
                            PreferenceDataStoreConstants.HamrahAdsNative,
                            data
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