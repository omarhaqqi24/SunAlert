package com.example.sunalert

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

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

    fun insertHistory(history: HistoryEntity) {
        viewModelScope.launch {
            repository.insertHistory(history)
        }
    }

    fun deleteHistory(item: HistoryEntity) {
        viewModelScope.launch {
            repository.deleteHistory(item)
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            repository.deleteAllHistory()
        }
    }
}
