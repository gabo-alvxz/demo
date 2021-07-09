package com.gabo.architecture.system

import android.app.Application
import com.gabo.architecture.BuildConfig
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 *    We need this base class to initialize here any common libraries that requires it, such as Timber.
 */

open class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if(BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}