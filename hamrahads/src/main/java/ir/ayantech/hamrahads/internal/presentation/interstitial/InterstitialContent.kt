package ir.ayantech.hamrahads.internal.presentation.interstitial

import android.widget.FrameLayout
import android.widget.ImageView
import coil3.transform.Transformation

internal data class InterstitialContent(val root: FrameLayout, val images: List<AdImage>)
internal data class AdImage(val view: ImageView, val url: String?, val transformations: List<Transformation> = emptyList())
