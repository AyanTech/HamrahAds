package ir.ayantech.hamrahads

import android.content.Context
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import ir.ayantech.hamrahads.core.Initializer
import ir.ayantech.hamrahads.core.RequestBannerAds
import ir.ayantech.hamrahads.domain.enums.HamrahAdsBannerType
import ir.ayantech.hamrahads.listener.InitListener
import ir.ayantech.hamrahads.listener.RequestListener
import ir.ayantech.hamrahads.listener.ShowListener

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

        fun build(): ir.ayantech.hamrahads.core.Initializer? {
            return if (::context.isInitialized &&
                ::hamrahAdsId.isInitialized &&
                ::listener.isInitialized &&
                hamrahAdsId.isNotBlank()
            ) {
                Initializer(context, hamrahAdsId, listener)
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

        fun build(): ir.ayantech.hamrahads.core.RequestBannerAds? {
            return if (::context.isInitialized &&
                ::zoneId.isInitialized &&
                ::requestListener.isInitialized &&
                zoneId.isNotBlank()
            ) {
                return RequestBannerAds(context, zoneId, requestListener)
            } else {
                null
            }
        }
    }

    class ShowBannerAds() {
        private lateinit var activity: AppCompatActivity
        private lateinit var size: HamrahAdsBannerType
        private lateinit var zoneId: String
        private var viewGroup: ViewGroup? = null
        private lateinit var showListener: ShowListener

        fun setContext(activity: AppCompatActivity) = apply {
            this.activity = activity
        }

        fun setSize(size: HamrahAdsBannerType) = apply {
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

        fun build(): ir.ayantech.hamrahads.core.ShowBannerAds? {
            return if (::activity.isInitialized &&
                ::showListener.isInitialized &&
                ::size.isInitialized &&
                zoneId.isNotBlank()
            ) {
                return ir.ayantech.hamrahads.core.ShowBannerAds(
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

        fun build(): ir.ayantech.hamrahads.core.RequestInterstitialAds? {
            return if (::context.isInitialized &&
                ::zoneId.isInitialized &&
                ::requestListener.isInitialized &&
                zoneId.isNotBlank()
            ) {
                return ir.ayantech.hamrahads.core.RequestInterstitialAds(
                    context,
                    zoneId,
                    requestListener
                )
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

        fun build(): ir.ayantech.hamrahads.core.ShowInterstitialAds? {
            return if (::activity.isInitialized &&
                ::showListener.isInitialized &&
                zoneId.isNotBlank()
            ) {
                return ir.ayantech.hamrahads.core.ShowInterstitialAds(
                    activity,
                    zoneId,
                    showListener
                )
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

        fun build(): ir.ayantech.hamrahads.core.RequestNativeAds? {
            return if (::context.isInitialized &&
                ::zoneId.isInitialized &&
                ::requestListener.isInitialized &&
                zoneId.isNotBlank()
            ) {
                return ir.ayantech.hamrahads.core.RequestNativeAds(
                    context,
                    zoneId,
                    requestListener
                )
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

        fun build(): ir.ayantech.hamrahads.core.ShowNativeAds? {
            return if (::activity.isInitialized &&
                ::showListener.isInitialized &&
                ::viewGroup.isInitialized &&
                zoneId.isNotBlank()
            ) {
                return ir.ayantech.hamrahads.core.ShowNativeAds(
                    activity,
                    viewGroup,
                    zoneId,
                    showListener
                )
            } else {
                null
            }
        }
    }
}
