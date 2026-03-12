package com.example.healthheatv2

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.healthheatv2.ui.screens.mainSc
import com.example.healthheatv2.ui.screens.BarcodeScannerScreen
import com.example.healthheatv2.ui.screens.ProductScreen
import com.example.healthheatv2.ui.screens.HistoryScreen
import com.example.healthheatv2.ui.viewmodel.ScannerViewModel

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object Scanner : Screen("scanner")
    object Product : Screen("product")
    object History : Screen("history")
}

@Composable
fun App(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val scannerViewModel: ScannerViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Welcome.route,
        modifier = modifier
    ) {
        composable(Screen.Welcome.route) {
            mainSc(
                onGetStartedClick = { navController.navigate(Screen.Scanner.route) },
                onHistoryClick = { navController.navigate(Screen.History.route) } // Navigate to History
            )
        }
        composable(Screen.Scanner.route) {
            BarcodeScannerScreen(
                viewModel = scannerViewModel,
                onScanSuccess = {
                    navController.navigate(Screen.Product.route) {
                        popUpTo(Screen.Scanner.route) { inclusive = false }
                    }
                }
            )
        }
        composable(Screen.Product.route) {
            ProductScreen(
                viewModel = scannerViewModel,
                onScanAnother = {
                    scannerViewModel.resetState()
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.History.route) {
            HistoryScreen(
                viewModel = scannerViewModel,
                onBackClick = { navController.popBackStack() },
                onProductSelected = {
                    navController.navigate(Screen.Product.route)
                }
            )
        }
    }
}