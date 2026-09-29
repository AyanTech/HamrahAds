package ir.ayantech.hamrahads.internal.presentation

import android.widget.FrameLayout
import android.widget.ImageView
import coil3.ColorImage
import coil3.ImageLoader
import coil3.intercept.Interceptor
import coil3.request.SuccessResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlinx.coroutines.test.resetMain
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import ir.ayantech.hamrahads.R
import ir.ayantech.hamrahads.domain.model.NativeAd
import ir.ayantech.hamrahads.internal.presentation.native.NativeAdBinder
import ir.ayantech.hamrahads.listener.AdDisplayListener
import ir.ayantech.hamrahads.model.error.AdError
import ir.ayantech.hamrahads.model.error.HamrahAdsError
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class AdViewLifecycleTest {
    private fun controller() = Robolectric.buildActivity(AppCompatActivity::class.java).apply {
        get().setTheme(androidx.appcompat.R.style.Theme_AppCompat)
        setup()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test fun `images load before the banner or dialog is attached`() = runTest {
        Dispatchers.setMain(StandardTestDispatcher(testScheduler))
        val controller = controller()
        val session = AdViewSession(controller.get(), object : AdDisplayListener {
            override fun onError(error: HamrahAdsError) = fail("Unexpected image error: ${error.code}")
        })
        val loader = ImageLoader.Builder(controller.get()).components {
            add(Interceptor { chain -> SuccessResult(ColorImage(width = 20, height = 20), chain.request) })
        }.build()
        try {
            val target = ImageView(controller.get())
            assertFalse(target.isAttachedToWindow)
            var loaded = 0
            val request = AdImages(session) { loader }.load("https://example.test/image", target) { loaded++ }
            requireNotNull(request).job.await()
            assertEquals(1, loaded)
            assertNotNull(target.drawable)
        } finally {
            session.dispose()
            loader.shutdown()
            controller.pause().stop().destroy()
            Dispatchers.resetMain()
        }
    }

    @Test fun `activity destruction disposes session resources exactly once`() {
        val controller = controller()
        val session = AdViewSession(controller.get(), object : AdDisplayListener {})
        var disposals = 0
        session.onDispose { disposals++ }
        controller.pause().stop().destroy()
        session.dispose()
        assertEquals(1, disposals)
        assertFalse(session.isActive)
        assertNull(session.activity())
    }

    @Test fun `cleanup continues when one resource fails`() {
        val controller = controller()
        val session = AdViewSession(controller.get(), object : AdDisplayListener {})
        var released = false
        session.onDispose { released = true }
        session.onDispose { throw IllegalStateException("failure") }
        session.dispose()
        assertTrue(released)
        controller.pause().stop().destroy()
    }

    @Test fun `display failure closes resources and notifies once`() {
        val controller = controller()
        var errors = 0
        val session = AdViewSession(controller.get(), object : AdDisplayListener {
            override fun onError(error: HamrahAdsError) { assertEquals("G00015", error.code); errors++ }
        })
        session.fail(AdError.IMAGE_FAILED)
        session.fail(AdError.IMAGE_FAILED)
        assertEquals(1, errors)
        assertFalse(session.isActive)
        controller.pause().stop().destroy()
    }

    @Test fun `native binder handles CTA containers and removes callbacks on disposal`() {
        val controller = controller()
        val activity = controller.get()
        val root = FrameLayout(activity).apply { id = R.id.hamrah_ad_native_cta_view }
        val title = TextView(activity).apply { id = R.id.hamrah_ad_native_title }
        val cta = TextView(activity).apply { id = R.id.hamrah_ad_native_cta }
        root.addView(title); root.addView(cta)
        val session = AdViewSession(activity, object : AdDisplayListener {})
        var clicks = 0
        NativeAdBinder(session, AdImages(session)).bind(root, NativeAd(caption = "Title", cta = "Open")) { clicks++ }
        assertEquals("Title", title.text.toString())
        assertEquals("Open", cta.text.toString())
        root.performClick(); cta.performClick()
        assertEquals(2, clicks)
        session.dispose()
        root.performClick(); cta.performClick()
        assertEquals(2, clicks)
        controller.pause().stop().destroy()
    }
}
