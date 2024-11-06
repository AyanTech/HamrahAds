package ir.ayantech.hamrahads.core

import BlurTransformation
import android.app.Activity
import android.content.res.Resources
import android.graphics.Color
import android.os.CountDownTimer
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import coil3.ImageLoader
import coil3.asDrawable
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.transformations
import com.google.android.material.button.MaterialButton
import com.google.android.material.shape.CornerFamily
import com.google.android.material.textview.MaterialTextView
import ir.ayantech.hamrahads.R
import ir.ayantech.hamrahads.di.NetworkModule
import ir.ayantech.hamrahads.listener.HamrahAdsInitListener
import ir.ayantech.hamrahads.network.model.NetworkError
import ir.ayantech.hamrahads.network.model.NetworkInterstitialAd
import ir.ayantech.hamrahads.repository.InterstitialAdsRepository
import ir.ayantech.hamrahads.utils.UnitUtils
import ir.ayantech.hamrahads.utils.handleIntent
import ir.ayantech.hamrahads.utils.preferenceDataStore.PreferenceDataStoreConstants
import ir.ayantech.hamrahads.utils.preferenceDataStore.PreferenceDataStoreHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ShowInterstitialAds constructor(
    private val activity: Activity,
    private val listener: HamrahAdsInitListener
) {
    private lateinit var container: FrameLayout
    private lateinit var countdownTextView: TextView
    private lateinit var closeTextView: MaterialTextView
    private lateinit var backgroundImageView: ImageView
    private lateinit var indexImageView: ImageView
    private lateinit var iconImageView: ImageView
    private lateinit var iconTitleTextView: TextView
    private lateinit var titleTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var installButton: MaterialButton
    private lateinit var urlWebView: WebView
    private lateinit var countDownTimerSkip: CountDownTimer
    private lateinit var countDownTimerOut: CountDownTimer

    private var imageLoaderCount = 0

    init {
        val interstitial =
            PreferenceDataStoreHelper(activity.applicationContext).getPreferenceInterstitial(
                PreferenceDataStoreConstants.HamrahAdsInterstitial,
                null
            )
        if (interstitial?.interstitialTemplate != null) {
            when (interstitial.interstitialTemplate) {
                0 -> initView1(interstitial)
                1 -> initView2(interstitial)
                3 -> initView3(interstitial)
            }
        } else {
            listener.onError(NetworkError().getError(6))
        }
    }

    private fun initView3(interstitial: NetworkInterstitialAd) {
        if (interstitial.webTemplateUrl.isNullOrEmpty()) {
            listener.onError(NetworkError().getError(6))
            return
        }
        imageLoaderCount = 0

        val screenSize = UnitUtils.getScreenSize(activity)
        container = FrameLayout(activity).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.WHITE)
        }
        urlWebView = WebView(activity.applicationContext).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            ).apply {
                gravity = Gravity.CENTER
            }
            settings.javaScriptEnabled = true

            webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    loadContainer(interstitial)
                }
            }
        }
        interstitial.webTemplateUrl?.let { urlWebView.loadUrl(it) }

        countdownTextView = TextView(activity).apply {
            layoutParams = FrameLayout.LayoutParams(
                ((screenSize[1] * 0.07)).toInt(),
                ((screenSize[1] * 0.07)).toInt()
            ).apply {
                gravity = Gravity.TOP or Gravity.END
                topMargin = 16
                rightMargin = 16
            }
            typeface = ResourcesCompat.getFont(activity.applicationContext, R.font.regular)
            textSize = UnitUtils.pxToDp(50f, activity.applicationContext)
            setTextColor(Color.DKGRAY)
            gravity = Gravity.CENTER
        }

        closeTextView = MaterialTextView(activity).apply {
            layoutParams = FrameLayout.LayoutParams(
                ((screenSize[1] * 0.07)).toInt(),
                ((screenSize[1] * 0.07)).toInt()
            ).apply {
                gravity = Gravity.TOP or Gravity.END
                topMargin = 16
                rightMargin = 16
            }
            text = resources.getText(R.string.hamrah_ads_font_close)
            typeface = ResourcesCompat.getFont(activity.applicationContext, R.font.icon)
            setTextColor(Color.DKGRAY)
            textSize = UnitUtils.pxToDp(60f, activity.applicationContext)
            visibility = View.GONE
            gravity = Gravity.CENTER
            setOnClickListener {
                finishAds()
            }
        }
    }

    private fun initView2(interstitial: NetworkInterstitialAd) {
        if (interstitial.interstitialBanner.isNullOrEmpty()
            || interstitial.landingType == null
            || interstitial.landingLink.isNullOrEmpty()
            || interstitial.logo.isNullOrEmpty()
            || interstitial.trackers?.click.isNullOrEmpty()
            || interstitial.trackers?.impression.isNullOrEmpty()
        ) {
            listener.onError(NetworkError().getError(6))
            return
        }

        imageLoaderCount = 1

        val screenSize = UnitUtils.getScreenSize(activity)
        container = FrameLayout(activity).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.WHITE)
        }

        backgroundImageView = ImageView(activity).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                (screenSize[0] * 0.3).toInt()
            ).apply {
                gravity = Gravity.TOP
            }
            scaleType = ImageView.ScaleType.CENTER_CROP
        }

        titleTextView = TextView(activity).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.TOP
                topMargin = (screenSize[0] * 0.32).toInt()
                rightMargin = 32
                leftMargin = 32
                textSize = UnitUtils.pxToDp(50f, activity.applicationContext)
                setTextColor(Color.BLACK)
                typeface = ResourcesCompat.getFont(activity.applicationContext, R.font.medium)
                text = interstitial.caption
            }
            gravity = Gravity.CENTER
        }

        descriptionTextView = TextView(activity).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.TOP
                topMargin = (screenSize[0] * 0.36).toInt()
                textSize = UnitUtils.pxToDp(40f, activity.applicationContext)
                rightMargin = 32
                leftMargin = 32
                setTextColor(Color.BLACK)
                typeface = ResourcesCompat.getFont(activity.applicationContext, R.font.regular)
                text = interstitial.description
            }
            gravity = Gravity.CENTER
        }

        iconImageView = ImageView(activity).apply {
            layoutParams = FrameLayout.LayoutParams(
                ((screenSize[1] * 0.2)).toInt(),
                (screenSize[1] * 0.2).toInt()
            ).apply {
                gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                bottomMargin = (screenSize[0] * 0.3).toInt()
            }
            scaleType = ImageView.ScaleType.FIT_XY
        }

        iconTitleTextView = TextView(activity).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.BOTTOM
                setTextColor(Color.BLACK)
                rightMargin = 32
                leftMargin = 32
                bottomMargin = (screenSize[0] * 0.25).toInt()
                textSize = UnitUtils.pxToDp(50f, activity.applicationContext)
                typeface = ResourcesCompat.getFont(activity.applicationContext, R.font.regular)
                text = interstitial.interstitialLabel
            }
            gravity = Gravity.CENTER
        }

        installButton = MaterialButton(activity).apply {
            layoutParams = FrameLayout.LayoutParams(
                ((screenSize[1] * 0.6)).toInt(),
                (screenSize[1] * 0.15).toInt()
            ).apply {
                shapeAppearanceModel = shapeAppearanceModel.toBuilder()
                    .setAllCorners(CornerFamily.ROUNDED, (screenSize[1] * 0.075).toFloat())
                    .build()
                gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                bottomMargin = (screenSize[0] * 0.15).toInt()
                setBackgroundColor(Color.BLUE)
                setTextColor(Color.WHITE)
                textSize = UnitUtils.pxToDp(50f, activity.applicationContext)
                typeface = ResourcesCompat.getFont(activity.applicationContext, R.font.medium)
                text = interstitial.cta
                setOnClickListener {
                    CoroutineScope(Dispatchers.IO).launch {
                        interstitial.trackers?.click?.let {
                            InterstitialAdsRepository(NetworkModule(activity.applicationContext)).click(
                                it
                            )
                        }
                    }
                    handleIntent(
                        context.applicationContext,
                        interstitial.landingType,
                        interstitial.landingLink
                    )
                }
            }
            gravity = Gravity.CENTER
        }

        countdownTextView = TextView(activity).apply {
            layoutParams = FrameLayout.LayoutParams(
                ((screenSize[1] * 0.07)).toInt(),
                ((screenSize[1] * 0.07)).toInt()
            ).apply {
                gravity = Gravity.TOP or Gravity.END
                topMargin = 16
                rightMargin = 16
            }
            typeface = ResourcesCompat.getFont(activity.applicationContext, R.font.regular)
            textSize = UnitUtils.pxToDp(50f, activity.applicationContext)
            setTextColor(Color.DKGRAY)
            gravity = Gravity.CENTER
        }

        closeTextView = MaterialTextView(activity).apply {
            layoutParams = FrameLayout.LayoutParams(
                ((screenSize[1] * 0.07)).toInt(),
                ((screenSize[1] * 0.07)).toInt()
            ).apply {
                gravity = Gravity.TOP or Gravity.END
                topMargin = 16
                rightMargin = 16
            }
            text = resources.getText(R.string.hamrah_ads_font_close)
            typeface = ResourcesCompat.getFont(activity.applicationContext, R.font.icon)
            setTextColor(Color.DKGRAY)
            textSize = UnitUtils.pxToDp(60f, activity.applicationContext)
            visibility = View.GONE
            gravity = Gravity.CENTER
            setOnClickListener {
                finishAds()
            }
        }

        val imageLoader = ImageLoader.Builder(activity.applicationContext)
            .build()
        imageLoader.enqueue(ImageRequest.Builder(activity.applicationContext)
            .data(interstitial.interstitialBanner)
            .target(
                onStart = { placeholder ->
                },
                onSuccess = { result ->
                    backgroundImageView.setImageDrawable(result.asDrawable(Resources.getSystem()))
                    loadContainer(interstitial)
                },
                onError = { error ->
                    listener.onError(NetworkError().getError(5))
                }
            )
            .memoryCachePolicy(CachePolicy.DISABLED)
            .diskCachePolicy(CachePolicy.DISABLED)
            .build())

        imageLoader.enqueue(ImageRequest.Builder(activity.applicationContext)
            .data(interstitial.logo)
            .target(
                onStart = { placeholder ->
                },
                onSuccess = { result ->
                    iconImageView.setImageDrawable(result.asDrawable(Resources.getSystem()))
                    loadContainer(interstitial)
                },
                onError = { error ->
                    listener.onError(NetworkError().getError(5))
                }
            )
            .memoryCachePolicy(CachePolicy.DISABLED)
            .diskCachePolicy(CachePolicy.DISABLED)
            .build())
    }

    private fun initView1(interstitial: NetworkInterstitialAd) {
        if (interstitial.interstitialBanner.isNullOrEmpty()
            || interstitial.landingType == null
            || interstitial.landingLink.isNullOrEmpty()
            || interstitial.logo.isNullOrEmpty()
            || interstitial.trackers?.click.isNullOrEmpty()
            || interstitial.trackers?.impression.isNullOrEmpty()
        ) {
            listener.onError(NetworkError().getError(6))
            return
        }

        imageLoaderCount = 2
        val screenSize = UnitUtils.getScreenSize(activity)
        container = FrameLayout(activity).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(Color.WHITE)
        }

        backgroundImageView = ImageView(activity).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                (screenSize[0] * 0.4).toInt()
            ).apply {
                gravity = Gravity.TOP
            }
            scaleType = ImageView.ScaleType.CENTER_CROP
        }

        indexImageView = ImageView(activity).apply {
            layoutParams = FrameLayout.LayoutParams(
                (screenSize[1] - (screenSize[1] * 0.1)).toInt(),
                (screenSize[0] * 0.2).toInt()
            ).apply {
                gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                topMargin = (screenSize[0] * 0.1).toInt()
            }
            scaleType = ImageView.ScaleType.FIT_XY
        }

        iconImageView = ImageView(activity).apply {
            layoutParams = FrameLayout.LayoutParams(
                ((screenSize[1] * 0.2)).toInt(),
                (screenSize[1] * 0.2).toInt()
            ).apply {
                gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                topMargin = (screenSize[0] * 0.35).toInt()
            }
            scaleType = ImageView.ScaleType.FIT_XY
        }

        iconTitleTextView = TextView(activity).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.TOP
                setTextColor(Color.BLACK)
                rightMargin = 32
                leftMargin = 32
                topMargin = (screenSize[0] * 0.46).toInt()
                textSize = UnitUtils.pxToDp(50f, activity.applicationContext)
                typeface = ResourcesCompat.getFont(activity.applicationContext, R.font.regular)
                text = interstitial.interstitialLabel
            }
            gravity = Gravity.CENTER
        }

        installButton = MaterialButton(activity).apply {
            layoutParams = FrameLayout.LayoutParams(
                ((screenSize[1] * 0.6)).toInt(),
                (screenSize[1] * 0.15).toInt()
            ).apply {
                shapeAppearanceModel = shapeAppearanceModel.toBuilder()
                    .setAllCorners(CornerFamily.ROUNDED, (screenSize[1] * 0.075).toFloat())
                    .build()
                gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                bottomMargin = (screenSize[0] * 0.1).toInt()
                setBackgroundColor(Color.BLUE)
                setTextColor(Color.WHITE)
                textSize = UnitUtils.pxToDp(50f, activity.applicationContext)
                typeface = ResourcesCompat.getFont(activity.applicationContext, R.font.regular)
                text = interstitial.cta
                setOnClickListener {
                    CoroutineScope(Dispatchers.IO).launch {
                        interstitial.trackers?.click?.let {
                            InterstitialAdsRepository(NetworkModule(activity.applicationContext)).click(
                                it
                            )
                        }
                    }
                    handleIntent(
                        context.applicationContext,
                        interstitial.landingType,
                        interstitial.landingLink
                    )
                }
            }
            gravity = Gravity.CENTER
        }

        countdownTextView = TextView(activity).apply {
            layoutParams = FrameLayout.LayoutParams(
                ((screenSize[1] * 0.07)).toInt(),
                ((screenSize[1] * 0.07)).toInt()
            ).apply {
                gravity = Gravity.TOP or Gravity.END
                topMargin = 16
                rightMargin = 16
            }
            typeface = ResourcesCompat.getFont(activity.applicationContext, R.font.regular)
            textSize = UnitUtils.pxToDp(50f, activity.applicationContext)
            setTextColor(Color.DKGRAY)
            gravity = Gravity.CENTER
        }

        closeTextView = MaterialTextView(activity).apply {
            layoutParams = FrameLayout.LayoutParams(
                ((screenSize[1] * 0.07)).toInt(),
                ((screenSize[1] * 0.07)).toInt()
            ).apply {
                gravity = Gravity.TOP or Gravity.END
                topMargin = 16
                rightMargin = 16
            }
            text = resources.getText(R.string.hamrah_ads_font_close)
            typeface = ResourcesCompat.getFont(activity.applicationContext, R.font.icon)
            setTextColor(Color.DKGRAY)
            textSize = UnitUtils.pxToDp(60f, activity.applicationContext)
            visibility = View.GONE
            gravity = Gravity.CENTER
            setOnClickListener {
                finishAds()
            }
        }

        val imageLoader = ImageLoader.Builder(activity.applicationContext)
            .build()
        imageLoader.enqueue(ImageRequest.Builder(activity.applicationContext)
            .data(interstitial.interstitialBanner)
            .transformations(
                listOf(
                    BlurTransformation(radius = 25, scale = 0.5f)
                )
            )
            .target(
                onStart = { placeholder ->
                },
                onSuccess = { result ->
                    backgroundImageView.setImageDrawable(result.asDrawable(Resources.getSystem()))
                    loadContainer(interstitial)
                },
                onError = { error ->
                    listener.onError(NetworkError().getError(5))
                }
            )
            .memoryCachePolicy(CachePolicy.DISABLED)
            .diskCachePolicy(CachePolicy.DISABLED)
            .build())

        imageLoader.enqueue(ImageRequest.Builder(activity.applicationContext)
            .data(interstitial.interstitialBanner)
            .target(
                onStart = { placeholder ->
                },
                onSuccess = { result ->
                    indexImageView.setImageDrawable(result.asDrawable(Resources.getSystem()))
                    loadContainer(interstitial)
                },
                onError = { error ->
                    listener.onError(NetworkError().getError(5))
                }
            )
            .memoryCachePolicy(CachePolicy.DISABLED)
            .diskCachePolicy(CachePolicy.DISABLED)
            .build())

        imageLoader.enqueue(ImageRequest.Builder(activity.applicationContext)
            .data(interstitial.logo)
            .target(
                onStart = { placeholder ->
                },
                onSuccess = { result ->
                    iconImageView.setImageDrawable(result.asDrawable(Resources.getSystem()))
                    loadContainer(interstitial)
                },
                onError = { error ->
                    listener.onError(NetworkError().getError(5))
                }
            )
            .memoryCachePolicy(CachePolicy.DISABLED)
            .diskCachePolicy(CachePolicy.DISABLED)
            .build())
    }

    private fun loadContainer(interstitial: NetworkInterstitialAd) {
        if (imageLoaderCount != 0) {
            imageLoaderCount--
            return
        }

        if (::backgroundImageView.isInitialized)
            container.addView(backgroundImageView)

        if (::indexImageView.isInitialized)
            container.addView(indexImageView)

        if (::iconImageView.isInitialized)
            container.addView(iconImageView)

        if (::titleTextView.isInitialized)
            container.addView(titleTextView)

        if (::iconTitleTextView.isInitialized)
            container.addView(iconTitleTextView)

        if (::descriptionTextView.isInitialized)
            container.addView(descriptionTextView)

        if (::installButton.isInitialized)
            container.addView(installButton)

        if (::urlWebView.isInitialized)
            container.addView(urlWebView)

        if (::closeTextView.isInitialized)
            container.addView(closeTextView)

        if (::countdownTextView.isInitialized)
            container.addView(countdownTextView)

        interstitial.timeToSkip?.let {
            timeToSkip(it)
        } ?: {
            if (::countdownTextView.isInitialized)
                countdownTextView.visibility = View.GONE

            if (::closeTextView.isInitialized)
                closeTextView.visibility = View.VISIBLE
        }
        interstitial.timeOut?.let { timeToOut(it) }

        (activity.findViewById<View>(android.R.id.content) as ViewGroup).addView(
            container
        )
        CoroutineScope(Dispatchers.IO).launch {
            interstitial.trackers?.impression?.let {
                InterstitialAdsRepository(NetworkModule(activity.applicationContext)).impression(it)
            }

            PreferenceDataStoreHelper(activity.applicationContext).removePreferenceCoroutine(
                PreferenceDataStoreConstants.HamrahAdsInterstitial
            )
        }
    }

    private fun timeToSkip(seconds: Int) {
        if (seconds == 0) return
        countDownTimerSkip = object : CountDownTimer(seconds * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val remainingTime = millisUntilFinished / 1000

                if (::countdownTextView.isInitialized)
                    countdownTextView.text = remainingTime.toString()
            }

            override fun onFinish() {
                if (::countdownTextView.isInitialized)
                    countdownTextView.visibility = View.GONE

                if (::closeTextView.isInitialized)
                    closeTextView.visibility = View.VISIBLE
            }
        }
        countDownTimerSkip.start()
    }

    private fun timeToOut(seconds: Int) {
        if (seconds == 0) return
        countDownTimerOut = object : CountDownTimer(seconds * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
            }

            override fun onFinish() {
                finishAds()
            }
        }
        countDownTimerOut.start()
    }

    private fun finishAds() {
        if (::urlWebView.isInitialized)
            urlWebView.destroy()

        if (::countDownTimerOut.isInitialized)
            countDownTimerOut.cancel()

        if (::countDownTimerSkip.isInitialized)
            countDownTimerSkip.cancel()

        container.removeAllViews()
    }
}