package ir.ayantech.hamrahads.internal.presentation

import android.content.Context
import android.view.View
import android.widget.ImageView
import coil3.ImageLoader
import coil3.asDrawable
import coil3.request.CachePolicy
import coil3.request.Disposable
import coil3.request.ImageRequest
import coil3.request.transformations
import coil3.transform.Transformation
import ir.ayantech.hamrahads.internal.diagnostics.AdDiagnostics
import ir.ayantech.hamrahads.internal.image.imageLoader
import ir.ayantech.hamrahads.model.error.AdError

/** Every image request is cancelled together with the ad that owns it. */
internal class AdImages(
    private val session: AdViewSession,
    private val loader: (Context) -> ImageLoader = ::imageLoader,
) {
    private val requests = mutableListOf<Disposable>()
    private val targets = mutableSetOf<ImageView>()

    init {
        session.onDispose {
            requests.forEach { it.dispose() }
            requests.clear()
            targets.forEach { it.setImageDrawable(null) }
            targets.clear()
        }
    }

    fun load(
        url: String?,
        target: ImageView,
        transformations: List<Transformation> = emptyList(),
        onLoaded: () -> Unit = {}
    ): Disposable? {
        val activity = session.activity() ?: return null
        targets += target
        target.setImageDrawable(null)
        // Callback targets work before attachment. Bound decoding even before measurement.
        val metrics = target.resources.displayMetrics
        val width = target.imageDimension(horizontal = true) ?: metrics.widthPixels
        val height = target.imageDimension(horizontal = false) ?: metrics.heightPixels
        val request = ImageRequest.Builder(activity)
            .data(url)
            .size(width.coerceAtLeast(1), height.coerceAtLeast(1))
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            // A ViewTarget waits for attachment. Banners/dialogs must load before being attached.
            .target(onSuccess = { image ->
                if (session.isActive) {
                    target.setImageDrawable(image.asDrawable(target.resources))
                    onLoaded()
                }
            })
            .transformations(transformations)
            .listener(
                onError = { _, result ->
                    if (session.isActive) {
                        AdDiagnostics.failure("image", result.throwable)
                        session.fail(AdError.IMAGE_FAILED)
                    }
                },
            )
            .build()
        return loader(activity).enqueue(request).also { requests += it }
    }
}

private fun View.imageDimension(horizontal: Boolean): Int? {
    var current: View? = this
    while (current != null) {
        val measured = if (horizontal) current.width else current.height
        if (measured > 0) return measured
        val parameter =
            if (horizontal) current.layoutParams?.width else current.layoutParams?.height
        if (parameter != null && parameter > 0) return parameter
        current = current.parent as? View
    }
    return null
}
