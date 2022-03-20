package com.jlroberts.flixat.utils

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

const val MOVIES_PAGE_SIZE = 20
const val DETAIL_RESPONSES_TO_APPEND = "videos,credits,similar,watch/providers,external_ids"
const val TMDB_URL = "https://api.themoviedb.org/3/"
const val THEME_LIGHT = 0
const val THEME_DARK = 1
const val THEME_AUTO = 2