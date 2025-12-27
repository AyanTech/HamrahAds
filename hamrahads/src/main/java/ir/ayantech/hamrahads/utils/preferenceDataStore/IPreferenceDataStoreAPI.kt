package ir.ayantech.hamrahads.utils.preferenceDataStore

import androidx.datastore.preferences.core.Preferences
import ir.ayantech.hamrahads.network.model.NetworkBannerAd
import ir.ayantech.hamrahads.network.model.NetworkInterstitialAd
import ir.ayantech.hamrahads.network.model.NetworkNativeAd

interface IPreferenceDataStoreAPI {

    suspend fun putPreferenceBanner(key: String, value: NetworkBannerAd)

    suspend fun putPreferenceNative(key: String, value: NetworkNativeAd)

    suspend fun putPreferenceInterstitial(key: String, value: NetworkInterstitialAd)

    fun getPreferenceBanner(
        key: String,
    ): NetworkBannerAd?

    fun getPreferenceNative(
        key: String,
    ): NetworkNativeAd?

    fun getPreferenceInterstitial(
        key: String,
    ): NetworkInterstitialAd?

    suspend fun getPreferenceBannerCoroutine(
        key: String,
    ): NetworkBannerAd?

    suspend fun getPreferenceNativeCoroutine(
        key: String,
    ): NetworkNativeAd?

    suspend fun getPreferenceInterstitialCoroutine(
        key: String,
    ): NetworkInterstitialAd?

    fun <T> getPreference(key: Preferences.Key<T>, defaultValue: T): T

    fun <T> putPreference(key: Preferences.Key<T>, value: T)

    suspend fun <T> getPreferenceCoroutine(key: Preferences.Key<T>, defaultValue: T): T
    suspend fun <T> putPreferenceCoroutine(key: Preferences.Key<T>, value: T)
    fun <T> removePreference(key: Preferences.Key<T>)
    suspend fun removePreferenceCoroutine(key: String)
    suspend fun clearAllPreference()
}
