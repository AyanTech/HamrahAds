package ir.ayantech.hamrahads.internal.storage

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import ir.ayantech.hamrahads.internal.dto.BannerAdDto
import ir.ayantech.hamrahads.internal.dto.InterstitialAdDto
import ir.ayantech.hamrahads.internal.dto.NativeAdDto
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
        value: BannerAdDto
    ) {
        dataSource.edit { preferences ->
            preferences[stringPreferencesKey(key)] =
                json.encodeToString(BannerAdDto.serializer(), value)
        }
    }

    override suspend fun putPreferenceNative(
        key: String,
        value: NativeAdDto
    ) {
        dataSource.edit { preferences ->
            preferences[stringPreferencesKey(key)] =
                json.encodeToString(NativeAdDto.serializer(), value)
        }
    }

    override suspend fun putPreferenceInterstitial(
        key: String,
        value: InterstitialAdDto
    ) {
        dataSource.edit { preferences ->
            preferences[stringPreferencesKey(key)] =
                json.encodeToString(InterstitialAdDto.serializer(), value)
        }
    }

    override fun getPreferenceBanner(
        key: String,
    ): BannerAdDto? = runBlocking {
        val text = dataSource.data.first()[stringPreferencesKey(key)]
        try {
            text?.let { json.decodeFromString<BannerAdDto>(it) }
        } catch (e: Exception) {
            null
        }
    }

    override fun getPreferenceNative(
        key: String,
    ): NativeAdDto? = runBlocking {
        val text = dataSource.data.first()[stringPreferencesKey(key)]
        try {
            text?.let { json.decodeFromString<NativeAdDto>(it) }
        } catch (e: Exception) {
            null
        }
    }

    override fun getPreferenceInterstitial(
        key: String,
    ): InterstitialAdDto? = runBlocking {
        val text = dataSource.data.first()[stringPreferencesKey(key)]
        try {
            text?.let { json.decodeFromString<InterstitialAdDto>(it) }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getPreferenceBannerCoroutine(
        key: String,
    ): BannerAdDto? {
        val text = dataSource.data.first()[stringPreferencesKey(key)]
        return try {
            text?.let { json.decodeFromString<BannerAdDto>(it) }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getPreferenceNativeCoroutine(
        key: String,
    ): NativeAdDto? {
        val text = dataSource.data.first()[stringPreferencesKey(key)]
        return try {
            text?.let { json.decodeFromString<NativeAdDto>(it) }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getPreferenceInterstitialCoroutine(
        key: String,
    ): InterstitialAdDto? {
        val text = dataSource.data.first()[stringPreferencesKey(key)]
        return try {
            text?.let { json.decodeFromString<InterstitialAdDto>(it) }
        } catch (e: Exception) {
            null
        }
    }
}
