package ir.ayantech.hamrahads.ads.banner

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import ir.ayantech.hamrahads.di.AdDependencies
import ir.ayantech.hamrahads.internal.presentation.AdRequestRunner
import ir.ayantech.hamrahads.listener.AdLoadListener

class BannerAdLoader @JvmOverloads constructor(
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
            "loadBanner",
            AdDependencies.get(context).loadBanner::invoke,
            listener::onBannerAdLoaded
        )
    }

    fun cancelRequest() = request.cancel()
}
