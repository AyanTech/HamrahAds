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

internal fun createTemplate3(activity: AppCompatActivity, interstitial: InterstitialAd): InterstitialContent {
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
            FrameLayout.LayoutParams.WRAP_CONTENT,
        ).apply {
            gravity = Gravity.TOP
            topMargin = resources.getDimensionPixelSize(com.intuit.sdp.R.dimen._150sdp)
        }
        scaleType = ImageView.ScaleType.FIT_XY
        adjustViewBounds = true
    }

    val iconImageView = ImageView(activity).apply {
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

    val iconTitleTextView = TextView(activity).apply {
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

    val webUrlTextView = TextView(activity).apply {
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

    val titleTextView = TextView(activity).apply {
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

    val descriptionTextView = TextView(activity).apply {
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

    container.addView(backgroundImageView)
    container.addView(iconImageView)
    container.addView(iconTitleTextView)
    container.addView(webUrlTextView)
    container.addView(titleTextView)
    container.addView(descriptionTextView)
    return InterstitialContent(container, listOf(
            AdImage(backgroundImageView, interstitial.interstitialBanner),
            AdImage(iconImageView, interstitial.logo),
    ))
}
