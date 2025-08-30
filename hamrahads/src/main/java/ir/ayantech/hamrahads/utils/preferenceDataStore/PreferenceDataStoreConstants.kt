package ir.ayantech.hamrahads.utils.preferenceDataStore

import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PreferenceDataStoreConstants {
    val HamrahInitializer = stringPreferencesKey("HamrahInitializer")

    val HamrahLatitude = doublePreferencesKey("HamrahLatitude")
    val HamrahLongitude = doublePreferencesKey("HamrahLongitude")

//    val HamrahAdsBanner = stringPreferencesKey("HamrahAdsBanner")
//    val HamrahAdsNative = stringPreferencesKey("HamrahAdsNative")
//    val HamrahAdsInterstitial = stringPreferencesKey("HamrahAdsInterstitial")

//    fun hamrahAdsBanner(key: String): Preferences.Key<String> {
//        return stringPreferencesKey(key)
//    }
//
//    fun hamrahAdsNative(key: String): Preferences.Key<String> {
//        return stringPreferencesKey(key)
//    }
//
//    fun hamrahAdsInterstitial(key: String): Preferences.Key<String> {
//        return stringPreferencesKey(key)
//    }
}