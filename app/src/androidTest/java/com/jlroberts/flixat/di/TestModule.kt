package com.jlroberts.flixat.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.room.Room
import coil.ImageLoader
import coil.util.CoilUtils
import com.jlroberts.flixat.data.local.FlixatDatabase
import com.jlroberts.flixat.data.paging.MovieListRemoteMediator
import com.jlroberts.flixat.data.remote.MoviesApi
import com.jlroberts.flixat.data.repository.FakeRepository
import com.jlroberts.flixat.data.repository.PreferencesManagerImpl
import com.jlroberts.flixat.domain.repository.MoviesRepository
import com.jlroberts.flixat.domain.repository.PreferencesManager
import com.jlroberts.flixat.ui.preferences.Theme
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

object TestModule {

    val testModule = module {
        single { provideDatabase(androidContext()) }
        single { provideImageLoader(androidContext()) }
        single { provideDatastore(androidContext()) }
        single { provideMoviesRepository() }
        single { providePreferenceRepository() }
        single { provideRemoteMediator(get(), get()) }
    }

    fun provideDatabase(context: Context): FlixatDatabase {
        return Room.inMemoryDatabaseBuilder(
            context,
            FlixatDatabase::class.java
        ).build()
    }

    fun provideImageLoader(context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .okHttpClient {
                OkHttpClient.Builder()
                    .cache(
                        CoilUtils.createDefaultCache(context)
                    )
                    .build()
            }.build()
    }

    fun provideDatastore(context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(produceFile = { context.preferencesDataStoreFile("test_settings") })
    }

    fun provideMoviesRepository(
    ): MoviesRepository {
        return FakeRepository()
    }

    fun providePreferenceRepository(
    ): PreferencesManager {
        val mockPreferencesManager = mockk<PreferencesManagerImpl>()
        coEvery { mockPreferencesManager.getOnboardingStatus() } returns flowOf(true)
        coEvery { mockPreferencesManager.getCountryCode() } returns flowOf("US")
        coEvery { mockPreferencesManager.getTheme() } returns flowOf(Theme.ThemeSystem)
        return mockPreferencesManager
    }

    fun provideRemoteMediator(
        database: FlixatDatabase,
        moviesApi: MoviesApi
    ): MovieListRemoteMediator {
        return MovieListRemoteMediator(database, moviesApi)
    }
}