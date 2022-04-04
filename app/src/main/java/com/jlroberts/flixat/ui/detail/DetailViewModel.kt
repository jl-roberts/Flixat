package com.jlroberts.flixat.ui.detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jlroberts.flixat.data.remote.model.asDomainModel
import com.jlroberts.flixat.domain.repository.MoviesRepository
import com.jlroberts.flixat.utils.DETAIL_RESPONSES_TO_APPEND
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import logcat.logcat
import java.io.IOException

@OptIn(InternalCoroutinesApi::class)
class DetailViewModel(
    private val movieId: Int,
    private val moviesRepository: MoviesRepository,
    private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _state = MutableStateFlow(DetailState())
    val state = _state.asStateFlow()

    init {
        getDetailMovie()
    }

    private fun getDetailMovie() {
        viewModelScope.launch {
            moviesRepository.getMovieById(
                movieId,
                DETAIL_RESPONSES_TO_APPEND
            ).map { it?.asDomainModel() }
                .flowOn(dispatcher)
                .catch { exception ->
                    when (exception) {
                        is IOException -> _state.value =
                            state.value.copy(error = true, loading = false)
                        else -> logcat { "Error retrieving detail movie data, $exception.localizedMessage" }
                    }
                }
                .collect {
                    _state.value =
                        state.value.copy(
                            movie = it,
                            trailerKey = it?.videos?.firstOrNull()?.key.toString(),
                            loading = false,
                            error = false
                        )
                }
        }
    }

    fun retry() {
        getDetailMovie()
    }
}