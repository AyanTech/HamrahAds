package ir.ayantech.hamrahads.listener

import ir.ayantech.hamrahads.network.model.NetworkError

interface ShowListener {
    fun onLoaded() {}
    fun onError(error: NetworkError) {}
    fun onClose() {}
    fun onClick() {}
    fun onDisplayed() {}
}