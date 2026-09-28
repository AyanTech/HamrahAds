package ir.ayantech.hamrahads.domain.usecase

import ir.ayantech.hamrahads.domain.model.*
import ir.ayantech.hamrahads.model.error.HamrahAdsError
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

class TrackImpressionUseCaseTest {
    @Test fun `tracking forwards full URL once`() = runTest {
        val url = "https://track.example.test/event?id=1&token=a%2Bb"
        var calls = 0
        val useCase: TrackImpressionUseCase = TrackImpressionUseCaseImpl {
            assertEquals(url, it)
            calls++
            AdResult.Success(Unit)
        }
        assertEquals(AdResult.Success(Unit), useCase(url))
        assertEquals(1, calls)
    }

    @Test fun `blank URL does not make a request`() = runTest {
        val useCase = TrackImpressionUseCaseImpl { error("Must not track") }
        assertEquals("G00010", (useCase(" ") as AdResult.Error).errorResponse.code)
    }

    @Test fun `tracking failure is preserved`() = runTest {
        val failure = AdResult.Error(HamrahAdsError(code = "tracking-error"))
        val useCase = TrackImpressionUseCaseImpl { failure }
        assertSame(failure, useCase("https://track.example.test"))
    }

    @Test fun `tracking cancellation propagates`() = runTest {
        val cancellation = CancellationException("cancelled")
        val useCase = TrackImpressionUseCaseImpl { throw cancellation }
        try {
            useCase("https://track.example.test")
            fail("Expected cancellation")
        } catch (actual: CancellationException) { assertSame(cancellation, actual) }
    }
}
