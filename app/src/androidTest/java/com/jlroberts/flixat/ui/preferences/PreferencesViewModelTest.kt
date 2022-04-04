package com.jlroberts.flixat.ui.preferences

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.jlroberts.flixat.data.repository.PreferencesManagerImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class, kotlinx.coroutines.ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class PreferencesViewModelTest {

    private val dispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(dispatcher + Job())
    private lateinit var viewModel: PreferencesViewModel

    private val preferenceStore = PreferenceDataStoreFactory.create(
        scope = testScope,
        produceFile = {
            ApplicationProvider.getApplicationContext<Context>()
                .preferencesDataStoreFile("test_settings")
        })
    private val preferencesManager = PreferencesManagerImpl(preferenceStore, dispatcher)

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        viewModel = PreferencesViewModel(preferencesManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        File(
            ApplicationProvider.getApplicationContext<Context>().filesDir,
            "datastore"
        ).deleteRecursively()
        testScope.cancel()
    }

    @Test
    fun testGetCountry() = testScope.runTest {
        viewModel.country.test {
            assertEquals("US", awaitItem())
        }
    }

    @Test
    fun testGetTheme() = testScope.runTest {
        viewModel.theme.test {
            assertEquals(Theme.ThemeSystem, awaitItem())
        }
    }

    @Test
    fun testSetAndGetCountry() = testScope.runTest {
        viewModel.country.test {
            viewModel.setCountry("Test")
            delay(250)
            assertEquals("Test", expectMostRecentItem())
        }
    }

    @Test
    fun testSetAndGetTheme() = testScope.runTest {
        viewModel.theme.test {
            viewModel.setTheme(Theme.ThemeDark)
            delay(250)
            assertEquals(Theme.ThemeDark, expectMostRecentItem())
        }
    }

    @Test
    fun testClearCountry() = testScope.runTest {
        viewModel.country.test {
            viewModel.clearCountry()
            delay(250)
            assertEquals("", expectMostRecentItem())
        }
    }
}