package ir.ayantech.hamrahads.data.repository

import ir.ayantech.hamrahads.data.dto.InitializerDto
import ir.ayantech.hamrahads.data.network.NetworkClient
import ir.ayantech.hamrahads.data.network.NetworkDataFetcher
import ir.ayantech.hamrahads.data.network.NetworkResult

class InitializerRepository(private val networkClient: NetworkClient) {

    private val dataFetcher = NetworkDataFetcher()

    suspend fun fetchProfileInfo(appKey: String): NetworkResult<InitializerDto> {
        return dataFetcher.fetchData { networkClient.createApiService().initializer(appKey) }
    }
}
