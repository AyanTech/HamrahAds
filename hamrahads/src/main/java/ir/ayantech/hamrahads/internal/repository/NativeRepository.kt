package ir.ayantech.hamrahads.internal.repository

import ir.ayantech.hamrahads.internal.device.DeviceInfo
import ir.ayantech.hamrahads.internal.dto.ApiResponseDto
import ir.ayantech.hamrahads.internal.dto.NativeAdDto
import ir.ayantech.hamrahads.internal.network.NetworkClient
import ir.ayantech.hamrahads.internal.network.NetworkDataFetcher
import ir.ayantech.hamrahads.internal.network.NetworkResult

class NativeRepository(private val networkClient: NetworkClient) {

    private val dataFetcher = NetworkDataFetcher()

    suspend fun fetchNativeAds(
        zoneId: String,
        deviceInfo: DeviceInfo
    ): NetworkResult<NativeAdDto> {
        return dataFetcher.fetchData {
            networkClient.createApiService().getNativeAds(
                zoneId = zoneId,
                appVer = deviceInfo.appVer,
                ver = deviceInfo.ver,
                brand = deviceInfo.brand.toString(),
                gdprConsent = deviceInfo.gdprConsent,
                height = deviceInfo.height,
                width = deviceInfo.width,
                ifa = deviceInfo.ifa,
                macSha1 = deviceInfo.macSha1,
                model = deviceInfo.model,
                operator = deviceInfo.operator,
                os = deviceInfo.os,
                osVer = deviceInfo.osVer,
                pkg = deviceInfo.pkg,
                ua = deviceInfo.ua,
                utcOffset = deviceInfo.utcOffset,
                geoType = deviceInfo.geoType,
                lat = deviceInfo.lat,
                lon = deviceInfo.lon,
                network = deviceInfo.network,
            )
        }
    }
    suspend fun click(
        url: String
    ): NetworkResult<ApiResponseDto> {
        return dataFetcher.fetchData {
            networkClient.createApiService().click(url)
        }
    }

    suspend fun impression(
        url: String
    ): NetworkResult<ApiResponseDto> {
        return dataFetcher.fetchData {
            networkClient.createApiService().impression(url)
        }
    }
}
