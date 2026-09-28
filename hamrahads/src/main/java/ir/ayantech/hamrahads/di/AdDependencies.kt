package ir.ayantech.hamrahads.di

import android.content.Context
import ir.ayantech.hamrahads.data.mapper.toDeviceInfo
import ir.ayantech.hamrahads.data.mapper.toDomain
import ir.ayantech.hamrahads.data.network.NetworkClient
import ir.ayantech.hamrahads.data.network.NetworkResult
import ir.ayantech.hamrahads.data.repository.BannerRepository
import ir.ayantech.hamrahads.data.repository.InitializerRepository
import ir.ayantech.hamrahads.data.repository.InterstitialRepository
import ir.ayantech.hamrahads.data.repository.NativeRepository
import ir.ayantech.hamrahads.data.storage.AdCache
import ir.ayantech.hamrahads.data.storage.PreferenceDataStoreConstants
import ir.ayantech.hamrahads.data.storage.PreferenceDataStoreHelper
import ir.ayantech.hamrahads.domain.model.AdResult
import ir.ayantech.hamrahads.domain.usecase.InitializeAdsUseCase
import ir.ayantech.hamrahads.domain.usecase.InitializeAdsUseCaseImpl
import ir.ayantech.hamrahads.domain.usecase.LoadBannerAdUseCase
import ir.ayantech.hamrahads.domain.usecase.LoadBannerAdUseCaseImpl
import ir.ayantech.hamrahads.domain.usecase.LoadInterstitialAdUseCase
import ir.ayantech.hamrahads.domain.usecase.LoadInterstitialAdUseCaseImpl
import ir.ayantech.hamrahads.domain.usecase.LoadNativeAdUseCase
import ir.ayantech.hamrahads.domain.usecase.LoadNativeAdUseCaseImpl
import ir.ayantech.hamrahads.domain.usecase.TrackClickUseCase
import ir.ayantech.hamrahads.domain.usecase.TrackClickUseCaseImpl
import ir.ayantech.hamrahads.domain.usecase.TrackImpressionUseCase
import ir.ayantech.hamrahads.domain.usecase.TrackImpressionUseCaseImpl

/** Composition root. The legacy GET adapter remains until the backend POST contract is supplied. */
internal class AdDependencies private constructor(context: Context) {
    private val preferences = PreferenceDataStoreHelper(context.applicationContext)
    val cache = AdCache(preferences)
    private val client = NetworkClient(context.applicationContext)
    private val initializer = InitializerRepository(client)
    private val banner = BannerRepository(client)
    private val native = NativeRepository(client)
    private val interstitial = InterstitialRepository(client)

    private suspend fun appKey(): String = preferences.getPreferenceCoroutine(
        PreferenceDataStoreConstants.HamrahInitializer, ""
    )

    val initialize: InitializeAdsUseCase = InitializeAdsUseCaseImpl(
        cachedAppKey = ::appKey,
        initialize = { initializer.fetchProfileInfo(it).withoutData() },
        saveAppKey = { preferences.putPreferenceCoroutine(PreferenceDataStoreConstants.HamrahInitializer, it) },
    )

    val loadBanner: LoadBannerAdUseCase = LoadBannerAdUseCaseImpl(
        appKey = ::appKey,
        fetch = { banner.fetchBannerAds(requireNotNull(it.zoneId), it.toDeviceInfo()).map { dto -> dto.toDomain() } },
        cache = { zoneId, ad -> cache.putBanner(zoneId, ad) },
    )

    val loadNative: LoadNativeAdUseCase = LoadNativeAdUseCaseImpl(
        appKey = ::appKey,
        fetch = { native.fetchNativeAds(requireNotNull(it.zoneId), it.toDeviceInfo()).map { dto -> dto.toDomain() } },
        cache = { zoneId, ad -> cache.putNative(zoneId, ad) },
    )

    val loadInterstitial: LoadInterstitialAdUseCase = LoadInterstitialAdUseCaseImpl(
        appKey = ::appKey,
        fetch = { interstitial.fetchInterstitialAds(requireNotNull(it.zoneId), it.toDeviceInfo()).map { dto -> dto.toDomain() } },
        cache = { zoneId, ad -> cache.putInterstitial(zoneId, ad) },
    )

    val trackClick: TrackClickUseCase = TrackClickUseCaseImpl { banner.click(it).withoutData() }
    val trackImpression: TrackImpressionUseCase = TrackImpressionUseCaseImpl { banner.impression(it).withoutData() }

    companion object {
        @Volatile private var instance: AdDependencies? = null

        fun get(context: Context): AdDependencies = instance ?: synchronized(this) {
            instance ?: AdDependencies(context.applicationContext).also { instance = it }
        }
    }
}

private inline fun <T, R> NetworkResult<T>.map(transform: (T) -> R): AdResult<R> = when (this) {
    is NetworkResult.Success -> AdResult.Success(transform(data))
    is NetworkResult.Error -> AdResult.Error(errorResponse)
}

private fun NetworkResult<*>.withoutData(): AdResult<Unit> = when (this) {
    is NetworkResult.Success -> AdResult.Success(Unit)
    is NetworkResult.Error -> AdResult.Error(errorResponse)
}
