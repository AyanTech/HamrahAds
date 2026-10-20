package ir.ayantech.hamrahads.internal.presentation.native

import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import ir.ayantech.hamrahads.R
import ir.ayantech.hamrahads.domain.model.NativeAd
import ir.ayantech.hamrahads.internal.presentation.AdImages
import ir.ayantech.hamrahads.internal.presentation.AdViewSession

/** Binds the host's named ad views, including clickable containers. Owns no network/tracking logic. */
internal class NativeAdBinder(private val session: AdViewSession, private val images: AdImages) {
    fun bind(view: View, ad: NativeAd, onReady: () -> Unit = {}, onClick: () -> Unit) {
        val pendingImages = mutableListOf<Pair<ImageView, String>>()
        bindViews(view, ad, onClick, pendingImages)
        if (pendingImages.isEmpty()) {
            onReady()
        } else {
            var remaining = pendingImages.size
            pendingImages.forEach { (target, url) ->
                images.load(url, target) {
                    remaining--
                    if (remaining == 0 && session.isActive) onReady()
                }
            }
        }
    }

    private fun bindViews(
        view: View,
        ad: NativeAd,
        onClick: () -> Unit,
        pending: MutableList<Pair<ImageView, String>>
    ) {
        when (view.id) {
            R.id.hamrah_ad_native_title -> (view as? TextView)?.text = ad.caption
            R.id.hamrah_ad_native_description -> (view as? TextView)?.text = ad.description
            R.id.hamrah_ad_native_cta -> {
                (view as? TextView)?.text = ad.cta
                bindClick(view, onClick)
            }

            R.id.hamrah_ad_native_cta_view -> bindClick(view, onClick)
            R.id.hamrah_ad_native_logo -> bindImage(view, ad.logo, pending)
            R.id.hamrah_ad_native_banner -> bindImage(view, ad.banner1136x640, pending)
        }
        if (view is ViewGroup) {
            for (index in 0 until view.childCount) bindViews(
                view.getChildAt(index),
                ad,
                onClick,
                pending
            )
        }
    }

    private fun bindClick(view: View, onClick: () -> Unit) {
        view.setOnClickListener {
            if (session.isActive) {
                onClick()
            }
        }
        session.onDispose { view.setOnClickListener(null) }
    }

    private fun bindImage(view: View, url: String?, pending: MutableList<Pair<ImageView, String>>) {
        if (view is ImageView) {
            view.setImageDrawable(null)
            if (!url.isNullOrBlank()) pending += view to url
        }
    }
}
