package ir.ayantech.hamrahads.ads.native

import android.content.Context
import ir.ayantech.hamrahads.di.AdDependencies
import ir.ayantech.hamrahads.internal.presentation.AdRequestRunner
import ir.ayantech.hamrahads.listener.AdLoadListener

class NativeAdLoader(context: Context, zoneId: String, listener: AdLoadListener) {
    private val request = AdRequestRunner(listener)

    init { request.start(context, zoneId, "loadNative", AdDependencies.get(context).loadNative::invoke) }

    fun cancelRequest() = request.cancel()
}
