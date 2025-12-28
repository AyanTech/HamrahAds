package ir.ayantech.hamrahads.internal.repository

import ir.ayantech.hamrahads.internal.dto.InitializerDto
import ir.ayantech.hamrahads.internal.network.NetworkClient
import ir.ayantech.hamrahads.internal.network.NetworkDataFetcher
import ir.ayantech.hamrahads.internal.network.NetworkResult

class InitializerRepository(private val networkClient: NetworkClient) {

    private val dataFetcher = NetworkDataFetcher()

    suspend fun fetchProfileInfo(appKey: String): NetworkResult<InitializerDto> {
        return dataFetcher.fetchData { networkClient.createApiService().initializer(appKey) }
    }
}
