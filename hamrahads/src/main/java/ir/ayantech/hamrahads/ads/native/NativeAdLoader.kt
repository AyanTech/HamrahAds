package ir.ayantech.hamrahads.ads.native

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import ir.ayantech.hamrahads.di.AdDependencies
import ir.ayantech.hamrahads.internal.presentation.AdRequestRunner
import ir.ayantech.hamrahads.listener.AdLoadListener

class NativeAdLoader @JvmOverloads constructor(
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
            "loadNative",
            AdDependencies.get(context).loadNative::invoke,
            listener::onNativeAdLoaded
        )
    }

    fun cancelRequest() = request.cancel()
}
