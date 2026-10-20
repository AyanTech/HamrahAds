package ir.ayantech.hamrahads.ads.interstitial

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import ir.ayantech.hamrahads.di.AdDependencies
import ir.ayantech.hamrahads.internal.presentation.AdRequestRunner
import ir.ayantech.hamrahads.listener.AdLoadListener

class InterstitialAdLoader @JvmOverloads constructor(
    context: Context,
    zoneId: String,
    listener: AdLoadListener,
    lifecycleOwner: LifecycleOwner? = null
) {
    private val request = AdRequestRunner(listener, lifecycleOwner)

    init {
        request.start(
            context,
            zoneId,
            "loadInterstitial",
            AdDependencies.get(context).loadInterstitial::invoke,
            listener::onInterstitialAdLoaded
        )
    }

    fun cancelRequest() = request.cancel()
}
