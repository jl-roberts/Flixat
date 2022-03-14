package com.jlroberts.flixat.ui.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jlroberts.flixat.domain.repository.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreferencesViewModel @Inject constructor(private val preferencesManager: PreferencesManager) :
    ViewModel() {

    private val _country: MutableStateFlow<String> = MutableStateFlow<String>("")
    val country = _country.asStateFlow()

    private val _theme: MutableStateFlow<Theme> = MutableStateFlow(Theme.ThemeSystem)
    val theme = _theme.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesManager.getCountryCode().collect {
                _country.value = it
            }
        }
        viewModelScope.launch {
            preferencesManager.getTheme().collect {
                _theme.value = it
            }
        }
    }

    fun setCountry(code: String) {
        viewModelScope.launch {
            preferencesManager.saveCountryCode(code)
        }
    }

    fun clearCountry() {
        viewModelScope.launch {
            preferencesManager.clearCountryCode()
        }
    }

    fun setTheme(theme: Theme) {
        viewModelScope.launch {
            preferencesManager.saveTheme(theme)
        }
    }
}