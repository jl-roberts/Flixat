package com.jlroberts.flixat.domain.model

import android.os.Parcelable
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
data class Image(val url: String) : Parcelable {

    @IgnoredOnParcel
    val medium: String = "$IMAGE_PATH/w185/$url"

    @IgnoredOnParcel
    val large: String = "$IMAGE_PATH/w342/$url"

    @IgnoredOnParcel
    val original: String = "$IMAGE_PATH/original/$url"

    companion object {
        private const val IMAGE_PATH = "https://image.tmdb.org/t/p"
    }
}