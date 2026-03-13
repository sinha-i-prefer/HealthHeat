package com.example.healthheatv2

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.healthheatv2.data.AppDatabase
import com.example.healthheatv2.data.ProductRepository
import com.example.healthheatv2.network.RetrofitClient
import com.example.healthheatv2.ui.screens.*
import com.example.healthheatv2.ui.viewmodel.ScannerViewModel

sealed class Screen(val route: String) {
    object Welcome : Screen("welcome")
    object SearchHub : Screen("search_hub") // Replaced Scanner with SearchHub
    object Scanner : Screen("scanner")      // Now a sub-screen
    object ManualSearch : Screen("manual_search") // New sub-screen
    object Product : Screen("product")
    object History : Screen("history")
}

data class BottomNavItem(
    val title: String,
    val route: String,
    val icon: ImageVector
)

@Composable
fun App(modifier: Modifier = Modifier) {
    val navController = rememberNavController()
    val context = LocalContext.current

    val database = AppDatabase.getDatabase(context)
    val repository = ProductRepository(
        productDao = database.productDao(),
        apiService = RetrofitClient.apiService
    )

    val scannerViewModel: ScannerViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ScannerViewModel(repository) as T
            }
        }
    )

    // Updated to point to SearchHub
    val bottomNavItems = listOf(
        BottomNavItem("Search", Screen.SearchHub.route, Icons.Filled.Search),
        BottomNavItem("History", Screen.History.route, Icons.Filled.List)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Show Bottom bar ONLY on SearchHub and History
    val showBottomBar = currentRoute in listOf(
        Screen.SearchHub.route,
        Screen.History.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = { Text(item.title) },
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Welcome.route,
            modifier = modifier.padding(innerPadding)
        ) {
            composable(Screen.Welcome.route) {
                mainSc(
                    // Now navigates to SearchHub instead of Scanner directly
                    onGetStartedClick = { navController.navigate(Screen.SearchHub.route) },
                    onHistoryClick = { navController.navigate(Screen.History.route) }
                )
            }

            // NEW: The Hub Screen
            composable(Screen.SearchHub.route) {
                SearchHubScreen(
                    onScanClick = { navController.navigate(Screen.Scanner.route) },
                    onManualEntryClick = { navController.navigate(Screen.ManualSearch.route) }
                )
            }

            // NEW: The Manual Search Screen
            composable(Screen.ManualSearch.route) {
                ManualSearchScreen(
                    viewModel = scannerViewModel,
                    onBackClick = { navController.popBackStack() },
                    onSearchSuccess = {
                        navController.navigate(Screen.Product.route) {
                            // Pop the manual search screen off so "Back" from product goes to Hub
                            popUpTo(Screen.ManualSearch.route) { inclusive = true }
                        }
                    }
                )
            }

            // Existing Scanner Screen
            composable(Screen.Scanner.route) {
                BarcodeScannerScreen(
                    viewModel = scannerViewModel,
                    onScanSuccess = {
                        navController.navigate(Screen.Product.route) {
                            // Pop the scanner screen off so "Back" from product goes to Hub
                            popUpTo(Screen.Scanner.route) { inclusive = true }
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
}