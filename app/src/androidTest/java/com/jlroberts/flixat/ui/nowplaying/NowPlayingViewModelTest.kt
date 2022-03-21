package com.jlroberts.flixat.ui.nowplaying

import app.cash.turbine.test
import com.jlroberts.flixat.data.local.MovieListResultDB
import com.jlroberts.flixat.data.repository.FakeRepository
import com.jlroberts.flixat.domain.model.Image
import com.jlroberts.flixat.domain.repository.PreferencesManager
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalCoroutinesApi::class, ExperimentalTime::class)
class NowPlayingViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: NowPlayingViewModel
    private val preferencesManager = mockk<PreferencesManager>()
    private val repository = FakeRepository()

    private val pagingData = listOf(
        MovieListResultDB(
            movieId = 1,
            posterPath = Image("testpath1")
        ),
        MovieListResultDB(
            movieId = 2,
            posterPath = Image("testpath2")
        ),
        MovieListResultDB(
            movieId = 3,
            posterPath = Image("testpath3")
        )
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        repository.setPagingData(pagingData)
        coEvery { preferencesManager.getCountryCode() } returns flowOf("Test")
        viewModel = NowPlayingViewModel(repository, preferencesManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        repository.clearPagingData()
    }

    @Test
    fun testRetrieveCountryCode() = runTest {
        viewModel.state.test {
            assertEquals("Test", awaitItem().country)
        }
    }

    @Test
    fun testLoadingFalseWithData() = runTest {
        viewModel.state.test {
            assertEquals(false, awaitItem().loading)
        }
    }

    @Test
    fun testErrorFalseWithData() = runTest {
        viewModel.state.test {
            assertEquals(false, awaitItem().error)
        }
    }
}