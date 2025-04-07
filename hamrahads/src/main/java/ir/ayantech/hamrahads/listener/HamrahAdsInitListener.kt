package ir.ayantech.hamrahads.listener

import ir.ayantech.hamrahads.network.model.NetworkError

interface HamrahAdsInitListener {
    fun onSuccess() {}
    fun onError(error: NetworkError) {}
    fun onClose() {}
    fun onClick() {}
//    fun onKeyboardVisibility(viewGroup: ViewGroup, isKeyboardVisible: Boolean) {}
}