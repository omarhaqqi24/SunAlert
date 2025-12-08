package com.example.sunalert

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.location.LocationServices

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()
            val sharedHistoryVM: SharedHistoryViewModel = viewModel(this)

            // 🔵 HistoryViewModel juga bisa dibuat di sini
            val historyVM: HistoryViewModel = viewModel(factory = HistoryViewModelFactory(application))

            NavGraph(
                navController = navController,
                fusedLocationClient = fusedLocationClient,
                sharedVM = sharedHistoryVM,   // <-- PASS KE NAVGRAPH
                historyVM = historyVM
            )
        }
    }
}