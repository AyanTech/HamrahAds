package ir.ayantech.hamrahads.data

import ir.ayantech.hamrahads.data.dto.*
import ir.ayantech.hamrahads.data.mapper.*
import kotlinx.serialization.json.Json
import org.junit.Assert.*
import org.junit.Test

class AdSerializationTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test fun `banner snake case wire names survive domain and cache round trip`() {
        val dto = json.decodeFromString<BannerAdDto>("""{
            "banner_320x50":"small", "banner_1136x640":"wide", "banner_640x1136":"tall",
            "landing_type":1, "landing_link":"https://example.test",
            "trackers":{"click":"click-url","impression":"impression-url"}, "future":true
        }""")
        assertEquals("small", dto.banner320x50)
        assertEquals("click-url", dto.toDomain().trackers?.click)
        assertEquals(dto, dto.toDomain().toDto())
        val encoded = json.encodeToString(BannerAdDto.serializer(), dto)
        assertTrue(encoded.contains("\"landing_type\""))
        assertFalse(encoded.contains("landingType"))
        assertEquals(dto, json.decodeFromString<BannerAdDto>(encoded))
    }

    @Test fun `native and interstitial map all rendering fields`() {
        val payload = """{
            "caption":"caption", "description":"description", "cta":"cta", "logo":"logo",
            "banner_320x50":"small", "banner_1136x640":"wide", "banner_640x1136":"tall",
            "video":"video", "interstitial_label":"label", "interstitial_banner":"banner",
            "landing_type":2, "landing_link":"landing", "interstitial_template":3,
            "web_template_url":"web", "time_to_skip":5, "time_out":30,
            "trackers":{"click":"click","impression":"impression"}
        }"""
        val native = json.decodeFromString<NativeAdDto>(payload)
        val interstitial = json.decodeFromString<InterstitialAdDto>(payload)
        assertEquals(5, native.toDomain().timeToSkip)
        assertEquals("web", interstitial.toDomain().webTemplateUrl)
        assertEquals(native, native.toDomain().toDto())
        assertEquals(interstitial, interstitial.toDomain().toDto())
    }

    @Test fun `missing optional response fields remain null`() {
        assertEquals(BannerAdDto(), json.decodeFromString<BannerAdDto>("{}"))
        assertEquals(NativeAdDto(), json.decodeFromString<NativeAdDto>("{}"))
        assertEquals(InterstitialAdDto(), json.decodeFromString<InterstitialAdDto>("{}"))
    }
}
