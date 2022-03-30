package com.jlroberts.flixat.ui.detail

import com.jlroberts.flixat.domain.model.DetailMovie

data class DetailState(
    val movie: DetailMovie? = null,
    val trailerKey: String = "",
    val loading: Boolean = true,
    val error: Boolean = false
)