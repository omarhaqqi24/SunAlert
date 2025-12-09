package com.example.sunalert

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    var lastHistoryId: Long? = null

    private val db = SunAlertDatabase.getInstance(application)
    private val repository = HistoryRepository(
        dao = db.historyDao(),
        firestore = FirebaseFirestore.getInstance(),
        storage = FirebaseStorage.getInstance()
    )

    val historyList: StateFlow<List<HistoryEntity>> =
        repository.getAllHistory()
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                emptyList()
            )

    init {
        viewModelScope.launch { repository.syncPending() }
    }

    fun insertHistory(history: HistoryEntity, onResult: (Long) -> Unit) {
        viewModelScope.launch {
            val id = repository.insertHistory(history)
            lastHistoryId = id
            onResult(id)
        }
    }

    fun updatePhoto(id: Long, fotoUri: String) {
        viewModelScope.launch {
            repository.updateHistoryPhoto(id, fotoUri)
        }
    }

    fun deleteHistory(item: HistoryEntity) {
        viewModelScope.launch {
            // delete lokal dulu supaya UI langsung refresh
            repository.deleteById(item.id)

            // delete di Firebase (best-effort, tidak blocking UI)
            try {
                repository.deleteFromFirebase(item.id)
            } catch (_: Exception) { }
        }
    }

    fun deleteHistoryById(id: Long) {
        viewModelScope.launch {
            repository.deleteHistoryById(id)
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            repository.deleteAllHistory()
        }
    }
}
