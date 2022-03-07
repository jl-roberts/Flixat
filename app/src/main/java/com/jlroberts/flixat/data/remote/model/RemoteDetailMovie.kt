package com.jlroberts.flixat.data.remote.model

import com.jlroberts.flixat.domain.model.DetailMovie
import com.jlroberts.flixat.domain.model.Genre
import com.jlroberts.flixat.domain.model.Image
import com.squareup.moshi.Json
import java.text.SimpleDateFormat
import java.util.*

data class RemoteDetailMovie(
    val adult: Boolean,
    @Json(name = "backdrop_path")
    val backdropPath: String?,
    val budget: Int,
    val id: Long,
    val genres: List<RemoteGenre>,
    val homepage: String?,
    @Json(name = "imdb_id")
    val imdbId: String?,
    @Json(name = "original_language")
    val originalLanguage: String,
    @Json(name = "original_title")
    val originalTitle: String,
    val overview: String,
    val popularity: Double,
    @Json(name = "poster_path")
    val posterPath: String?,
    @Json(name = "release_date")
    val releaseDate: String,
    val title: String,
    val tagline: String,
    val runtime: Int?,
    val video: Boolean,
    val status: String,
    @Json(name = "vote_average")
    val voteAverage: Double,
    @Json(name = "vote_count")
    val voteCount: Long
)

fun RemoteDetailMovie.asDomainModel(): DetailMovie {
    return DetailMovie(
        backdropPath = backdropPath?.let { path -> Image(path) },
        id = id,
        genres = genres.map {
            Genre(
                id = it.id,
                name = it.name
            )
        },
        originalLanguage = originalLanguage,
        originalTitle = originalTitle,
        overview = overview,
        popularity = popularity,
        posterPath = posterPath?.let { path -> Image(path) },
        releaseDate = releaseDate?.let { date ->
            if (date.isNotEmpty()) {
                SimpleDateFormat("yyyy-mm-dd", java.util.Locale.getDefault()).parse(date)
            } else {
                null
            }
        },
        title = title,
        video = video,
        voteAverage = voteAverage,
        voteCount = voteCount,
        adult = adult,
        budget = budget,
        homepage = homepage,
        imdbId = imdbId,
        status = status,
        runtime = runtime,
        tagline = tagline
    )
}