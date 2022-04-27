package com.example.navigationtest.Data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LeaderBoardsDAO {

    @Query("SELECT * FROM ranking Order by result DESC Limit 20")
    fun getLeaderboard(): List<LeaderBoards>
    //
    @Insert
    fun insertRanking(vararg leaders: LeaderBoards)

    @Query("SELECT nick FROM ranking where id=1")
    fun pierwszyraz():String
}
