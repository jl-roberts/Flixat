package com.jlroberts.flixat.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class WatchProvider(
    val displayPriority: Int,
    val logoPath: Image,
    val providerId: Int,
    val providerName: String
) : Parcelable