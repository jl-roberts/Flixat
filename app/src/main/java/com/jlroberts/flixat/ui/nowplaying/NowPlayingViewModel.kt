package com.jlroberts.flixat.ui.nowplaying

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.jlroberts.flixat.domain.repository.MoviesRepository
import com.jlroberts.flixat.domain.repository.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NowPlayingViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _state = MutableStateFlow(NowPlayingState())
    val state = _state.asStateFlow()

    init {
        getMovies()
    }

    private fun getMovies() {
        viewModelScope.launch {
            preferencesManager.getCountryCode().collect { country ->
                _state.value = state.value.copy(country = country)
                moviesRepository.nowPlaying(country).cachedIn(viewModelScope)
                    .collectLatest {
                        _state.value = state.value.copy(movies = it, loading = false, error = false)
                    }
            }
        }
    }

    fun setCountry(country: String) {
        viewModelScope.launch {
            preferencesManager.saveCountryCode(country)
        }
    }

    fun onLoading(loading: Boolean) {
        _state.value = state.value.copy(loading = loading, error = false)
    }

    fun onError(error: Boolean) {
        _state.value = state.value.copy(error = error, loading = false)
    }
}