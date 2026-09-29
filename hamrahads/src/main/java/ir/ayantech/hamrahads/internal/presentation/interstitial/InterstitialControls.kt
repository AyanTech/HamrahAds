package ir.ayantech.hamrahads.internal.presentation.interstitial

import android.graphics.Color
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import ir.ayantech.hamrahads.R

internal class InterstitialControls(activity: AppCompatActivity, cta: String?, onClick: () -> Unit, onClose: () -> Unit) {
    private val resources = activity.resources
    private fun dimension(id: Int) = resources.getDimensionPixelSize(id)

    val install = CardView(activity).apply {
        layoutParams = FrameLayout.LayoutParams(dimension(com.intuit.sdp.R.dimen._170sdp), dimension(com.intuit.sdp.R.dimen._40sdp)).apply {
            bottomMargin = dimension(com.intuit.sdp.R.dimen._80sdp)
            gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        }
        setCardBackgroundColor(ContextCompat.getColor(context, R.color.color_2))
        cardElevation = resources.getDimension(com.intuit.sdp.R.dimen._2sdp)
        radius = resources.getDimension(com.intuit.sdp.R.dimen._20sdp)
        addView(TextView(activity).apply {
            layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            setTextColor(Color.WHITE)
            textSize = resources.getDimension(com.intuit.sdp.R.dimen._6sdp)
            typeface = ResourcesCompat.getFont(activity, R.font.medium)
            text = cta
            gravity = Gravity.CENTER
            setOnClickListener { onClick() }
        })
    }

    val countdown = TextView(activity).apply {
        layoutParams = LinearLayout.LayoutParams(dimension(com.intuit.sdp.R.dimen._40sdp), ViewGroup.LayoutParams.WRAP_CONTENT)
        maxLines = 1
        gravity = Gravity.CENTER
        typeface = ResourcesCompat.getFont(activity, R.font.regular)
        textSize = resources.getDimension(com.intuit.sdp.R.dimen._4sdp)
        setTextColor(Color.BLACK)
    }

    val close = CardView(activity).apply {
        layoutParams = FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dimension(com.intuit.sdp.R.dimen._22sdp)).apply {
            topMargin = dimension(com.intuit.sdp.R.dimen._10sdp)
            marginEnd = dimension(com.intuit.sdp.R.dimen._10sdp)
            gravity = Gravity.TOP or Gravity.END
        }
        setCardBackgroundColor(Color.WHITE)
        cardElevation = resources.getDimension(com.intuit.sdp.R.dimen._2sdp)
        radius = resources.getDimension(com.intuit.sdp.R.dimen._10sdp)
        addView(LinearLayout(activity).apply {
            gravity = Gravity.CENTER
            addView(countdown)
            addView(TextView(activity).apply {
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
                    marginEnd = dimension(com.intuit.sdp.R.dimen._7sdp)
                }
                text = resources.getText(R.string.hamrah_ads_font_close)
                typeface = ResourcesCompat.getFont(activity, R.font.icon)
                setTextColor(Color.BLACK)
                textSize = resources.getDimension(com.intuit.sdp.R.dimen._6sdp)
            })
        })
        setOnClickListener { onClose() }
    }
}
