package com.jlroberts.flixat.di

import android.app.Application
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
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext application: Context): FlixatDatabase {
        return Room.databaseBuilder(
            application,
            FlixatDatabase::class.java,
            "FlixatDatabase.db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideImageLoader(application: Application): ImageLoader {
        return ImageLoader.Builder(application)
            .crossfade(true)
            .okHttpClient {
                OkHttpClient.Builder()
                    .cache(CoilUtils.createDefaultCache(application))
                    .build()
            }.build()
    }

    @Provides
    @Singleton
    fun provideDatastore(@ApplicationContext context: Context): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(produceFile = { context.preferencesDataStoreFile("settings") })
    }

    @Provides
    @Singleton
    fun provideMoviesRepository(
        moviesApi: MoviesApi,
        database: FlixatDatabase,
        remoteMediator: MovieListRemoteMediator,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): MoviesRepository {
        return MoviesRepositoryImpl(
            moviesApi,
            database,
            remoteMediator,
            ioDispatcher
        )
    }

    @Provides
    @Singleton
    fun providePreferenceRepository(
        preferenceStore: DataStore<Preferences>,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): PreferencesManager {
        return PreferencesManagerImpl(preferenceStore, ioDispatcher)
    }

    @Provides
    @Singleton
    fun provideRemoteMediator(
        database: FlixatDatabase,
        moviesApi: MoviesApi
    ): MovieListRemoteMediator {
        return MovieListRemoteMediator(database, moviesApi)
    }
}