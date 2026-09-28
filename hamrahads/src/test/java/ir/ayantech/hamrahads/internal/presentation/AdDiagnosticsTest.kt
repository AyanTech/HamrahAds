package ir.ayantech.hamrahads.internal.presentation

import ir.ayantech.hamrahads.internal.diagnostics.AdDiagnostics
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class AdDiagnosticsTest {
    @Test fun `disabled diagnostics are silent and enabled failures omit exception messages`() {
        ShadowLog.clear()
        AdDiagnostics.enabled = false
        AdDiagnostics.failure("image", IllegalStateException("token=secret"))
        assertTrue(ShadowLog.getLogsForTag("HamrahAds").isEmpty())
        try {
            AdDiagnostics.enabled = true
            AdDiagnostics.failure("image", IllegalStateException("token=secret"))
            val message = ShadowLog.getLogsForTag("HamrahAds").single().msg
            assertTrue(message.contains("IllegalStateException"))
            assertTrue(message.contains("AdDiagnosticsTest"))
            assertFalse(message.contains("secret"))
        } finally { AdDiagnostics.enabled = false }
    }
}
