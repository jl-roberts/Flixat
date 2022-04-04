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
import com.jlroberts.flixat.data.repository.MoviesRepositoryImpl
import com.jlroberts.flixat.data.repository.PreferencesManagerImpl
import com.jlroberts.flixat.domain.repository.MoviesRepository
import com.jlroberts.flixat.domain.repository.PreferencesManager
import kotlinx.coroutines.CoroutineDispatcher
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

object AppModule {

    val appModule = module {
        single { provideDatabase(androidContext()) }
        single { provideImageLoader(androidContext()) }
        single { provideDatastore(androidContext()) }
        single { provideMoviesRepository(get(), get(), get(), get()) }
        single { providePreferenceRepository(get(), get()) }
        single { provideRemoteMediator(get(), get()) }
    }

    private fun provideDatabase(context: Context): FlixatDatabase {
        return Room.databaseBuilder(
            context,
            FlixatDatabase::class.java,
            "FlixatDatabase.db"
        ).build()
    }

    private fun provideImageLoader(context: Context): ImageLoader {
        return ImageLoader.Builder(context)
            .crossfade(true)
            .okHttpClient {
                OkHttpClient.Builder()
                    .cache(
                        CoilUtils.createDefaultCache(context)
                    )
                    .build()
            }.build()
    }

    private fun provideDatastore(context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(produceFile = { context.preferencesDataStoreFile("settings") })
    }

    private fun provideMoviesRepository(
        moviesApi: MoviesApi,
        database: FlixatDatabase,
        remoteMediator: MovieListRemoteMediator,
        ioDispatcher: CoroutineDispatcher
    ): MoviesRepository {
        return MoviesRepositoryImpl(
            moviesApi,
            database,
            remoteMediator,
            ioDispatcher
        )
    }

    private fun providePreferenceRepository(
        preferenceStore: DataStore<Preferences>,
        ioDispatcher: CoroutineDispatcher
    ): PreferencesManager {
        return PreferencesManagerImpl(preferenceStore, ioDispatcher)
    }

    private fun provideRemoteMediator(
        database: FlixatDatabase,
        moviesApi: MoviesApi
    ): MovieListRemoteMediator {
        return MovieListRemoteMediator(database, moviesApi)
    }
}