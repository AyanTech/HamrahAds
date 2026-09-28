package ir.ayantech.hamrahads.domain.model

import ir.ayantech.hamrahads.model.enums.BannerSize
import org.junit.Assert.*
import org.junit.Test

class AdValidationTest {
    private val trackers = Tracker(click = "click", impression = "impression")

    @Test fun `banner validation requires the requested image size`() {
        val ad = BannerAd(banner320x50 = "image", landingType = 1, landingLink = "landing", trackers = trackers)
        assertTrue(ad.isDisplayable(BannerSize.BANNER_320x50))
        assertFalse(ad.isDisplayable(BannerSize.BANNER_1136x640))
        assertFalse(ad.copy(trackers = trackers.copy(impression = " ")).isDisplayable(BannerSize.BANNER_320x50))
    }

    @Test fun `unsupported interstitial template is rejected before opening dialog`() {
        val ad = InterstitialAd(interstitialTemplate = 1, interstitialBanner = "banner", logo = "logo", caption = "caption",
            cta = "Open", landingType = 1, landingLink = "landing", trackers = trackers)
        for (template in 1..3) assertTrue(ad.copy(interstitialTemplate = template).isDisplayable())
        for (template in listOf(null, 0, 4)) assertFalse(ad.copy(interstitialTemplate = template).isDisplayable())
        assertFalse(ad.copy(logo = null).isDisplayable())
    }

    @Test fun `native validation permits optional media but requires content and trackers`() {
        val ad = NativeAd(caption = "caption", cta = "Open", landingType = 1, landingLink = "landing", trackers = trackers)
        assertTrue(ad.isDisplayable())
        assertFalse(ad.copy(caption = " ").isDisplayable())
        assertFalse(ad.copy(trackers = null).isDisplayable())
    }
}
