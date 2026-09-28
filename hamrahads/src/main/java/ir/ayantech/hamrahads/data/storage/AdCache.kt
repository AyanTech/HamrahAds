package ir.ayantech.hamrahads.data.storage

import ir.ayantech.hamrahads.data.mapper.toDomain
import ir.ayantech.hamrahads.data.mapper.toDto
import ir.ayantech.hamrahads.domain.model.BannerAd
import ir.ayantech.hamrahads.domain.model.InterstitialAd
import ir.ayantech.hamrahads.domain.model.NativeAd

/** Domain-facing cache boundary; presentation never handles serialized DTOs. */
internal class AdCache(private val preferences: IPreferenceDataStoreAPI) {
    suspend fun getBanner(zoneId: String): BannerAd? = preferences.getPreferenceBannerCoroutine(zoneId)?.toDomain()
    suspend fun getNative(zoneId: String): NativeAd? = preferences.getPreferenceNativeCoroutine(zoneId)?.toDomain()
    suspend fun getInterstitial(zoneId: String): InterstitialAd? = preferences.getPreferenceInterstitialCoroutine(zoneId)?.toDomain()
    suspend fun putBanner(zoneId: String, ad: BannerAd) = preferences.putPreferenceBanner(zoneId, ad.toDto())
    suspend fun putNative(zoneId: String, ad: NativeAd) = preferences.putPreferenceNative(zoneId, ad.toDto())
    suspend fun putInterstitial(zoneId: String, ad: InterstitialAd) = preferences.putPreferenceInterstitial(zoneId, ad.toDto())
    suspend fun remove(zoneId: String) = preferences.removePreferenceCoroutine(zoneId)
}
