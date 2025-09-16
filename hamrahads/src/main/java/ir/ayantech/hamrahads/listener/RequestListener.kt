package ir.ayantech.hamrahads.listener

import ir.ayantech.hamrahads.network.model.NetworkError

interface RequestListener {
    fun onSuccess() {}
    fun onError(error: NetworkError) {}
}