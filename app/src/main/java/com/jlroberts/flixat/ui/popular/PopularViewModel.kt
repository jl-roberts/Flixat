package com.jlroberts.flixat.ui.popular

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.jlroberts.flixat.data.local.asDomainModel
import com.jlroberts.flixat.domain.model.MovieListResult
import com.jlroberts.flixat.domain.repository.MoviesRepository
import com.jlroberts.flixat.domain.repository.PreferencesManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PopularViewModel @Inject constructor(
    private val moviesRepository: MoviesRepository,
    private val preferencesManager: PreferencesManager
) :
    ViewModel() {

    private val _state = MutableStateFlow(PopularState())
    val state = _state.asStateFlow()

    init {
        getMovies()
    }

    private fun getMovies() {
        viewModelScope.launch {
            preferencesManager.getOnboardingStatus().collectLatest { onboardingComplete ->
                _state.value = state.value.copy(onboardingComplete = onboardingComplete)
                moviesRepository.popularMovies
                    .cachedIn(viewModelScope)
                    .map { pagingData ->
                        pagingData.map { it.asDomainModel() }
                    }.collectLatest {
                        _state.value = state.value.copy(movies = it)
                    }
            }
        }
    }
}