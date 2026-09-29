package ir.ayantech.hamrahads.internal.presentation.interstitial

import android.os.CountDownTimer

/** Owns the skip and timeout timers; neither can outlive its dialog. */
internal class InterstitialTimers {
    private val timers = mutableListOf<CountDownTimer>()

    fun start(seconds: Int?, onTick: (Long) -> Unit = {}, onFinish: () -> Unit) {
        if (seconds == null || seconds <= 0) {
            return
        }
        timers += object : CountDownTimer(seconds.toLong() * 1000, 1000) {
            override fun onTick(millisUntilFinished: Long) = onTick((millisUntilFinished + 999) / 1000)
            override fun onFinish() = onFinish.invoke()
        }.start()
    }

    fun dispose() {
        timers.forEach { it.cancel() }
        timers.clear()
    }
}
