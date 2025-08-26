package ir.ayantech.hamrahads.di

import android.content.Context
import android.util.Log
import ir.ayantech.hamrahads.utils.preferenceDataStore.PreferenceDataStoreConstants
import ir.ayantech.hamrahads.utils.preferenceDataStore.PreferenceDataStoreHelper
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

class NetworkHeader(private val context: Context) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val ongoing: Request.Builder = chain.request().newBuilder()
        ongoing.addHeader("Accept", "application/json; charset=utf-8")
        val appKey = PreferenceDataStoreHelper(context).getPreference(
            PreferenceDataStoreConstants.HamrahInitializer,
            ""
        )
        if (appKey.isNotBlank()) {
            ongoing.addHeader("X-App-Key", appKey)
        }
        return chain.proceed(ongoing.build())
    }
}