package ir.ayantech.hamrahads.ads.banner

import android.content.Context
import ir.ayantech.hamrahads.di.AdDependencies
import ir.ayantech.hamrahads.internal.presentation.AdRequestRunner
import ir.ayantech.hamrahads.listener.AdLoadListener

class BannerAdLoader(context: Context, zoneId: String, listener: AdLoadListener) {
    private val request = AdRequestRunner(listener)

    init {
        request.start(context, zoneId, "loadBanner", AdDependencies.get(context).loadBanner::invoke)
    }

    fun cancelRequest() = request.cancel()
}
