package ir.ayantech.hamrahads.data.storage

import ir.ayantech.hamrahads.ads.LoadedAd
import ir.ayantech.hamrahads.domain.model.BannerAd
import ir.ayantech.hamrahads.domain.model.InterstitialAd
import ir.ayantech.hamrahads.domain.model.NativeAd

/**
 * Bounded, in-memory handoff for request(zone)/show(zone). New callers pass LoadedAd directly.
 * Entries are removed when acquired, never by tracking.
 */
internal class AdCache(private val capacity: Int = 16) {
    private val ads = LinkedHashMap<String, LoadedAd<*>>()

    init {
        require(capacity > 0)
    }

    @Synchronized
    fun put(ad: LoadedAd<*>) {
        ads.entries.removeAll { it.value.isExpired }
        ads[ad.id] = ad
        while (ads.size > capacity) ads.remove(ads.keys.first())
    }

    @Synchronized
    fun remove(id: String) {
        ads.remove(id)
    }

    @Synchronized
    private fun <T : Any> take(zoneId: String, type: Class<T>): LoadedAd<T>? {
        ads.entries.removeAll { it.value.isExpired }
        val entry =
            ads.values.firstOrNull { it.zoneId == zoneId && type.isInstance(it.ad) } ?: return null
        ads.remove(entry.id)
        @Suppress("UNCHECKED_CAST")
        return entry as LoadedAd<T>
    }

    fun takeBanner(zoneId: String) = take(zoneId, BannerAd::class.java)
    fun takeNative(zoneId: String) = take(zoneId, NativeAd::class.java)
    fun takeInterstitial(zoneId: String) = take(zoneId, InterstitialAd::class.java)
}
