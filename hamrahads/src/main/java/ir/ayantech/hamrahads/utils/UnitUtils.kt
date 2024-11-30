package ir.ayantech.hamrahads.utils;


import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;

import java.util.Locale;

public class UnitUtils {


    private UnitUtils() {
        // private constructor
    }

    public static int[] getScreenSize(Activity activity) {
        int[] ints = new int[2];
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        ints[0] = displayMetrics.heightPixels;
        ints[1] = displayMetrics.widthPixels;
        return ints;
    }

    public static String getSize(int b) {
        String s = "";
        long size = b * 1024L;

        double kb = size / 1024;
        double mb = kb / 1024;
        double gb = mb / 1024;
        double tb = gb / 1024;
        if (size < 1024L) {
            s = size + " Bytes";
        } else if (size >= 1024 && size < (1024L * 1024)) {
            s = String.format("%.2f", kb) + " KB";
        } else if (size >= (1024L * 1024) && size < (1024L * 1024 * 1024)) {
            s = String.format("%.2f", mb) + " MB";
        } else if (size >= (1024L * 1024 * 1024) && size < (1024L * 1024 * 1024 * 1024)) {
            s = String.format("%.2f", gb) + " GB";
        } else if (size >= (1024L * 1024 * 1024 * 1024)) {
            s = String.format("%.2f", tb) + " TB";
        }
        return s;
    }

    public static float dpToPx(float dp, Context ctx) {
        DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();
        return dp * metrics.density;
    }

    public static int dpToPx(int dp, Context ctx) {
        DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();
        return (int) (dp * metrics.density);
    }

    public static float pxToDp(float px, Context ctx) {
        DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();
        return px / metrics.density;
    }

    public static int pxToDp(int px, Context ctx) {
        DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();
        return (int) (px / metrics.density);
    }

    public static float spToPx(float sp, Context ctx) {
        DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, metrics);
    }

    public static int spToPx(int sp, Context ctx) {
        DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, metrics);
    }

    public static float pxToSp(float px, Context ctx) {
        DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();
        return px / metrics.scaledDensity;
    }

    public static int pxToSp(int px, Context ctx) {
        DisplayMetrics metrics = ctx.getResources().getDisplayMetrics();
        return px / (int) metrics.scaledDensity;
    }

    public static String humanReadableByteCount(long bytes, boolean si) {
        int unit = si ? 1000 : 1024;
        if (bytes < unit) return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(unit));
        String pre = (si ? "kMGTPE" : "KMGTPE").charAt(exp - 1) + (si ? "" : "i");
        return String.format(Locale.US, "%.1f %sB", bytes / Math.pow(unit, exp), pre);
    }

}