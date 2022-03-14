package com.jlroberts.flixat.ui.nowplaying

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.jlroberts.flixat.domain.model.MovieListResult
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

    private val _movies = MutableStateFlow<PagingData<MovieListResult>>(PagingData.empty())
    val movies = _movies.asStateFlow()

    private val _isCountrySet = MutableStateFlow<Boolean>(false)
    val isCountrySet = _isCountrySet.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesManager.isCountrySet().collect {
                _isCountrySet.value = it
            }
        }
    }

    fun getMovies() {
        viewModelScope.launch {
            preferencesManager.getCountryCode().collect { country ->
                moviesRepository.nowPlaying(country).cachedIn(viewModelScope)
                    .collectLatest {
                        _movies.value = it
                    }
            }
        }
    }

    fun setCountry(country: String) {
        viewModelScope.launch {
            preferencesManager.saveCountryCode(country)
        }
    }
}