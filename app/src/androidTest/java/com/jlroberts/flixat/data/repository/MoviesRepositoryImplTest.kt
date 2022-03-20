package com.jlroberts.flixat.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.jlroberts.flixat.data.local.FlixatDatabase
import com.jlroberts.flixat.data.local.MovieRemoteKey
import com.jlroberts.flixat.data.paging.MovieListRemoteMediator
import com.jlroberts.flixat.data.remote.MockMovieApi
import com.jlroberts.flixat.data.remote.model.RemoteMovieListResponse
import com.jlroberts.flixat.data.remote.model.RemoteMovieListResult
import com.jlroberts.flixat.data.remote.model.asDatabaseModel
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Test
import org.robolectric.annotation.Config

@HiltAndroidTest
@Config(application = HiltTestApplication::class)
class MoviesRepositoryImplTest {

    private val database = Room.inMemoryDatabaseBuilder(
        ApplicationProvider.getApplicationContext(),
        FlixatDatabase::class.java
    ).build()
    private val moviesApi = MockMovieApi()
    private val mediator = mockk<MovieListRemoteMediator>()

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

    private val keys =
        listOf(
            MovieRemoteKey(
                1,
                null,
                1
            ),
            MovieRemoteKey(
                2,
                1,
                2
            ), MovieRemoteKey(
                3,
                2,
                3
            )
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    private val repository = MoviesRepositoryImpl(
        database = database,
        moviesApi = moviesApi,
        remoteMediator = mediator,
        dispatcher = UnconfinedTestDispatcher()
    )

    @After
    fun tearDown() {
        database.clearAllTables()
        moviesApi.clearMovies()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun getMovieById() = runTest {
        moviesApi.addMovies(mockMovies)
        val id = 1

        val result = repository.getMovieById(id, "").first()
        assertEquals(id, result?.id)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertAllMovies() = runTest {
        repository.insertAllMovies(mockMovies.asDatabaseModel())

        val results = repository.getMovies()
        assertEquals(results[0].movieId, mockMovies.movieListResults[0].id)
        assertEquals(results[1].movieId, mockMovies.movieListResults[1].id)
        assertEquals(results[2].movieId, mockMovies.movieListResults[2].id)
        assertEquals(mockMovies.movieListResults.size, results.size)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun clearAllMovies() = runTest {
        repository.insertAllMovies(mockMovies.asDatabaseModel())
        repository.clearAllMovies()

        val results = repository.getMovies()
        assertEquals(0, results.size)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun insertAndGetAllKeys() = runTest {
        repository.insertAllKeys(keys)

        val results = repository.remoteKeys()
        assertEquals(3, results?.size)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun remoteKeyByMovieId() = runTest {
        repository.insertAllKeys(keys)

        val id = 1
        val results = repository.remoteKeyByMovieId(id)
        assertEquals(id, results?.movieId)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun clearAllKeys() = runTest {
        repository.insertAllKeys(keys)
        repository.clearAllKeys()

        val results = repository.remoteKeys()
        assertEquals(0, results?.size)
    }
}