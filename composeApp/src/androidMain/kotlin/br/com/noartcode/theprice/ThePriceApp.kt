package br.com.noartcode.theprice

import android.app.Activity
import android.app.Application
import android.os.Bundle
import br.com.noartcode.theprice.ui.di.KoinInitializer
import java.lang.ref.WeakReference

class ThePriceApp : Application() {

    private var currentActivity: WeakReference<Activity?> = WeakReference(null)

    fun getCurrentActivity() : Activity? = currentActivity.get()

    override fun onCreate() {
        super.onCreate()
        KoinInitializer(this).init()

        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {

            //Initialize everything. Called only once.
            override fun onActivityCreated(a: Activity, out: Bundle?) {
                currentActivity = WeakReference(a)
            }

            // Activity becomes visible
            override fun onActivityStarted(a: Activity) {
                currentActivity = WeakReference(a)
            }

            // Activity becomes interactable
            override fun onActivityResumed(a: Activity) {
                currentActivity = WeakReference(a)
            }

            // Pause interactions, save transient data
            override fun onActivityPaused(a: Activity) {}

            // Release resources, save persistent data
            override fun onActivityStopped(a: Activity) {}

            // Closely related to the onPause() and onStop() lifecycle events
            // but not a core lifecycle method.
            override fun onActivitySaveInstanceState(a: Activity, out: Bundle) {}

            // Cleanup before the Activity is removed from memory.
            override fun onActivityDestroyed(a: Activity) {
                if(currentActivity.get() == a) currentActivity = WeakReference(null)
            }

        })
    }
}