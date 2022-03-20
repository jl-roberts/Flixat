package com.jlroberts.flixat.data.remote

import com.jlroberts.flixat.data.remote.model.RemoteDetailMovie
import com.jlroberts.flixat.data.remote.model.RemoteMovieListResponse
import java.io.IOException

class MockMovieApi : MoviesApi {

    private val defaultEmptyData = RemoteMovieListResponse(
        movieListResults = listOf(),
        page = 0,
        totalPages = 0,
        totalResults = 0
    )

    private var mockData = defaultEmptyData

    private var shouldReturnError = false

    fun setReturnError(value: Boolean) {
        shouldReturnError = value
    }

    fun addMovies(response: RemoteMovieListResponse) {
        mockData = response
    }

    fun clearMovies() {
        mockData = defaultEmptyData
    }

    override suspend fun getPopularMovies(page: Int?): RemoteMovieListResponse {
        if (shouldReturnError) {
            throw IOException()
        }
        return mockData
    }

    override suspend fun getNowPlaying(
        page: Int?,
        language: String?,
        region: String?
    ): RemoteMovieListResponse {
        if (shouldReturnError) {
            throw IOException()
        }
        return mockData
    }

    override suspend fun search(query: String, page: Int): RemoteMovieListResponse {
        if (shouldReturnError) {
            throw IOException()
        }
        return mockData
    }

    override suspend fun getMovieById(movieID: Int, appendResponse: String?): RemoteDetailMovie {
        if (shouldReturnError) {
            throw IOException()
        }
        val movie = mockData.movieListResults.first { it.id == movieID }

        return RemoteDetailMovie(
            adult = false,
            budget = 0,
            id = movie.id,
            genres = listOf(),
            backdropPath = null,
            originalLanguage = "",
            originalTitle = "",
            overview = "",
            popularity = 0.0,
            posterPath = "",
            releaseDate = "",
            title = "",
            tagline = "",
            video = false,
            status = "",
            voteAverage = 0.0,
            voteCount = 0L,
            watchProviders = null,
            credits = null,
            externalIds = null,
            homepage = null,
            imdbId = null,
            runtime = null,
            similar = null,
            videos = null
        )
    }
}