package com.jlroberts.flixat.ui.preferences

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jlroberts.flixat.domain.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PreferencesViewModel @Inject constructor(preferencesRepository: PreferencesRepository) :
    ViewModel() {

    private val _country: MutableStateFlow<String> = MutableStateFlow<String>("")
    val country = _country.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesRepository.getCountryCode().collect {
                _country.value = it
            }
        }
    }
}