package ir.ayantech.hamrahads

import android.app.Activity
import android.content.Context
import android.view.ViewGroup
import ir.ayantech.hamrahads.core.Initializer
import ir.ayantech.hamrahads.core.RequestBannerAds
import ir.ayantech.hamrahads.core.ShowBannerAds
import ir.ayantech.hamrahads.core.ShowInterstitialAds
import ir.ayantech.hamrahads.domain.enums.HamrahAdsBannerType
import ir.ayantech.hamrahads.listener.HamrahAdsInitListener

class HamrahAds {

    class Initializer {
        private lateinit var context: Context
        private lateinit var hamrahAdsId: String
        private lateinit var hamrahAdsInitListener: HamrahAdsInitListener

        fun setContext(context: Context) = apply {
            this.context = context
        }

        fun initId(hamrahAdsId: String) = apply {
            this.hamrahAdsId = hamrahAdsId
        }

        fun initListener(hamrahAdsInitListener: HamrahAdsInitListener) = apply {
            this.hamrahAdsInitListener = hamrahAdsInitListener
        }

        fun build(): ir.ayantech.hamrahads.core.Initializer? {
            return if (::context.isInitialized &&
                ::hamrahAdsId.isInitialized &&
                ::hamrahAdsInitListener.isInitialized
            ) {
                Initializer(context, hamrahAdsId, hamrahAdsInitListener)
            } else {
                null
            }
        }
    }

    class RequestBannerAds {
        private lateinit var context: Context
        private lateinit var zoneId: String
        private lateinit var hamrahAdsInitListener: HamrahAdsInitListener

        fun setContext(context: Context) = apply {
            this.context = context
        }

        fun initId(zoneId: String) = apply {
            this.zoneId = zoneId
        }

        fun initListener(hamrahAdsInitListener: HamrahAdsInitListener) = apply {
            this.hamrahAdsInitListener = hamrahAdsInitListener
        }

        fun build(): ir.ayantech.hamrahads.core.RequestBannerAds? {
            return if (::context.isInitialized &&
                ::zoneId.isInitialized &&
                ::hamrahAdsInitListener.isInitialized
            ) {
                return RequestBannerAds(context, zoneId, hamrahAdsInitListener)
            } else {
                null
            }
        }
    }

    class ShowBannerAds {
        private lateinit var activity: Activity
        private lateinit var size: HamrahAdsBannerType
        private var viewGroup: ViewGroup? = null
        private lateinit var hamrahAdsInitListener: HamrahAdsInitListener

        fun setContext(activity: Activity) = apply {
            this.activity = activity
        }

        fun setSize(size: HamrahAdsBannerType) = apply {
            this.size = size
        }

        fun setViewGroup(viewGroup: ViewGroup?) = apply {
            this.viewGroup = viewGroup
        }

        fun initListener(hamrahAdsInitListener: HamrahAdsInitListener) = apply {
            this.hamrahAdsInitListener = hamrahAdsInitListener
        }

        fun build(): ir.ayantech.hamrahads.core.ShowBannerAds? {
            return if (::activity.isInitialized &&
                ::hamrahAdsInitListener.isInitialized &&
                ::size.isInitialized
            ) {
                return ShowBannerAds(activity, size, viewGroup, hamrahAdsInitListener)
            } else {
                null
            }
        }
    }

    class RequestInterstitialAds {
        private lateinit var context: Context
        private lateinit var zoneId: String
        private lateinit var hamrahAdsInitListener: HamrahAdsInitListener

        fun setContext(context: Context) = apply {
            this.context = context
        }

        fun initId(zoneId: String) = apply {
            this.zoneId = zoneId
        }

        fun initListener(hamrahAdsInitListener: HamrahAdsInitListener) = apply {
            this.hamrahAdsInitListener = hamrahAdsInitListener
        }

        fun build(): ir.ayantech.hamrahads.core.RequestInterstitialAds? {
            return if (::context.isInitialized &&
                ::zoneId.isInitialized &&
                ::hamrahAdsInitListener.isInitialized
            ) {
                return ir.ayantech.hamrahads.core.RequestInterstitialAds(
                    context,
                    zoneId,
                    hamrahAdsInitListener
                )
            } else {
                null
            }
        }
    }

    class ShowInterstitialAds {
        private lateinit var activity: Activity
        private lateinit var hamrahAdsInitListener: HamrahAdsInitListener

        fun setContext(activity: Activity) = apply {
            this.activity = activity
        }

        fun initListener(hamrahAdsInitListener: HamrahAdsInitListener) = apply {
            this.hamrahAdsInitListener = hamrahAdsInitListener
        }

        fun build(): ir.ayantech.hamrahads.core.ShowInterstitialAds? {
            return if (::activity.isInitialized &&
                ::hamrahAdsInitListener.isInitialized
            ) {
                return ShowInterstitialAds(activity, hamrahAdsInitListener)
            } else {
                null
            }
        }
    }

    class RequestNativeAds {
        private lateinit var context: Context
        private lateinit var zoneId: String
        private lateinit var hamrahAdsInitListener: HamrahAdsInitListener

        fun setContext(context: Context) = apply {
            this.context = context
        }

        fun initId(zoneId: String) = apply {
            this.zoneId = zoneId
        }

        fun initListener(hamrahAdsInitListener: HamrahAdsInitListener) = apply {
            this.hamrahAdsInitListener = hamrahAdsInitListener
        }

        fun build(): ir.ayantech.hamrahads.core.RequestBannerAds? {
            return if (::context.isInitialized &&
                ::zoneId.isInitialized &&
                ::hamrahAdsInitListener.isInitialized
            ) {
                return RequestBannerAds(context, zoneId, hamrahAdsInitListener)
            } else {
                null
            }
        }
    }

    class ShowNativeAds {
        private lateinit var activity: Activity
        private lateinit var viewGroup: ViewGroup
        private lateinit var hamrahAdsInitListener: HamrahAdsInitListener

        fun setContext(activity: Activity) = apply {
            this.activity = activity
        }

        fun setViewGroup(viewGroup: ViewGroup) = apply {
            this.viewGroup = viewGroup
        }

        fun initListener(hamrahAdsInitListener: HamrahAdsInitListener) = apply {
            this.hamrahAdsInitListener = hamrahAdsInitListener
        }

        fun build(): ir.ayantech.hamrahads.core.ShowNativeAds? {
            return if (::activity.isInitialized &&
                ::hamrahAdsInitListener.isInitialized &&
                ::viewGroup.isInitialized
            ) {
                return ir.ayantech.hamrahads.core.ShowNativeAds(
                    activity,
                    viewGroup,
                    hamrahAdsInitListener
                )
            } else {
                null
            }
        }
    }
}
