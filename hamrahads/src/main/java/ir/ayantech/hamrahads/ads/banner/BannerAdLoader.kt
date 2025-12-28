package ir.ayantech.hamrahads.ads.banner

import android.content.Context
import ir.ayantech.hamrahads.internal.device.DeviceInfo
import ir.ayantech.hamrahads.internal.network.NetworkClient
import ir.ayantech.hamrahads.internal.network.NetworkResult
import ir.ayantech.hamrahads.internal.repository.BannerRepository
import ir.ayantech.hamrahads.internal.storage.PreferenceDataStoreConstants
import ir.ayantech.hamrahads.internal.storage.PreferenceDataStoreHelper
import ir.ayantech.hamrahads.listener.AdLoadListener
import ir.ayantech.hamrahads.model.error.ErrorType
import ir.ayantech.hamrahads.model.error.HamrahAdsError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class BannerAdLoader(
    private val context: Context,
    private val zoneId: String,
    private val listener: AdLoadListener
) {
    private val job = SupervisorJob()
    private val ioScope = CoroutineScope(Dispatchers.IO + job)

    init {
        DeviceInfo().fetchDeviceInfo(context) {
            ioScope.launch {
                val appKey = PreferenceDataStoreHelper(context).getPreferenceCoroutine(
                    PreferenceDataStoreConstants.HamrahInitializer,
                    ""
                )
                if (zoneId.isBlank()) {
                    withContext(Dispatchers.Main) {
                        listener.onError(HamrahAdsError().getError(0, ErrorType.Local))
                    }
                    return@launch
                }
                if (appKey.isBlank()) {
                    withContext(Dispatchers.Main) {
                        listener.onError(HamrahAdsError().getError(8, ErrorType.Local))
                    }
                    return@launch
                }
                when (val result =
                    BannerRepository(NetworkClient(context)).fetchBannerAds(zoneId, it)) {
                    is NetworkResult.Success -> {
                        result.data.let { data ->
                            PreferenceDataStoreHelper(context).putPreferenceBanner(
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
