package com.jlroberts.flixat.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.jlroberts.flixat.domain.model.MovieListResult
import com.jlroberts.flixat.domain.repository.MoviesRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class SearchViewModel constructor(private val moviesRepository: MoviesRepository) :
    ViewModel() {

    private var searchJob: Job? = null

    private val _searchResults = MutableStateFlow<PagingData<MovieListResult>>(PagingData.empty())
    val searchResults = _searchResults.asStateFlow()

    fun search(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300L)
            moviesRepository.search(query)
                .cachedIn(viewModelScope)
                .distinctUntilChanged()
                .collectLatest {
                    _searchResults.value = it
                }
        }
    }
}