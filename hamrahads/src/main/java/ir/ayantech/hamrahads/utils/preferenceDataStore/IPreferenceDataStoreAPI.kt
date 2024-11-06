package ir.ayantech.hamrahads.utils.preferenceDataStore

import androidx.datastore.preferences.core.Preferences
import ir.ayantech.hamrahads.network.model.NetworkBannerAd
import ir.ayantech.hamrahads.network.model.NetworkInterstitialAd
import ir.ayantech.hamrahads.network.model.NetworkNativeAd

interface IPreferenceDataStoreAPI {

    suspend fun putPreferenceBanner(key: Preferences.Key<String>, value: NetworkBannerAd)
    suspend fun putPreferenceNative(key: Preferences.Key<String>, value: NetworkNativeAd)
    suspend fun putPreferenceInterstitial(
        key: Preferences.Key<String>,
        value: NetworkInterstitialAd
    )

    fun getPreferenceBanner(
        key: Preferences.Key<String>,
        defaultValue: String?
    ): NetworkBannerAd?

    fun getPreferenceNative(
        key: Preferences.Key<String>,
        defaultValue: String?
    ): NetworkNativeAd?

    fun getPreferenceInterstitial(
        key: Preferences.Key<String>,
        defaultValue: String?
    ): NetworkInterstitialAd?

    fun <T> getPreference(key: Preferences.Key<T>, defaultValue: T): T
    fun <T> putPreference(key: Preferences.Key<T>, value: T)

    suspend fun <T> getPreferenceCoroutine(key: Preferences.Key<T>, defaultValue: T): T
    suspend fun <T> putPreferenceCoroutine(key: Preferences.Key<T>, value: T)
    fun <T> removePreference(key: Preferences.Key<T>)
    suspend fun <T> removePreferenceCoroutine(key: Preferences.Key<T>)
    suspend fun <T> clearAllPreference()
}
