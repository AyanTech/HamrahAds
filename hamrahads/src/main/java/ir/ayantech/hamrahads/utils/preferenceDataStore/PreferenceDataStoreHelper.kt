package ir.ayantech.hamrahads.utils.preferenceDataStore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import ir.ayantech.hamrahads.network.model.NetworkBannerAd
import ir.ayantech.hamrahads.network.model.NetworkInterstitialAd
import ir.ayantech.hamrahads.network.model.NetworkNativeAd
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json


private val Context.dataStore by preferencesDataStore(
    name = "HamrahAdsPreference"
)

class PreferenceDataStoreHelper(
    contextApplication: Context
) : IPreferenceDataStoreAPI {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val dataSource = contextApplication.dataStore

    override fun <T> getPreference(
        key: Preferences.Key<T>,
        defaultValue: T
    ) = runBlocking {
        async {
            dataSource.data.first()[key] ?: defaultValue
        }.await()
    }

    override fun <T> putPreference(key: Preferences.Key<T>, value: T) {
        runBlocking {
            launch {
                dataSource.edit { preferences ->
                    preferences[key] = value
                }
            }
        }
    }

    override suspend fun <T> getPreferenceCoroutine(key: Preferences.Key<T>, defaultValue: T):
            T = dataSource.data.first()[key] ?: defaultValue

    override suspend fun <T> putPreferenceCoroutine(key: Preferences.Key<T>, value: T) {
        dataSource.edit { preferences ->
            preferences[key] = value
        }
    }

    override suspend fun removePreferenceCoroutine(keyName: String) {
        dataSource.edit { preferences ->
            val key = stringPreferencesKey(keyName)
            preferences.remove(key)
        }
    }

    override fun <T> removePreference(key: Preferences.Key<T>): Unit = runBlocking {
        async {
            dataSource.edit { preferences ->
                preferences.remove(key)
            }
        }.await()
    }

    override suspend fun clearAllPreference() {
        dataSource.edit { preferences ->
            preferences.clear()
        }
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
        async {
            val text = dataSource.data.first()[stringPreferencesKey(key)]
            try {
                text?.let { json.decodeFromString<NetworkBannerAd>(it) }
            } catch (e: Exception) {
                null
            }
        }.await()
    }

    override fun getPreferenceNative(
        key: String,
    ): NetworkNativeAd? = runBlocking {
        async {
            val text = dataSource.data.first()[stringPreferencesKey(key)]
            try {
                text?.let { json.decodeFromString<NetworkNativeAd>(it) }
            } catch (e: Exception) {
                null
            }
        }.await()
    }

    override fun getPreferenceInterstitial(
        key: String,
    ): NetworkInterstitialAd? = runBlocking {
        async {
            val text = dataSource.data.first()[stringPreferencesKey(key)]
            try {
                text?.let { json.decodeFromString<NetworkInterstitialAd>(text) }
            } catch (e: Exception) {
                null
            }
        }.await()
    }
}