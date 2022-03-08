package com.jlroberts.flixat.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jlroberts.flixat.data.Repository
import com.jlroberts.flixat.data.remote.model.asDomainModel
import com.jlroberts.flixat.domain.model.CastMember
import com.jlroberts.flixat.domain.model.DetailMovie
import com.jlroberts.flixat.domain.model.MovieTrailer
import com.jlroberts.flixat.domain.model.WatchProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import logcat.logcat
import java.lang.NullPointerException

import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: Repository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _movie = MutableStateFlow<DetailMovie?>(null)
    val movie = _movie.asStateFlow()

    private val _watchProviders = MutableStateFlow<List<WatchProvider>?>(null)
    val watchProviders = _watchProviders.asStateFlow()

    private val movieId: Int? = savedStateHandle["movieId"]

    init {
        viewModelScope.launch {
            try {
                val getMovie = async { repository.getMovieById(movieId!!, "videos,credits") }
                val getWatchProviders =
                    async { repository.getWatchProviders(movieId!!) }

                _movie.value = getMovie.await().asDomainModel()
                _watchProviders.value = getWatchProviders.await().results?.asDomainModel()
            } catch (e: NullPointerException) {
                logcat { "Error retrieving movieId" }
            }
        }
    }
}