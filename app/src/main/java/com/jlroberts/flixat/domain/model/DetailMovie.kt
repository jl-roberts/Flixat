package com.jlroberts.flixat.domain.model

import java.util.*

data class DetailMovie(
    val adult: Boolean,
    val backdropPath: Image?,
    val budget: Int,
    val id: Long,
    val genres: List<Genre>,
    val homepage: String?,
    val imdbId: String?,
    val originalLanguage: String,
    val originalTitle: String,
    val overview: String,
    val popularity: Double,
    val posterPath: Image?,
    val releaseDate: Date?,
    val title: String,
    val runtime: Int?,
    val tagline: String,
    val video: Boolean,
    val status: String,
    val voteAverage: Double,
    val voteCount: Long
)