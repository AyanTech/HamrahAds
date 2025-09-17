package ir.ayantech.hamrahads.core

import android.content.res.Resources
import android.graphics.Rect
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import coil3.asDrawable
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.target
import ir.ayantech.hamrahads.di.NetworkModule
import ir.ayantech.hamrahads.di.NetworkResult
import ir.ayantech.hamrahads.domain.enums.HamrahAdsBannerType
import ir.ayantech.hamrahads.listener.ShowListener
import ir.ayantech.hamrahads.network.model.NetworkBannerAd
import ir.ayantech.hamrahads.network.model.NetworkError
import ir.ayantech.hamrahads.repository.BannerAdsRepository
import ir.ayantech.hamrahads.utils.handleIntent
import ir.ayantech.hamrahads.utils.imageLoader
import ir.ayantech.hamrahads.utils.preferenceDataStore.PreferenceDataStoreHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

class ShowBannerAds(
    firstActivity: AppCompatActivity,
    private val size: HamrahAdsBannerType,
    private val zoneId: String,
    private var viewGroup: ViewGroup? = null,
    private val listener: ShowListener
) {
    private lateinit var container: FrameLayout
    private val job = SupervisorJob()
    private val ioScope = CoroutineScope(Dispatchers.IO + job)
    private val activityRef = WeakReference(firstActivity)

    init {
        if (zoneId.isNotBlank()) {
            activityRef.get()?.let { activity ->
                if (!activity.isFinishing && !activity.isDestroyed) {
                    val banner =
                        PreferenceDataStoreHelper(activity.applicationContext).getPreferenceBanner(
                            zoneId
                        )
                    if (banner != null) {
                        if (banner.landingType != null
                            && !banner.landingLink.isNullOrEmpty()
                            && !banner.trackers?.click.isNullOrEmpty()
                            && !banner.trackers?.impression.isNullOrEmpty()
                            && hasBannerImage(size, banner)
                        ) {
                            showView(banner, activity)
                        } else {
                            listener.onError(NetworkError().getError(6))
                        }
                    }
                }
            }
        }
    }

    private fun showView(banner: NetworkBannerAd, activity: AppCompatActivity) {
        container = FrameLayout(activity)
        val params = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            if (viewGroup == null) {
                gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            }
        }
        container.layoutParams = params
        container.setOnClickListener {
            return@setOnClickListener
        }
        val adImage = AppCompatImageView(activity).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            )
            scaleType = ImageView.ScaleType.FIT_XY
            adjustViewBounds = true
            setOnClickListener {
                onClickView(banner, activity)
            }
        }

        val bannerImage = when (size) {
            HamrahAdsBannerType.BANNER_320x50 -> banner.banner320x50
            HamrahAdsBannerType.BANNER_640x1136 -> banner.banner640x1136
            HamrahAdsBannerType.BANNER_1136x640 -> banner.banner1136x640
        }

        val imageLoader = imageLoader(activity.applicationContext)
        imageLoader.enqueue(
            ImageRequest.Builder(activity.applicationContext)
                .data(bannerImage)
                .listener(
                    onError = { request, result ->
                        destroyAds()
                        if (!result.throwable.message.isNullOrBlank()) {
                            listener.onError(
                                NetworkError(
                                    description = "Failed to load image: ${result.throwable.message}",
                                    code = "G00015"
                                )
                            )
                        } else {
                            listener.onError(NetworkError().getError(5))
                        }
                    }
                )
                .target(
                    onSuccess = { result ->
                        activityRef.get()?.let { currentActivity ->
                            if (!currentActivity.isFinishing && !currentActivity.isDestroyed) {
                                imageLoader.enqueue(
                                    ImageRequest.Builder(currentActivity.applicationContext)
                                        .target(adImage)
                                        .data(result.asDrawable(Resources.getSystem()))
                                        .build()
                                )

                                if (viewGroup != null) {
                                    viewGroup?.addView(container)
                                } else {
                                    currentActivity.addContentView(container, params)
                                }
                                container.addView(adImage)
                                listener.onLoaded()
                                trackImpressionOnce(adImage, banner, currentActivity)
                            }
                        }
                    },
                )
                .memoryCachePolicy(CachePolicy.DISABLED)
                .diskCachePolicy(CachePolicy.DISABLED)
                .build()
        )
    }

    private fun onClickView(banner: NetworkBannerAd, activity: AppCompatActivity) {
        ioScope.launch {
            banner.trackers?.click?.let {
                when (val result =
                    BannerAdsRepository(NetworkModule(activity.applicationContext)).click(it)) {
                    is NetworkResult.Success -> {
                        result.data.let { data ->
                            listener.onClick()
                        }
                    }

                    is NetworkResult.Error -> {
                    }
                }
            }
        }
        handleIntent(activity, banner.landingType, banner.landingLink)
    }

    private fun isViewVisibleEnough(view: View): Boolean {
        if (!view.isShown || view.alpha <= 0f) return false
        val rect = Rect()
        val visible = view.getGlobalVisibleRect(rect)
        return visible &&
                rect.height() >= view.height / 2 &&
                rect.width() >= view.width / 2
    }

    private fun trackImpressionOnce(
        view: View,
        banner: NetworkBannerAd,
        activity: AppCompatActivity
    ) {
        var tracked = false

        fun tryTrack() {
            if (!tracked && isViewVisibleEnough(view)) {
                tracked = true
                ioScope.launch {
                    banner.trackers?.impression?.let {
                        when (BannerAdsRepository(NetworkModule(activity.applicationContext)).impression(it)) {
                            is NetworkResult.Success -> listener.onDisplayed()
                            is NetworkResult.Error -> {}
                        }
                    }
                    PreferenceDataStoreHelper(activity.applicationContext)
                        .removePreferenceCoroutine(zoneId)
                }
            }
        }

        view.viewTreeObserver.addOnGlobalLayoutListener(object :
            ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                tryTrack()
                if (tracked) {
                    view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                }
            }
        })

        view.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                tryTrack()
                if (tracked) {
                    view.viewTreeObserver.removeOnPreDrawListener(this)
                }
                return true
            }
        })

        view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) = tryTrack()
            override fun onViewDetachedFromWindow(v: View) {}
        })

        activity.supportFragmentManager.registerFragmentLifecycleCallbacks(
            object : androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks() {
                override fun onFragmentResumed(
                    fm: androidx.fragment.app.FragmentManager,
                    f: androidx.fragment.app.Fragment
                ) {
                    tryTrack()
                    if (tracked) {
                        activity.supportFragmentManager.unregisterFragmentLifecycleCallbacks(this)
                    }
                }
            }, true
        )
    }

//    private fun trackImpressionOnce(
//        view: View,
//        banner: NetworkBannerAd,
//        activity: AppCompatActivity
//    ) {
//        var tracked = false
//        fun tryTrack() {
//            if (!tracked && view.isShown) {
//                val rect = Rect()
//                val visible = view.getGlobalVisibleRect(rect)
//                if (visible && rect.height() > 0 && rect.width() > 0) {
//                    tracked = true
//                    ioScope.launch {
//                        banner.trackers?.impression?.let {
//                            when (val result =
//                                BannerAdsRepository(NetworkModule(activity.applicationContext)).impression(
//                                    it
//                                )) {
//                                is NetworkResult.Success -> {
//                                    result.data.let { data ->
//                                        listener.onDisplayed()
//                                    }
//                                }
//
//                                is NetworkResult.Error -> {
//                                }
//                            }
//                        }
//                        PreferenceDataStoreHelper(activity.applicationContext)
//                            .removePreferenceCoroutine(zoneId)
//                    }
//                }
//            }
//        }
//
//        view.viewTreeObserver.addOnGlobalLayoutListener(object :
//            ViewTreeObserver.OnGlobalLayoutListener {
//            override fun onGlobalLayout() {
//                tryTrack()
//                if (tracked) {
//                    view.viewTreeObserver.removeOnGlobalLayoutListener(this)
//                }
//            }
//        })
//
//        view.addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
//            override fun onViewAttachedToWindow(v: View) {
//                tryTrack()
//            }
//
//            override fun onViewDetachedFromWindow(v: View) {}
//        })
//    }

    fun destroyAds() {
        job.cancel()
        if (::container.isInitialized)
            container.removeAllViews()
    }

    private fun hasBannerImage(size: HamrahAdsBannerType, banner: NetworkBannerAd): Boolean {
        when (size) {
            HamrahAdsBannerType.BANNER_320x50 -> {
                if (!banner.banner320x50.isNullOrEmpty()) {
                    return true
                }
            }

            HamrahAdsBannerType.BANNER_640x1136 -> {
                if (!banner.banner640x1136.isNullOrEmpty()) {
                    return true
                }
            }

            HamrahAdsBannerType.BANNER_1136x640 -> {
                if (!banner.banner1136x640.isNullOrEmpty()) {
                    return true
                }
            }
        }
        return false
    }
}