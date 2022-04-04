package com.jlroberts.flixat.di

import com.jlroberts.flixat.data.local.FlixatDatabase
import com.jlroberts.flixat.data.local.MovieListResultDao
import com.jlroberts.flixat.data.local.MovieRemoteKeyDao
import org.koin.dsl.module

object LocalModule {

    val localModule = module {
        single { provideMovieListResultDao(get()) }
        single { provideMovieRemoteKeysDao(get()) }
    }

    private fun provideMovieListResultDao(database: FlixatDatabase): MovieListResultDao {
        return database.movieListResultDao()
    }


    private fun provideMovieRemoteKeysDao(database: FlixatDatabase): MovieRemoteKeyDao {
        return database.movieRemoteKeysDao()
    }
}