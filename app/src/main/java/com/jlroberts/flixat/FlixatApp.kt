package com.jlroberts.flixat

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.databinding.DataBindingUtil
import com.jlroberts.flixat.di.BindingComponentBuilder
import com.jlroberts.flixat.di.BindingEntryPoint
import com.jlroberts.flixat.domain.repository.PreferencesManager
import com.jlroberts.flixat.ui.preferences.Theme
import com.jlroberts.flixat.utils.THEME_AUTO
import com.jlroberts.flixat.utils.THEME_DARK
import com.jlroberts.flixat.utils.THEME_LIGHT
import dagger.hilt.EntryPoints
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.*
import logcat.AndroidLogcatLogger
import javax.inject.Inject
import javax.inject.Provider

@HiltAndroidApp
class FlixatApp : Application() {

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    @Inject
    lateinit var preferencesManager: PreferencesManager

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

        coroutineScope.launch {
            preferencesManager.getTheme().collect { theme ->
                when (theme) {
                    Theme.ThemeLight -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    Theme.ThemeDark -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                    Theme.ThemeSystem -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                }
            }
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        coroutineScope.cancel()
    }
}