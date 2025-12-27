package ir.ayantech.hamrahads.core

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import coil3.ImageLoader
import coil3.asDrawable
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.target
import ir.ayantech.hamrahads.R
import ir.ayantech.hamrahads.di.NetworkModule
import ir.ayantech.hamrahads.di.NetworkResult
import ir.ayantech.hamrahads.listener.ShowListener
import ir.ayantech.hamrahads.network.model.ErrorType
import ir.ayantech.hamrahads.network.model.NetworkError
import ir.ayantech.hamrahads.network.model.NetworkNativeAd
import ir.ayantech.hamrahads.repository.NativeAdsRepository
import ir.ayantech.hamrahads.utils.handleIntent
import ir.ayantech.hamrahads.utils.imageLoader
import ir.ayantech.hamrahads.utils.preferenceDataStore.PreferenceDataStoreHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ref.WeakReference


class ShowNativeAds(
    firstActivity: AppCompatActivity,
    private val viewGroup: ViewGroup,
    private val zoneId: String,
    private val listener: ShowListener
) {
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
            val native = PreferenceDataStoreHelper(activity.applicationContext).getPreferenceNativeCoroutine(zoneId)
            withContext(Dispatchers.Main) {
                if (native == null) {
                    listener.onError(NetworkError().getError(6, ErrorType.Local))
                    return@withContext
                }

                if (native.landingType != null
                    && !native.cta.isNullOrEmpty()
                    && !native.caption.isNullOrEmpty()
                    && !native.landingLink.isNullOrEmpty()
                    && !native.trackers?.click.isNullOrEmpty()
                    && !native.trackers?.impression.isNullOrEmpty()
                ) {
                    showView(viewGroup, native, activity)
                    listener.onLoaded()
                    trackImpressionOnce(viewGroup, native, activity)
                } else {
                    listener.onError(NetworkError().getError(6, ErrorType.Local))
                }
            }
        }
    }

    private fun showView(
        viewGroup: ViewGroup,
        native: NetworkNativeAd,
        activity: AppCompatActivity
    ) {
        for (i in 0 until viewGroup.childCount) {
            val childView = viewGroup.getChildAt(i)
            if (childView is ViewGroup) {
                showView(childView, native, activity)
            } else {
                when (childView.id) {
                    R.id.hamrah_ad_native_title -> {
                        if (childView is TextView) {
                            childView.text = native.caption
                        }
                    }

                    R.id.hamrah_ad_native_description -> {
                        if (childView is TextView) {
                            childView.text = native.description
                        }
                    }

                    R.id.hamrah_ad_native_cta -> {
                        if (childView is Button) {
                            childView.text = native.cta
                            childView.setOnClickListener {
                                onClickView(native, activity)
                            }
                        }
                    }

                    R.id.hamrah_ad_native_logo -> {
                        if (childView is ImageView && !native.logo.isNullOrEmpty()) {
                            val imageLoader = ImageLoader.Builder(activity.applicationContext)
                                .build()

                            imageLoader.enqueue(
                                ImageRequest.Builder(activity.applicationContext)
                                    .data(native.logo)
//                                    .listener(
//                                        onError = { request, result ->
//                                            destroyAds()
//                                            if (!result.throwable.message.isNullOrBlank()) {
//                                                listener.onError(
//                                                    NetworkError(
//                                                        description = "Failed to load image: ${result.throwable.message}",
//                                                        code = "G00015"
//                                                    )
//                                                )
//                                            } else {
//                                                listener.onError(NetworkError().getError(5))
//                                            }
//                                        }
//                                    )
                                    .target(
                                        onSuccess = { result ->
                                            activityRef.get()?.let { currentActivity ->
                                                if (!currentActivity.isFinishing && !currentActivity.isDestroyed) {
                                                    imageLoader.enqueue(
                                                        ImageRequest.Builder(currentActivity.applicationContext)
                                                            .target(childView)
                                                            .data(result.asDrawable(currentActivity.resources))
                                                            .build()
                                                    )
                                                }
                                            }
                                        },
                                    )
                                    .memoryCachePolicy(CachePolicy.DISABLED)
                                    .diskCachePolicy(CachePolicy.DISABLED)
                                    .build()
                            )
                        }
                    }

                    R.id.hamrah_ad_native_banner -> {
                        if (childView is ImageView && !native.banner1136x640.isNullOrEmpty()) {
                            val imageLoader = imageLoader(activity.applicationContext)
                            imageLoader.enqueue(
                                ImageRequest.Builder(activity.applicationContext)
                                    .data(native.banner1136x640)
//                                    .listener(
//                                        onError = { request, result ->
//                                            destroyAds()
//                                            if (!result.throwable.message.isNullOrBlank()) {
//                                                listener.onError(
//                                                    NetworkError(
//                                                        description = "Failed to load image: ${result.throwable.message}",
//                                                        code = "G00015"
//                                                    )
//                                                )
//                                            } else {
//                                                listener.onError(NetworkError().getError(5))
//                                            }
//                                        }
//                                    )
                                    .target(
                                        onSuccess = { result ->
                                            activityRef.get()?.let { currentActivity ->
                                                if (!currentActivity.isFinishing && !currentActivity.isDestroyed) {
                                                    imageLoader.enqueue(
                                                        ImageRequest.Builder(currentActivity.applicationContext)
                                                            .target(childView)
                                                            .data(result.asDrawable(currentActivity.resources))
                                                            .build()
                                                    )
                                                }
                                            }
                                        }
                                    )
                                    .memoryCachePolicy(CachePolicy.DISABLED)
                                    .diskCachePolicy(CachePolicy.DISABLED)
                                    .build()
                            )
                        }
                    }

                    R.id.hamrah_ad_native_cta_view -> {
                        if (childView is View) {
                            childView.setOnClickListener {
                                onClickView(native, activity)
                            }
                        }
                    }
                }
            }
        }
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
        native: NetworkNativeAd,
        activity: AppCompatActivity
    ) {
        var tracked = false

        fun tryTrack() {
            if (!tracked && isViewVisibleEnough(view)) {
                tracked = true
                clearTrackingObservers(activity)
                ioScope.launch {
                    native.trackers?.impression?.let {
                        when (NativeAdsRepository(NetworkModule(activity.applicationContext)).impression(
                            it
                        )) {
                            is NetworkResult.Success -> listener.onDisplayed()
                            is NetworkResult.Error -> {

                            }
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

    private fun onClickView(native: NetworkNativeAd, activity: AppCompatActivity) {
        if (!isClick) {
            isClick = true
            ioScope.launch {
                native.trackers?.click?.let {
                    when (val result =
                        NativeAdsRepository(NetworkModule(activity.applicationContext)).click(it)) {
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

        handleIntent(
            activity,
            native.landingType,
            native.landingLink
        )
    }

    fun destroyAds() {
        clearTrackingObservers(activityRef.get())
        job.cancel()
    }
}
