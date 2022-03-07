package com.jlroberts.flixat.data

import androidx.paging.PagingData
import com.jlroberts.flixat.data.local.MovieListResultDB
import com.jlroberts.flixat.domain.model.MovieListResult
import kotlinx.coroutines.flow.Flow

interface Repository {

    val popularMovies: Flow<PagingData<MovieListResultDB>>
    val inTheaters: Flow<PagingData<MovieListResult>>
}