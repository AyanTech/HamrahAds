package ir.ayantech.hamrahads.internal.presentation

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import ir.ayantech.hamrahads.internal.diagnostics.AdDiagnostics
import ir.ayantech.hamrahads.listener.AdDisplayListener
import ir.ayantech.hamrahads.model.error.AdError
import ir.ayantech.hamrahads.model.error.HamrahAdsError
import java.lang.ref.WeakReference

/** Main-thread owner of a displayed ad's work, images, observers, and window resources. */
internal class AdViewSession(
    activity: AppCompatActivity,
    private val listener: AdDisplayListener,
) : DefaultLifecycleObserver {
    private val activityRef = WeakReference(activity)
    private val cleanup = mutableListOf<() -> Unit>()
    val tasks = AdTaskScope(::fail)
    val isActive: Boolean get() = tasks.isActive

    init {
        activity.lifecycle.addObserver(this)
    }

    fun activity(): AppCompatActivity? = activityRef.get()?.takeUnless {
        it.isFinishing || it.isDestroyed || !isActive
    }

    fun onDispose(action: () -> Unit) {
        if (isActive) {
            cleanup += action
        } else {
            action()
        }
    }

    fun fail(error: HamrahAdsError) {
        if (!isActive) {
            return
        }
        AdDiagnostics.event("display", "failed", error.code)
        dispose()
        listener.onError(error)
    }

    fun fail(error: AdError) = fail(error.toError())

    fun dispose() {
        if (!isActive) {
            return
        }
        tasks.cancel()
        activityRef.get()?.lifecycle?.removeObserver(this)
        val actions = cleanup.asReversed().toList()
        cleanup.clear()
        actions.forEach { action ->
            try {
                action()
            } catch (exception: Exception) {
                AdDiagnostics.event("dispose", "failed", exception.javaClass.simpleName)
            }
        }
        activityRef.clear()
    }

    override fun onDestroy(owner: LifecycleOwner) = dispose()
}
