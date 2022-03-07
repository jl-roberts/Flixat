package com.jlroberts.flixat.domain.model

data class Actor(
    val id: Int,
    val profilePath: String?,
    val name: String,
    val biography: String
)