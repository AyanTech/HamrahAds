package ir.ayantech.hamrahads.core

import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.CountDownTimer
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebView
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
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
import ir.ayantech.hamrahads.utils.BlurTransformation
import ir.ayantech.hamrahads.utils.handleIntent
import ir.ayantech.hamrahads.utils.imageLoader
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
    private lateinit var webUrlTextView: TextView
    private lateinit var titleTextView: TextView
    private lateinit var descriptionTextView: TextView
    private lateinit var installCardView: CardView
    private lateinit var urlWebView: WebView
    private lateinit var countDownTimerSkip: CountDownTimer
    private lateinit var countDownTimerOut: CountDownTimer
    private lateinit var resources: Resources

    private lateinit var dialog: Dialog
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
            resources = activity.applicationContext.resources
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
        if (interstitial.interstitialBanner.isNullOrEmpty()
            || interstitial.landingType == null
            || interstitial.caption.isNullOrEmpty()
            || interstitial.cta.isNullOrEmpty()
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
                FrameLayout.LayoutParams.WRAP_CONTENT,
            ).apply {
                gravity = Gravity.TOP
                topMargin = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._150sdp)
            }
            scaleType = ImageView.ScaleType.FIT_XY
            adjustViewBounds = true
        }

        iconImageView = ImageView(activity).apply {
            layoutParams = FrameLayout.LayoutParams(
                resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._80sdp),
                resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._80sdp)
            ).apply {
                gravity = Gravity.RIGHT
                rightMargin = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._15sdp)
                topMargin = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._55sdp)
            }
            scaleType = ImageView.ScaleType.FIT_XY
        }

        iconTitleTextView = TextView(activity).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setTextColor(Color.BLACK)
                gravity = Gravity.RIGHT
                rightMargin = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._110sdp)
                leftMargin = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._15sdp)
                topMargin = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._85sdp)
                textSize = resources.getDimension(com.intuit.sdp.R.dimen._6sdp)
                typeface = ResourcesCompat.getFont(activity.applicationContext, R.font.regular)
                text = interstitial.interstitialLabel
            }
            gravity = Gravity.CENTER
        }

        webUrlTextView = TextView(activity).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                setTextColor(Color.GRAY)
                gravity = Gravity.RIGHT
                rightMargin = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._110sdp)
                leftMargin = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._15sdp)
                topMargin = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._110sdp)
                textSize = resources.getDimension(com.intuit.sdp.R.dimen._4sdp)
                typeface = ResourcesCompat.getFont(activity.applicationContext, R.font.regular)
                text = interstitial.webTemplateUrl
            }
            gravity = Gravity.CENTER
        }

        titleTextView = TextView(activity).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.BOTTOM
                bottomMargin = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._175sdp)
                rightMargin = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._15sdp)
                leftMargin = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._15sdp)
                textSize = resources.getDimension(com.intuit.sdp.R.dimen._6sdp)
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
                gravity = Gravity.BOTTOM
                bottomMargin = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._155sdp)
                textSize = resources.getDimension(com.intuit.sdp.R.dimen._4sdp)
                rightMargin = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._15sdp)
                leftMargin = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._15sdp)
                setTextColor(Color.BLACK)
                typeface = ResourcesCompat.getFont(activity.applicationContext, R.font.regular)
                text = interstitial.description
            }
            gravity = Gravity.CENTER
        }

        button(interstitial)

        val imageLoader = imageLoader(activity.applicationContext)

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
                },
                onError = { error ->
//                    destroyAds()
//                    listener.onError(NetworkError().getError(5))
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
                },
                onError = { error ->
//                    destroyAds()
//                    listener.onError(NetworkError().getError(5))
                }
            )
            .memoryCachePolicy(CachePolicy.DISABLED)
            .diskCachePolicy(CachePolicy.DISABLED)
            .build())

        loadContainer(interstitial)
    }

    private fun initView2(interstitial: NetworkInterstitialAd) {
        if (interstitial.interstitialBanner.isNullOrEmpty()
            || interstitial.landingType == null
            || interstitial.caption.isNullOrEmpty()
            || interstitial.cta.isNullOrEmpty()
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
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.TOP
            }
            scaleType = ImageView.ScaleType.FIT_XY
            adjustViewBounds = true
        }

        titleTextView = TextView(activity).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.TOP
                topMargin = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._195sdp)
                rightMargin = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._15sdp)
                leftMargin = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._15sdp)
                textSize = resources.getDimension(com.intuit.sdp.R.dimen._6sdp)
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
                topMargin = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._225sdp)
                textSize = resources.getDimension(com.intuit.sdp.R.dimen._4sdp)
                rightMargin = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._15sdp)
                leftMargin = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._15sdp)
                setTextColor(Color.BLACK)
                typeface = ResourcesCompat.getFont(activity.applicationContext, R.font.regular)
                text = interstitial.description
            }
            gravity = Gravity.CENTER
        }

        iconImageView = ImageView(activity).apply {
            layoutParams = FrameLayout.LayoutParams(
                resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._70sdp),
                resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._70sdp)
            ).apply {
                gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
                bottomMargin = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._190sdp)
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
                rightMargin = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._15sdp)
                leftMargin = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._15sdp)
                bottomMargin = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._150sdp)
                textSize = resources.getDimension(com.intuit.sdp.R.dimen._6sdp)
                typeface = ResourcesCompat.getFont(activity.applicationContext, R.font.regular)
                text = interstitial.interstitialLabel
            }
            gravity = Gravity.CENTER
        }
        button(interstitial)

        val imageLoader = imageLoader(activity.applicationContext)

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
                },
                onError = { error ->
//                    destroyAds()
//                    listener.onError(NetworkError().getError(5))
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
                },
                onError = { error ->
//                    destroyAds()
//                    listener.onError(NetworkError().getError(5))
                }
            )
            .memoryCachePolicy(CachePolicy.DISABLED)
            .diskCachePolicy(CachePolicy.DISABLED)
            .build())

        loadContainer(interstitial)
    }

    private fun initView1(interstitial: NetworkInterstitialAd) {
        if (interstitial.interstitialBanner.isNullOrEmpty()
            || interstitial.landingType == null
            || interstitial.caption.isNullOrEmpty()
            || interstitial.cta.isNullOrEmpty()
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
                resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._250sdp)
            ).apply {
                gravity = Gravity.TOP
            }
            scaleType = ImageView.ScaleType.CENTER_CROP
        }

        indexImageView = ImageView(activity).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                scaleType = ImageView.ScaleType.FIT_CENTER
                leftMargin = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._25sdp)
                rightMargin = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._25sdp)
                topMargin = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._50sdp)
            }
            scaleType = ImageView.ScaleType.FIT_XY
            adjustViewBounds = true
        }

        iconImageView = ImageView(activity).apply {
            layoutParams = FrameLayout.LayoutParams(
                resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._70sdp),
                resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._70sdp)
            ).apply {
                gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
                topMargin = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._215sdp)
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
                rightMargin = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._15sdp)
                leftMargin = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._15sdp)
                topMargin = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._290sdp)
                textSize = resources.getDimension(com.intuit.sdp.R.dimen._6sdp)
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
                topMargin = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._320sdp)
                textSize = resources.getDimension(com.intuit.sdp.R.dimen._4sdp)
                rightMargin = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._15sdp)
                leftMargin = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._15sdp)
                setTextColor(Color.BLACK)
                typeface = ResourcesCompat.getFont(activity.applicationContext, R.font.regular)
                text = interstitial.description
            }
            gravity = Gravity.CENTER
        }

        button(interstitial)

        val imageLoader = imageLoader(activity.applicationContext)

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
                },
                onError = { error ->
//                    destroyAds()
//                    listener.onError(NetworkError().getError(5))
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
                },
                onError = { error ->
//                    destroyAds()
//                    listener.onError(NetworkError().getError(5))
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
                },
                onError = { error ->
//                    destroyAds()
//                    listener.onError(NetworkError().getError(5))
                }
            )
            .memoryCachePolicy(CachePolicy.DISABLED)
            .diskCachePolicy(CachePolicy.DISABLED)
            .build())

        loadContainer(interstitial)
    }

    private fun button(interstitial: NetworkInterstitialAd) {
        installCardView = CardView(activity).apply {
            layoutParams = FrameLayout.LayoutParams(
                resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._170sdp),
                resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._40sdp)
            ).apply {
                bottomMargin = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._80sdp)
                gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            }
            setCardBackgroundColor(ContextCompat.getColor(context, R.color.color_2))
            cardElevation = resources.getDimension(com.intuit.sdp.R.dimen._2sdp)
            radius = resources.getDimension(com.intuit.sdp.R.dimen._20sdp)

            val installButton = TextView(activity).apply {
                layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                ).apply {
                    setTextColor(Color.WHITE)
                    textSize = resources.getDimension(com.intuit.sdp.R.dimen._6sdp)
                    typeface = ResourcesCompat.getFont(activity.applicationContext, R.font.medium)
                    text = interstitial.cta
                    setOnClickListener {
                        listener.onClick()
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
                resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._22sdp)
            ).apply {
                topMargin = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp)
                rightMargin = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp)
                leftMargin = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._10sdp)
                gravity = Gravity.TOP or Gravity.END
            }
            setCardBackgroundColor(Color.WHITE)
            cardElevation = resources.getDimension(com.intuit.sdp.R.dimen._2sdp)
            radius = resources.getDimension(com.intuit.sdp.R.dimen._10sdp)

            val linear = LinearLayout(activity).apply {
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                ).apply {
                }
                gravity = Gravity.CENTER

                countdownTextView = TextView(activity).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._40sdp),
                        FrameLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        maxLines = 1
                    }
                    gravity = Gravity.CENTER
                    typeface = ResourcesCompat.getFont(activity.applicationContext, R.font.regular)
                    textSize = resources.getDimension(com.intuit.sdp.R.dimen._4sdp)
                    setTextColor(Color.BLACK)
                }

                val closeTextView = TextView(activity).apply {
                    layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.WRAP_CONTENT,
                        FrameLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        marginEnd = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._7sdp)
                    }
                    text = resources.getText(R.string.hamrah_ads_font_close)
                    typeface = ResourcesCompat.getFont(activity.applicationContext, R.font.icon)
                    setTextColor(Color.BLACK)
                    textSize = resources.getDimension(com.intuit.sdp.R.dimen._6sdp)

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

    private fun loadContainer(interstitial: NetworkInterstitialAd) {
//        if (imageLoaderCount != 0) {
//            imageLoaderCount--
//            return
//        }

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

            if (::webUrlTextView.isInitialized)
                container.addView(webUrlTextView)

            if (::descriptionTextView.isInitialized)
                container.addView(descriptionTextView)

            if (::installCardView.isInitialized) {
                container.addView(installCardView)
                startSwingAnimation(installCardView)
            }

            if (::countdownCardView.isInitialized)
                container.addView(countdownCardView)

        }
        dialog = Dialog(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen).apply {
            window?.apply {
                setBackgroundDrawable(ColorDrawable(Color.WHITE))
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN)
                addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

                @Suppress("DEPRECATION")
                decorView.systemUiVisibility = (
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        )

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    attributes.layoutInDisplayCutoutMode =
                        WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                }
            }
        }

        interstitial.timeToSkip?.let {
            timeToSkip(it)
        } ?: {
            countdownCardView.visibility = View.GONE
        }
        interstitial.timeOut?.let { timeToOut(it) }

        dialog.setContentView(container)
        dialog.show()

        ioScope.launch {
            interstitial.trackers?.impression?.let {
                InterstitialAdsRepository(NetworkModule(activity.applicationContext)).impression(it)
            }

            PreferenceDataStoreHelper(activity.applicationContext).removePreferenceCoroutine(
                PreferenceDataStoreConstants.HamrahAdsInterstitial
            )
        }
        listener.onSuccess()
    }

    private fun startSwingAnimation(view: View) {
        val animator = ObjectAnimator.ofFloat(view, "rotation", -5f, 5f)
        animator.duration = 300
        animator.repeatCount = 5
        animator.repeatMode = ObjectAnimator.REVERSE
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(p0: Animator) {
            }

            override fun onAnimationEnd(p0: Animator) {
                view.rotation = 0f
            }

            override fun onAnimationCancel(p0: Animator) {
            }

            override fun onAnimationRepeat(p0: Animator) {
            }
        })
        animator.start()
    }

    private fun timeToSkip(seconds: Int) {
        if (seconds == 0) {
            dialog.setCancelable(true)
            if (::countdownTextView.isInitialized)
                countdownTextView.text =
                    activity.applicationContext.getString(
                        R.string.hamrah_ads_end
                    )
            return
        }
        dialog.setCancelable(false)
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
                dialog.setCancelable(true)
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

        if (::dialog.isInitialized && dialog.isShowing) {
            dialog.dismiss()
        }
        if (::container.isInitialized)
            container.removeAllViews()

    }
}