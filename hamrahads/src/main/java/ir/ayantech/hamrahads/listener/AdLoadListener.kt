package ir.ayantech.hamrahads.listener

import ir.ayantech.hamrahads.model.error.HamrahAdsError

interface AdLoadListener {
    fun onSuccess() {}
    fun onError(error: HamrahAdsError) {}
}
