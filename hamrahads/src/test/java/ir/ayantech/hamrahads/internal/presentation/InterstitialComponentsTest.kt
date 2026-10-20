package ir.ayantech.hamrahads.internal.presentation

import android.os.Looper
import androidx.appcompat.R as AppCompatR
import androidx.appcompat.app.AppCompatActivity
import ir.ayantech.hamrahads.domain.model.InterstitialAd
import ir.ayantech.hamrahads.internal.presentation.interstitial.InterstitialTimers
import ir.ayantech.hamrahads.internal.presentation.interstitial.createTemplate1
import ir.ayantech.hamrahads.internal.presentation.interstitial.createTemplate2
import ir.ayantech.hamrahads.internal.presentation.interstitial.createTemplate3
import java.time.Duration
import kotlin.time.Duration.Companion.seconds
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class InterstitialComponentsTest {
    @Test
    fun `templates build required image bindings without opening a dialog`() {
        val controller = Robolectric.buildActivity(AppCompatActivity::class.java)
        controller.get().setTheme(AppCompatR.style.Theme_AppCompat)
        controller.setup()
        try {
            val activity = controller.get()
            val ad = InterstitialAd(interstitialBanner = "banner", logo = "logo", caption = "Title")
            val first = createTemplate1(activity, ad)
            val second = createTemplate2(activity, ad)
            val third = createTemplate3(activity, ad)
            assertEquals(3, first.images.size)
            assertEquals(2, second.images.size)
            assertEquals(2, third.images.size)
            for (content in listOf(first, second, third)) {
                assertFalse(content.root.isAttachedToWindow)
                content.images.forEach { assertSame(content.root, it.view.parent) }
                assertEquals("logo", content.images.last().url)
            }
        } finally {
            controller.pause().stop().destroy()
        }
    }

    @Test
    fun `disposing timers suppresses delayed close and tick callbacks`() {
        val timers = InterstitialTimers()
        var ticks = 0
        var finishes = 0
        timers.start(3.seconds, { ticks++ }, { finishes++ })
        shadowOf(Looper.getMainLooper()).idleFor(Duration.ofSeconds(1))
        assertTrue(ticks > 0)
        timers.dispose()
        val ticksAtDisposal = ticks
        shadowOf(Looper.getMainLooper()).idleFor(Duration.ofSeconds(5))
        assertEquals(ticksAtDisposal, ticks)
        assertEquals(0, finishes)
    }

    @Test
    fun `timeout finishes once and nonpositive timeout remains disabled`() {
        val timers = InterstitialTimers()
        var finishes = 0
        timers.start(2.seconds, onFinish = { finishes++ })
        timers.start(0.seconds, onFinish = { fail("Zero timeout must be disabled") })
        timers.start(-1.seconds, onFinish = { fail("Negative timeout must be disabled") })
        shadowOf(Looper.getMainLooper()).idleFor(Duration.ofSeconds(5))
        assertEquals(1, finishes)
        timers.dispose()
    }
}
