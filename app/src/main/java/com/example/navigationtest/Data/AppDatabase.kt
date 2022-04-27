package com.example.navigationtest.Data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [(LeaderBoards::class)],version=1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun LeaderBoardsDAO(): LeaderBoardsDAO
}