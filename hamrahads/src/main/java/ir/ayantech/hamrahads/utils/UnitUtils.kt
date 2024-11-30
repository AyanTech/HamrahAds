package ir.ayantech.hamrahads.utils

import android.app.Activity
import android.content.Context
import android.util.DisplayMetrics
import android.util.TypedValue
import java.util.Locale
import kotlin.math.log
import kotlin.math.pow

object UnitUtils {

    fun getScreenSize(activity: Activity): IntArray {
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
        return intArrayOf(displayMetrics.heightPixels, displayMetrics.widthPixels)
    }

    fun getSize(b: Int): String {
        val size = b * 1024L
        val kb = size / 1024.0
        val mb = kb / 1024.0
        val gb = mb / 1024.0
        val tb = gb / 1024.0

        return when {
            size < 1024L -> "$size Bytes"
            size >= 1024 && size < (1024L * 1024) -> String.format("%.2f KB", kb)
            size >= (1024L * 1024) && size < (1024L * 1024 * 1024) -> String.format("%.2f MB", mb)
            size >= (1024L * 1024 * 1024) && size < (1024L * 1024 * 1024 * 1024) -> String.format(
                "%.2f GB",
                gb
            )

            else -> String.format("%.2f TB", tb)
        }
    }

    fun dpToPx(dp: Float, ctx: Context): Float {
        val metrics = ctx.resources.displayMetrics
        return dp * metrics.density
    }

    fun dpToPx(dp: Int, ctx: Context): Int {
        val metrics = ctx.resources.displayMetrics
        return (dp * metrics.density).toInt()
    }

    fun pxToDp(px: Float, ctx: Context): Float {
        val metrics = ctx.resources.displayMetrics
        return px / metrics.density
    }

    fun pxToDp(px: Int, ctx: Context): Int {
        val metrics = ctx.resources.displayMetrics
        return (px / metrics.density).toInt()
    }

    fun spToPx(sp: Float, ctx: Context): Float {
        val metrics = ctx.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, metrics)
    }

    fun spToPx(sp: Int, ctx: Context): Int {
        val metrics = ctx.resources.displayMetrics
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp.toFloat(), metrics).toInt()
    }

    fun pxToSp(px: Float, ctx: Context): Float {
        val metrics = ctx.resources.displayMetrics
        return px / metrics.scaledDensity
    }

    fun pxToSp(px: Int, ctx: Context): Int {
        val metrics = ctx.resources.displayMetrics
        return (px / metrics.scaledDensity).toInt()
    }

    fun humanReadableByteCount(bytes: Long, si: Boolean): String {
        val unit = if (si) 1000 else 1024
        if (bytes < unit) return "$bytes B"
        val exp = (log(bytes.toDouble(), unit.toDouble())).toInt()
        val pre = (if (si) "kMGTPE" else "KMGTPE")[exp - 1] + if (si) "" else "i"
        return String.format(Locale.US, "%.1f %sB", bytes / unit.toDouble().pow(exp), pre)
    }
}

