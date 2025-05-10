package ir.ayantech.hamrahads.core

import android.content.res.Resources
import android.view.View
import android.view.ViewGroup
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
import ir.ayantech.hamrahads.listener.HamrahAdsInitListener
import ir.ayantech.hamrahads.network.model.NetworkError
import ir.ayantech.hamrahads.network.model.NetworkNativeAd
import ir.ayantech.hamrahads.repository.NativeAdsRepository
import ir.ayantech.hamrahads.utils.handleIntent
import ir.ayantech.hamrahads.utils.imageLoader
import ir.ayantech.hamrahads.utils.preferenceDataStore.PreferenceDataStoreConstants
import ir.ayantech.hamrahads.utils.preferenceDataStore.PreferenceDataStoreHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference


class ShowNativeAds(
    private val firstActivity: AppCompatActivity,
    private val viewGroup: ViewGroup,
    private val listener: HamrahAdsInitListener
) {
    private val job = SupervisorJob()
    private val ioScope = CoroutineScope(Dispatchers.IO + job)
    private val activityRef = WeakReference(firstActivity)

    init {
        activityRef.get()?.let { activity ->
            if (!activity.isFinishing && !activity.isDestroyed) {
                val native =
                    PreferenceDataStoreHelper(activity.applicationContext).getPreferenceNative(
                        PreferenceDataStoreConstants.HamrahAdsNative,
                        null
                    )
                if (native?.landingType != null
                    && !native.cta.isNullOrEmpty()
                    && !native.caption.isNullOrEmpty()
                    && !native.landingLink.isNullOrEmpty()
                    && !native.trackers?.click.isNullOrEmpty()
                    && !native.trackers?.impression.isNullOrEmpty()
                ) {
                    showView(viewGroup, native, activity)
                    setTrackers(native, activity)
                } else {
                    listener.onError(NetworkError().getError(6))
                }
            }
        } ?: run {
            listener.onError(NetworkError().getError(6))
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
                                    .target(
                                        onStart = { placeholder ->
                                        },
                                        onSuccess = { result ->
                                            activityRef.get()?.let { currentActivity ->
                                                if (!currentActivity.isFinishing && !currentActivity.isDestroyed) {
                                                    imageLoader.enqueue(
                                                        ImageRequest.Builder(currentActivity.applicationContext)
                                                            .target(childView)
                                                            .data(result.asDrawable(Resources.getSystem()))
                                                            .build()
                                                    )
                                                }
                                            }
                                        },
                                        onError = { error ->
                                            listener.onError(NetworkError().getError(5))
                                        }
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
                                    .target(
                                        onStart = { placeholder ->
                                        },
                                        onSuccess = { result ->
                                            activityRef.get()?.let { currentActivity ->
                                                if (!currentActivity.isFinishing && !currentActivity.isDestroyed) {
                                                    imageLoader.enqueue(
                                                        ImageRequest.Builder(currentActivity.applicationContext)
                                                            .target(childView)
                                                            .data(result.asDrawable(Resources.getSystem()))
                                                            .build()
                                                    )
                                                }
                                            }
                                        },
                                        onError = { error ->
                                            listener.onError(NetworkError().getError(5))
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

    private fun setTrackers(native: NetworkNativeAd, activity: AppCompatActivity) {
        ioScope.launch {
            native.trackers?.impression?.let {
                NativeAdsRepository(NetworkModule(activity.applicationContext))
                    .impression(it)
            }
            PreferenceDataStoreHelper(activity.applicationContext).removePreferenceCoroutine(
                PreferenceDataStoreConstants.HamrahAdsNative
            )
        }
        listener.onSuccess()
    }

    private fun onClickView(native: NetworkNativeAd, activity: AppCompatActivity) {
        listener.onClick()
        ioScope.launch {
            native.trackers?.click?.let {
                NativeAdsRepository(NetworkModule(activity.applicationContext)).click(
                    it
                )
            }
        }
        handleIntent(
            activity,
            native.landingType,
            native.landingLink
        )
    }

    fun destroyAds() {
        job.cancel()
    }
}