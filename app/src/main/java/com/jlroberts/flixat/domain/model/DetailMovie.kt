package com.jlroberts.flixat.domain.model

import java.util.*

data class DetailMovie(
    val adult: Boolean = false,
    val backdropPath: Image? = null,
    val budget: Int = 0,
    val id: Int = 0,
    val genres: List<Genre> = listOf(),
    val homepage: String? = null,
    val imdbId: String? = null,
    val originalLanguage: String = "",
    val originalTitle: String = "",
    val overview: String = "",
    val popularity: Double = 0.0,
    val posterPath: Image? = null,
    val releaseDate: Date? = null,
    val title: String = "",
    val runtime: Int? = null,
    val tagline: String = "",
    val video: Boolean = false,
    val status: String = "",
    val voteAverage: Double = 0.0,
    val voteCount: Long = 0L,
    val videos: List<MovieTrailer>?,
    val credits: List<CastMember>?,
    val similar: List<MovieListResult>?,
    val watchProviders: List<WatchProvider>?,
    val externalIds: ExternalID
)