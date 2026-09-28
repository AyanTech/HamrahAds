package ir.ayantech.hamrahads.ads.interstitial

import android.content.Context
import ir.ayantech.hamrahads.di.AdDependencies
import ir.ayantech.hamrahads.internal.presentation.AdRequestRunner
import ir.ayantech.hamrahads.listener.AdLoadListener

class InterstitialAdLoader(context: Context, zoneId: String, listener: AdLoadListener) {
    private val request = AdRequestRunner(listener)

    init { request.start(context, zoneId, "loadInterstitial", AdDependencies.get(context).loadInterstitial::invoke) }

    fun cancelRequest() = request.cancel()
}
