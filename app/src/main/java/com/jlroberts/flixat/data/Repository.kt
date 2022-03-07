package com.jlroberts.flixat.data

import androidx.paging.PagingData
import com.jlroberts.flixat.data.local.MovieListResultDB
import kotlinx.coroutines.flow.Flow

interface Repository {

    val popularMovies: Flow<PagingData<MovieListResultDB>>
}