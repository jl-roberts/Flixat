package com.jlroberts.flixat.di

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module


object DispatcherModule {

    val dispatcherModule = module {
        factory { providesIoDispatcher() }
    }

    private fun providesIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}