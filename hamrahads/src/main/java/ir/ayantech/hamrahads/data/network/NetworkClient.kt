package ir.ayantech.hamrahads.data.network

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.seconds
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

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
            .connectTimeout(5.seconds.inWholeMilliseconds, TimeUnit.MILLISECONDS)
            .readTimeout(30.seconds.inWholeMilliseconds, TimeUnit.MILLISECONDS)
            .writeTimeout(15.seconds.inWholeMilliseconds, TimeUnit.MILLISECONDS)
            .callTimeout(30.seconds.inWholeMilliseconds, TimeUnit.MILLISECONDS)
            .build()
    }

    private val apiService: ApiService by lazy { retrofit().create(ApiService::class.java) }

    fun createApiService(): ApiService = apiService
}
