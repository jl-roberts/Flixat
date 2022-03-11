package com.jlroberts.flixat.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jlroberts.flixat.data.remote.model.asDomainModel
import com.jlroberts.flixat.domain.repository.Repository
import com.jlroberts.flixat.utils.DETAIL_RESPONSES_TO_APPEND
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import logcat.logcat
import java.io.IOException
import javax.inject.Inject

@OptIn(InternalCoroutinesApi::class)
@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: Repository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    val state = savedStateHandle.getStateFlow<DetailState>("state", DetailState())
    val trailerKey = savedStateHandle.getStateFlow("trailerKey", "")

    private val _event = MutableSharedFlow<DetailEvent>()
    val event = _event.asSharedFlow()

    private val movieId: Int? = savedStateHandle["movieId"]

    init {
        getDetailMovie()
    }

    private fun getDetailMovie() {
        viewModelScope.launch {
            repository.getMovieById(
                movieId!!,
                DETAIL_RESPONSES_TO_APPEND
            ).map { it.asDomainModel() }
                .flowOn(Dispatchers.Default)
                .catch { exception ->
                    when (exception) {
                        is IOException -> savedStateHandle["state"] = state.value.copy(error = true, loading = false)
                        else -> logcat { "Error retrieving detail movie data, $exception.localizedMessage" }
                    }
                }
                .collect {
                    savedStateHandle["state"] = state.value.copy(movie = it, loading = false, error = false)
                    savedStateHandle["trailerKey"] = it.videos?.firstOrNull()?.key.toString()
                }
        }
    }

    fun retry() {
        getDetailMovie()
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