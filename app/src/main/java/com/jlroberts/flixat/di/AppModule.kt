package com.jlroberts.flixat.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import coil.ImageLoader
import coil.util.CoilUtils
import com.jlroberts.flixat.data.Repository
import com.jlroberts.flixat.data.local.FlixatDatabase
import com.jlroberts.flixat.data.remote.MoviesApi
import com.jlroberts.flixat.domain.paging.MovieListRemoteMediator
import com.jlroberts.flixat.domain.repository.RepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
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
    fun provideRepository(
        moviesApi: MoviesApi,
        database: FlixatDatabase,
        remoteMediator: MovieListRemoteMediator
    ): Repository {
        return RepositoryImpl(moviesApi, database, remoteMediator)
    }

    @Provides
    @Singleton
    fun provideRemoteMediator(database: FlixatDatabase, moviesApi: MoviesApi): MovieListRemoteMediator {
        return MovieListRemoteMediator(database, moviesApi)
    }
}