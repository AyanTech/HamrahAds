package ir.ayantech.hamrahads.network

import ir.ayantech.hamrahads.network.model.NetworkBannerAd
import ir.ayantech.hamrahads.network.model.NetworkInitializer
import ir.ayantech.hamrahads.network.model.NetworkInterstitialAd
import ir.ayantech.hamrahads.network.model.NetworkNativeAd
import ir.ayantech.hamrahads.network.model.NetworkResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import retrofit2.http.Url

interface NetworkService {
    @GET("init/")
    suspend fun initializer(@Query("app_key") appKey: String?): Response<NetworkInitializer>

    @GET
    suspend fun impression(@Url url: String?): Response<NetworkResponse>

    @GET
    suspend fun click(@Url url: String?): Response<NetworkResponse>

    @GET("ads/")
    suspend fun getBannerAds(
        @Query("ver") ver: String?,
        @Query("zone_id") zoneId: String?,
        @Query("app_ver") appVer: Int?,
        @Query("brand") brand: String?,
        @Query("gdpr_consent") gdprConsent: String?,
        @Query("height") height: Int?,
        @Query("width") width: Int?,
        @Query("ifa") ifa: String?,
        @Query("mac_sha1") macSha1: String?,
        @Query("model") model: String?,
        @Query("operator") operator: String?,
        @Query("os") os: String?,
        @Query("os_ver") osVer: String?,
        @Query("pkg") pkg: String?,
        @Query("ua") ua: String?,
        @Query("utc_offset") utcOffset: Int?,
        @Query("geo_type") geoType: Int?,
        @Query("lat") lat: Double?,
        @Query("lon") lon: Double?,
        @Query("network") network: String?,
    ): Response<NetworkBannerAd>

    @GET("ads/")
    suspend fun getNativeAds(
        @Query("ver") ver: String?,
        @Query("zone_id") zoneId: String?,
        @Query("app_ver") appVer: Int?,
        @Query("brand") brand: String?,
        @Query("gdpr_consent") gdprConsent: String?,
        @Query("height") height: Int?,
        @Query("width") width: Int?,
        @Query("ifa") ifa: String?,
        @Query("mac_sha1") macSha1: String?,
        @Query("model") model: String?,
        @Query("operator") operator: String?,
        @Query("os") os: String?,
        @Query("os_ver") osVer: String?,
        @Query("pkg") pkg: String?,
        @Query("ua") ua: String?,
        @Query("utc_offset") utcOffset: Int?,
        @Query("geo_type") geoType: Int?,
        @Query("lat") lat: Double?,
        @Query("lon") lon: Double?,
        @Query("network") network: String?,
    ): Response<NetworkNativeAd>

    @GET("ads/")
    suspend fun getInterstitialAds(
        @Query("ver") ver: String?,
        @Query("zone_id") zoneId: String?,
        @Query("app_ver") appVer: Int?,
        @Query("brand") brand: String?,
        @Query("gdpr_consent") gdprConsent: String?,
        @Query("height") height: Int?,
        @Query("width") width: Int?,
        @Query("ifa") ifa: String?,
        @Query("mac_sha1") macSha1: String?,
        @Query("model") model: String?,
        @Query("operator") operator: String?,
        @Query("os") os: String?,
        @Query("os_ver") osVer: String?,
        @Query("pkg") pkg: String?,
        @Query("ua") ua: String?,
        @Query("utc_offset") utcOffset: Int?,
        @Query("geo_type") geoType: Int?,
        @Query("lat") lat: Double?,
        @Query("lon") lon: Double?,
        @Query("network") network: String?,
    ): Response<NetworkInterstitialAd>
}