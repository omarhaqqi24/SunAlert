package com.example.sunalert

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [HistoryEntity::class],
    version = 1,
    exportSchema = false
)
abstract class SunAlertDatabase : RoomDatabase() {

    abstract fun historyDao(): HistoryDao

    companion object {
        @Volatile
        private var INSTANCE: SunAlertDatabase? = null

        fun getInstance(context: Context): SunAlertDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    SunAlertDatabase::class.java,
                    "sunalert_db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
