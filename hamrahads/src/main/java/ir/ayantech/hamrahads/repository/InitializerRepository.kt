package ir.ayantech.hamrahads.repository

import ir.ayantech.hamrahads.di.NetworkDataFetcher
import ir.ayantech.hamrahads.di.NetworkModule
import ir.ayantech.hamrahads.di.NetworkResult
import ir.ayantech.hamrahads.network.model.NetworkInitializer

class InitializerRepository(private var networkModule: NetworkModule) {

    private val dataFetcher = NetworkDataFetcher()

    suspend fun fetchProfileInfo(appKey: String): NetworkResult<NetworkInitializer> {
        return dataFetcher.fetchData { networkModule.createNetworkService().initializer(appKey) }
    }
}