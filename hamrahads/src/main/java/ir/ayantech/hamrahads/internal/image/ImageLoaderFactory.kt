package ir.ayantech.hamrahads.internal.image

import android.content.Context
import coil3.ImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

fun imageLoader(context: Context): ImageLoader {
    return ImageLoader.Builder(context)
        .components {
            OkHttpNetworkFetcherFactory(
                callFactory = {
                    OkHttpClient.Builder()
                        .retryOnConnectionFailure(true)
                        .connectTimeout(5, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(15, TimeUnit.SECONDS)
                        .callTimeout(30, TimeUnit.SECONDS)
                        .build()
                }
            )
        }.build()
}
