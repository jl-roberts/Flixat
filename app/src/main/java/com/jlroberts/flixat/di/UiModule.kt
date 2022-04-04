package com.jlroberts.flixat.di

import com.jlroberts.flixat.ui.detail.DetailViewModel
import com.jlroberts.flixat.ui.nowplaying.NowPlayingViewModel
import com.jlroberts.flixat.ui.popular.PopularViewModel
import com.jlroberts.flixat.ui.preferences.PreferencesViewModel
import com.jlroberts.flixat.ui.search.SearchViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object UiModule {
    val uiModule = module {
        viewModel { (movieId: Int) ->
            DetailViewModel(movieId, get(), get())
        }
        viewModel {
            NowPlayingViewModel(get(), get())
        }
        viewModel {
            PopularViewModel(get(), get())
        }
        viewModel {
            PreferencesViewModel(get())
        }
        viewModel {
            SearchViewModel(get())
        }
    }
}