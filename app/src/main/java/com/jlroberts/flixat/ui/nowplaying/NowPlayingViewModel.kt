package com.jlroberts.flixat.ui.nowplaying

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.jlroberts.flixat.data.Repository
import com.jlroberts.flixat.data.local.asDomainModel
import com.jlroberts.flixat.domain.model.MovieListResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NowPlayingViewModel @Inject constructor(private val repository: Repository) : ViewModel() {

    private val _movies = MutableStateFlow<PagingData<MovieListResult>>(PagingData.empty())
    val movies = _movies.asStateFlow()

    init {
        getMovies()
    }

    private fun getMovies() {
        viewModelScope.launch {
            repository.inTheaters
                .cachedIn(viewModelScope).collectLatest {
                    _movies.value = it
                }
        }
    }
}