package com.jlroberts.flixat.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.jlroberts.flixat.data.Repository
import com.jlroberts.flixat.domain.model.MovieListResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import logcat.logcat
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    private var searchJob: Job? = null

    private val _searchResults = MutableStateFlow<PagingData<MovieListResult>>(PagingData.empty())
    val searchResults = _searchResults.asStateFlow()

    private val _searchActive = MutableStateFlow(false)
    val searchActive = _searchActive.asStateFlow()

    fun search(query: String) {
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300L)
            repository.search(query)
                .cachedIn(viewModelScope)
                .distinctUntilChanged()
                .collectLatest {
                    _searchResults.value = it
                }
        }
    }

    fun onSearchFabClicked() {
        logcat { "viewModel detected fab click"}
        _searchActive.value = !searchActive.value
    }
}