package com.jlroberts.flixat.data.remote.model

import com.squareup.moshi.Json

data class RemoteCastMember(
    @Json(name = "cast_id")
    val castId: Int,
    val character: String,
    @Json(name = "credit_id")
    val creditId: String,
    val id: Int,
    val name: String,
    val order: Int,
    @Json(name = "profile_path")
    val profilePath: String?
)