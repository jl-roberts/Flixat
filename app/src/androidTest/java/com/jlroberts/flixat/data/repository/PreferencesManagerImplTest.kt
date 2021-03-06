package com.jlroberts.flixat.data.repository

import android.content.Context
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.jlroberts.flixat.ui.preferences.Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class PreferencesManagerImplTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private val testScope = TestScope(testDispatcher + Job())
    private val preferenceStore = PreferenceDataStoreFactory.create(
        scope = testScope,
        produceFile = {
            ApplicationProvider.getApplicationContext<Context>()
                .preferencesDataStoreFile("test_settings")
        })
    private val preferencesManager = PreferencesManagerImpl(preferenceStore, testDispatcher)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
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
    fun saveAndRetrieveCountryCode() = testScope.runTest {
        preferencesManager.saveCountryCode("Test")

        val result = preferencesManager.getCountryCode().first()

        assertEquals("Test", result)
    }

    @Test
    fun saveAndRetrieveTheme() = testScope.runTest {
        preferencesManager.saveTheme(Theme.ThemeDark)

        val result = preferencesManager.getTheme().first()

        assertEquals(Theme.ThemeDark, result)
    }

    @Test
    fun isCountrySet() = testScope.runTest {
        preferencesManager.saveCountryCode("Test")

        val result = preferencesManager.isCountrySet().first()

        assertEquals(true, result)
    }

    @Test
    fun clearCountryCode() = testScope.runTest {
        preferencesManager.saveCountryCode("Test")

        preferencesManager.clearCountryCode()
        val result = preferencesManager.isCountrySet().first()

        assertEquals(false, result)
    }
}