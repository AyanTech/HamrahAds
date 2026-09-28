package ir.ayantech.hamrahads.internal.presentation

import android.content.Context
import android.widget.ImageView
import coil3.ImageLoader
import coil3.asDrawable
import coil3.request.Disposable
import coil3.request.CachePolicy
import ir.ayantech.hamrahads.internal.diagnostics.AdDiagnostics
import coil3.request.ImageRequest
import coil3.request.target
import coil3.request.transformations
import coil3.transform.Transformation
import ir.ayantech.hamrahads.internal.image.imageLoader
import ir.ayantech.hamrahads.model.error.AdError

/** Every image request is cancelled together with the ad that owns it. */
internal class AdImages(
    private val session: AdViewSession,
    private val loader: (Context) -> ImageLoader = ::imageLoader,
) {
    private val requests = mutableListOf<Disposable>()

    init { session.onDispose { requests.forEach { it.dispose() }; requests.clear() } }

    fun load(url: String?, target: ImageView, transformations: List<Transformation> = emptyList(), onLoaded: () -> Unit = {}): Disposable? {
        val activity = session.activity() ?: return null
        val request = ImageRequest.Builder(activity)
            .data(url)
            .memoryCachePolicy(CachePolicy.DISABLED)
            .diskCachePolicy(CachePolicy.DISABLED)
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
                    AdDiagnostics.failure("image", result.throwable)
                    session.fail(AdError.IMAGE_FAILED)
                },
            )
            .build()
        return loader(activity).enqueue(request).also { requests += it }
    }
}
