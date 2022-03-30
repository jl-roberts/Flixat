package com.jlroberts.flixat.domain.repository

import com.jlroberts.flixat.ui.preferences.Theme
import kotlinx.coroutines.flow.Flow

interface PreferencesManager {

    suspend fun saveCountryCode(code: String)
    suspend fun saveTheme(theme: Theme)
    suspend fun isCountrySet(): Flow<Boolean>
    suspend fun getCountryCode(): Flow<String>
    suspend fun clearCountryCode()
    suspend fun getTheme(): Flow<Theme>
    suspend fun getOnboardingStatus(): Flow<Boolean>
    suspend fun setOnboardingComplete()
}