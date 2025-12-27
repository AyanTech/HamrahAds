package ir.ayantech.hamrahads.core

import android.content.Context
import ir.ayantech.hamrahads.di.NetworkModule
import ir.ayantech.hamrahads.di.NetworkResult
import ir.ayantech.hamrahads.listener.RequestListener
import ir.ayantech.hamrahads.network.model.ErrorType
import ir.ayantech.hamrahads.network.model.NetworkDeviceInfo
import ir.ayantech.hamrahads.network.model.NetworkError
import ir.ayantech.hamrahads.repository.NativeAdsRepository
import ir.ayantech.hamrahads.utils.preferenceDataStore.PreferenceDataStoreConstants
import ir.ayantech.hamrahads.utils.preferenceDataStore.PreferenceDataStoreHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RequestNativeAds(
    private val context: Context,
    private val zoneId: String,
    private val listener: RequestListener
) {
    private val job = SupervisorJob()
    private val ioScope = CoroutineScope(Dispatchers.IO + job)

    init {
        NetworkDeviceInfo().fetchNetworkDeviceInfo(context) {
            ioScope.launch {
                val appKey = PreferenceDataStoreHelper(context).getPreferenceCoroutine(
                    PreferenceDataStoreConstants.HamrahInitializer,
                    ""
                )
                if (zoneId.isBlank()) {
                    withContext(Dispatchers.Main) {
                        listener.onError(NetworkError().getError(0, ErrorType.Local))
                    }
                    return@launch
                }
                if (appKey.isBlank()) {
                    withContext(Dispatchers.Main) {
                        listener.onError(NetworkError().getError(8, ErrorType.Local))
                    }
                    return@launch
                }
                when (val result =
                    NativeAdsRepository(NetworkModule(context)).fetchNativeAds(zoneId, it)) {
                    is NetworkResult.Success -> {
                        result.data.let { data ->
                            PreferenceDataStoreHelper(context).putPreferenceNative(
                                zoneId,
                                data
                            )
                            withContext(Dispatchers.Main) {
                                listener.onSuccess()
                            }
                        }
                    }

                    is NetworkResult.Error -> {
                        val error = result.errorResponse
                        withContext(Dispatchers.Main) {
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
