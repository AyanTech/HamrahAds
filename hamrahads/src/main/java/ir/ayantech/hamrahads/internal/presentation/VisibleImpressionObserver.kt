package ir.ayantech.hamrahads.internal.presentation

import android.graphics.Rect
import android.view.View
import android.view.ViewTreeObserver

/** Observes one view until at least half of each dimension is visible, then detaches itself. */
internal class VisibleImpressionObserver(
    private val view: View,
    private val onVisible: () -> Unit,
) : ViewTreeObserver.OnPreDrawListener, View.OnAttachStateChangeListener {
    private var disposed = false
    private var observer: ViewTreeObserver? = null

    fun start() {
        if (disposed) return
        view.addOnAttachStateChangeListener(this)
        observe()
        checkVisibility()
    }

    private fun observe() {
        observer?.takeIf { it.isAlive }?.removeOnPreDrawListener(this)
        observer = view.viewTreeObserver.also { it.addOnPreDrawListener(this) }
    }

    private fun checkVisibility() {
        if (disposed || !view.isShown || view.alpha <= 0f) return
        val visible = Rect()
        if (view.getGlobalVisibleRect(visible) && isSufficientlyVisible(
                view.width, view.height, visible.width(), visible.height()
            )) {
            dispose()
            onVisible()
        }
    }

    override fun onPreDraw(): Boolean { checkVisibility(); return true }
    override fun onViewAttachedToWindow(v: View) { observe(); checkVisibility() }
    override fun onViewDetachedFromWindow(v: View) {
        observer?.takeIf { it.isAlive }?.removeOnPreDrawListener(this)
        observer = null
    }

    fun dispose() {
        if (disposed) return
        disposed = true
        observer?.takeIf { it.isAlive }?.removeOnPreDrawListener(this)
        observer = null
        view.removeOnAttachStateChangeListener(this)
    }
}

internal fun isSufficientlyVisible(width: Int, height: Int, visibleWidth: Int, visibleHeight: Int): Boolean =
    width > 0 && height > 0 && visibleWidth.toLong() * 2 >= width && visibleHeight.toLong() * 2 >= height
