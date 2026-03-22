package com.example.healthheatv2

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
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
    object SearchHub : Screen("search_hub")
    object Scanner : Screen("scanner")
    object ManualSearch : Screen("manual_search")
    object Product : Screen("product")
    object History : Screen("history")
    object DetailedNutrition : Screen("detailed_nutrition")
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

    // Switched to the History icon to match the HTML design
    val bottomNavItems = listOf(
        BottomNavItem("Search", Screen.SearchHub.route, Icons.Filled.Search),
        BottomNavItem("History", Screen.History.route, Icons.Filled.History)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf(
        Screen.SearchHub.route,
        Screen.History.route
    )

    Scaffold(
        containerColor = Color(0xFF131313), // Ensuring the scaffold background is dark
        bottomBar = {
            if (showBottomBar) {
                CustomBottomNavigationBar(
                    items = bottomNavItems,
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        navController.navigate(route) {
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
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.SearchHub.route,
            modifier = modifier.padding(innerPadding)
        ) {
            composable(Screen.SearchHub.route) {
                SearchHubScreen(
                    viewModel = scannerViewModel,
                    onScanClick = {
                        // 1. Reset state before opening the camera
                        scannerViewModel.resetState()
                        navController.navigate(Screen.Scanner.route)
                    },
                    onManualEntryClick = {
                        // 2. Reset state before opening the manual entry form
                        scannerViewModel.resetState()
                        navController.navigate(Screen.ManualSearch.route)
                    },
                    onViewAllHistoryClick = { navController.navigate(Screen.History.route) },
                    onProductSelected = { navController.navigate(Screen.Product.route) }
                )
            }
            composable(Screen.ManualSearch.route) {
                ManualSearchScreen(
                    viewModel = scannerViewModel,
                    onBackClick = { navController.popBackStack() },
                    onSearchSuccess = {
                        navController.navigate(Screen.Product.route) {
                            popUpTo(Screen.ManualSearch.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.Scanner.route) {
                BarcodeScannerScreen(
                    viewModel = scannerViewModel,
                    onScanSuccess = {
                        navController.navigate(Screen.Product.route) {
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
                        navController.popBackStack(Screen.SearchHub.route, inclusive = false)
                    },
                    onViewDetails = {
                        navController.navigate(Screen.DetailedNutrition.route)
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
            composable(Screen.DetailedNutrition.route) {
                DetailedNutritionScreen(
                    viewModel = scannerViewModel,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}

// -------------------------------------------------------------------
// CUSTOM COMPOSABLE FOR THE BOTTOM NAVIGATION BAR
// -------------------------------------------------------------------
@Composable
fun CustomBottomNavigationBar(
    items: List<BottomNavItem>,
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    // Colors mapped directly from your HTML configuration
    val navBackgroundColor = Color(0xFF201F1F).copy(alpha = 0.95f)
    val navBorderColor = Color.White.copy(alpha = 0.05f)
    val activeGradient = Brush.linearGradient(
        colors = listOf(Color(0xFFFFD79B), Color(0xFFFFB300))
    )
    val activeIconColor = Color(0xFF131313)
    val inactiveIconColor = Color(0xFFE5E2E1).copy(alpha = 0.4f)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            // Rounded top corners and semi-transparent background
            .background(
                color = navBackgroundColor,
                shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)
            )
            // Subtle top border for the glassmorphism edge
            .border(
                width = 1.dp,
                color = navBorderColor,
                shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)
            )
            .padding(horizontal = 40.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onNavigate(item.route) }
                    .then(
                        if (isSelected) {
                            Modifier.background(activeGradient)
                        } else {
                            Modifier
                        }
                    )
                    // The active state gets more padding to create the large circular pill look
                    .padding(if (isSelected) 16.dp else 12.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = if (isSelected) activeIconColor else inactiveIconColor,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}