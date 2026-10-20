package ir.ayantech.hamrahads.internal.presentation

import ir.ayantech.hamrahads.ads.AdEvents
import ir.ayantech.hamrahads.domain.model.AdResult
import ir.ayantech.hamrahads.domain.usecase.TrackClickUseCaseImpl
import ir.ayantech.hamrahads.domain.usecase.TrackImpressionUseCaseImpl
import ir.ayantech.hamrahads.listener.AdDisplayListener
import ir.ayantech.hamrahads.model.error.HamrahAdsError
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class AdTrackingTest {
    @Test
    fun `repeated click and visibility events send each tracker once`() = runTest {
        var clicks = 0
        var impressions = 0
        val events = mutableListOf<String>()
        val tasks = AdTaskScope({ fail("Unexpected error") }, StandardTestDispatcher(testScheduler))
        val tracking = AdTracking(
            tasks,
            TrackClickUseCaseImpl {
                assertEquals(
                    "click-url",
                    it
                ); clicks++; AdResult.Success(Unit)
            },
            TrackImpressionUseCaseImpl {
                assertEquals(
                    "impression-url",
                    it
                ); impressions++; AdResult.Success(Unit)
            },
            object : AdDisplayListener {
                override fun onClick() {
                    events += "clicked"
                }

                override fun onDisplayed() {
                    events += "displayed"
                }
            },
        )
        repeat(3) { tracking.click("click-url"); tracking.impression("impression-url") }
        advanceUntilIdle()
        assertEquals(1, clicks)
        assertEquals(1, impressions)
        assertEquals(listOf("clicked", "displayed"), events)
        tasks.cancel()
    }

    @Test
    fun `tracking delivery failure does not suppress local display or click events`() = runTest {
        val failure = AdResult.Error(HamrahAdsError(code = "server-error"))
        val tasks = AdTaskScope(
            { fail("Tracking errors must be nonfatal") },
            StandardTestDispatcher(testScheduler)
        )
        var clicked = 0
        var displayed = 0
        val tracking = AdTracking(
            tasks,
            TrackClickUseCaseImpl { failure },
            TrackImpressionUseCaseImpl { failure },
            object : AdDisplayListener {
                override fun onClick() {
                    clicked++
                }

                override fun onDisplayed() {
                    displayed++
                }
            })
        tracking.click("click"); tracking.impression("impression")
        assertEquals(1, clicked)
        assertEquals(1, displayed)
        advanceUntilIdle()
        assertTrue(tasks.isActive)
        tasks.cancel()
    }

    @Test
    fun `accepted tracking survives view disposal without late view callbacks`() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val viewTasks = AdTaskScope({ fail("Unexpected error") }, dispatcher)
        val deliveryTasks = AdTaskScope({ fail("Unexpected error") }, dispatcher)
        var delivered = 0
        var callbacks = 0
        val tracking = AdTracking(
            viewTasks,
            TrackClickUseCaseImpl { delay(1000); delivered++; AdResult.Success(Unit) },
            TrackImpressionUseCaseImpl { delay(1000); delivered++; AdResult.Success(Unit) },
            object : AdDisplayListener {
                override fun onClick() {
                    callbacks++
                }

                override fun onDisplayed() {
                    callbacks++
                }
            }, deliveryTasks = deliveryTasks
        )
        tracking.click("click"); tracking.impression("impression")
        assertEquals(2, callbacks)
        viewTasks.cancel()
        advanceUntilIdle()
        tracking.click("click"); tracking.impression("impression")
        assertEquals(2, delivered)
        assertEquals(2, callbacks)
        deliveryTasks.cancel()
    }

    @Test
    fun `rebinding the same loaded ad cannot duplicate impressions`() = runTest {
        val tasks = AdTaskScope({ fail("Unexpected error") }, StandardTestDispatcher(testScheduler))
        val events = AdEvents()
        var deliveries = 0
        var callbacks = 0
        repeat(2) {
            AdTracking(
                tasks, TrackClickUseCaseImpl { AdResult.Success(Unit) },
                TrackImpressionUseCaseImpl { deliveries++; AdResult.Success(Unit) },
                object : AdDisplayListener {
                    override fun onDisplayed() {
                        callbacks++
                    }
                }, events
            ).impression("same-creative")
        }
        advanceUntilIdle()
        assertEquals(1, deliveries)
        assertEquals(1, callbacks)
        tasks.cancel()
    }

    @Test
    fun `unexpected operation exception is reported on owned dispatcher`() = runTest {
        var error: HamrahAdsError? = null
        val tasks = AdTaskScope({ error = it }, StandardTestDispatcher(testScheduler))
        tasks.launch("cache") { throw IllegalStateException("broken cache") }
        advanceUntilIdle()
        assertEquals("G00014", error?.code)
        tasks.cancel()
    }

    @Test
    fun `zero size and less than half visibility never qualify`() {
        assertFalse(isSufficientlyVisible(0, 0, 0, 0))
        assertFalse(isSufficientlyVisible(101, 101, 50, 51))
        assertFalse(isSufficientlyVisible(100, 100, 100, 49))
        assertTrue(isSufficientlyVisible(100, 100, 50, 50))
        assertTrue(isSufficientlyVisible(101, 101, 51, 51))
    }
}
