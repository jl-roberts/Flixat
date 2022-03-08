package com.jlroberts.flixat.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.jlroberts.flixat.data.local.FlixatDatabase
import com.jlroberts.flixat.data.remote.MoviesApi
import com.jlroberts.flixat.data.remote.model.RemoteDetailMovie
import com.jlroberts.flixat.data.remote.model.RemoteWatchProviderResponse
import com.jlroberts.flixat.di.IoDispatcher
import com.jlroberts.flixat.domain.model.MovieListResult
import com.jlroberts.flixat.domain.paging.MovieListRemoteMediator
import com.jlroberts.flixat.domain.paging.NowPlayingPagingSource
import com.jlroberts.flixat.domain.paging.SearchPagingSource
import com.jlroberts.flixat.domain.repository.Repository
import com.jlroberts.flixat.utils.MOVIES_PAGE_SIZE
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RepositoryImpl (
    private val moviesApi: MoviesApi,
    private val database: FlixatDatabase,
    private val remoteMediator: MovieListRemoteMediator,
    @IoDispatcher private val dispatcher: CoroutineDispatcher
) : Repository {

    @OptIn(ExperimentalPagingApi::class)
    override val popularMovies
        get() = Pager(
            config = PagingConfig(
                pageSize = MOVIES_PAGE_SIZE,
                enablePlaceholders = true
            ),
            pagingSourceFactory = { database.movieListResultDao().getMovies() },
            remoteMediator = remoteMediator
        ).flow.flowOn(dispatcher)

    override val inTheaters: Flow<PagingData<MovieListResult>>
        get() = Pager(
            config = PagingConfig(
                pageSize = MOVIES_PAGE_SIZE,
                enablePlaceholders = true
            ),
            pagingSourceFactory = { NowPlayingPagingSource(moviesApi) },
        ).flow.flowOn(dispatcher)

    override fun search(query: String) = Pager(
        config = PagingConfig(
            pageSize = MOVIES_PAGE_SIZE,
            enablePlaceholders = true
        ),
        pagingSourceFactory = { SearchPagingSource(moviesApi, query) }
    ).flow.flowOn(dispatcher)

    override suspend fun getMovieById(movieId: Int, appendResponse: String): RemoteDetailMovie =
        withContext(dispatcher) {
            moviesApi.getMovieById(movieId, appendResponse)
        }

    override suspend fun getWatchProviders(movieId: Int): RemoteWatchProviderResponse =
        withContext(dispatcher) {
            moviesApi.getWatchProviders(movieId)
        }
}