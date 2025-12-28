package ir.ayantech.hamrahads.internal.storage

import androidx.datastore.preferences.core.Preferences
import ir.ayantech.hamrahads.internal.dto.BannerAdDto
import ir.ayantech.hamrahads.internal.dto.InterstitialAdDto
import ir.ayantech.hamrahads.internal.dto.NativeAdDto

interface IPreferenceDataStoreAPI {

    suspend fun putPreferenceBanner(key: String, value: BannerAdDto)

    suspend fun putPreferenceNative(key: String, value: NativeAdDto)

    suspend fun putPreferenceInterstitial(key: String, value: InterstitialAdDto)

    fun getPreferenceBanner(
        key: String,
    ): BannerAdDto?

    fun getPreferenceNative(
        key: String,
    ): NativeAdDto?

    fun getPreferenceInterstitial(
        key: String,
    ): InterstitialAdDto?

    suspend fun getPreferenceBannerCoroutine(
        key: String,
    ): BannerAdDto?

    suspend fun getPreferenceNativeCoroutine(
        key: String,
    ): NativeAdDto?

    suspend fun getPreferenceInterstitialCoroutine(
        key: String,
    ): InterstitialAdDto?

    fun <T> getPreference(key: Preferences.Key<T>, defaultValue: T): T

    fun <T> putPreference(key: Preferences.Key<T>, value: T)

    suspend fun <T> getPreferenceCoroutine(key: Preferences.Key<T>, defaultValue: T): T
    suspend fun <T> putPreferenceCoroutine(key: Preferences.Key<T>, value: T)
    fun <T> removePreference(key: Preferences.Key<T>)
    suspend fun removePreferenceCoroutine(key: String)
    suspend fun clearAllPreference()
}
