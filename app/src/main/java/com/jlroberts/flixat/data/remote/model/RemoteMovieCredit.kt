package com.jlroberts.flixat.data.remote.model

import com.jlroberts.flixat.domain.model.CastMember
import com.jlroberts.flixat.domain.model.Image

data class RemoteMovieCredit(
    val id: Int,
    val cast: List<RemoteCastMember>
)

fun RemoteMovieCredit.asDomainModel(): List<CastMember> {
    return cast.map {
        CastMember(
            castId = it.castId,
            character = it.character,
            creditId = it.creditId,
            id = it.id,
            name = it.name,
            order = it.order,
            profilePath = it.profilePath?.let { path -> Image(path) }
        )
    }
}