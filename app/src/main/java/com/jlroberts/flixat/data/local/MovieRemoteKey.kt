package com.jlroberts.flixat.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class MovieRemoteKey(
    @PrimaryKey val movieId: Int,
    val prevKey: Int?,
    val nextKey: Int?
)