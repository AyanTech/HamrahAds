package ir.ayantech.hamrahads.ads.banner

import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import ir.ayantech.hamrahads.di.AdDependencies
import ir.ayantech.hamrahads.domain.model.BannerAd
import ir.ayantech.hamrahads.domain.model.imageUrl
import ir.ayantech.hamrahads.domain.model.isDisplayable
import ir.ayantech.hamrahads.internal.presentation.AdImages
import ir.ayantech.hamrahads.internal.presentation.AdTracking
import ir.ayantech.hamrahads.internal.presentation.AdViewSession
import ir.ayantech.hamrahads.internal.presentation.VisibleImpressionObserver
import ir.ayantech.hamrahads.internal.util.handleIntent
import ir.ayantech.hamrahads.listener.AdDisplayListener
import ir.ayantech.hamrahads.model.enums.BannerSize
import ir.ayantech.hamrahads.model.error.AdError

@MainThread
class BannerAdView(
    firstActivity: AppCompatActivity,
    private val size: BannerSize,
    private val zoneId: String,
    private var viewGroup: ViewGroup? = null,
    private val listener: AdDisplayListener,
) {
    private val session = AdViewSession(firstActivity, listener)
    private val dependencies = AdDependencies.get(firstActivity)
    private val images = AdImages(session)
    private val tracking =
        AdTracking(
            session.tasks, dependencies.trackClick, dependencies.trackImpression,
            { dependencies.cache.remove(zoneId) }, listener
        )

    init {
        session.onDispose { viewGroup = null }
        session.tasks.launch("showBanner") {
            val activity = session.activity()
            if (zoneId.isBlank() || activity == null) {
                session.fail(AdError.INVALID_REQUEST)
                return@launch
            }
            val ad = dependencies.cache.getBanner(zoneId)
            if (ad == null || !ad.isDisplayable(size)) {
                session.fail(AdError.AD_UNAVAILABLE)
                return@launch
            }
            render(activity, ad)
        }
    }

    private fun render(activity: AppCompatActivity, ad: BannerAd) {
        val container = FrameLayout(activity)
        val params = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
            .apply { if (viewGroup == null) gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL }
        container.layoutParams = params
        val image = AppCompatImageView(activity).apply {
            layoutParams = FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            scaleType = ImageView.ScaleType.FIT_XY
            adjustViewBounds = true
            setOnClickListener {
                if (session.isActive) {
                    tracking.click(ad.trackers?.click)
                    handleIntent(activity, ad.landingType, ad.landingLink)
                }
            }
        }
        container.addView(image)
        session.onDispose {
            image.setOnClickListener(null)
            (container.parent as? ViewGroup)?.removeView(container)
            container.removeAllViews()
        }
        images.load(ad.imageUrl(size), image) {
            if (session.activity() == null) return@load
            viewGroup?.addView(container) ?: activity.addContentView(container, params)
            listener.onLoaded()
            if (session.isActive) {
                val observer =
                    VisibleImpressionObserver(image) { tracking.impression(ad.trackers?.impression) }
                session.onDispose(observer::dispose)
                observer.start()
            }
        }
    }

    fun destroyAds() = session.dispose()
}
