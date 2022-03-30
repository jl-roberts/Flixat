package com.jlroberts.flixat.ui.popular

import androidx.paging.PagingData
import com.jlroberts.flixat.domain.model.MovieListResult

data class PopularState(
    val onboardingComplete: Boolean = true,
    val movies: PagingData<MovieListResult> = PagingData.empty()
)