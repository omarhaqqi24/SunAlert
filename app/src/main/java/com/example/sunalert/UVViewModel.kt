package com.example.sunalert

import android.app.Application
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class UVViewModel(application: Application) : AndroidViewModel(application) {
    private val _UVIndex = mutableStateOf<Double?>(null)
    val UVIndex: State<Double?> = _UVIndex

    @Suppress("MissingPermission")
    fun fetchUV(lat: Double, lng: Double) {
        viewModelScope.launch {
            try {
                val response = APIClient.api.getUV(
                    apiKey = "openuv-4uokrmiahlpz5-io",
                    lat = lat,
                    lng = lng
                )

                _UVIndex.value = response.result.uv
            } catch (e: Exception) {
                e.printStackTrace()
                _UVIndex.value = null
            }
        }
    }

}