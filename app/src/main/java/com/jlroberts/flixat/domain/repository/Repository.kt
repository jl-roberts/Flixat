package com.jlroberts.flixat.domain.repository

import androidx.paging.PagingData
import com.jlroberts.flixat.data.local.MovieListResultDB
import com.jlroberts.flixat.data.remote.model.*
import com.jlroberts.flixat.domain.model.MovieListResult
import kotlinx.coroutines.flow.Flow

interface Repository {

    val popularMovies: Flow<PagingData<MovieListResultDB>>
    val inTheaters: Flow<PagingData<MovieListResult>>
    fun search(query: String): Flow<PagingData<MovieListResult>>
    suspend fun getMovieById(movieId: Int, appendResponse: String): RemoteDetailMovie
}