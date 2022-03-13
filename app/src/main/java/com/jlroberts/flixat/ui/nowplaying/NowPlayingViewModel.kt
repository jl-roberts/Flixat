package com.jlroberts.flixat.ui.nowplaying

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.jlroberts.flixat.domain.model.MovieListResult
import com.jlroberts.flixat.domain.repository.MoviesRepository
import com.jlroberts.flixat.domain.repository.PreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import logcat.logcat
import javax.inject.Inject

@HiltViewModel
class NowPlayingViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModel() {

    private val _movies = MutableStateFlow<PagingData<MovieListResult>>(PagingData.empty())
    val movies = _movies.asStateFlow()

    init {
        getMovies()
    }

    private fun getMovies() {
        viewModelScope.launch {
            preferencesRepository.getCountryCode().collect { country ->
                logcat { "Country code from preferences is: $country" }
                moviesRepository.nowPlaying(country).cachedIn(viewModelScope)
                    .collectLatest {
                        _movies.value = it
                    }
            }
        }
    }

    fun setCountry(country: String) {
        viewModelScope.launch {
            preferencesRepository.saveCountryCode(country)
            getMovies()
        }
    }
}