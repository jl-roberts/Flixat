package com.jlroberts.flixat.ui.detail

import com.jlroberts.flixat.domain.model.DetailMovie

sealed class DetailState {
    data class Success(val movie: DetailMovie): DetailState()
    object Loading : DetailState()
    object Error : DetailState()
}