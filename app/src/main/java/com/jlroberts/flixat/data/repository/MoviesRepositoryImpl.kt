package com.jlroberts.flixat.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.jlroberts.flixat.data.local.FlixatDatabase
import com.jlroberts.flixat.data.local.MovieListResultDB
import com.jlroberts.flixat.data.local.MovieRemoteKey
import com.jlroberts.flixat.data.paging.MovieListRemoteMediator
import com.jlroberts.flixat.data.paging.NowPlayingPagingSource
import com.jlroberts.flixat.data.paging.SearchPagingSource
import com.jlroberts.flixat.data.remote.MoviesApi
import com.jlroberts.flixat.data.remote.model.RemoteDetailMovie
import com.jlroberts.flixat.domain.model.MovieListResult
import com.jlroberts.flixat.domain.repository.MoviesRepository
import com.jlroberts.flixat.utils.MOVIES_PAGE_SIZE
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class MoviesRepositoryImpl(
    private val moviesApi: MoviesApi,
    private val database: FlixatDatabase,
    private val remoteMediator: MovieListRemoteMediator,
    private val dispatcher: CoroutineDispatcher
) : MoviesRepository {

    @OptIn(ExperimentalPagingApi::class)
    override val popularMovies: Flow<PagingData<MovieListResultDB>>
        get() = Pager(
            config = PagingConfig(
                pageSize = MOVIES_PAGE_SIZE,
                enablePlaceholders = true
            ),
            pagingSourceFactory = {
                database.movieListResultDao().getMovies()
            },
            remoteMediator = remoteMediator
        ).flow.flowOn(dispatcher)

    override suspend fun nowPlaying(region: String): Flow<PagingData<MovieListResult>> {
        return Pager(
            config = PagingConfig(
                pageSize = MOVIES_PAGE_SIZE,
                enablePlaceholders = true
            ),
            pagingSourceFactory = { NowPlayingPagingSource(moviesApi, region) },
        ).flow.flowOn(dispatcher)
    }

    override fun search(query: String) = Pager(
        config = PagingConfig(
            pageSize = MOVIES_PAGE_SIZE,
            enablePlaceholders = true
        ),
        pagingSourceFactory = { SearchPagingSource(moviesApi, query) }
    ).flow.flowOn(dispatcher)

    override suspend fun getMovieById(
        movieId: Int,
        appendResponse: String
    ): Flow<RemoteDetailMovie?> = flow {
        emit(moviesApi.getMovieById(movieId, appendResponse))
    }.flowOn(dispatcher)

    override suspend fun insertAllMovies(movieList: List<MovieListResultDB>) {
        withContext(dispatcher) {
            database.movieListResultDao().insertAllMovies(movieList)
        }
    }

    override suspend fun getMovies(): List<MovieListResultDB> {
        return database.movieListResultDao().getMoviesList()
    }

    override suspend fun clearAllMovies() = database.movieListResultDao().clearAllMovies()

    override suspend fun insertAllKeys(remoteKey: List<MovieRemoteKey>) {
        database.movieRemoteKeysDao().insertAllKeys(remoteKey)
    }

    override suspend fun remoteKeyByMovieId(movieId: Int): MovieRemoteKey? {
        return database.movieRemoteKeysDao().remoteKeyByMovieId(movieId)
    }

    override suspend fun remoteKeys(): List<MovieRemoteKey>? {
        return database.movieRemoteKeysDao().remoteKeys()
    }

    override suspend fun clearAllKeys() {
        database.movieRemoteKeysDao().clearAllKeys()
    }
}