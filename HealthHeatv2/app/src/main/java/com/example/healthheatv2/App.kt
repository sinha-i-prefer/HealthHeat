package com.example.healthheatv2

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.healthheatv2.ui.screens.mainSc
import com.example.healthheatv2.ui.screens.BarcodeScannerScreen
import com.example.healthheatv2.ui.viewmodel.ScannerViewModel

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Scanner : Screen("scanner")
}

@Composable
fun App(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    // 1. Initialize the ViewModel
    val scannerViewModel: ScannerViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route,
        modifier = modifier
    ) {
        composable(Screen.Welcome.route) {
            mainSc(
                onGetStartedClick = {
                    navController.navigate(Screen.Scanner.route)
                }
            )
        }
        composable(Screen.Scanner.route) {
            // 2. Pass the ViewModel into the Scanner Screen
            BarcodeScannerScreen(
                viewModel = scannerViewModel,
                onBarcodeDetected = { barcode ->
                    // Handle barcode detection, e.g., navigate to a details screen
                    println("Detected barcode: $barcode")
                }
            )
        }
    }
}