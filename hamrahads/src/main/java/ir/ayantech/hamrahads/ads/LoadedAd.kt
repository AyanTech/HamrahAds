package ir.ayantech.hamrahads.ads

import java.util.UUID
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes

/**
 * One response, including its own trackers. Keep this object when rebinding a logical ad slot.
 * A loaded ad may be bound to only one view at a time. Close it when the slot is removed.
 */
class LoadedAd<T> internal constructor(
    val zoneId: String,
    val ad: T,
    private val lifetime: Duration = 5.minutes,
    private val clock: () -> Long = { System.nanoTime() / 1_000_000 },
) : AutoCloseable {
    val id: String = UUID.randomUUID().toString()
    private val createdAt = clock()
    private var owner: Any? = null
    private var closed = false
    internal val events = AdEvents()

    @get:Synchronized
    val isExpired: Boolean get() = closed || (clock() - createdAt).milliseconds >= lifetime

    @Synchronized
    internal fun acquire(owner: Any): Boolean {
        if (isExpired || this.owner != null) return false
        this.owner = owner
        return true
    }

    @Synchronized
    internal fun release(owner: Any) {
        if (this.owner === owner) this.owner = null
    }

    @Synchronized
    override fun close() {
        closed = true
    }
}

/** Shared across bindings of the same creative; accessed on the main thread. */
internal class AdEvents {
    var clicked = false
    var displayed = false
}
