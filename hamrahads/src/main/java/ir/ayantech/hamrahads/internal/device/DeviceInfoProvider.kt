package ir.ayantech.hamrahads.internal.device

import android.content.Context
import ir.ayantech.hamrahads.data.mapper.toAdRequest
import ir.ayantech.hamrahads.domain.model.AdRequest
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

/** One metadata refresh at a time; every caller receives its own immutable request. */
internal class DeviceInfoProvider(private val application: Context) {
    private val mutex = Mutex()
    private var snapshot: DeviceInfo? = null
    private var refreshedAt = 0L

    suspend fun request(zoneId: String): AdRequest = withContext(Dispatchers.IO) {
        mutex.withLock {
            val now = System.nanoTime() / 1_000_000
            val device = snapshot?.takeIf { (now - refreshedAt).milliseconds < 1.minutes } ?: DeviceInfo()
                .fetchDeviceInfo(application).also {
                    snapshot = it
                    refreshedAt = now
                }
            device.copy(
                width = application.resources.displayMetrics.widthPixels,
                height = application.resources.displayMetrics.heightPixels,
            ).toAdRequest(zoneId)
        }
    }
}
