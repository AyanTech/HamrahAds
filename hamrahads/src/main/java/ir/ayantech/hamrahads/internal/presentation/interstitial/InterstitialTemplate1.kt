package ir.ayantech.hamrahads.internal.presentation.interstitial

import android.graphics.Color
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import ir.ayantech.hamrahads.R
import ir.ayantech.hamrahads.domain.model.InterstitialAd
import ir.ayantech.hamrahads.internal.image.BlurTransformation

internal fun createTemplate1(activity: AppCompatActivity, interstitial: InterstitialAd): InterstitialContent {
    val resources = activity.resources
    val container = FrameLayout(activity).apply {
        layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        )
        setBackgroundColor(Color.WHITE)
    }
    val backgroundImageView = ImageView(activity).apply {
        layoutParams = FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._250sdp)
        ).apply {
            gravity = Gravity.TOP
        }
        scaleType = ImageView.ScaleType.CENTER_CROP
    }

    val indexImageView = ImageView(activity).apply {
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

    val iconImageView = ImageView(activity).apply {
        layoutParams = FrameLayout.LayoutParams(
            resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._70sdp),
            resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._70sdp)
        ).apply {
            gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
            topMargin = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._215sdp)
        }
        scaleType = ImageView.ScaleType.FIT_XY
    }

    val iconTitleTextView = TextView(activity).apply {
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

    val descriptionTextView = TextView(activity).apply {
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

    container.addView(backgroundImageView)
    container.addView(indexImageView)
    container.addView(iconImageView)
    container.addView(iconTitleTextView)
    container.addView(descriptionTextView)
    return InterstitialContent(container, listOf(
            AdImage(backgroundImageView, interstitial.interstitialBanner, listOf(BlurTransformation(radius = 25, scale = 0.5f))),
            AdImage(indexImageView, interstitial.interstitialBanner),
            AdImage(iconImageView, interstitial.logo),
    ))
}
