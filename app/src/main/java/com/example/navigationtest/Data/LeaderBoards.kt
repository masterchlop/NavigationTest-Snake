package com.example.navigationtest.Data

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "ranking")
@Parcelize
data class LeaderBoards (

    @ColumnInfo(name ="nick") var name :String="",
    @ColumnInfo(name="result") var result: Int=0,
    @ColumnInfo(name ="id") @PrimaryKey(autoGenerate = true) var id: Long = 0
) : Parcelable