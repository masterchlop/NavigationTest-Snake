package com.example.navigationtest

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.navigationtest.Data.AppDatabase
import com.example.navigationtest.Data.LeaderBoards
import org.hamcrest.CoreMatchers.equalTo
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    @Test
    @Throws(Exception::class)
    fun getLeaderboard(){
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.databaseBuilder(context,AppDatabase::class.java, "ranking3.db").build()
        val dbDao = db.LeaderBoardsDAO()
        val newPlayer = LeaderBoards(id = 0, name = "Andrzej", result = 12)
        dbDao.insertRanking(newPlayer)
        val rankingList = dbDao.getLeaderboard()


        //without id - auto increment
        assertThat(rankingList[0].name,equalTo(newPlayer.name))
        assertThat(rankingList[0].result, equalTo(newPlayer.result))

        dbDao.usuwanieDoTestu(newPlayer.name)

        db.close()
    }

}