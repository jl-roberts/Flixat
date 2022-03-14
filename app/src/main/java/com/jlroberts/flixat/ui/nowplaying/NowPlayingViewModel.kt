package com.jlroberts.flixat.ui.nowplaying

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.jlroberts.flixat.domain.model.MovieListResult
import com.jlroberts.flixat.domain.repository.MoviesRepository
import com.jlroberts.flixat.domain.repository.PreferencesManager
import com.jlroberts.flixat.ui.detail.DetailState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import logcat.logcat
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class NowPlayingViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository,
    private val preferencesManager: PreferencesManager,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val state = savedStateHandle.getStateFlow("state", NowPlayingState())

    init {
        getMovies()
    }

    private fun getMovies() {
        viewModelScope.launch {
            preferencesManager.getCountryCode().collect { country ->
                savedStateHandle["state"] = state.value.copy(country = country)
                moviesRepository.nowPlaying(country).cachedIn(viewModelScope)
                    .collectLatest {
                        savedStateHandle["state"] = state.value.copy(movies = it, loading = false, error = false)
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
        savedStateHandle["state"] = state.value.copy(loading = loading, error = false)
    }

    fun onError(error: Boolean) {
        savedStateHandle["state"] = state.value.copy(error = error, loading = false)
    }
}