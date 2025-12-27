package ir.ayantech.hamrahads.core

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
import ir.ayantech.hamrahads.network.model.ErrorType
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
import kotlinx.coroutines.withContext
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
    private var isClick = false

    private var trackedViewRef: WeakReference<View>? = null
    private var globalLayoutListener: ViewTreeObserver.OnGlobalLayoutListener? = null
    private var preDrawListener: ViewTreeObserver.OnPreDrawListener? = null
    private var attachStateListener: View.OnAttachStateChangeListener? = null
    private var fragmentLifecycleCallbacks: androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks? =
        null

    init {
        start()
    }

    private fun start() {
        if (zoneId.isBlank()) {
            listener.onError(NetworkError().getError(0, ErrorType.Local))
            return
        }

        val activity = activityRef.get()
        if (activity == null || activity.isFinishing || activity.isDestroyed) {
            listener.onError(NetworkError().getError(0, ErrorType.Local))
            return
        }

        ioScope.launch {
            val banner = PreferenceDataStoreHelper(activity.applicationContext).getPreferenceBannerCoroutine(zoneId)
            withContext(Dispatchers.Main) {
                if (banner == null) {
                    listener.onError(NetworkError().getError(6, ErrorType.Local))
                    return@withContext
                }
                if (banner.landingType != null
                    && !banner.landingLink.isNullOrEmpty()
                    && !banner.trackers?.click.isNullOrEmpty()
                    && !banner.trackers?.impression.isNullOrEmpty()
                    && hasBannerImage(size, banner)
                ) {
                    showView(banner, activity)
                } else {
                    listener.onError(NetworkError().getError(6, ErrorType.Local))
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
                            listener.onError(NetworkError().getError(5, ErrorType.Remote))
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
                                        .data(result.asDrawable(currentActivity.resources))
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
        if (!isClick) {
            isClick = true
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
                clearTrackingObservers(activity)
                ioScope.launch {
                    banner.trackers?.impression?.let {
                        when (BannerAdsRepository(NetworkModule(activity.applicationContext)).impression(
                            it
                        )) {
                            is NetworkResult.Success -> listener.onDisplayed()
                            is NetworkResult.Error -> {}
                        }
                    }
                    PreferenceDataStoreHelper(activity.applicationContext)
                        .removePreferenceCoroutine(zoneId)
                }
            }
        }

        trackedViewRef = WeakReference(view)

        globalLayoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                tryTrack()
            }
        }
        view.viewTreeObserver.addOnGlobalLayoutListener(globalLayoutListener)

        preDrawListener = object : ViewTreeObserver.OnPreDrawListener {
            override fun onPreDraw(): Boolean {
                tryTrack()
                return true
            }
        }
        view.viewTreeObserver.addOnPreDrawListener(preDrawListener)

        attachStateListener = object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) = tryTrack()
            override fun onViewDetachedFromWindow(v: View) {}
        }
        view.addOnAttachStateChangeListener(attachStateListener)

        fragmentLifecycleCallbacks =
            object : androidx.fragment.app.FragmentManager.FragmentLifecycleCallbacks() {
                override fun onFragmentResumed(
                    fm: androidx.fragment.app.FragmentManager,
                    f: androidx.fragment.app.Fragment
                ) {
                    tryTrack()
                }
            }
        fragmentLifecycleCallbacks?.let {
            activity.supportFragmentManager.registerFragmentLifecycleCallbacks(it, true)
        }
    }

    private fun clearTrackingObservers(activity: AppCompatActivity?) {
        val trackedView = trackedViewRef?.get()
        if (trackedView != null) {
            val observer = trackedView.viewTreeObserver
            if (observer.isAlive) {
                globalLayoutListener?.let { observer.removeOnGlobalLayoutListener(it) }
                preDrawListener?.let { observer.removeOnPreDrawListener(it) }
            }
            attachStateListener?.let { trackedView.removeOnAttachStateChangeListener(it) }
        }
        globalLayoutListener = null
        preDrawListener = null
        attachStateListener = null
        trackedViewRef = null

        fragmentLifecycleCallbacks?.let { callbacks ->
            activity?.supportFragmentManager?.unregisterFragmentLifecycleCallbacks(callbacks)
        }
        fragmentLifecycleCallbacks = null
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
        clearTrackingObservers(activityRef.get())
        job.cancel()
        if (::container.isInitialized) {
            (container.parent as? ViewGroup)?.removeView(container)
            container.removeAllViews()
        }
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
