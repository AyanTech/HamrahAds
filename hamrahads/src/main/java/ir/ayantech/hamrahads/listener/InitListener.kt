package ir.ayantech.hamrahads.listener

import ir.ayantech.hamrahads.network.model.NetworkError

interface InitListener {
    fun onSuccess() {}
    fun onError(error: NetworkError) {}
}