package ir.ayantech.hamrahads.internal.util

import android.app.Activity
import android.graphics.Rect
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver

object KeyboardUtils {

      fun setKeyboardVisibilityListener(
        activity: Activity,
        onKeyboardVisibilityListener: BooleanCallBack
    ) {
        activity.apply {
            val parentView = (findViewById<View>(android.R.id.content) as? ViewGroup)?.getChildAt(0)
            parentView?.viewTreeObserver?.addOnGlobalLayoutListener(object :
                ViewTreeObserver.OnGlobalLayoutListener {
                var alreadyOpen = false
                val defaultKeyboardHeightDP = 100
                val EstimatedKeyboardDP =
                    defaultKeyboardHeightDP + if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) 48 else 0
                val rect = Rect()
                override fun onGlobalLayout() {
                    val estimatedKeyboardHeight = TypedValue.applyDimension(
                        TypedValue.COMPLEX_UNIT_DIP,
                        EstimatedKeyboardDP.toFloat(),
                        parentView.resources.displayMetrics
                    ).toInt()
                    parentView.getWindowVisibleDisplayFrame(rect)
                    val heightDiff = parentView.rootView.height - (rect.bottom - rect.top)
                    val isShown = heightDiff >= estimatedKeyboardHeight
                    if (isShown == alreadyOpen) {
                        return
                    }
                    alreadyOpen = isShown
                    onKeyboardVisibilityListener(isShown)
                }
            })
        }
    }
}
