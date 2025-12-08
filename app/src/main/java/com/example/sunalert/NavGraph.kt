package com.example.sunalert

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
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
    const val SKYCHECK = "skycheck"
}

@Composable
fun NavGraph(
    navController: NavHostController,
    fusedLocationClient: FusedLocationProviderClient,
    sharedVM: SharedHistoryViewModel,
    historyVM: HistoryViewModel
) {

    NavHost(navController = navController, startDestination = Destinations.HOME) {

        composable(Destinations.HOME) {
            LandingPage(navController, fusedLocationClient)
        }
        composable(Destinations.CEKUV) {
            val context = LocalContext.current
            val app = context.applicationContext as Application

            CekUVScreen(
                fusedLocationClient = fusedLocationClient,
                navController = navController,
                historyViewModel = historyVM,
                sharedHistoryVM = sharedVM
            )
        }
        composable(Destinations.HISTORY) {
            HistoryScreen()
        }
        composable(Destinations.SKYCHECK) {
            val context = LocalContext.current
            val app = context.applicationContext as Application

            SkyCheckScreen(
                navBack = { navController.popBackStack() },
                historyViewModel = historyVM,
                sharedHistoryVM = sharedVM
            )
        }

    }
}