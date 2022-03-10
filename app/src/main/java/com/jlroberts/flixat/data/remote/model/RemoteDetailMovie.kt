package com.jlroberts.flixat.data.remote.model

import com.jlroberts.flixat.domain.model.*
import com.squareup.moshi.Json
import java.text.SimpleDateFormat

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
    val voteCount: Long,
    val videos: RemoteTrailersResponse,
    val credits: RemoteMovieCredit,
    val similar: RemoteSimilarMoviesResponse,
    @Json(name = "watch/providers")
    val watchProviders: RemoteWatchProviderResponse,
    @Json(name = "external_ids")
    val externalIds: RemoteExternalID
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
        tagline = tagline,
        videos = videos.results.filter { it.type == "Trailer" && it.site == "YouTube" }.map {
            MovieTrailer(
                id = it.id,
                key = it.key,
                name = it.name,
                site = it.site,
                size = it.size,
                type = it.type
            )
        },
        credits = credits.cast.map {
            CastMember(
                castId = it.castId,
                character = it.character,
                creditId = it.creditId,
                id = it.id,
                name = it.name,
                order = it.order,
                profilePath = it.profilePath?.let { path -> Image(path) }
            )
        },
        similar = similar.results.map {
            MovieListResult(
                movieId = it.id,
                posterPath = it.posterPath?.let { path -> Image(path) }
            )
        },
        watchProviders = watchProviders.results?.asDomainModel(),
        externalIds = ExternalID(
            imdbId = externalIds.imdbId,
            facebookId = externalIds.facebookId,
            instagramId = externalIds.instagramId,
            twitterId = externalIds.twitterId
        )
    )
}