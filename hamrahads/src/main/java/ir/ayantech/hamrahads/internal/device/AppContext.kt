package ir.ayantech.hamrahads.internal.device

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.annotation.NonNull
import java.util.LinkedList

object Utils {

    @SuppressLint("StaticFieldLeak")
    private lateinit var sApplication: Application

    private val ACTIVITY_LIST: LinkedList<Activity> = LinkedList()

    private val mCallbacks = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
            setTopActivity(activity)
        }

        override fun onActivityStarted(activity: Activity) {
            setTopActivity(activity)
        }

        override fun onActivityResumed(activity: Activity) {
            setTopActivity(activity)
        }

        override fun onActivityPaused(activity: Activity) {}

        override fun onActivityStopped(activity: Activity) {}

        override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {}

        override fun onActivityDestroyed(activity: Activity) {
            ACTIVITY_LIST.remove(activity)
        }
    }

    /**
     * Init utils.
     * <p>Init it in the class of Application.</p>
     *
     * @param context context
     */
    fun init(@NonNull context: Context) {
        init(context.applicationContext as Application)
    }

    /**
     * Init utils.
     * <p>Init it in the class of Application.</p>
     *
     * @param app application
     */
    fun init(@NonNull app: Application) {
        sApplication = app
        sApplication.registerActivityLifecycleCallbacks(mCallbacks)
    }

    /**
     * Return the context of Application object.
     *
     * @return the context of Application object
     */
    fun getApp(): Application {
        if (::sApplication.isInitialized) return sApplication
        throw NullPointerException("u should init first")
    }

    private fun setTopActivity(activity: Activity) {
        if (activity.javaClass == PermissionUtils.PermissionActivity::class.java) return
        if (ACTIVITY_LIST.contains(activity)) {
            if (ACTIVITY_LIST.last != activity) {
                ACTIVITY_LIST.remove(activity)
                ACTIVITY_LIST.addLast(activity)
            }
        } else {
            ACTIVITY_LIST.addLast(activity)
        }
    }
}

