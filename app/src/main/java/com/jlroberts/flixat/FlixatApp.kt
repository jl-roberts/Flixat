package com.jlroberts.flixat

import android.app.Application
import androidx.databinding.DataBindingUtil
import com.jlroberts.flixat.di.BindingComponentBuilder
import com.jlroberts.flixat.di.BindingEntryPoint
import dagger.hilt.EntryPoints
import dagger.hilt.android.HiltAndroidApp
import logcat.AndroidLogcatLogger
import javax.inject.Inject
import javax.inject.Provider

@HiltAndroidApp
class FlixatApp : Application() {

    @Inject
    lateinit var bindingComponentProvider: Provider<BindingComponentBuilder>

    override fun onCreate() {
        super.onCreate()
        AndroidLogcatLogger.installOnDebuggableApp(this)

        val dataBindingComponent = bindingComponentProvider.get().build()
        val dataBindingEntryPoint = EntryPoints.get(
            dataBindingComponent, BindingEntryPoint::class.java
        )

        DataBindingUtil.setDefaultComponent(dataBindingEntryPoint)
    }
}