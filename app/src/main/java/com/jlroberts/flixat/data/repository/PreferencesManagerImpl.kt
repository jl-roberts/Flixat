package com.jlroberts.flixat.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.jlroberts.flixat.domain.repository.PreferencesManager
import com.jlroberts.flixat.ui.preferences.Theme
import com.jlroberts.flixat.utils.THEME_AUTO
import com.jlroberts.flixat.utils.THEME_DARK
import com.jlroberts.flixat.utils.THEME_LIGHT
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class PreferencesManagerImpl(private val preferenceStore: DataStore<Preferences>) :
    PreferencesManager {

    private val COUNTRY_CODE = stringPreferencesKey("country_code")
    private val COUNTRY_SET = booleanPreferencesKey("country_set")
    private val THEME = intPreferencesKey("theme")

    override suspend fun saveCountryCode(code: String) {
        preferenceStore.edit { preferences ->
            preferences[COUNTRY_CODE] = code
            preferences[COUNTRY_SET] = true
        }
    }

    override suspend fun saveTheme(theme: Theme) {
        preferenceStore.edit { preferences ->
            when(theme) {
                Theme.ThemeLight -> preferences[THEME] = THEME_LIGHT
                Theme.ThemeDark -> preferences[THEME] = THEME_DARK
                Theme.ThemeSystem -> preferences[THEME] = THEME_AUTO
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
        }
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
        }
    }

    override suspend fun clearCountryCode() {
        preferenceStore.edit { preferences ->
            preferences[COUNTRY_CODE] = ""
            preferences[COUNTRY_SET] = false
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
        }
    }
}
