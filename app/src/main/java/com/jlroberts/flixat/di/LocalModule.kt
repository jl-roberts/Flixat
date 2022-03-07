package com.jlroberts.flixat.di

import android.content.Context
import androidx.room.Room
import com.jlroberts.flixat.data.local.FlixatDatabase
import com.jlroberts.flixat.data.local.MovieListResultDao
import com.jlroberts.flixat.data.local.MovieRemoteKeyDao
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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