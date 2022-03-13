package com.jlroberts.flixat.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.jlroberts.flixat.domain.repository.PreferencesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import logcat.logcat
import java.io.IOException

class PreferencesRepositoryImpl(private val preferenceStore: DataStore<Preferences>) :
    PreferencesRepository {

    private val COUNTRY_CODE = stringPreferencesKey("country_code")

    override suspend fun saveCountryCode(code: String) {
        preferenceStore.edit { preferences ->
            preferences[COUNTRY_CODE] = code
        }.also {
            logcat { "$code saved in DataStore" }
        }
    }

    override suspend fun getCountryCode(): Flow<String> {
        return preferenceStore.data.catch { exception ->
            if (exception is IOException) {
                logcat { "io exception in retrieving country code" }
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            preferences[COUNTRY_CODE] ?: "US"
        }.also {
            logcat { " code attempted to be retreived from datastore" }
        }
    }
}
