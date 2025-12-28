package ir.ayantech.hamrahads.listener

import ir.ayantech.hamrahads.model.error.HamrahAdsError

interface InitializationListener {
    fun onSuccess() {}
    fun onError(error: HamrahAdsError) {}
}

typealias InitListener = InitializationListener

typealias RequestListener = AdLoadListener

typealias ShowListener = AdDisplayListener
