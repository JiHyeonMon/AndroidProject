package com.example.ch0204.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.ch0204.model.HistoryModel

@Dao
interface HistoryDao{
    @Query("SELECT * FROM HISTORYMODEL")
    fun getAll(): List<HistoryModel>

    @Insert
    fun insertHistory(history: HistoryModel)

    @Query("DELETE FROM HistoryModel")
    fun deleteAll()
}