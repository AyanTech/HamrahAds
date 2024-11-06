package ir.ayantech.hamrahads.core

import android.app.Activity
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import ir.ayantech.hamrahads.di.NetworkModule
import ir.ayantech.hamrahads.domain.enums.HamrahAdsBannerType
import ir.ayantech.hamrahads.listener.HamrahAdsInitListener
import ir.ayantech.hamrahads.network.model.NetworkBannerAd
import ir.ayantech.hamrahads.network.model.NetworkError
import ir.ayantech.hamrahads.repository.BannerAdsRepository
import ir.ayantech.hamrahads.utils.UnitUtils
import ir.ayantech.hamrahads.utils.handleIntent
import ir.ayantech.hamrahads.utils.preferenceDataStore.PreferenceDataStoreConstants
import ir.ayantech.hamrahads.utils.preferenceDataStore.PreferenceDataStoreHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ShowBannerAds constructor(
    private val activity: Activity,
    private val size: HamrahAdsBannerType,
    private var viewGroup: ViewGroup? = null,
    private val listener: HamrahAdsInitListener
) {
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
        var (width, height) = size.getSize()

        width = UnitUtils.dpToPx(width, activity.applicationContext)
        height = UnitUtils.dpToPx(height, activity.applicationContext)

        val adContainer = FrameLayout(activity)
        val params = FrameLayout.LayoutParams(
            width,
            height
        ).apply {
            if (viewGroup == null) {
                gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            }
        }
        adContainer.layoutParams = params

        val adImage = AppCompatImageView(activity).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            scaleType = ImageView.ScaleType.FIT_XY
            setOnClickListener {
                CoroutineScope(Dispatchers.IO).launch {
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

        CoroutineScope(Dispatchers.Main).launch {
            Glide.with(activity.applicationContext)
                .load(bannerImage)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>,
                        isFirstResource: Boolean
                    ): Boolean {
                        listener.onError(NetworkError().getError(5))
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable,
                        model: Any,
                        target: Target<Drawable>?,
                        dataSource: DataSource,
                        isFirstResource: Boolean
                    ): Boolean {

                        adContainer.addView(adImage)
                        if (viewGroup != null) {
                            val paramViewGroup = viewGroup?.layoutParams
                            paramViewGroup?.width = width
                            paramViewGroup?.height = height
                            viewGroup?.layoutParams = paramViewGroup
                            viewGroup?.addView(adContainer, paramViewGroup)
                        } else {
                            activity.addContentView(adContainer, params)
                        }

                        listener.onSuccess()
                        return false
                    }
                }).into(adImage)
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