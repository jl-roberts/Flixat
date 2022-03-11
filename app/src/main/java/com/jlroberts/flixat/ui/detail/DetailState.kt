package com.jlroberts.flixat.ui.detail

import android.os.Parcelable
import com.jlroberts.flixat.domain.model.DetailMovie
import kotlinx.parcelize.Parcelize

@Parcelize
data class DetailState(
    val movie: DetailMovie? = null,
    val loading: Boolean = true,
    val error: Boolean = false
): Parcelable