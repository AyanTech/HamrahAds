package ir.ayantech.hamrahads.data.storage

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import ir.ayantech.hamrahads.data.dto.BannerAdDto
import ir.ayantech.hamrahads.data.dto.InterstitialAdDto
import ir.ayantech.hamrahads.data.dto.NativeAdDto
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

    private val dataSource = contextApplication.applicationContext.dataStore

    // Blocking entry points are retained for compatibility; SDK coroutines use the suspend methods.
    override fun <T> getPreference(key: Preferences.Key<T>, defaultValue: T): T =
        runBlocking { getPreferenceCoroutine(key, defaultValue) }

    override fun <T> putPreference(key: Preferences.Key<T>, value: T) =
        runBlocking { putPreferenceCoroutine(key, value) }

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
        dataSource.edit { it.remove(key) }
        if (key == PreferenceDataStoreConstants.HamrahInitializer) cachedAppKey = null
    }

    override suspend fun clearAllPreference() {
        dataSource.edit { preferences ->
            preferences.clear()
        }
        cachedAppKey = null
    }

    private suspend inline fun <reified T> writeAd(key: String, value: T) {
        val encoded = json.encodeToString(value)
        dataSource.edit { it[stringPreferencesKey(key)] = encoded }
    }

    private suspend inline fun <reified T> readAd(key: String): T? {
        val encoded = dataSource.data.first()[stringPreferencesKey(key)] ?: return null
        return try {
            json.decodeFromString<T>(encoded)
        } catch (_: kotlinx.serialization.SerializationException) {
            ir.ayantech.hamrahads.internal.diagnostics.AdDiagnostics.event("adCache", "invalid JSON")
            null
        } catch (_: IllegalArgumentException) {
            ir.ayantech.hamrahads.internal.diagnostics.AdDiagnostics.event("adCache", "invalid value")
            null
        }
    }

    override suspend fun putPreferenceBanner(key: String, value: BannerAdDto) = writeAd(key, value)
    override suspend fun putPreferenceNative(key: String, value: NativeAdDto) = writeAd(key, value)
    override suspend fun putPreferenceInterstitial(key: String, value: InterstitialAdDto) = writeAd(key, value)

    override suspend fun getPreferenceBannerCoroutine(key: String): BannerAdDto? = readAd(key)
    override suspend fun getPreferenceNativeCoroutine(key: String): NativeAdDto? = readAd(key)
    override suspend fun getPreferenceInterstitialCoroutine(key: String): InterstitialAdDto? = readAd(key)

    override fun getPreferenceBanner(key: String): BannerAdDto? = runBlocking { getPreferenceBannerCoroutine(key) }
    override fun getPreferenceNative(key: String): NativeAdDto? = runBlocking { getPreferenceNativeCoroutine(key) }
    override fun getPreferenceInterstitial(key: String): InterstitialAdDto? = runBlocking { getPreferenceInterstitialCoroutine(key) }
}
