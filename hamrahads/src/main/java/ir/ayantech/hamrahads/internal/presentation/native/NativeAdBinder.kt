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
    fun bind(view: View, ad: NativeAd, onClick: () -> Unit) {
        when (view.id) {
            R.id.hamrah_ad_native_title -> (view as? TextView)?.text = ad.caption
            R.id.hamrah_ad_native_description -> (view as? TextView)?.text = ad.description
            R.id.hamrah_ad_native_cta -> {
                (view as? TextView)?.text = ad.cta
                bindClick(view, onClick)
            }
            R.id.hamrah_ad_native_cta_view -> bindClick(view, onClick)
            R.id.hamrah_ad_native_logo -> bindImage(view, ad.logo)
            R.id.hamrah_ad_native_banner -> bindImage(view, ad.banner1136x640)
        }
        if (view is ViewGroup) {
            for (index in 0 until view.childCount) bind(view.getChildAt(index), ad, onClick)
        }
    }

    private fun bindClick(view: View, onClick: () -> Unit) {
        view.setOnClickListener { if (session.isActive) onClick() }
        session.onDispose { view.setOnClickListener(null) }
    }

    private fun bindImage(view: View, url: String?) {
        if (view is ImageView && !url.isNullOrBlank()) images.load(url, view)
    }
}
