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

import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: Repository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _movie = MutableStateFlow<DetailMovie?>(null)
    val movie = _movie.asStateFlow()

    private val _cast = MutableStateFlow<List<CastMember>?>(null)
    val cast = _cast.asStateFlow()

    private val _trailer = MutableStateFlow<MovieTrailer?>(null)
    val trailer = _trailer.asStateFlow()

    private val _watchProviders = MutableStateFlow<List<WatchProvider>?>(null)
    val watchProviders = _watchProviders.asStateFlow()

    init {
        viewModelScope.launch {
            val getMovie = async { repository.getMovieById(savedStateHandle["movieId"]!!) }
            val getCast = async { repository.getCast(savedStateHandle["movieId"]!!) }
            val getVideos = async { repository.getVideos(savedStateHandle["movieId"]!!) }
            val getWatchProviders = async { repository.getWatchProviders(savedStateHandle["movieId"]!!) }

                _movie.value = getMovie.await().asDomainModel()

                _cast.value = getCast.await().asDomainModel()

                try {
                    _trailer.value = getVideos.await().asDomainModel()
                        .first { it.site == "YouTube" && it.type == "Trailer" }
                } catch (e: Exception) {

                }

                 _movie.value =  repository.getMovieById(savedStateHandle["movieId"]!!).asDomainModel()

                _watchProviders.value = getWatchProviders.await().results?.asDomainModel()
        }
    }
}