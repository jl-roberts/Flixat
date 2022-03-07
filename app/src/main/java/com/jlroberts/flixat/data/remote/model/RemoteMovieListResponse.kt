package com.jlroberts.flixat.data.remote.model


import com.jlroberts.flixat.data.local.MovieListResultDB
import com.jlroberts.flixat.domain.model.Image
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RemoteMovieListResponse(
    @Json(name = "page")
    val page: Int,
    @Json(name = "results")
    val movieListResultObjects: List<RemoteMovieListResult>,
    @Json(name = "total_pages")
    val totalPages: Int,
    @Json(name = "total_results")
    val totalResults: Int
)

fun RemoteMovieListResponse.asDatabaseModel(): List<MovieListResultDB> {
    return movieListResultObjects.map {
        MovieListResultDB(
            0,
            movieId = it.id,
            posterPath = it.posterPath?.let { path -> Image(path) }
        )
    }
}