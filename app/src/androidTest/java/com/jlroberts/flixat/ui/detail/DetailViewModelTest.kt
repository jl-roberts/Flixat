package com.jlroberts.flixat.ui.detail

import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.jlroberts.flixat.data.remote.model.*
import com.jlroberts.flixat.data.repository.FakeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class DetailViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private val movieId = 1

    private lateinit var viewModel: DetailViewModel
    private val repository = FakeRepository()

    private val remoteDetailMovie = RemoteDetailMovie(
        adult = false,
        backdropPath = "testBackdropPath",
        budget = 1000,
        id = 1,
        genres = listOf(),
        homepage = "testhomepage",
        imdbId = "testimdbid",
        originalLanguage = "testoriginallanguage",
        originalTitle = "testoriginaltitle",
        overview = "testoverview",
        popularity = 0.0,
        posterPath = "testPosterPath",
        releaseDate = "2020-10-12",
        title = "testtitle",
        tagline = "testtagline",
        runtime = 1,
        video = false,
        status = "teststatus",
        voteAverage = 0.0,
        voteCount = 0L,
        videos = RemoteTrailersResponse(
            results = listOf(
                RemoteMovieTrailer(
                    id = "1",
                    key = "1",
                    name = "TestTrailer1",
                    site = "Vimeo",
                    size = 1,
                    type = "Teaser"
                ),
                RemoteMovieTrailer(
                    id = "2",
                    key = "2",
                    name = "TestTrailer2",
                    site = "YouTube",
                    size = 1,
                    type = "Teaser"
                ),
                RemoteMovieTrailer(
                    id = "3",
                    key = "3",
                    name = "TestTrailer3",
                    site = "YouTube",
                    size = 1,
                    type = "Trailer"
                )
            )
        ),
        credits = RemoteMovieCredit(
            cast = listOf()
        ),
        similar = RemoteSimilarMoviesResponse(
            results = listOf()
        ),
        watchProviders = RemoteWatchProviderResponse(
            results = Results(
                US = US(
                    buy = listOf(),
                    flatrate = listOf(),
                    rent = listOf(),
                    link = "testlink"
                )
            )
        ),
        externalIds = RemoteExternalID(
            facebookId = "testfacebookid",
            twitterId = "testtwitterid",
            imdbId = "testimdbid",
            instagramId = "testinstaid"
        )
    )


    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        repository.setDetailMovie(remoteDetailMovie)
        viewModel = DetailViewModel(movieId, repository, dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        repository.clearDetailMovie()
    }

    @OptIn(ExperimentalTime::class)
    @Test
    fun getDetailMovie() = runTest {
        viewModel.state.test {
            assertEquals(
                DetailState(
                    movie = remoteDetailMovie.asDomainModel(),
                    trailerKey = "3",
                    loading = false,
                    error = false
                ), awaitItem()
            )
        }
    }
}