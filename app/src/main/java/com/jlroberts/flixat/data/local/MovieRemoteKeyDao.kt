package com.jlroberts.flixat.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MovieRemoteKeyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<MovieRemoteKey>)

    @Query("SELECT * FROM movieremotekey WHERE movieId = :movieId")
    suspend fun remoteKeyByMovieId(movieId: Int): MovieRemoteKey?

    @Query("SELECT * FROM movieremotekey")
    suspend fun remoteKeys(): List<MovieRemoteKey>?

    @Query("DELETE FROM movieremotekey")
    suspend fun clearAll()
}