package com.jlroberts.flixat.data.paging

import androidx.paging.PagingSource
import com.jlroberts.flixat.data.remote.MockMovieApi
import com.jlroberts.flixat.data.remote.model.RemoteMovieListResponse
import com.jlroberts.flixat.data.remote.model.RemoteMovieListResult
import com.jlroberts.flixat.data.remote.model.asDomainModel
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.robolectric.annotation.Config

@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@OptIn(ExperimentalCoroutinesApi::class)
class NowPlayingPagingSourceTest {

    private val mockMovies = RemoteMovieListResponse(
        movieListResults = listOf(
            RemoteMovieListResult(id = 1, posterPath = "TestPosterPath1"),
            RemoteMovieListResult(id = 2, posterPath = "TestPosterPath2"),
            RemoteMovieListResult(id = 3, posterPath = "TestPosterPath3")
        ),
        page = 1,
        totalPages = 1000,
        totalResults = 10000
    )

    private val mockApi = MockMovieApi()

    @After
    fun tearDown() {
        mockApi.setReturnError(false)
        mockApi.clearMovies()
    }

    @Test
    fun loadReturnsPageWhenOnSuccessfulLoadOfData() = runTest {
        val pagingSource = NowPlayingPagingSource(mockApi, "US")
        mockApi.addMovies(mockMovies)
        assertEquals(
            PagingSource.LoadResult.Page(
                data = mockMovies.asDomainModel(),
                prevKey = null,
                nextKey = mockMovies.movieListResults[1].id
            ),
            pagingSource.load(
                PagingSource.LoadParams.Refresh(
                    key = null,
                    loadSize = 2,
                    placeholdersEnabled = false
                )
            )
        )
    }
}