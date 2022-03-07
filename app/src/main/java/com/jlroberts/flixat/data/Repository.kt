package com.jlroberts.flixat.data

import androidx.paging.PagingData
import com.jlroberts.flixat.data.local.MovieListResultDB
import com.jlroberts.flixat.data.remote.model.*
import com.jlroberts.flixat.domain.model.MovieListResult
import kotlinx.coroutines.flow.Flow

interface Repository {

    val popularMovies: Flow<PagingData<MovieListResultDB>>
    val inTheaters: Flow<PagingData<MovieListResult>>
    fun search(query: String): Flow<PagingData<MovieListResult>>
    suspend fun getMovieById(movieId: Long): RemoteDetailMovie
    suspend fun getCast(movieId: Long): RemoteMovieCredit
    suspend fun getVideos(movieId: Long): RemoteTrailersResponse
    suspend fun getWatchProviders(movidId: Long): RemoteWatchProviderResponse
}