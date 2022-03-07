package com.jlroberts.flixat.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.jlroberts.flixat.domain.model.Image
import com.jlroberts.flixat.domain.model.MovieListResult

@Entity
data class MovieListResultDB(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val movieId: Int,
    val posterPath: Image?
)

fun MovieListResultDB.asDomainModel(): MovieListResult {
    return MovieListResult(
        movieId = movieId,
        posterPath = posterPath
    )
}