package ir.ayantech.hamrahads.core

import android.content.res.Resources
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import coil3.asDrawable
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.target
import ir.ayantech.hamrahads.di.NetworkModule
import ir.ayantech.hamrahads.domain.enums.HamrahAdsBannerType
import ir.ayantech.hamrahads.listener.HamrahAdsInitListener
import ir.ayantech.hamrahads.network.model.NetworkBannerAd
import ir.ayantech.hamrahads.network.model.NetworkError
import ir.ayantech.hamrahads.repository.BannerAdsRepository
import ir.ayantech.hamrahads.utils.handleIntent
import ir.ayantech.hamrahads.utils.imageLoader
import ir.ayantech.hamrahads.utils.preferenceDataStore.PreferenceDataStoreConstants
import ir.ayantech.hamrahads.utils.preferenceDataStore.PreferenceDataStoreHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


class ShowBannerAds(
    private val activity: AppCompatActivity,
    private val size: HamrahAdsBannerType,
    private var viewGroup: ViewGroup? = null,
    private val listener: HamrahAdsInitListener
) {
    private lateinit var container: FrameLayout
    private val job = SupervisorJob()
    private val ioScope = CoroutineScope(Dispatchers.IO + job)

    init {
        val banner = PreferenceDataStoreHelper(activity.applicationContext).getPreferenceBanner(
            PreferenceDataStoreConstants.HamrahAdsBanner,
            null
        )
        if (banner?.landingType != null
            && !banner.landingLink.isNullOrEmpty()
            && !banner.trackers?.click.isNullOrEmpty()
            && !banner.trackers?.impression.isNullOrEmpty()
            && hasBannerImage(size, banner)
        ) {
            showView(banner)
        } else {
            listener.onError(NetworkError().getError(6))
        }
    }

    private fun showView(banner: NetworkBannerAd) {

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
                listener.onClick()
                ioScope.launch {
                    banner.trackers?.click?.let {
                        BannerAdsRepository(NetworkModule(activity.applicationContext)).click(it)
                    }
                }
                handleIntent(activity, banner.landingType, banner.landingLink)
            }
        }

        val bannerImage = when (size) {
            HamrahAdsBannerType.BANNER_320x50 -> banner.banner320x50
            HamrahAdsBannerType.BANNER_640x1136 -> banner.banner640x1136
            HamrahAdsBannerType.BANNER_1136x640 -> banner.banner1136x640
        }

        val imageLoader = imageLoader(activity.applicationContext)
        imageLoader.enqueue(ImageRequest.Builder(activity.applicationContext)
            .data(bannerImage)
            .target(
                onStart = { placeholder ->
                },
                onSuccess = { result ->
                    imageLoader.enqueue(
                        ImageRequest.Builder(activity.applicationContext)
                            .target(adImage)
                            .data(result.asDrawable(Resources.getSystem()))
                            .build()
                    )

                    if (viewGroup != null) {
                        viewGroup?.addView(container)
                    } else {
                        activity.addContentView(container, params)
                    }
                    container.addView(adImage)

                    ioScope.launch {
                        banner.trackers?.impression?.let {
                            BannerAdsRepository(NetworkModule(activity.applicationContext)).impression(
                                it
                            )
                        }
                        PreferenceDataStoreHelper(activity.applicationContext).removePreferenceCoroutine(
                            PreferenceDataStoreConstants.HamrahAdsBanner
                        )
                    }
                    listener.onSuccess()
                },
                onError = { error ->
                    destroyAds()
                    listener.onError(NetworkError().getError(5))
                }
            )
            .memoryCachePolicy(CachePolicy.DISABLED)
            .diskCachePolicy(CachePolicy.DISABLED)
            .build())
    }

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