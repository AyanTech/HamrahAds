package ir.ayantech.hamrahads.repository

import ir.ayantech.hamrahads.di.NetworkDataFetcher
import ir.ayantech.hamrahads.di.NetworkModule
import ir.ayantech.hamrahads.di.NetworkResult
import ir.ayantech.hamrahads.network.model.NetworkBannerAd
import ir.ayantech.hamrahads.network.model.NetworkDeviceInfo
import ir.ayantech.hamrahads.network.model.NetworkResponse

class BannerAdsRepository(private var networkModule: NetworkModule) {

    private val dataFetcher = NetworkDataFetcher()

    suspend fun fetchBannerAds(
        zoneId: String,
        networkDeviceInfo: NetworkDeviceInfo
    ): NetworkResult<NetworkBannerAd> {
        return dataFetcher.fetchData {
            networkModule.createNetworkService().getBannerAds(
                zoneId = zoneId,
                appVer = networkDeviceInfo.appVer,
                brand = networkDeviceInfo.brand.toString(),
                gdprConsent = networkDeviceInfo.gdprConsent,
                height = networkDeviceInfo.height,
                width = networkDeviceInfo.width,
                ifa = networkDeviceInfo.ifa,
                macSha1 = networkDeviceInfo.macSha1,
                model = networkDeviceInfo.model,
                operator = networkDeviceInfo.operator,
                os = networkDeviceInfo.os,
                osVer = networkDeviceInfo.osVer,
                pkg = networkDeviceInfo.pkg,
                ua = networkDeviceInfo.ua,
                utcOffset = networkDeviceInfo.utcOffset,
                geoType = networkDeviceInfo.geoType,
                lat = networkDeviceInfo.lat,
                lon = networkDeviceInfo.lon,
                network = networkDeviceInfo.network,
            )
        }
    }

    suspend fun click(
        url: String
    ): NetworkResult<NetworkResponse> {
        return dataFetcher.fetchData {
            networkModule.createNetworkService().click(url)
        }
    }

    suspend fun impression(
        url: String
    ): NetworkResult<NetworkResponse> {
        return dataFetcher.fetchData {
            networkModule.createNetworkService().impression(url)
        }
    }
}