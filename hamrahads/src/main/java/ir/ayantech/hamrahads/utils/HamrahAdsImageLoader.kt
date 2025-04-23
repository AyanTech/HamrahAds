package ir.ayantech.hamrahads.utils

import android.content.Context
import coil3.ImageLoader
import coil3.network.okhttp.OkHttpNetworkFetcherFactory
import okhttp3.OkHttpClient
import java.net.Proxy
import java.util.concurrent.TimeUnit

fun imageLoader(context: Context): ImageLoader {
    return ImageLoader.Builder(context)
        .components {
            OkHttpNetworkFetcherFactory(
                callFactory = {
                    OkHttpClient.Builder()
                        .addInterceptor(RetryInterceptor(maxRetries = 3))
                        .proxy(Proxy.NO_PROXY)
                        .readTimeout(10, TimeUnit.SECONDS)
                        .callTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .build()
                }
            )
        }.build()
}
