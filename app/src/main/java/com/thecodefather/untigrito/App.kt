package com.thecodefather.untigrito

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

/**
 * UnTigritoApp Application class
 *
 * Entry point for the application with dependency injection setup using Hilt.
 * Initializes Timber for logging.
 */
@HiltAndroidApp
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Timber for logging
//        if (BuildConfig.DEBUG) {
//            Timber.plant(Timber.DebugTree())
//        }
//
//        Timber.d("Application initialized")
    }
}

