package ir.ayantech.hamrahads.listener

import ir.ayantech.hamrahads.model.error.HamrahAdsError

interface AdDisplayListener {
    fun onLoaded() {}
    fun onError(error: HamrahAdsError) {}
    fun onClose() {}
    fun onClick() {}
    fun onDisplayed() {}
}
