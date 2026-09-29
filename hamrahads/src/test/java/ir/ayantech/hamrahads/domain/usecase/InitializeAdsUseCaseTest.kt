package ir.ayantech.hamrahads.domain.usecase

import ir.ayantech.hamrahads.domain.model.*
import ir.ayantech.hamrahads.model.error.HamrahAdsError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class InitializeAdsUseCaseTest {
    @Test fun `successful initialization saves the requested key`() = runTest {
        var saved: String? = null
        var calls = 0
        val useCase: InitializeAdsUseCase = InitializeAdsUseCaseImpl(
            cachedAppKey = { "old" },
            initialize = { assertEquals("new", it); calls++; AdResult.Success(Unit) },
            saveAppKey = { saved = it },
        )
        assertEquals(AdResult.Success(Unit), useCase("new"))
        assertEquals("new", saved)
        assertEquals(1, calls)
    }

    @Test fun `same key skips network and storage writes`() = runTest {
        val useCase = InitializeAdsUseCaseImpl(
            cachedAppKey = { "key" }, initialize = { error("Must not initialize") },
            saveAppKey = { error("Must not save") },
        )
        assertEquals(AdResult.Success(Unit), useCase("key"))
    }

    @Test fun `blank key fails without reading cache or calling network`() = runTest {
        val useCase = InitializeAdsUseCaseImpl(
            cachedAppKey = { error("Must not read") }, initialize = { error("Must not initialize") },
            saveAppKey = { error("Must not save") },
        )
        assertEquals("G00019", (useCase(" ") as AdResult.Error).errorResponse.code)
    }

    @Test fun `failed initialization does not save key`() = runTest {
        val failure = AdResult.Error(HamrahAdsError(code = "rejected"))
        val useCase = InitializeAdsUseCaseImpl(
            cachedAppKey = { "old" }, initialize = { failure }, saveAppKey = { error("Must not save") },
        )
        assertSame(failure, useCase("new"))
    }

    @Test fun `cancelled initialization does not save key`() = runTest {
        val cancellation = CancellationException("cancelled")
        val useCase = InitializeAdsUseCaseImpl(
            cachedAppKey = { "" }, initialize = { throw cancellation }, saveAppKey = { error("Must not save") },
        )
        try {
            useCase("key")
            fail("Expected cancellation")
        } catch (actual: CancellationException) { assertSame(cancellation, actual) }
    }
}
