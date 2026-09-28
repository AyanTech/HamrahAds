package ir.ayantech.hamrahads.domain.usecase

import ir.ayantech.hamrahads.domain.model.*
import ir.ayantech.hamrahads.model.error.HamrahAdsError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class LoadBannerAdUseCaseTest {
    @Test fun `successful load forwards request and caches ad by zone`() = runTest {
        val request = AdRequest(zoneId = "zone", appVer = 7, lat = 35.7)
        val ad = BannerAd(landingLink = "https://example.test")
        var captured: AdRequest? = null
        var cached: Pair<String, BannerAd>? = null
        val useCase: LoadBannerAdUseCase = LoadBannerAdUseCaseImpl(
            appKey = { "key" },
            fetch = { captured = it; AdResult.Success(ad) },
            cache = { zone, value -> cached = zone to value },
        )
        assertEquals(AdResult.Success(ad), useCase(request))
        assertEquals(request, captured)
        assertEquals("zone" to ad, cached)
    }

    @Test fun `blank zone fails before fetching or caching`() = runTest {
        val useCase = LoadBannerAdUseCaseImpl(
            appKey = { error("Must not read key") },
            fetch = { error("Must not fetch") },
            cache = { _, _ -> error("Must not cache") },
        )
        for (zone in listOf(null, "", " ")) {
            val result = useCase(AdRequest(zoneId = zone)) as AdResult.Error
            assertEquals("G00010", result.errorResponse.code)
        }
    }

    @Test fun `missing app key fails before fetching`() = runTest {
        val useCase = LoadBannerAdUseCaseImpl(
            appKey = { " " }, fetch = { error("Must not fetch") },
            cache = { _, _ -> error("Must not cache") },
        )
        assertEquals("G00019", (useCase(AdRequest(zoneId = "zone")) as AdResult.Error).errorResponse.code)
    }

    @Test fun `remote error propagates without caching`() = runTest {
        val failure = AdResult.Error(HamrahAdsError(code = "server-error"))
        val useCase = LoadBannerAdUseCaseImpl(
            appKey = { "key" }, fetch = { failure },
            cache = { _, _ -> error("Must not cache") },
        )
        assertSame(failure, useCase(AdRequest(zoneId = "zone")))
    }

    @Test fun `cancellation propagates without caching`() = runTest {
        val cancellation = CancellationException("cancelled")
        val useCase = LoadBannerAdUseCaseImpl(
            appKey = { "key" }, fetch = { throw cancellation },
            cache = { _, _ -> error("Must not cache") },
        )
        try {
            useCase(AdRequest(zoneId = "zone"))
            fail("Expected cancellation")
        } catch (actual: CancellationException) { assertSame(cancellation, actual) }
    }
}
