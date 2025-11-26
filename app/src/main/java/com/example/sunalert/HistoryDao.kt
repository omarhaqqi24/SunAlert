package com.example.sunalert

import com.example.sunalert.HistoryEntity
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: HistoryEntity): Long

    @Query("SELECT * FROM history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<HistoryEntity>>

    @Query("DELETE FROM history WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM history")
    suspend fun deleteAll()

    @Query("SELECT * FROM history WHERE isSynced = 0")
    suspend fun getNotSynced(): List<HistoryEntity>

    @Query("UPDATE history SET isSynced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: Long)
}
