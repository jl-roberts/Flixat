package com.jlroberts.flixat.ui

import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import com.jlroberts.flixat.DataBindingIdlingResource
import com.jlroberts.flixat.R
import com.jlroberts.flixat.di.*
import com.jlroberts.flixat.monitorActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.hamcrest.core.IsNot.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.test.KoinTest

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class MainActivityTest : KoinTest {

    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @get:Rule
    var permissionRule = GrantPermissionRule.grant(android.Manifest.permission.ACCESS_FINE_LOCATION)

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        stopKoin()
        startKoin {
            androidContext(getApplicationContext())
            modules(
                listOf(
                    UiModule.uiModule,
                    TestModule.testModule,
                    NetworkModule.networkModule,
                    LocalModule.localModule,
                    DispatcherModule.dispatcherModule
                )
            )
        }
    }

    @After
    fun tearDown() {
    }

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
    }

    @Test
    fun testBottomAppBehavior() = runTest {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(scenario)

        onView(withId(R.id.bottom_app_bar)).check(matches(isDisplayed()))
        onView(withId(R.id.nowPlayingFragment)).check(matches(isDisplayed()))
        onView(withId(R.id.search_fab)).perform(click())
        onView(withId(R.id.bottom_app_bar)).check(matches(not(isDisplayed())))
        pressBack()
        pressBack()
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
        onView(withText("About")).perform(click())
        onView(withId(R.id.bottom_app_bar)).check(matches(not(isDisplayed())))
        pressBack()
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
        onView(withText("Preferences")).perform(click())
        onView(withId(R.id.bottom_app_bar)).check(matches(not(isDisplayed())))
        scenario.close()
    }

    @Test
    fun testFabBehavior() = runTest {
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        dataBindingIdlingResource.monitorActivity(scenario)

        onView(withId(R.id.search_fab)).check(matches(isDisplayed()))
        onView(withId(R.id.nowPlayingFragment)).perform(click())
        onView(withId(R.id.search_fab)).check(matches(isDisplayed()))
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
        onView(withText("About")).perform(click())
        onView(withId(R.id.search_fab)).check(matches(not(isDisplayed())))
        pressBack()
        openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().targetContext)
        onView(withText("Preferences")).perform(click())
        onView(withId(R.id.search_fab)).check(matches(not(isDisplayed())))
        pressBack()
        onView(withId(R.id.search_fab)).perform(click())
        onView(withId(R.id.search_fab)).check(matches(not(isDisplayed())))
        scenario.close()
    }
}