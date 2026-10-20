package ir.ayantech.hamrahads.internal.presentation

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
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
    lifecycleOwner: LifecycleOwner = activity,
) : DefaultLifecycleObserver {
    private val activityRef = WeakReference(activity)
    private val ownerRef = WeakReference(lifecycleOwner)
    private val cleanup = mutableListOf<() -> Unit>()
    val tasks = AdTaskScope(::fail)
    val isActive: Boolean get() = tasks.isActive
    val isStarted: Boolean get() = ownerRef.get()?.lifecycle?.currentState?.isAtLeast(Lifecycle.State.STARTED) == true

    init {
        if (lifecycleOwner.lifecycle.currentState == Lifecycle.State.DESTROYED) {
            tasks.cancel()
            activityRef.clear()
            ownerRef.clear()
        } else {
            lifecycleOwner.lifecycle.addObserver(this)
        }
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
        ownerRef.get()?.lifecycle?.removeObserver(this)
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
        ownerRef.clear()
    }

    override fun onDestroy(owner: LifecycleOwner) = dispose()
}
