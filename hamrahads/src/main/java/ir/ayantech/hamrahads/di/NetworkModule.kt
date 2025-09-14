package ir.ayantech.hamrahads.di

import android.content.Context
import android.util.Log
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import ir.ayantech.hamrahads.network.NetworkService
import ir.ayantech.hamrahads.utils.RetryInterceptor
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.net.Proxy
import java.util.concurrent.TimeUnit

class NetworkModule(private val context: Context) {

    private fun retrofit(): Retrofit {
        val json = Json {
            ignoreUnknownKeys = true
        }
        return Retrofit.Builder()
            .baseUrl("https://sdk.hamrahad.ir/v1/")
            .client(okHttpClient())
            .addConverterFactory(json.asConverterFactory("application/json; charset=utf-8".toMediaType()))
            .build()
    }

    private fun okHttpClient(): OkHttpClient {
        return OkHttpClient
            .Builder()
            .addInterceptor(NetworkHeader(context))
            .addInterceptor(RetryInterceptor())
            .addInterceptor(loggingInterceptor())
            .proxy(Proxy.NO_PROXY)
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .callTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private fun loggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor { message ->
            Log.d("loggingInterceptor", "" + message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    fun createNetworkService(): NetworkService {
        return retrofit().create(NetworkService::class.java)
    }
}