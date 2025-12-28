package ir.ayantech.hamrahads.internal.network

import android.content.Context
import android.util.Log
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import ir.ayantech.hamrahads.BuildConfig
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

class NetworkClient(private val context: Context) {

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
            .retryOnConnectionFailure(false)
            .addInterceptor(HeaderInterceptor(context))
            .addInterceptor(RetryInterceptor())
            .addInterceptor(loggingInterceptor())
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
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
    }

    fun createApiService(): ApiService {
        return retrofit().create(ApiService::class.java)
    }
}
