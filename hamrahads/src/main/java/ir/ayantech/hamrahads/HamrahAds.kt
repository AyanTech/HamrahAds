package ir.ayantech.hamrahads

import android.content.Context
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import ir.ayantech.hamrahads.ads.banner.BannerAdLoader
import ir.ayantech.hamrahads.ads.banner.BannerAdView
import ir.ayantech.hamrahads.ads.interstitial.InterstitialAdView
import ir.ayantech.hamrahads.ads.interstitial.InterstitialAdLoader
import ir.ayantech.hamrahads.ads.native.NativeAdLoader
import ir.ayantech.hamrahads.ads.native.NativeAdView
import ir.ayantech.hamrahads.init.HamrahAdsInitializer
import ir.ayantech.hamrahads.listener.InitListener
import ir.ayantech.hamrahads.listener.RequestListener
import ir.ayantech.hamrahads.listener.ShowListener
import ir.ayantech.hamrahads.model.enums.BannerSize

class HamrahAds {

    class Initializer {
        private lateinit var context: Context
        private lateinit var hamrahAdsId: String
        private lateinit var listener: InitListener

        fun setContext(context: Context) = apply {
            this.context = context
        }

        fun initId(hamrahAdsId: String) = apply {
            this.hamrahAdsId = hamrahAdsId
        }

        fun initListener(listener: InitListener) = apply {
            this.listener = listener
        }

        fun build(): HamrahAdsInitializer? {
            return if (::context.isInitialized &&
                ::hamrahAdsId.isInitialized &&
                ::listener.isInitialized &&
                hamrahAdsId.isNotBlank()
            ) {
                HamrahAdsInitializer(context, hamrahAdsId, listener)
            } else {
                null
            }
        }
    }

    class RequestBannerAds {
        private lateinit var context: Context
        private lateinit var zoneId: String
        private lateinit var requestListener: RequestListener

        fun setContext(context: Context) = apply {
            this.context = context
        }

        fun initId(zoneId: String) = apply {
            this.zoneId = zoneId
        }

        fun initListener(requestListener: RequestListener) = apply {
            this.requestListener = requestListener
        }

        fun build(): BannerAdLoader? {
            return if (::context.isInitialized &&
                ::zoneId.isInitialized &&
                ::requestListener.isInitialized &&
                zoneId.isNotBlank()
            ) {
                BannerAdLoader(context, zoneId, requestListener)
            } else {
                null
            }
        }
    }

    class ShowBannerAds() {
        private lateinit var activity: AppCompatActivity
        private lateinit var size: BannerSize
        private lateinit var zoneId: String
        private var viewGroup: ViewGroup? = null
        private lateinit var showListener: ShowListener

        fun setContext(activity: AppCompatActivity) = apply {
            this.activity = activity
        }

        fun setSize(size: BannerSize) = apply {
            this.size = size
        }

        fun initId(zoneId: String) = apply {
            this.zoneId = zoneId
        }

        fun setViewGroup(viewGroup: ViewGroup?) = apply {
            this.viewGroup = viewGroup
        }

        fun initListener(showListener: ShowListener) = apply {
            this.showListener = showListener
        }

        fun build(): BannerAdView? {
            return if (::activity.isInitialized &&
                ::zoneId.isInitialized &&
                ::showListener.isInitialized &&
                ::size.isInitialized &&
                zoneId.isNotBlank()
            ) {
                BannerAdView(
                    activity,
                    size,
                    zoneId,
                    viewGroup,
                    showListener
                )
            } else {
                null
            }
        }
    }

    class RequestInterstitialAds {
        private lateinit var context: Context
        private lateinit var zoneId: String
        private lateinit var requestListener: RequestListener

        fun setContext(context: Context) = apply {
            this.context = context
        }

        fun initId(zoneId: String) = apply {
            this.zoneId = zoneId
        }

        fun initListener(requestListener: RequestListener) = apply {
            this.requestListener = requestListener
        }

        fun build(): InterstitialAdLoader? {
            return if (::context.isInitialized &&
                ::zoneId.isInitialized &&
                ::requestListener.isInitialized &&
                zoneId.isNotBlank()
            ) {
                InterstitialAdLoader(context, zoneId, requestListener)
            } else {
                null
            }
        }
    }

    class ShowInterstitialAds {
        private lateinit var activity: AppCompatActivity
        private lateinit var zoneId: String
        private lateinit var showListener: ShowListener

        fun setContext(activity: AppCompatActivity) = apply {
            this.activity = activity
        }

        fun initId(zoneId: String) = apply {
            this.zoneId = zoneId
        }

        fun initListener(showListener: ShowListener) = apply {
            this.showListener = showListener
        }

        fun build(): InterstitialAdView? {
            return if (::activity.isInitialized &&
                ::zoneId.isInitialized &&
                ::showListener.isInitialized &&
                zoneId.isNotBlank()
            ) {
                InterstitialAdView(activity, zoneId, showListener)
            } else {
                null
            }
        }
    }

    class RequestNativeAds {
        private lateinit var context: Context
        private lateinit var zoneId: String
        private lateinit var requestListener: RequestListener

        fun setContext(context: Context) = apply {
            this.context = context
        }

        fun initId(zoneId: String) = apply {
            this.zoneId = zoneId
        }

        fun initListener(requestListener: RequestListener) = apply {
            this.requestListener = requestListener
        }

        fun build(): NativeAdLoader? {
            return if (::context.isInitialized &&
                ::zoneId.isInitialized &&
                ::requestListener.isInitialized &&
                zoneId.isNotBlank()
            ) {
                NativeAdLoader(context, zoneId, requestListener)
            } else {
                null
            }
        }
    }

    class ShowNativeAds {
        private lateinit var activity: AppCompatActivity
        private lateinit var zoneId: String
        private lateinit var viewGroup: ViewGroup
        private lateinit var showListener: ShowListener

        fun setContext(activity: AppCompatActivity) = apply {
            this.activity = activity
        }

        fun setViewGroup(viewGroup: ViewGroup) = apply {
            this.viewGroup = viewGroup
        }

        fun initId(zoneId: String) = apply {
            this.zoneId = zoneId
        }

        fun initListener(showListener: ShowListener) = apply {
            this.showListener = showListener
        }

        fun build(): NativeAdView? {
            return if (::activity.isInitialized &&
                ::zoneId.isInitialized &&
                ::showListener.isInitialized &&
                ::viewGroup.isInitialized &&
                zoneId.isNotBlank()
            ) {
                NativeAdView(activity, viewGroup, zoneId, showListener)
            } else {
                null
            }
        }
    }
}
