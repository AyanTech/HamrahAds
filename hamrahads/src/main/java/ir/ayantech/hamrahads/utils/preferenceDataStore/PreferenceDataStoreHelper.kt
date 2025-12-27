package ir.ayantech.hamrahads.utils.preferenceDataStore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import ir.ayantech.hamrahads.network.model.NetworkBannerAd
import ir.ayantech.hamrahads.network.model.NetworkInterstitialAd
import ir.ayantech.hamrahads.network.model.NetworkNativeAd
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json


private val Context.dataStore by preferencesDataStore(
    name = "HamrahAdsPreference"
)

class PreferenceDataStoreHelper(
    contextApplication: Context
) : IPreferenceDataStoreAPI {

    companion object {
        @Volatile
        private var cachedAppKey: String? = null
    }

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val dataSource = contextApplication.dataStore

    override fun <T> getPreference(
        key: Preferences.Key<T>,
        defaultValue: T
    ): T {
        if (key == PreferenceDataStoreConstants.HamrahInitializer) {
            val cached = cachedAppKey
            if (cached != null && defaultValue is String) {
                @Suppress("UNCHECKED_CAST")
                return cached as T
            }
        }

        val value = runBlocking {
            dataSource.data.first()[key] ?: defaultValue
        }
        if (key == PreferenceDataStoreConstants.HamrahInitializer && value is String) {
            cachedAppKey = value
        }
        return value
    }

    override fun <T> putPreference(key: Preferences.Key<T>, value: T) {
        runBlocking {
            dataSource.edit { preferences ->
                preferences[key] = value
            }
        }
        if (key == PreferenceDataStoreConstants.HamrahInitializer && value is String) {
            cachedAppKey = value
        }
    }

    override suspend fun <T> getPreferenceCoroutine(key: Preferences.Key<T>, defaultValue: T):
            T {
        if (key == PreferenceDataStoreConstants.HamrahInitializer) {
            val cached = cachedAppKey
            if (cached != null && defaultValue is String) {
                @Suppress("UNCHECKED_CAST")
                return cached as T
            }
        }
        val value = dataSource.data.first()[key] ?: defaultValue
        if (key == PreferenceDataStoreConstants.HamrahInitializer && value is String) {
            cachedAppKey = value
        }
        return value
    }

    override suspend fun <T> putPreferenceCoroutine(key: Preferences.Key<T>, value: T) {
        dataSource.edit { preferences ->
            preferences[key] = value
        }
        if (key == PreferenceDataStoreConstants.HamrahInitializer && value is String) {
            cachedAppKey = value
        }
    }

    override suspend fun removePreferenceCoroutine(key: String) {
        dataSource.edit { preferences ->
            preferences.remove(stringPreferencesKey(key))
        }
        if (key == PreferenceDataStoreConstants.HamrahInitializer.name) {
            cachedAppKey = null
        }
    }

    override fun <T> removePreference(key: Preferences.Key<T>): Unit = runBlocking {
        dataSource.edit { preferences ->
            preferences.remove(key)
        }
    }

    override suspend fun clearAllPreference() {
        dataSource.edit { preferences ->
            preferences.clear()
        }
        cachedAppKey = null
    }

    override suspend fun putPreferenceBanner(
        key: String,
        value: NetworkBannerAd
    ) {
        dataSource.edit { preferences ->
            preferences[stringPreferencesKey(key)] =
                json.encodeToString(NetworkBannerAd.serializer(), value)
        }
    }

    override suspend fun putPreferenceNative(
        key: String,
        value: NetworkNativeAd
    ) {
        dataSource.edit { preferences ->
            preferences[stringPreferencesKey(key)] =
                json.encodeToString(NetworkNativeAd.serializer(), value)
        }
    }

    override suspend fun putPreferenceInterstitial(
        key: String,
        value: NetworkInterstitialAd
    ) {
        dataSource.edit { preferences ->
            preferences[stringPreferencesKey(key)] =
                json.encodeToString(NetworkInterstitialAd.serializer(), value)
        }
    }

    override fun getPreferenceBanner(
        key: String,
    ): NetworkBannerAd? = runBlocking {
        val text = dataSource.data.first()[stringPreferencesKey(key)]
        try {
            text?.let { json.decodeFromString<NetworkBannerAd>(it) }
        } catch (e: Exception) {
            null
        }
    }

    override fun getPreferenceNative(
        key: String,
    ): NetworkNativeAd? = runBlocking {
        val text = dataSource.data.first()[stringPreferencesKey(key)]
        try {
            text?.let { json.decodeFromString<NetworkNativeAd>(it) }
        } catch (e: Exception) {
            null
        }
    }

    override fun getPreferenceInterstitial(
        key: String,
    ): NetworkInterstitialAd? = runBlocking {
        val text = dataSource.data.first()[stringPreferencesKey(key)]
        try {
            text?.let { json.decodeFromString<NetworkInterstitialAd>(it) }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getPreferenceBannerCoroutine(
        key: String,
    ): NetworkBannerAd? {
        val text = dataSource.data.first()[stringPreferencesKey(key)]
        return try {
            text?.let { json.decodeFromString<NetworkBannerAd>(it) }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getPreferenceNativeCoroutine(
        key: String,
    ): NetworkNativeAd? {
        val text = dataSource.data.first()[stringPreferencesKey(key)]
        return try {
            text?.let { json.decodeFromString<NetworkNativeAd>(it) }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getPreferenceInterstitialCoroutine(
        key: String,
    ): NetworkInterstitialAd? {
        val text = dataSource.data.first()[stringPreferencesKey(key)]
        return try {
            text?.let { json.decodeFromString<NetworkInterstitialAd>(it) }
        } catch (e: Exception) {
            null
        }
    }
}
