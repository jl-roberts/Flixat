package com.jlroberts.flixat.domain.model

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class Image(val url: String) : Parcelable {
    @IgnoredOnParcel
    val small: Uri = Uri.parse("$IMAGE_PATH/w92/$url")

    @IgnoredOnParcel
    val medium: Uri = Uri.parse("$IMAGE_PATH/w185/$url")

    @IgnoredOnParcel
    val large: Uri = Uri.parse("$IMAGE_PATH/w342/$url")

    @IgnoredOnParcel
    val original: Uri = Uri.parse("$IMAGE_PATH/original/$url")

    companion object {
        private const val IMAGE_PATH = "https://image.tmdb.org/t/p"
    }
}