package com.jlroberts.flixat.domain.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.jlroberts.flixat.data.remote.MoviesApi
import com.jlroberts.flixat.data.remote.model.asDomainModel
import com.jlroberts.flixat.domain.model.MovieListResult
import retrofit2.HttpException
import java.io.IOException

class SearchPagingSource(private val moviesApi: MoviesApi, private val query: String) :
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
            val response = moviesApi.search(query, nextPageNumber)
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