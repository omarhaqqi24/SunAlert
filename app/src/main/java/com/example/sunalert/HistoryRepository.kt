package com.example.sunalert

import android.net.Uri
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class HistoryRepository(
    private val dao: HistoryDao,
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) {

    fun getAllHistory(): Flow<List<HistoryEntity>> = dao.getAllHistory()

    suspend fun getHistoryById(id: Long): HistoryEntity? = dao.getHistoryById(id)

    suspend fun insertHistory(history: HistoryEntity): Long {
        // Simpan ke Room dulu
        val newId = dao.insertHistory(history)

        // Coba sync ke Firebase
        try {
            syncToFirebase(history.copy(id = newId))
        } catch (_: Exception) {
            // Kalau gagal, nanti sync ulang
        }

        return newId
    }

    suspend fun updateHistoryPhoto(id: Long, fotoUri: String) {
        dao.updatePhoto(id, fotoUri)
    }

    suspend fun deleteHistory(history: HistoryEntity) {
        // Hapus di Firebase (best effort)
        try {
            firestore.collection("history")
                .document(history.id.toString())
                .delete()
                .await()
        } catch (_: Exception) { }

        dao.deleteById(history.id)
    }

    suspend fun deleteAllHistory() {
        dao.deleteAll()
    }

    suspend fun syncPending() {
        val unsynced = dao.getNotSynced()
        for (item in unsynced) {
            try { syncToFirebase(item) } catch (_: Exception) {}
        }
    }

    private suspend fun syncToFirebase(history: HistoryEntity) = withContext(Dispatchers.IO) {
        // Upload foto ke Firebase Storage
        val fileUri = Uri.parse(history.fotoUri)
        val fileRef = storage.reference.child("history/${history.id}_${history.timestamp}.jpg")

        fileRef.putFile(fileUri).await()
        val downloadUrl = fileRef.downloadUrl.await().toString()

        // Simpan metadata ke Firestore
        val data = mapOf(
            "timestamp" to history.timestamp,
            "latitude" to history.latitude,
            "longitude" to history.longitude,
            "alamat" to history.alamat,
            "uvIndex" to history.uvIndex,
            "kategoriRisiko" to history.kategoriRisiko,
            "rekomendasi" to history.rekomendasi,
            "fotoUrl" to downloadUrl,
            "note" to history.note
        )

        firestore.collection("history")
            .document(history.id.toString())
            .set(data)
            .await()

        dao.markAsSynced(history.id)
    }
}
