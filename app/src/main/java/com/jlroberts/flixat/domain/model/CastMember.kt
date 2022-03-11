package com.jlroberts.flixat.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CastMember(
    val castId: Int,
    val character: String,
    val creditId: String,
    val id: Int,
    val name: String,
    val order: Int,
    val profilePath: Image?
) : Parcelable