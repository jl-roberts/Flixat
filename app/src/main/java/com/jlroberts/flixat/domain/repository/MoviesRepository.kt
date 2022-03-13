package com.jlroberts.flixat.domain.repository

import androidx.paging.PagingData
import androidx.paging.PagingSource
import com.jlroberts.flixat.data.local.MovieListResultDB
import com.jlroberts.flixat.data.local.MovieRemoteKey
import com.jlroberts.flixat.data.remote.model.RemoteDetailMovie
import com.jlroberts.flixat.domain.model.MovieListResult
import kotlinx.coroutines.flow.Flow

interface MoviesRepository {

    val popularMovies: Flow<PagingData<MovieListResultDB>>
    suspend fun nowPlaying(region: String): Flow<PagingData<MovieListResult>>
    fun search(query: String): Flow<PagingData<MovieListResult>>
    suspend fun getMovieById(movieId: Int, appendResponse: String): Flow<RemoteDetailMovie>
    suspend fun insertAllMovies(movieList: List<MovieListResultDB>)
    suspend fun getMovies(): Flow<PagingSource<Int, MovieListResultDB>>
    suspend fun clearAllMovies()
    suspend fun insertAllKeys(remoteKey: List<MovieRemoteKey>)
    suspend fun remoteKeyByMovieId(movieId: Int): MovieRemoteKey?
    suspend fun remoteKeys(): List<MovieRemoteKey>?
    suspend fun clearAllKeys()
}