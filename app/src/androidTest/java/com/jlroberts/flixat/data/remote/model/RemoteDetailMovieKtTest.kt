package com.jlroberts.flixat.data.remote.model

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jlroberts.flixat.domain.model.Image
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import java.text.SimpleDateFormat
import java.util.*

@RunWith(AndroidJUnit4::class)
class RemoteDetailMovieKtTest {

    @Test
    fun mapAsDomainModelIsAsExpected() {
        val remoteDetailMovie = RemoteDetailMovie(
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

        val result = remoteDetailMovie.asDomainModel()

        assertEquals(remoteDetailMovie.backdropPath?.let { Image(url = it) }, result.backdropPath)
        assertEquals(remoteDetailMovie.releaseDate.let {
            SimpleDateFormat(
                "yyyy-mm-dd",
                Locale.getDefault()
            ).parse(it)
        }, result.releaseDate)

        assertEquals(remoteDetailMovie.posterPath?.let { Image(url = it) }, result.posterPath)
        assertEquals(1, result.videos?.size)
        assertEquals("YouTube", result.videos?.first()?.site)
        assertEquals("Trailer", result.videos?.first()?.type)
    }
}