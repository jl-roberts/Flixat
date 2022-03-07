package com.jlroberts.flixat.domain.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.jlroberts.flixat.data.local.FlixatDatabase
import com.jlroberts.flixat.data.local.MovieListResultDB
import com.jlroberts.flixat.data.local.MovieRemoteKey
import com.jlroberts.flixat.data.remote.MoviesApi
import com.jlroberts.flixat.data.remote.model.asDatabaseModel
import retrofit2.HttpException
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class MovieListRemoteMediator(
    private val database: FlixatDatabase, private val moviesApi: MoviesApi
) : RemoteMediator<Int, MovieListResultDB>() {

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MovieListResultDB>
    ): MediatorResult {
        val loadKey = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = state.anchorPosition?.let {
                    state.closestItemToPosition(it)?.movieId?.let {
                        database.withTransaction {
                            database.movieRemoteKeysDao().remoteKeyByMovieId(it)
                        }
                    }
                }
                remoteKeys?.nextKey?.minus(1) ?: 1
            }
            LoadType.PREPEND -> {
                return MediatorResult.Success(endOfPaginationReached = true)
            }
            LoadType.APPEND -> {
                val lastItem = state.lastItemOrNull()
                val remoteKey = database.withTransaction {
                    lastItem?.let {
                        database.movieRemoteKeysDao().remoteKeyByMovieId(it.movieId)
                    }
                }
                if (remoteKey?.nextKey == null) {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }

                remoteKey.nextKey
            }
        }
        try {
            val response = moviesApi.getPopularMovies(loadKey)
            val endOfPaginationReached = response.movieListResultObjects.isEmpty()
            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.movieRemoteKeysDao().clearAll()
                    database.movieListResultDao().clearAll()
                }
                val prevKey = if (loadKey == 1) null else loadKey - 1
                val nextKey = if (endOfPaginationReached) null else loadKey + 1
                val keys = response.movieListResultObjects.map {
                    MovieRemoteKey(movieId = it.id, prevKey = prevKey, nextKey = nextKey)
                }
                database.movieRemoteKeysDao().insertAll(keys)
                database.movieListResultDao().insertAll(response.asDatabaseModel())
            }
            return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: IOException) {
            return MediatorResult.Error(e)
        } catch (e: HttpException) {
            return MediatorResult.Error(e)
        }
    }
}