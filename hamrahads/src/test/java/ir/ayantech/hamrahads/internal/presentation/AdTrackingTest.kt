package ir.ayantech.hamrahads.internal.presentation

import ir.ayantech.hamrahads.domain.model.AdResult
import ir.ayantech.hamrahads.domain.usecase.TrackClickUseCaseImpl
import ir.ayantech.hamrahads.domain.usecase.TrackImpressionUseCaseImpl
import ir.ayantech.hamrahads.listener.AdDisplayListener
import ir.ayantech.hamrahads.model.error.HamrahAdsError
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AdTrackingTest {
    @Test fun `repeated click and visibility events send each tracker once`() = runTest {
        var clicks = 0
        var impressions = 0
        var consumed = 0
        val events = mutableListOf<String>()
        val tasks = AdTaskScope({ fail("Unexpected error") }, StandardTestDispatcher(testScheduler))
        val tracking = AdTracking(tasks,
            TrackClickUseCaseImpl { assertEquals("click-url", it); clicks++; AdResult.Success(Unit) },
            TrackImpressionUseCaseImpl { assertEquals("impression-url", it); impressions++; AdResult.Success(Unit) },
            { consumed++; events += "consumed" },
            object : AdDisplayListener {
                override fun onClick() { events += "clicked" }
                override fun onDisplayed() { events += "displayed" }
            },
        )
        repeat(3) { tracking.click("click-url"); tracking.impression("impression-url") }
        advanceUntilIdle()
        assertEquals(1, clicks)
        assertEquals(1, impressions)
        assertEquals(1, consumed)
        assertEquals(listOf("clicked", "consumed", "displayed"), events)
        tasks.cancel()
    }

    @Test fun `failed tracking is nonfatal but consumes the displayed creative`() = runTest {
        var consumed = false
        val failure = AdResult.Error(HamrahAdsError(code = "server-error"))
        val tasks = AdTaskScope({ fail("Tracking errors must be nonfatal") }, StandardTestDispatcher(testScheduler))
        val tracking = AdTracking(tasks, TrackClickUseCaseImpl { failure }, TrackImpressionUseCaseImpl { failure },
            { consumed = true }, object : AdDisplayListener {
                override fun onClick() = fail("Failed click must not notify success")
                override fun onDisplayed() = fail("Failed impression must not notify success")
            })
        tracking.click("click"); tracking.impression("impression")
        advanceUntilIdle()
        assertTrue(consumed)
        assertTrue(tasks.isActive)
        tasks.cancel()
    }

    @Test fun `destroying ad cancels pending tracking and suppresses callbacks`() = runTest {
        val tasks = AdTaskScope({ fail("Cancellation is not an error") }, StandardTestDispatcher(testScheduler))
        val tracking = AdTracking(tasks,
            TrackClickUseCaseImpl { delay(1000); AdResult.Success(Unit) },
            TrackImpressionUseCaseImpl { delay(1000); AdResult.Success(Unit) },
            { fail("Cancelled impression must not consume cache") },
            object : AdDisplayListener {
                override fun onClick() = fail("Callback after destruction")
                override fun onDisplayed() = fail("Callback after destruction")
            })
        tracking.click("click"); tracking.impression("impression")
        runCurrent()
        tasks.cancel()
        advanceUntilIdle()
        tracking.click("click"); tracking.impression("impression")
        advanceUntilIdle()
        assertFalse(tasks.isActive)
    }

    @Test fun `unexpected operation exception is reported on owned dispatcher`() = runTest {
        var error: HamrahAdsError? = null
        val tasks = AdTaskScope({ error = it }, StandardTestDispatcher(testScheduler))
        tasks.launch("cache") { throw IllegalStateException("broken cache") }
        advanceUntilIdle()
        assertEquals("G00014", error?.code)
        tasks.cancel()
    }

    @Test fun `zero size and less than half visibility never qualify`() {
        assertFalse(isSufficientlyVisible(0, 0, 0, 0))
        assertFalse(isSufficientlyVisible(101, 101, 50, 51))
        assertFalse(isSufficientlyVisible(100, 100, 100, 49))
        assertTrue(isSufficientlyVisible(100, 100, 50, 50))
        assertTrue(isSufficientlyVisible(101, 101, 51, 51))
    }
}
