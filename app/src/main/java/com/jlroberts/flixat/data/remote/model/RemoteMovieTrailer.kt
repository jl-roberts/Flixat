package com.jlroberts.flixat.data.remote.model

data class RemoteMovieTrailer(
    val id: String,
    val key: String,
    val name: String,
    val site: String,
    val size: Int,
    val type: String
)