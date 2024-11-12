package ir.ayantech.hamrahads.core

import BlurTransformation
import android.content.res.Resources
import android.graphics.Color
import android.net.http.SslError
import android.os.CountDownTimer
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.webkit.SslErrorHandler
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import coil3.ImageLoader
import coil3.asDrawable
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.target
import coil3.request.transformations
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
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch


class ShowInterstitialAds(
    private val activity: AppCompatActivity,
    private val listener: HamrahAdsInitListener
) {
    private lateinit var container: FrameLayout
    private lateinit var countdownCardView: CardView
    private lateinit var countdownTextView: TextView
    private lateinit var backgroundImageView: ImageView
    private lateinit var indexImageView: ImageView
    private lateinit var iconImageView: ImageView
    private lateinit var iconTitleTextView: TextView
    private lateinit var titleTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var installCardView: CardView
    private lateinit var urlWebView: WebView
    private lateinit var countDownTimerSkip: CountDownTimer
    private lateinit var countDownTimerOut: CountDownTimer

    private lateinit var allView: ViewGroup
    private var isBackPressed = true

    private var imageLoaderCount = 0

    private val job = SupervisorJob()
    private val ioScope = CoroutineScope(Dispatchers.IO + job)
    private val mainScope = CoroutineScope(Dispatchers.Main + job)

    init {
        val interstitial =
            PreferenceDataStoreHelper(activity.applicationContext).getPreferenceInterstitial(
                PreferenceDataStoreConstants.HamrahAdsInterstitial,
                null
            )
        if (interstitial?.interstitialTemplate != null) {
            when (interstitial.interstitialTemplate) {
                1 -> initView1(interstitial)
                2 -> initView2(interstitial)
                3 -> initView3(interstitial)
            }
        } else {
            listener.onError(NetworkError().getError(6))
        }
    }

    private fun initView3(interstitial: NetworkInterstitialAd) {
        if (interstitial.webTemplateUrl.isNullOrEmpty()) {
            destroyAds()
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
            setBackgroundColor(Color.RED)
        }
        container.setOnClickListener {
            return@setOnClickListener
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

                override fun onReceivedSslError(
                    view: WebView?,
                    handler: SslErrorHandler?,
                    error: SslError?
                ) {
                    super.onReceivedSslError(view, handler, error)
                    listener.onError(NetworkError().getError(7))
                }

                override fun onReceivedHttpError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    errorResponse: WebResourceResponse?
                ) {
                    super.onReceivedHttpError(view, request, errorResponse)
                    listener.onError(NetworkError().getError(7))
                }

                override fun onReceivedError(
                    view: WebView?,
                    request: WebResourceRequest?,
                    error: WebResourceError?
                ) {
                    super.onReceivedError(view, request, error)
                    listener.onError(NetworkError().getError(7))
                }
            }
        }
        interstitial.webTemplateUrl?.let {
            urlWebView.loadUrl(it)
        }

        countdownCardView = CardView(activity).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                ((screenSize[1] * 0.07)).toInt()
            ).apply {
                topMargin = 24
                rightMargin = 24
                gravity = Gravity.TOP or Gravity.END
            }
            setCardBackgroundColor(Color.WHITE)
            cardElevation = 6f
            radius = 50f

            val linear = LinearLayout(activity).apply {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                ).apply {
                }
                gravity = Gravity.CENTER

                countdownTextView = TextView(activity).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        ((screenSize[1] * 0.15)).toInt(),
                        FrameLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        maxLines = 1
                    }
                    gravity = Gravity.CENTER
                    typeface = ResourcesCompat.getFont(activity.applicationContext, R.font.regular)
                    textSize = UnitUtils.pxToDp(35f, activity.applicationContext)
                    setTextColor(Color.BLACK)
                }

                val closeTextView = TextView(activity).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        marginEnd = 20
                    }
                    text = resources.getText(R.string.hamrah_ads_font_close)
                    typeface = ResourcesCompat.getFont(activity.applicationContext, R.font.icon)
                    setTextColor(Color.BLACK)
                    textSize = UnitUtils.pxToDp(60f, activity.applicationContext)
                }
                addView(countdownTextView)
                addView(closeTextView)
            }
            addView(linear)
            setOnClickListener {
                if (isBackPressed) {
                    listener.onClose()
                    destroyAds()
                }
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
            destroyAds()
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
        container.setOnClickListener {
            return@setOnClickListener
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

        installCardView = CardView(activity).apply {
            layoutParams = FrameLayout.LayoutParams(
                ((screenSize[1] * 0.6)).toInt(),
                (screenSize[1] * 0.15).toInt()
            ).apply {
                bottomMargin = (screenSize[0] * 0.14).toInt()
                gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            }
            setCardBackgroundColor(ContextCompat.getColor(context, R.color.color_2))
            cardElevation = 12f
            radius = 100f

            val installButton = TextView(activity).apply {
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                ).apply {
                    setTextColor(Color.WHITE)
                    textSize = UnitUtils.pxToDp(50f, activity.applicationContext)
                    typeface = ResourcesCompat.getFont(activity.applicationContext, R.font.medium)
                    text = interstitial.cta
                    setOnClickListener {
                        ioScope.launch {
                            interstitial.trackers?.click?.let {
                                InterstitialAdsRepository(NetworkModule(activity.applicationContext)).click(
                                    it
                                )
                            }
                        }
                        handleIntent(
                            activity,
                            interstitial.landingType,
                            interstitial.landingLink
                        )
                    }
                }
                gravity = Gravity.CENTER
            }
            addView(installButton)
        }

        countdownCardView = CardView(activity).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                ((screenSize[1] * 0.07)).toInt()
            ).apply {
                topMargin = 24
                rightMargin = 24
                gravity = Gravity.TOP or Gravity.END
            }
            setCardBackgroundColor(Color.WHITE)
            cardElevation = 6f
            radius = 50f

            val linear = LinearLayout(activity).apply {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                ).apply {
                }
                gravity = Gravity.CENTER

                countdownTextView = TextView(activity).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        ((screenSize[1] * 0.15)).toInt(),
                        FrameLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        maxLines = 1
                    }
                    gravity = Gravity.CENTER
                    typeface = ResourcesCompat.getFont(activity.applicationContext, R.font.regular)
                    textSize = UnitUtils.pxToDp(35f, activity.applicationContext)
                    setTextColor(Color.BLACK)
                }

                val closeTextView = TextView(activity).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        marginEnd = 20
                    }
                    text = resources.getText(R.string.hamrah_ads_font_close)
                    typeface = ResourcesCompat.getFont(activity.applicationContext, R.font.icon)
                    setTextColor(Color.BLACK)
                    textSize = UnitUtils.pxToDp(60f, activity.applicationContext)

                }
                addView(countdownTextView)
                addView(closeTextView)
            }
            addView(linear)
            setOnClickListener {
                if (isBackPressed) {
                    listener.onClose()
                    destroyAds()
                }
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
                    imageLoader.enqueue(
                        ImageRequest.Builder(activity.applicationContext)
                            .target(backgroundImageView)
                            .data(result.asDrawable(Resources.getSystem()))
                            .build()
                    )
                    loadContainer(interstitial)
                },
                onError = { error ->
                    destroyAds()
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
                    imageLoader.enqueue(
                        ImageRequest.Builder(activity.applicationContext)
                            .target(iconImageView)
                            .data(result.asDrawable(Resources.getSystem()))
                            .build()
                    )
                    loadContainer(interstitial)
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

    private fun initView1(interstitial: NetworkInterstitialAd) {
        if (interstitial.interstitialBanner.isNullOrEmpty()
            || interstitial.landingType == null
            || interstitial.landingLink.isNullOrEmpty()
            || interstitial.logo.isNullOrEmpty()
            || interstitial.trackers?.click.isNullOrEmpty()
            || interstitial.trackers?.impression.isNullOrEmpty()
        ) {
            destroyAds()
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
        container.setOnClickListener {
            return@setOnClickListener
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
                scaleType = ImageView.ScaleType.FIT_CENTER
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

        descriptionTextView = TextView(activity).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.TOP
                topMargin = (screenSize[0] * 0.52).toInt()
                textSize = UnitUtils.pxToDp(40f, activity.applicationContext)
                rightMargin = 32
                leftMargin = 32
                setTextColor(Color.BLACK)
                typeface = ResourcesCompat.getFont(activity.applicationContext, R.font.regular)
                text = interstitial.description
            }
            gravity = Gravity.CENTER
        }

        installCardView = CardView(activity).apply {
            layoutParams = FrameLayout.LayoutParams(
                ((screenSize[1] * 0.6)).toInt(),
                (screenSize[1] * 0.15).toInt()
            ).apply {
                bottomMargin = (screenSize[0] * 0.1).toInt()
                gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            }
            setCardBackgroundColor(ContextCompat.getColor(context, R.color.color_2))
            cardElevation = 12f
            radius = 100f

            val installButton = TextView(activity).apply {
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                ).apply {
                    setTextColor(Color.WHITE)
                    textSize = UnitUtils.pxToDp(50f, activity.applicationContext)
                    typeface = ResourcesCompat.getFont(activity.applicationContext, R.font.medium)
                    text = interstitial.cta
                    setOnClickListener {
                        ioScope.launch {
                            interstitial.trackers?.click?.let {
                                InterstitialAdsRepository(NetworkModule(activity.applicationContext)).click(
                                    it
                                )
                            }
                        }
                        handleIntent(
                            activity,
                            interstitial.landingType,
                            interstitial.landingLink
                        )
                    }
                }
                gravity = Gravity.CENTER
            }
            addView(installButton)
        }

        countdownCardView = CardView(activity).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                ((screenSize[1] * 0.07)).toInt()
            ).apply {
                topMargin = 24
                rightMargin = 24
                gravity = Gravity.TOP or Gravity.END
            }
            setCardBackgroundColor(Color.WHITE)
            cardElevation = 6f
            radius = 50f

            val linear = LinearLayout(activity).apply {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                ).apply {
                }
                gravity = Gravity.CENTER

                countdownTextView = TextView(activity).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        ((screenSize[1] * 0.15)).toInt(),
                        FrameLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        maxLines = 1
                    }
                    gravity = Gravity.CENTER
                    typeface = ResourcesCompat.getFont(activity.applicationContext, R.font.regular)
                    textSize = UnitUtils.pxToDp(35f, activity.applicationContext)
                    setTextColor(Color.BLACK)
                }

                val closeTextView = TextView(activity).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        marginEnd = 20
                    }
                    text = resources.getText(R.string.hamrah_ads_font_close)
                    typeface = ResourcesCompat.getFont(activity.applicationContext, R.font.icon)
                    setTextColor(Color.BLACK)
                    textSize = UnitUtils.pxToDp(60f, activity.applicationContext)
                }
                addView(countdownTextView)
                addView(closeTextView)
            }
            addView(linear)
            setOnClickListener {
                if (isBackPressed) {
                    listener.onClose()
                    destroyAds()
                }
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
                    imageLoader.enqueue(
                        ImageRequest.Builder(activity.applicationContext)
                            .target(backgroundImageView)
                            .data(result.asDrawable(Resources.getSystem()))
                            .build()
                    )
                    loadContainer(interstitial)
                },
                onError = { error ->
                    destroyAds()
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
                    imageLoader.enqueue(
                        ImageRequest.Builder(activity.applicationContext)
                            .target(indexImageView)
                            .data(result.asDrawable(Resources.getSystem()))
                            .build()
                    )
                    loadContainer(interstitial)
                },
                onError = { error ->
                    destroyAds()
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
                    imageLoader.enqueue(
                        ImageRequest.Builder(activity.applicationContext)
                            .target(iconImageView)
                            .data(result.asDrawable(Resources.getSystem()))
                            .build()
                    )
                    loadContainer(interstitial)
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

    private fun loadContainer(interstitial: NetworkInterstitialAd) {
        if (imageLoaderCount != 0) {
            imageLoaderCount--
            return
        }

        mainScope.launch {
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

            if (::installCardView.isInitialized) {
                container.addView(installCardView)
            }
            if (::urlWebView.isInitialized)
                container.addView(urlWebView)

            if (::countdownCardView.isInitialized)
                container.addView(countdownCardView)

            allView = (activity.findViewById<View>(android.R.id.content) as ViewGroup)
            allView.addView(container)
        }

        interstitial.timeToSkip?.let {
            timeToSkip(it)
        } ?: {
            countdownCardView.visibility = View.GONE
        }
        interstitial.timeOut?.let { timeToOut(it) }

        ioScope.launch {
            interstitial.trackers?.impression?.let {
                InterstitialAdsRepository(NetworkModule(activity.applicationContext)).impression(it)
            }

            PreferenceDataStoreHelper(activity.applicationContext).removePreferenceCoroutine(
                PreferenceDataStoreConstants.HamrahAdsInterstitial
            )
        }

        activity.onBackPressedDispatcher.addCallback(
            activity,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (isBackPressed) {
                        destroyAds()
                        remove()
                    }
                }
            })

        listener.onSuccess()
    }

    private fun timeToSkip(seconds: Int) {
        if (seconds == 0) return
        isBackPressed = false
        countDownTimerSkip = object : CountDownTimer(seconds * 1000L, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val remainingTime = millisUntilFinished / 1000
                if (::countdownTextView.isInitialized)
                    countdownTextView.text =
                        activity.applicationContext.getString(
                            R.string.hamrah_ads_second,
                            remainingTime.toString()
                        )
            }

            override fun onFinish() {
                isBackPressed = true
                if (::countdownTextView.isInitialized)
                    countdownTextView.text =
                        activity.applicationContext.getString(
                            R.string.hamrah_ads_end
                        )
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
                listener.onClose()
                destroyAds()
            }
        }
        countDownTimerOut.start()
    }

    fun destroyAds() {
        job.cancel()

        if (::urlWebView.isInitialized)
            urlWebView.destroy()

        if (::countDownTimerOut.isInitialized)
            countDownTimerOut.cancel()

        if (::countDownTimerSkip.isInitialized)
            countDownTimerSkip.cancel()

        if (::allView.isInitialized && container.parent != null) {
            allView.removeView(container)
        }
        if (::container.isInitialized)
            container.removeAllViews()

    }
}