package ir.ayantech.hamrahads.internal.diagnostics

import android.util.Log

/** Logs operation names and error codes only: never keys, device data, URLs, or payloads. */
internal object AdDiagnostics {
    @Volatile var enabled = false

    fun event(operation: String, state: String, code: String? = null) {
        if (enabled) Log.d("HamrahAds", "$operation: $state${code?.let { " [$it]" }.orEmpty()}")
    }
    fun failure(operation: String, cause: Throwable) {
        if (!enabled) return
        val frames = cause.stackTrace.take(8).joinToString("\n") { "    at $it" }
        Log.w("HamrahAds", "$operation: ${cause.javaClass.simpleName}\n$frames")
    }
}
