package com.jlroberts.flixat.ui.popular

import android.os.Bundle
import android.view.View
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.paging.PagingData
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.swipeDown
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.MediumTest
import com.jlroberts.flixat.R
import com.jlroberts.flixat.di.DispatcherModule
import com.jlroberts.flixat.di.LocalModule
import com.jlroberts.flixat.di.NetworkModule
import com.jlroberts.flixat.di.TestModule
import com.jlroberts.flixat.domain.model.Image
import com.jlroberts.flixat.domain.model.MovieListResult
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module
import org.koin.test.KoinTest

@MediumTest
class PopularFragmentTest : KoinTest {

    private val mockViewModel = mockk<PopularViewModel>(relaxed = true)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        stopKoin()
        startKoin {
            androidContext(ApplicationProvider.getApplicationContext())
            modules(
                listOf(
                    module {
                        viewModel {
                            mockViewModel
                        }
                    },
                    TestModule.testModule,
                    NetworkModule.networkModule,
                    LocalModule.localModule,
                    DispatcherModule.dispatcherModule
                )
            )
        }
        val list: MutableList<MovieListResult> = mutableListOf()
        for (i in 1..10) {
            list.add(
                MovieListResult(
                    movieId = i,
                    posterPath = Image("Test path $i")
                )
            )
        }
        val state = MutableStateFlow(
            PopularState(
                movies = PagingData.from(list),
                onboardingComplete = true
            )
        )
        coEvery { mockViewModel.state } returns state.asStateFlow()
    }

    @Test
    fun testPullToRefresh() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        val scenario = launchFragmentInContainer(Bundle(), R.style.Theme_Flixat) {
            PopularFragment().also { fragment ->
                fragment.viewLifecycleOwnerLiveData.observeForever { viewLifecycleOwner ->
                    if (viewLifecycleOwner != null) {
                        navController.setGraph(R.navigation.nav_graph)
                        Navigation.setViewNavController(fragment.requireView(), navController)
                    }
                }
            }
        }

        onView(withId(R.id.recycler_view)).perform(swipeDown())

        onView(withId(R.id.feed_refresh)).check(matches(isRefreshing()))
        scenario.close()
    }

    @Test
    fun testRvItems() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        val scenario = launchFragmentInContainer(Bundle(), R.style.Theme_Flixat) {
            PopularFragment().also { fragment ->
                fragment.viewLifecycleOwnerLiveData.observeForever { viewLifecycleOwner ->
                    if (viewLifecycleOwner != null) {
                        navController.setGraph(R.navigation.nav_graph)
                        Navigation.setViewNavController(fragment.requireView(), navController)
                    }
                }
            }
        }

        onView(withId(R.id.recycler_view)).check { view, noViewFoundException ->
            if (noViewFoundException != null) {
                throw noViewFoundException
            }

            val recyclerView = view as RecyclerView
            assertEquals(10, recyclerView.adapter?.itemCount)
        }
        scenario.close()
    }

    fun isRefreshing() = object : TypeSafeMatcher<View>() {
        override fun describeTo(description: Description) {
            description.appendText("is a SwipeRefreshLayout that is refreshing")
        }

        override fun matchesSafely(item: View?) =
            (item as SwipeRefreshLayout).isRefreshing
    }
}