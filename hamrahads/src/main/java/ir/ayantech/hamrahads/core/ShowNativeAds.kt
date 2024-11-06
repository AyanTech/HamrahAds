package ir.ayantech.hamrahads.core

import android.content.Context
import android.content.res.Resources
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
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
import ir.ayantech.hamrahads.repository.BannerAdsRepository
import ir.ayantech.hamrahads.utils.preferenceDataStore.PreferenceDataStoreConstants
import ir.ayantech.hamrahads.utils.preferenceDataStore.PreferenceDataStoreHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ShowNativeAds constructor(
    private val context: Context,
    private var viewGroup: ViewGroup,
    private val listener: HamrahAdsInitListener
) {
    init {
        val native = PreferenceDataStoreHelper(context.applicationContext).getPreferenceNative(
            PreferenceDataStoreConstants.HamrahAdsNative,
            null
        )
        if (native?.landingType != null
            && !native.landingLink.isNullOrEmpty()
            && !native.trackers?.click.isNullOrEmpty()
            && !native.trackers?.impression.isNullOrEmpty()
        ) {
            showView(native)
        } else {
            listener.onError(NetworkError().getError(6))
        }
    }

    private fun showView(native: NetworkNativeAd) {
        for (i in 0 until viewGroup.childCount) {
            val childView = viewGroup.getChildAt(i)
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
                    }
                }

                R.id.hamrah_ad_native_logo -> {
                    if (childView is ImageView) {
                        val imageLoader = ImageLoader.Builder(context.applicationContext)
                            .build()
                        imageLoader.enqueue(
                            ImageRequest.Builder(context.applicationContext)
                                .data(native.logo)
                                .target(childView)
                                .target(
                                    onStart = { placeholder ->
                                    },
                                    onSuccess = { result ->
                                        childView.setImageDrawable(result.asDrawable(Resources.getSystem()))
                                        CoroutineScope(Dispatchers.IO).launch {
                                            native.trackers?.impression?.let {
                                                BannerAdsRepository(NetworkModule(context.applicationContext))
                                                    .impression(it)
                                            }
                                            PreferenceDataStoreHelper(context.applicationContext).removePreferenceCoroutine(
                                                PreferenceDataStoreConstants.HamrahAdsNative
                                            )
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
                            CoroutineScope(Dispatchers.IO).launch {
                                native.trackers?.click?.let {
                                    BannerAdsRepository(NetworkModule(context.applicationContext)).click(
                                        it
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}