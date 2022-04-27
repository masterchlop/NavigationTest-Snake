package com.example.navigationtest.Data

import androidx.room.Dao
import androidx.room.Delete
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

    @Query("DELETE from ranking where nick = :Andrzej")
    fun usuwanieDoTestu(Andrzej: String)

}
