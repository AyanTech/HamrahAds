package ir.ayantech.hamrahads.data.network

import android.content.Context
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
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
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .callTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    private val apiService: ApiService by lazy { retrofit().create(ApiService::class.java) }

    fun createApiService(): ApiService = apiService
}
