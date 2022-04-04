package com.jlroberts.flixat.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.jlroberts.flixat.domain.repository.PreferencesManager
import com.jlroberts.flixat.ui.preferences.Theme
import com.jlroberts.flixat.utils.THEME_AUTO
import com.jlroberts.flixat.utils.THEME_DARK
import com.jlroberts.flixat.utils.THEME_LIGHT
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException

class PreferencesManagerImpl(
    private val preferenceStore: DataStore<Preferences>,
    private val dispatcher: CoroutineDispatcher
) :
    PreferencesManager {

    companion object {
        val COUNTRY_CODE = stringPreferencesKey("country_code")
        val COUNTRY_SET = booleanPreferencesKey("country_set")
        val THEME = intPreferencesKey("theme")
        val ONBOARDING_COMPLETE = booleanPreferencesKey("onboarding_complete")
    }

    override suspend fun saveCountryCode(code: String) {
        withContext(dispatcher) {
            preferenceStore.edit { preferences ->
                preferences[COUNTRY_CODE] = code
                preferences[COUNTRY_SET] = true
            }
        }
    }

    override suspend fun saveTheme(theme: Theme) {
        withContext(dispatcher) {
            preferenceStore.edit { preferences ->
                when (theme) {
                    Theme.ThemeLight -> preferences[THEME] = THEME_LIGHT
                    Theme.ThemeDark -> preferences[THEME] = THEME_DARK
                    Theme.ThemeSystem -> preferences[THEME] = THEME_AUTO
                }
            }
        }
    }

    override suspend fun isCountrySet(): Flow<Boolean> {
        return preferenceStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            preferences[COUNTRY_SET] ?: false
        }.flowOn(dispatcher)
    }

    override suspend fun getCountryCode(): Flow<String> {
        return preferenceStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            preferences[COUNTRY_CODE] ?: "US"
        }.flowOn(dispatcher)
    }

    override suspend fun clearCountryCode() {
        withContext(dispatcher) {
            preferenceStore.edit { preferences ->
                preferences[COUNTRY_CODE] = ""
                preferences[COUNTRY_SET] = false
            }
        }
    }

    override suspend fun getTheme(): Flow<Theme> {
        return preferenceStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            when (preferences[THEME]) {
                THEME_LIGHT -> Theme.ThemeLight
                THEME_DARK -> Theme.ThemeDark
                THEME_AUTO -> Theme.ThemeSystem
                else -> Theme.ThemeSystem
            }
        }.flowOn(dispatcher)
    }

    override suspend fun setOnboardingComplete() {
        withContext(dispatcher) {
            preferenceStore.edit { preferences ->
                preferences[ONBOARDING_COMPLETE] = true
            }
        }
    }

    override suspend fun getOnboardingStatus(): Flow<Boolean> {
        return preferenceStore.data.catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            preferences[ONBOARDING_COMPLETE] ?: false
        }.flowOn(dispatcher)
    }

    override suspend fun clearAll() {
        preferenceStore.edit {
            it.clear()
        }
    }
}
