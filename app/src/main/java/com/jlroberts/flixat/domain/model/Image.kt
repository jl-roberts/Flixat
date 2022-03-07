package com.jlroberts.flixat.domain.model

import android.net.Uri

data class Image(val url: String) {
    val small: Uri = Uri.parse("$IMAGE_PATH/w92/$url")
    val medium: Uri = Uri.parse("$IMAGE_PATH/w185/$url")
    val large: Uri = Uri.parse("$IMAGE_PATH/w342/$url")
    val original: Uri = Uri.parse("$IMAGE_PATH/original/$url")

    companion object {
        private const val IMAGE_PATH = "https://image.tmdb.org/t/p"
    }
}