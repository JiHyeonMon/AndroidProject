package com.example.ch0204

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.ch0204.dao.HistoryDao
import com.example.ch0204.model.HistoryModel

@Database(entities = [HistoryModel::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    // AppData를 생성할 때 historyDao를 가져와서 사용할 수 있게 됨.
    abstract fun historyDao(): HistoryDao

}