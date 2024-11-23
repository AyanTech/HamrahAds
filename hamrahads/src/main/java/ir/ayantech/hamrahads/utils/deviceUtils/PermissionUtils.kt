package ir.ayantech.hamrahads.utils.deviceUtils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.WindowManager
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat

/**
 * <pre>
 *     author: Blankj
 *     blog  : http://blankj.com
 *     time  : 2017/12/29
 *     desc  : utils about permission
 * </pre>
 */
class PermissionUtils private constructor(vararg permissions: String) {

    private val mPermissions: MutableSet<String> = LinkedHashSet()
    private val mPermissionsRequest: MutableList<String> = ArrayList()
    private val mPermissionsGranted: MutableList<String> = ArrayList()
    private val mPermissionsDenied: MutableList<String> = ArrayList()
    private val mPermissionsDeniedForever: MutableList<String> = ArrayList()

    private var mOnRationaleListener: OnRationaleListener? = null
    private var mSimpleCallback: SimpleCallback? = null
    private var mFullCallback: FullCallback? = null
    private var mThemeCallback: ThemeCallback? = null

    init {
        for (permission in permissions) {
            for (aPermission in PermissionConstants.getPermissions(permission)) {
                if (PERMISSIONS.contains(aPermission)) {
                    mPermissions.add(aPermission)
                }
            }
        }
        sInstance = this
    }

    companion object {
        private val PERMISSIONS: List<String> = getPermissions()
        private var sInstance: PermissionUtils? = null

        /**
         * Return the permissions used in application.
         *
         * @return the permissions used in application
         */
        fun getPermissions(): List<String> {
            return getPermissions(Utils.getApp().packageName)
        }

        /**
         * Return the permissions used in application.
         *
         * @param packageName The name of the package.
         * @return the permissions used in application
         */
        fun getPermissions(packageName: String): List<String> {
            val pm = Utils.getApp().packageManager
            return try {
                pm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS).requestedPermissions?.toList() ?: emptyList()
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
                emptyList()
            }
        }

        /**
         * Return whether <em>you</em> have granted the permissions.
         *
         * @param permissions The permissions.
         * @return {@code true}: yes<br>{@code false}: no
         */
        fun isGranted(vararg permissions: String): Boolean {
            return permissions.all { isGranted(it) }
        }

        private fun isGranted(permission: String): Boolean {
            return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                    ContextCompat.checkSelfPermission(Utils.getApp(), permission) == PackageManager.PERMISSION_GRANTED
        }

        /**
         * Launch the application's details settings.
         */
        fun launchAppDetailsSettings() {
            val intent = Intent("android.settings.APPLICATION_DETAILS_SETTINGS").apply {
                data = Uri.parse("package:${Utils.getApp().packageName}")
            }
            Utils.getApp().startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))
        }

        /**
         * Set the permissions.
         *
         * @param permissions The permissions.
         * @return the single [PermissionUtils] instance
         */
        fun permission(vararg permissions: String): PermissionUtils {
            return PermissionUtils(*permissions)
        }
    }

    /**
     * Set rationale listener.
     *
     * @param listener The rationale listener.
     * @return the single [PermissionUtils] instance
     */
    fun rationale(listener: OnRationaleListener): PermissionUtils {
        mOnRationaleListener = listener
        return this
    }

    /**
     * Set the simple call back.
     *
     * @param callback the simple call back
     * @return the single [PermissionUtils] instance
     */
    fun callback(callback: SimpleCallback): PermissionUtils {
        mSimpleCallback = callback
        return this
    }

    /**
     * Set the full call back.
     *
     * @param callback the full call back
     * @return the single [PermissionUtils] instance
     */
    fun callback(callback: FullCallback): PermissionUtils {
        mFullCallback = callback
        return this
    }

    /**
     * Set the theme callback.
     *
     * @param callback The theme callback.
     * @return the single [PermissionUtils] instance
     */
    fun theme(callback: ThemeCallback): PermissionUtils {
        mThemeCallback = callback
        return this
    }

    /**
     * Start request.
     */
    fun request() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mPermissionsGranted.addAll(mPermissions)
            requestCallback()
        } else {
            for (permission in mPermissions) {
                if (isGranted(permission)) {
                    mPermissionsGranted.add(permission)
                } else {
                    mPermissionsRequest.add(permission)
                }
            }
            if (mPermissionsRequest.isEmpty()) {
                requestCallback()
            } else {
                startPermissionActivity()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun startPermissionActivity() {
        mPermissionsDenied.clear()
        mPermissionsDeniedForever.clear()
        PermissionActivity.start(Utils.getApp())
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun rationale(activity: Activity): Boolean {
        var isRationale = false
        mOnRationaleListener?.let { listener ->
            for (permission in mPermissionsRequest) {
                if (activity.shouldShowRequestPermissionRationale(permission)) {
                    getPermissionsStatus(activity)
                    listener.rationale(object : OnRationaleListener.ShouldRequest {
                        override fun again(again: Boolean) {
                            if (again) {
                                startPermissionActivity()
                            } else {
                                requestCallback()
                            }
                        }
                    })
                    isRationale = true
                    break
                }
            }
            mOnRationaleListener = null
        }
        return isRationale
    }

    private fun getPermissionsStatus(activity: Activity) {
        for (permission in mPermissionsRequest) {
            if (isGranted(permission)) {
                mPermissionsGranted.add(permission)
            } else {
                mPermissionsDenied.add(permission)
                if (!activity.shouldShowRequestPermissionRationale(permission)) {
                    mPermissionsDeniedForever.add(permission)
                }
            }
        }
    }

    private fun requestCallback() {
        mSimpleCallback?.let {
            if (mPermissionsRequest.isEmpty() || mPermissions.size == mPermissionsGranted.size) {
                it.onGranted()
            } else if (mPermissionsDenied.isNotEmpty()) {
                it.onDenied()
            }
            mSimpleCallback = null
        }
        mFullCallback?.let {
            if (mPermissionsRequest.isEmpty() || mPermissions.size == mPermissionsGranted.size) {
                it.onGranted(mPermissionsGranted)
            } else if (mPermissionsDenied.isNotEmpty()) {
                it.onDenied(mPermissionsDeniedForever, mPermissionsDenied)
            }
            mFullCallback = null
        }
        mOnRationaleListener = null
        mThemeCallback = null
    }

    private fun onRequestPermissionsResult(activity: Activity) {
        getPermissionsStatus(activity)
        requestCallback()
    }

    @RequiresApi(Build.VERSION_CODES.M)
    class PermissionActivity : Activity() {

        companion object {
            fun start(context: Context) {
                val starter = Intent(context, PermissionActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(starter)
            }
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            window.addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH)
            if (sInstance == null) {
                super.onCreate(savedInstanceState)
                Log.e("PermissionUtils", "request permissions failed")
                finish()
                return
            }
            sInstance?.mThemeCallback?.onActivityCreate(this)
            super.onCreate(savedInstanceState)

            if (sInstance?.rationale(this) == true) {
                finish()
                return
            }
            sInstance?.mPermissionsRequest?.let {
                if (it.isEmpty()) {
                    finish()
                    return
                }
                requestPermissions(it.toTypedArray(), 1)
            }
        }

        override fun onRequestPermissionsResult(requestCode: Int, @NonNull permissions: Array<out String>, @NonNull grantResults: IntArray) {
            sInstance?.onRequestPermissionsResult(this)
            finish()
        }

        override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
            finish()
            return true
        }
    }

    interface OnRationaleListener {
        fun rationale(shouldRequest: ShouldRequest)

        interface ShouldRequest {
            fun again(again: Boolean)
        }
    }

    interface SimpleCallback {
        fun onGranted()
        fun onDenied()
    }

    interface FullCallback {
        fun onGranted(permissionsGranted: List<String>)
        fun onDenied(permissionsDeniedForever: List<String>, permissionsDenied: List<String>)
    }

    interface ThemeCallback {
        fun onActivityCreate(activity: Activity)
    }
}

