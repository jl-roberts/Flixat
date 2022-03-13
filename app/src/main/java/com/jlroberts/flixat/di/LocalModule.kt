package com.jlroberts.flixat.di

import com.jlroberts.flixat.data.local.FlixatDatabase
import com.jlroberts.flixat.data.local.MovieListResultDao
import com.jlroberts.flixat.data.local.MovieRemoteKeyDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class LocalModule {
    @Provides
    @Singleton
    fun provideMovieListResultDao(database: FlixatDatabase): MovieListResultDao {
        return database.movieListResultDao()
    }

    @Provides
    @Singleton
    fun provideMovieRemoteKeysDao(database: FlixatDatabase): MovieRemoteKeyDao {
        return database.movieRemoteKeysDao()
    }
}