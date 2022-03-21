package com.jlroberts.flixat.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MovieListResultDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllMovies(movieList: List<MovieListResultDB>)

    @Query("SELECT * FROM movielistresultdb ORDER BY id ASC")
    fun getMovies(): PagingSource<Int, MovieListResultDB>

    @Query("SELECT * FROM MovieListResultDB ORDER BY id ASC")
    suspend fun getMoviesList(): List<MovieListResultDB>

    @Query("DELETE FROM movielistresultdb")
    suspend fun clearAllMovies()
}