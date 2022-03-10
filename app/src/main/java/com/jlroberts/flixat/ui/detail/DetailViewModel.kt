package com.jlroberts.flixat.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jlroberts.flixat.data.remote.model.asDomainModel
import com.jlroberts.flixat.domain.model.DetailMovie
import com.jlroberts.flixat.domain.model.WatchProvider
import com.jlroberts.flixat.domain.repository.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import logcat.logcat
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: Repository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow<DetailState?>(null)
    val state = _state.asStateFlow()

    private val _event = MutableSharedFlow<DetailEvent>()
    val event = _event.asSharedFlow()

    private val movieId: Int? = savedStateHandle["movieId"]
    var trailerKey = ""

    init {
        viewModelScope.launch {
            _state.value = DetailState.Loading
            try {
                val getMovie = async { repository.getMovieById(movieId!!, "videos,credits,similar,watch/providers,external_ids") }
                _state.value = DetailState.Success(movie = getMovie.await().asDomainModel()).also {
                    trailerKey = it.movie.videos?.firstOrNull()?.key.toString()
                }
            } catch (e: NullPointerException) {
                _state.value = DetailState.Error
                logcat { "Error retrieving movieId" }
            }
        }
    }

    fun trailerButtonClicked() {
        viewModelScope.launch {
            _event.emit(DetailEvent.TrailerClicked)
        }
    }

    fun backClicked() {
        viewModelScope.launch {
            _event.emit(DetailEvent.BackClicked)
        }
    }
}