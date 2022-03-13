package com.jlroberts.flixat.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(
    entities = [MovieListResultDB::class, MovieRemoteKey::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FlixatDatabase : RoomDatabase() {
    abstract fun movieListResultDao(): MovieListResultDao
    abstract fun movieRemoteKeysDao(): MovieRemoteKeyDao
}