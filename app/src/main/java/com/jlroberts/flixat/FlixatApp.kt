package com.jlroberts.flixat

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.jlroberts.flixat.di.*
import com.jlroberts.flixat.domain.repository.PreferencesManager
import com.jlroberts.flixat.ui.preferences.Theme
import kotlinx.coroutines.*
import logcat.AndroidLogcatLogger
import org.koin.android.ext.android.inject
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class FlixatApp : Application() {

    private val coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    val preferencesManager: PreferencesManager by inject()

    override fun onCreate() {
        super.onCreate()
        AndroidLogcatLogger.installOnDebuggableApp(this)

        startKoin {
            androidContext(this@FlixatApp)
            modules(
                listOf(
                    UiModule.uiModule,
                    AppModule.appModule,
                    NetworkModule.networkModule,
                    LocalModule.localModule,
                    DispatcherModule.dispatcherModule
                )
            )
        }

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