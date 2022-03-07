package com.jlroberts.flixat.data.remote.model

import com.jlroberts.flixat.domain.model.Image
import com.jlroberts.flixat.domain.model.MovieListResult
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class RemoteMovieListResult(
    @Json(name = "id")
    val id: Int,
    @Json(name = "poster_path")
    val posterPath: String?
)