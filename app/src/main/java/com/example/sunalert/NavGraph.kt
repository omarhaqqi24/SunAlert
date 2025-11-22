package com.example.sunalert

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.sunalert.ui.CekUVScreen
import com.example.sunalert.ui.LandingPage
import com.google.android.gms.location.FusedLocationProviderClient

object Destinations {
    const val HOME = "home"
    const val CEKUV = "cekUV"
}

@Composable
fun NavGraph(navController: NavHostController, fusedLocationClient: FusedLocationProviderClient) {
    NavHost(navController = navController, startDestination = Destinations.HOME) {

        composable(Destinations.HOME) {
            LandingPage(navController, fusedLocationClient)
        }
        composable(Destinations.CEKUV) {
            CekUVScreen(fusedLocationClient)
        }
    }
}