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

internal fun createTemplate2(activity: AppCompatActivity, interstitial: InterstitialAd): InterstitialContent {
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
            FrameLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = Gravity.TOP
        }
        scaleType = ImageView.ScaleType.FIT_XY
        adjustViewBounds = true
    }

    val titleTextView = TextView(activity).apply {
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

    val descriptionTextView = TextView(activity).apply {
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

    val iconImageView = ImageView(activity).apply {
        layoutParams = FrameLayout.LayoutParams(
            resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._70sdp),
            resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._70sdp)
        ).apply {
            gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
            bottomMargin = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._190sdp)
        }
        scaleType = ImageView.ScaleType.FIT_XY
    }

    val iconTitleTextView = TextView(activity).apply {
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

    container.addView(backgroundImageView)
    container.addView(titleTextView)
    container.addView(descriptionTextView)
    container.addView(iconImageView)
    container.addView(iconTitleTextView)
    return InterstitialContent(container, listOf(
            AdImage(backgroundImageView, interstitial.interstitialBanner),
            AdImage(iconImageView, interstitial.logo),
    ))
}
