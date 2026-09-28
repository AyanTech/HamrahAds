package ir.ayantech.hamrahads.ads.native

import ir.ayantech.hamrahads.model.error.AdError
import android.view.ViewGroup
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import ir.ayantech.hamrahads.di.AdDependencies
import ir.ayantech.hamrahads.domain.model.isDisplayable
import ir.ayantech.hamrahads.internal.presentation.AdImages
import ir.ayantech.hamrahads.internal.presentation.AdTracking
import ir.ayantech.hamrahads.internal.presentation.AdViewSession
import ir.ayantech.hamrahads.internal.presentation.VisibleImpressionObserver
import ir.ayantech.hamrahads.internal.presentation.native.NativeAdBinder
import ir.ayantech.hamrahads.internal.util.handleIntent
import ir.ayantech.hamrahads.listener.AdDisplayListener

@MainThread
class NativeAdView(
    firstActivity: AppCompatActivity,
    viewGroup: ViewGroup,
    private val zoneId: String,
    private val listener: AdDisplayListener,
) {
    private val session = AdViewSession(firstActivity, listener)
    private val dependencies = AdDependencies.get(firstActivity)
    private val images = AdImages(session)
    private val tracking = AdTracking(session.tasks, dependencies.trackClick, dependencies.trackImpression,
        { dependencies.cache.remove(zoneId) }, listener)

    init {
        session.tasks.launch("showNative") {
            val activity = session.activity()
            if (zoneId.isBlank() || activity == null) { session.fail(AdError.INVALID_REQUEST); return@launch }
            val ad = dependencies.cache.getNative(zoneId)
            if (ad == null || !ad.isDisplayable()) { session.fail(AdError.AD_UNAVAILABLE); return@launch }
            NativeAdBinder(session, images).bind(viewGroup, ad) {
                tracking.click(ad.trackers?.click)
                handleIntent(activity, ad.landingType, ad.landingLink)
            }
            listener.onLoaded()
            if (session.isActive) {
                val observer = VisibleImpressionObserver(viewGroup) { tracking.impression(ad.trackers?.impression) }
                session.onDispose(observer::dispose)
                observer.start()
            }
        }
    }

    fun destroyAds() = session.dispose()
}
