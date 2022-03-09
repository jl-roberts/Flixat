package com.jlroberts.flixat.ui.detail

sealed class DetailEvent {
    object TrailerClicked : DetailEvent()
    object BackClicked : DetailEvent()
}