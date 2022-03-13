package com.jlroberts.flixat.data.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.jlroberts.flixat.data.remote.MoviesApi
import com.jlroberts.flixat.data.remote.model.asDomainModel
import com.jlroberts.flixat.domain.model.MovieListResult
import logcat.logcat
import retrofit2.HttpException
import java.io.IOException

class NowPlayingPagingSource(private val moviesApi: MoviesApi, private val region: String) :
    PagingSource<Int, MovieListResult>() {
    override fun getRefreshKey(state: PagingState<Int, MovieListResult>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieListResult> {
        return try {
            val nextPageNumber = params.key ?: 1
            logcat { "country code in paging adapter is: $region" }
            val response = moviesApi.getNowPlaying(nextPageNumber, "en-US", region)
            LoadResult.Page(
                data = response.asDomainModel(),
                prevKey = null,
                nextKey = if (response.movieListResults.isEmpty()) null else response.page + 1
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }
    }
}