package com.jlroberts.flixat

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import logcat.AndroidLogcatLogger

@HiltAndroidApp
class FlixatApp : Application() {

    override fun onCreate() {
        super.onCreate()
        AndroidLogcatLogger.installOnDebuggableApp(this)
    }
}