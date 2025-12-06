package com.example.sunalert

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.sunalert.ui.CekUVScreen
import com.example.sunalert.ui.LandingPage
import com.example.sunalert.ui.HistoryScreen
import com.example.sunalert.ui.SkyCheckScreen
import com.google.android.gms.location.FusedLocationProviderClient

object Destinations {
    const val HOME = "home"
    const val CEKUV = "cekUV"
    const val HISTORY = "history"
    // const val HISTORY_DETAIL = "history_detail"
}

@Composable
fun NavGraph(navController: NavHostController, fusedLocationClient: FusedLocationProviderClient) {
    NavHost(navController = navController, startDestination = Destinations.HOME) {

        composable(Destinations.HOME) {
            LandingPage(navController, fusedLocationClient)
        }
        composable(Destinations.CEKUV) {
            CekUVScreen(fusedLocationClient, navController = navController)
        }
        composable(Destinations.HISTORY) {
            HistoryScreen()
        }
        composable("skycheck") {
            SkyCheckScreen(navBack = { navController.popBackStack() })
        }
    }
}