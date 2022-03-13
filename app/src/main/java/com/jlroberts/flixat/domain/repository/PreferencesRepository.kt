package com.jlroberts.flixat.domain.repository

import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {

    suspend fun saveCountryCode(code: String)
    suspend fun getCountryCode(): Flow<String>
}