package ir.ayantech.hamrahads.internal.image

import android.content.Context
import coil3.ImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.seconds
import okhttp3.OkHttpClient

@Volatile
private var sharedImageLoader: ImageLoader? = null
private val imageLoaderLock = Any()

fun imageLoader(context: Context): ImageLoader =
    sharedImageLoader ?: synchronized(imageLoaderLock) {
        sharedImageLoader
            ?: createImageLoader(context.applicationContext).also { sharedImageLoader = it }
    }

private fun createImageLoader(context: Context): ImageLoader {
    return ImageLoader.Builder(context)
        .components {
            OkHttpNetworkFetcherFactory(
                callFactory = {
                    OkHttpClient.Builder()
                        .retryOnConnectionFailure(true)
                        .connectTimeout(15.seconds.inWholeMilliseconds, TimeUnit.MILLISECONDS)
                        .readTimeout(30.seconds.inWholeMilliseconds, TimeUnit.MILLISECONDS)
                        .writeTimeout(15.seconds.inWholeMilliseconds, TimeUnit.MILLISECONDS)
                        .callTimeout(30.seconds.inWholeMilliseconds, TimeUnit.MILLISECONDS)
                        .build()
                }
            )
        }.build()
}
