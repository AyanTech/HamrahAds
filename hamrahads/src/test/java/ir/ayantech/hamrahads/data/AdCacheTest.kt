package ir.ayantech.hamrahads.data

import ir.ayantech.hamrahads.ads.LoadedAd
import ir.ayantech.hamrahads.data.storage.AdCache
import ir.ayantech.hamrahads.domain.model.BannerAd
import ir.ayantech.hamrahads.domain.model.NativeAd
import kotlin.time.Duration.Companion.milliseconds
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test

class AdCacheTest {
    @Test
    fun `same-zone responses and formats remain independent and are consumed atomically`() {
        val cache = AdCache()
        val first = LoadedAd("zone", NativeAd(caption = "first"))
        val second = LoadedAd("zone", NativeAd(caption = "second"))
        val banner = LoadedAd("zone", BannerAd())
        cache.put(first); cache.put(second); cache.put(banner)
        assertSame(first, cache.takeNative("zone"))
        cache.remove(first.id) // A late release of A cannot remove B.
        assertSame(second, cache.takeNative("zone"))
        assertNull(cache.takeNative("zone"))
        assertSame(banner, cache.takeBanner("zone"))
    }

    @Test
    fun `cache is bounded and expired entries cannot be displayed`() {
        var now = 0L
        val cache = AdCache(2)
        val first = LoadedAd("a", NativeAd(), 10.milliseconds) { now }
        val second = LoadedAd("b", NativeAd(), 10.milliseconds) { now }
        val third = LoadedAd("c", NativeAd(), 10.milliseconds) { now }
        cache.put(first); cache.put(second); cache.put(third)
        assertNull(cache.takeNative("a"))
        now = 11
        assertNull(cache.takeNative("b"))
        assertFalse(third.acquire(Any()))
    }

    @Test
    fun `one creative cannot be displayed by two owners and can rebind after release`() {
        val ad = LoadedAd("zone", NativeAd())
        val first = Any()
        val second = Any()
        assertTrue(ad.acquire(first))
        assertFalse(ad.acquire(second))
        ad.release(second)
        assertFalse(ad.acquire(second))
        ad.events.displayed = true
        ad.release(first)
        assertTrue(ad.acquire(second))
        assertTrue(ad.events.displayed)
        ad.close()
        ad.release(second)
        assertFalse(ad.acquire(first))
    }
}
