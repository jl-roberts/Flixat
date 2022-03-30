package com.jlroberts.flixat.ui.nowplaying

import android.os.Parcelable
import androidx.paging.PagingData
import com.jlroberts.flixat.domain.model.MovieListResult
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

@Parcelize
data class NowPlayingState(
    val movies: @RawValue PagingData<MovieListResult> = PagingData.empty(),
    val country: String = "",
    val loading: Boolean = false,
    val error: Boolean = false
) : Parcelable