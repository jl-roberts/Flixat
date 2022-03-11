package com.jlroberts.flixat.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ExternalID(
    val imdbId: String?,
    val facebookId: String?,
    val instagramId: String?,
    val twitterId: String?
) : Parcelable