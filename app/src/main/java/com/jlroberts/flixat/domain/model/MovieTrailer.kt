package com.jlroberts.flixat.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MovieTrailer(
    val id: String = "",
    val key: String = "",
    val name: String = "",
    val site: String = "",
    val size: Int = 0,
    val type: String = ""
) : Parcelable