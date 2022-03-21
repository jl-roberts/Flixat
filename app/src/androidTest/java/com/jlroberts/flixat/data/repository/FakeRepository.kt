package com.jlroberts.flixat.data.repository

import androidx.paging.PagingData
import com.jlroberts.flixat.data.local.MovieListResultDB
import com.jlroberts.flixat.data.local.MovieRemoteKey
import com.jlroberts.flixat.data.remote.model.RemoteDetailMovie
import com.jlroberts.flixat.domain.model.MovieListResult
import com.jlroberts.flixat.domain.repository.MoviesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf

class FakeRepository() : MoviesRepository {

    private var fakePagingData: List<MovieListResultDB> = listOf()
    private var remoteDetailMovie: RemoteDetailMovie? = null

    fun setPagingData(pagingData: List<MovieListResultDB>) {
        fakePagingData = pagingData
    }

    fun clearPagingData() {
        fakePagingData = listOf()
    }

    fun setDetailMovie(detailMovie: RemoteDetailMovie) {
        remoteDetailMovie = detailMovie
    }

    fun clearDetailMovie() {
        remoteDetailMovie = null
    }

    override val popularMovies: Flow<PagingData<MovieListResultDB>> = flow {
        PagingData.from(fakePagingData)
    }

    override suspend fun nowPlaying(region: String): Flow<PagingData<MovieListResult>> {
        return flow {
            PagingData.from(fakePagingData)
        }

    }

    override fun search(query: String): Flow<PagingData<MovieListResult>> {
        return flow {
            PagingData.from(fakePagingData)
        }
    }

    override suspend fun getMovieById(
        movieId: Int,
        appendResponse: String
    ): Flow<RemoteDetailMovie?> {
        return flowOf(remoteDetailMovie)
    }

    override suspend fun insertAllMovies(movieList: List<MovieListResultDB>) {
    }

    override suspend fun getMovies(): List<MovieListResultDB> {
        return fakePagingData
    }

    override suspend fun clearAllMovies() {
    }

    override suspend fun insertAllKeys(remoteKey: List<MovieRemoteKey>) {
    }

    override suspend fun remoteKeyByMovieId(movieId: Int): MovieRemoteKey? {
        return MovieRemoteKey(movieId = 1, prevKey = null, nextKey = 1)
    }

    override suspend fun remoteKeys(): List<MovieRemoteKey>? {
        return listOf(MovieRemoteKey(1, null, 1), MovieRemoteKey(2, null, 2))
    }

    override suspend fun clearAllKeys() {
    }
}