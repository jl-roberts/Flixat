package com.jlroberts.flixat.data.paging

import androidx.paging.*
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jlroberts.flixat.data.local.FlixatDatabase
import com.jlroberts.flixat.data.local.MovieListResultDB
import com.jlroberts.flixat.data.remote.MockMovieApi
import com.jlroberts.flixat.data.remote.model.RemoteMovieListResponse
import com.jlroberts.flixat.data.remote.model.RemoteMovieListResult
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@OptIn(ExperimentalPagingApi::class, ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class MovieListRemoteMediatorTest {

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
    private val mockDb = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        FlixatDatabase::class.java
    ).build()

    @Before
    fun setUp() {
    }

    @After
    fun tearDown() {
        mockDb.clearAllTables()
        mockApi.setReturnError(false)
        mockApi.clearMovies()
    }

    @Test
    fun refreshLoadReturnsSuccessResultWhenMoreDataIsPresent() = runTest {
        mockApi.addMovies(mockMovies)
        val remoteMediator = MovieListRemoteMediator(
            mockDb,
            mockApi
        )
        val pagingState = PagingState<Int, MovieListResultDB>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @Test
    fun refreshLoadSuccessAndEndOfPaginationWhenNoMoreData() = runTest {
        val remoteMediator = MovieListRemoteMediator(
            mockDb,
            mockApi
        )
        val pagingState = PagingState<Int, MovieListResultDB>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @Test
    fun refreshLoadReturnsErrorResultWhenErrorOccurs() = runTest {
        mockApi.setReturnError(true)
        val remoteMediator = MovieListRemoteMediator(
            mockDb,
            mockApi
        )
        val pagingState = PagingState<Int, MovieListResultDB>(
            listOf(),
            null,
            PagingConfig(10),
            10
        )
        val result = remoteMediator.load(LoadType.REFRESH, pagingState)
        assertTrue(result is RemoteMediator.MediatorResult.Error)
    }
}