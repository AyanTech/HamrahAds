package ir.ayantech.hamrahads.ads.interstitial

import ir.ayantech.hamrahads.model.error.AdError
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import ir.ayantech.hamrahads.R
import ir.ayantech.hamrahads.di.AdDependencies
import ir.ayantech.hamrahads.domain.model.InterstitialAd
import ir.ayantech.hamrahads.domain.model.isDisplayable
import ir.ayantech.hamrahads.internal.presentation.AdImages
import ir.ayantech.hamrahads.internal.presentation.AdTracking
import ir.ayantech.hamrahads.internal.presentation.AdViewSession
import ir.ayantech.hamrahads.internal.presentation.interstitial.InterstitialControls
import ir.ayantech.hamrahads.internal.presentation.interstitial.InterstitialTimers
import ir.ayantech.hamrahads.internal.presentation.interstitial.createTemplate1
import ir.ayantech.hamrahads.internal.presentation.interstitial.createTemplate2
import ir.ayantech.hamrahads.internal.presentation.interstitial.createTemplate3
import ir.ayantech.hamrahads.internal.util.handleIntent
import ir.ayantech.hamrahads.listener.AdDisplayListener

@MainThread
class InterstitialAdView(
    firstActivity: AppCompatActivity,
    private val zoneId: String,
    private val listener: AdDisplayListener,
) {
    private val session = AdViewSession(firstActivity, listener)
    private val dependencies = AdDependencies.get(firstActivity)
    private val images = AdImages(session)
    private val tracking = AdTracking(session.tasks, dependencies.trackClick, dependencies.trackImpression,
        { dependencies.cache.remove(zoneId) }, listener)

    init {
        session.tasks.launch("showInterstitial") {
            val activity = session.activity()
            if (zoneId.isBlank() || activity == null) {
                session.fail(AdError.INVALID_REQUEST)
                return@launch
            }
            val ad = dependencies.cache.getInterstitial(zoneId)
            if (ad == null || !ad.isDisplayable()) {
                session.fail(AdError.AD_UNAVAILABLE)
                return@launch
            }
            render(activity, ad)
        }
    }

    private fun render(activity: AppCompatActivity, ad: InterstitialAd) {
        val content = when (ad.interstitialTemplate) {
            1 -> createTemplate1(activity, ad)
            2 -> createTemplate2(activity, ad)
            3 -> createTemplate3(activity, ad)
            else -> {
                session.fail(AdError.AD_UNAVAILABLE)
                return
            }
        }
        val dialog = createDialog(activity)
        val timers = InterstitialTimers()
        var canClose = (ad.timeToSkip ?: 0) <= 0
        fun close() {
            if (!session.isActive) {
                return
            }
            session.dispose()
            listener.onClose()
        }
        val controls = InterstitialControls(activity, ad.cta,
            onClick = {
                if (session.isActive) {
                    tracking.click(ad.trackers?.click)
                    handleIntent(activity, ad.landingType, ad.landingLink)
                }
            },
            onClose = {
                if (canClose) {
                    close()
                }
            },
        )
        content.root.addView(controls.install)
        content.root.addView(controls.close)
        dialog.setContentView(content.root)
        dialog.setCancelable(canClose)
        dialog.setOnCancelListener { close() }
        session.onDispose {
            timers.dispose()
            dialog.setOnCancelListener(null)
            dialog.dismiss()
            content.root.removeAllViews()
        }
        // Never announce or track an empty dialog while its required images are still downloading.
        var remainingImages = content.images.size
        content.images.forEach { image ->
            images.load(image.url, image.view, image.transformations) {
                remainingImages--
                if (remainingImages == 0 && session.activity() != null) {
                    dialog.show()
                    controls.countdown.text = activity.getString(R.string.hamrah_ads_end)
                    if (ad.timeToSkip == null) {
                        controls.close.visibility = View.GONE
                    }
                    timers.start(ad.timeToSkip,
                        onTick = { controls.countdown.text = activity.getString(R.string.hamrah_ads_second, it.toString()) },
                        onFinish = {
                            canClose = true
                            dialog.setCancelable(true)
                            controls.countdown.text = activity.getString(R.string.hamrah_ads_end)
                        },
                    )
                    timers.start(ad.timeOut, onFinish = ::close)
                    animateButton(controls.install)
                    listener.onLoaded()
                    if (session.isActive) {
                        tracking.impression(ad.trackers?.impression)
                    }
                }
            }
        }
    }

    private fun createDialog(activity: AppCompatActivity): Dialog =
        Dialog(activity, android.R.style.Theme_Black_NoTitleBar_Fullscreen).apply {
            window?.apply {
                setBackgroundDrawable(ColorDrawable(Color.WHITE))
                addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
                @Suppress("DEPRECATION")
                decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    attributes.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
                }
            }
        }

    private fun animateButton(view: View) {
        val animator = ObjectAnimator.ofFloat(view, "rotation", -5f, 5f).apply {
            duration = 300
            repeatCount = 5
            repeatMode = ObjectAnimator.REVERSE
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    view.rotation = 0f
                }
            })
        }
        session.onDispose { animator.cancel() }
        animator.start()
    }

    fun destroyAds() = session.dispose()
}
