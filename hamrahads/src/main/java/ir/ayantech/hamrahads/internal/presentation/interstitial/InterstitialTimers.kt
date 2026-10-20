package ir.ayantech.hamrahads.internal.presentation.interstitial

import android.os.CountDownTimer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/** Owns the skip and timeout timers; neither can outlive its dialog. */
internal class InterstitialTimers {
    private val timers = mutableListOf<CountDownTimer>()

    fun start(duration: Duration?, onTick: (Long) -> Unit = {}, onFinish: () -> Unit) {
        if (duration == null || duration <= Duration.ZERO) {
            return
        }
        timers += object : CountDownTimer(duration.inWholeMilliseconds, 1.seconds.inWholeMilliseconds) {
            override fun onTick(millisUntilFinished: Long) =
                onTick((millisUntilFinished + 999) / 1000)

            override fun onFinish() = onFinish.invoke()
        }.start()
    }

    fun dispose() {
        timers.forEach { it.cancel() }
        timers.clear()
    }
}
