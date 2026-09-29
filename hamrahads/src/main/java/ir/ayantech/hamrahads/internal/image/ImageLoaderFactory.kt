package ir.ayantech.hamrahads.internal.image

import android.content.Context
import coil3.ImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

@Volatile private var sharedImageLoader: ImageLoader? = null
private val imageLoaderLock = Any()

fun imageLoader(context: Context): ImageLoader = sharedImageLoader ?: synchronized(imageLoaderLock) {
    sharedImageLoader ?: createImageLoader(context.applicationContext).also { sharedImageLoader = it }
}

private fun createImageLoader(context: Context): ImageLoader {
    return ImageLoader.Builder(context)
        .components {
            OkHttpNetworkFetcherFactory(
                callFactory = {
                    OkHttpClient.Builder()
                        .retryOnConnectionFailure(true)
                        .connectTimeout(15, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(15, TimeUnit.SECONDS)
                        .callTimeout(30, TimeUnit.SECONDS)
                        .build()
                }
            )
        }.build()
}
