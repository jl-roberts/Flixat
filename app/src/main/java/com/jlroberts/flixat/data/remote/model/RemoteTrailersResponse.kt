package com.jlroberts.flixat.data.remote.model

import com.jlroberts.flixat.domain.model.MovieTrailer

data class RemoteTrailersResponse(
    val results: List<RemoteMovieTrailer>
)

fun RemoteTrailersResponse.asDomainModel(): List<MovieTrailer> {
    return results.map {
        MovieTrailer(
            id = it.id,
            key = it.key,
            name = it.name,
            site = it.site,
            size = it.size,
            type = it.type
        )
    }
}