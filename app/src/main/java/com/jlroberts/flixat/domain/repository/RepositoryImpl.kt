package com.jlroberts.flixat.domain.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.jlroberts.flixat.data.Repository
import com.jlroberts.flixat.data.local.FlixatDatabase
import com.jlroberts.flixat.data.local.MovieListResultDB
import com.jlroberts.flixat.data.remote.MoviesApi
import com.jlroberts.flixat.data.remote.model.RemoteDetailMovie
import com.jlroberts.flixat.data.remote.model.RemoteMovieCredit
import com.jlroberts.flixat.data.remote.model.RemoteTrailersResponse
import com.jlroberts.flixat.data.remote.model.RemoteWatchProviderResponse
import com.jlroberts.flixat.domain.model.MovieListResult
import com.jlroberts.flixat.domain.paging.MovieListRemoteMediator
import com.jlroberts.flixat.domain.paging.NowPlayingPagingSource
import com.jlroberts.flixat.domain.paging.SearchPagingSource
import com.jlroberts.flixat.utils.MOVIES_PAGE_SIZE
import kotlinx.coroutines.flow.Flow

class RepositoryImpl(
    private val moviesApi: MoviesApi,
    private val database: FlixatDatabase,
    private val remoteMediator: MovieListRemoteMediator
) : Repository {

    @OptIn(ExperimentalPagingApi::class)
    override val popularMovies get() = Pager(
        config = PagingConfig(
            pageSize = MOVIES_PAGE_SIZE,
            enablePlaceholders = true
        ),
        pagingSourceFactory = { database.movieListResultDao().getMovies() },
        remoteMediator = remoteMediator
    ).flow

    override val inTheaters: Flow<PagingData<MovieListResult>>
        get() = Pager(
            config = PagingConfig(
                pageSize = MOVIES_PAGE_SIZE,
                enablePlaceholders = true
            ),
            pagingSourceFactory = { NowPlayingPagingSource(moviesApi) },
        ).flow

    override fun search(query: String) = Pager(
        config = PagingConfig(
            pageSize = MOVIES_PAGE_SIZE,
            enablePlaceholders = true
        ),
        pagingSourceFactory = { SearchPagingSource(moviesApi, query) }
    ).flow

    override suspend fun getMovieById(movieId: Int, appendResponse: String): RemoteDetailMovie {
        return moviesApi.getMovieById(movieId, appendResponse)
    }

    override suspend fun getWatchProviders(movieId: Int): RemoteWatchProviderResponse {
        return moviesApi.getWatchProviders(movieId)
    }
}