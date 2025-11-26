package com.example.sunalert

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0L,

    val timestamp: Long,
    val latitude: Double,
    val longitude: Double,
    val alamat: String,
    val uvIndex: Double,
    val kategoriRisiko: String,
    val rekomendasi: String,
    val fotoUri: String,          // local uri / storage url
    val note: String? = null,

    // flag untuk sync ke Firebase
    val isSynced: Boolean = false
)
