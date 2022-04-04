package com.jlroberts.flixat.domain.model

data class WatchProvider(
    val displayPriority: Int,
    val logoPath: Image,
    val providerId: Int,
    val providerName: String
)