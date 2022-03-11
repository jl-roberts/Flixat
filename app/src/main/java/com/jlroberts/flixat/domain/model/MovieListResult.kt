package com.jlroberts.flixat.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MovieListResult(
    val movieId: Int = 0,
    val posterPath: Image? = null
) : Parcelable