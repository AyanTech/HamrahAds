package ir.ayantech.hamrahads.utils.preferenceDataStore

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceDataStoreConstants {
    val HamrahInitializer = stringPreferencesKey("HamrahInitializer")

    val HamrahLatitude = doublePreferencesKey("HamrahLatitude")
    val HamrahLongitude = doublePreferencesKey("HamrahLongitude")

    val HamrahAdsBanner = stringPreferencesKey("HamrahAdsBanner")
    val HamrahAdsNative = stringPreferencesKey("HamrahAdsNative")
    val HamrahAdsInterstitial = stringPreferencesKey("HamrahAdsInterstitial")
}