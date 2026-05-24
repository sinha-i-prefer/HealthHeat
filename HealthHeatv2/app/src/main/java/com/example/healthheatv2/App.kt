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
import androidx.compose.runtime.LaunchedEffect
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
import com.example.healthheatv2.ui.viewmodel.AuthState
import com.example.healthheatv2.ui.viewmodel.AuthViewModel
import com.example.healthheatv2.ui.viewmodel.ScannerViewModel

sealed class Screen(val route: String) {
    object Auth : Screen("auth")
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

    // Initialize ViewModels
    val scannerViewModel: ScannerViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ScannerViewModel(repository) as T
            }
        }
    )

    val authViewModel: AuthViewModel = viewModel()

    val bottomNavItems = listOf(
        BottomNavItem("Search", Screen.SearchHub.route, Icons.Filled.Search),
        BottomNavItem("History", Screen.History.route, Icons.Filled.History)
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Ensure the bottom bar only shows on the Hub and History screens
    val showBottomBar = currentRoute in listOf(
        Screen.SearchHub.route,
        Screen.History.route
    )

    Scaffold(
        containerColor = Color(0xFF131313),
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
        val startDestination = if (authViewModel.authState.value is AuthState.Success) {
            Screen.SearchHub.route
        } else {
            Screen.Auth.route
        }

        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = modifier.padding(innerPadding)
        ) {
            composable(Screen.Auth.route) {
                AuthScreen(
                    viewModel = authViewModel,
                    onAuthSuccess = {
                        navController.navigate(Screen.SearchHub.route) {
                            // Pop the auth screen off the stack so they can't 'back' into it
                            popUpTo(Screen.Auth.route) { inclusive = true }
                        }
                    }
                )
            }
            // ---------------------------------

            composable(Screen.SearchHub.route) {
                SearchHubScreen(
                    viewModel = scannerViewModel,
                    authViewModel = authViewModel,
                    onScanClick = {
                        scannerViewModel.resetState()
                        navController.navigate(Screen.Scanner.route)
                    },
                    onManualEntryClick = {
                        scannerViewModel.resetState()
                        navController.navigate(Screen.ManualSearch.route)
                    },
                    onViewAllHistoryClick = { navController.navigate(Screen.History.route) },
                    onProductSelected = { navController.navigate(Screen.Product.route) },
                    onLogout = {
                        navController.navigate(Screen.Auth.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
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
                    authViewModel = authViewModel,
                    onScanAnother = {
                        scannerViewModel.resetState()
                        navController.popBackStack(Screen.SearchHub.route, inclusive = false)
                    },
                    onViewDetails = {
                        navController.navigate(Screen.DetailedNutrition.route)
                    },
                    onLogout = {
                        navController.navigate(Screen.Auth.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.History.route) {
                HistoryScreen(
                    viewModel = scannerViewModel,
                    authViewModel = authViewModel,
                    onBackClick = { navController.popBackStack() },
                    onProductSelected = {
                        navController.navigate(Screen.Product.route)
                    },
                    onLogout = {
                        navController.navigate(Screen.Auth.route) {
                            popUpTo(0) { inclusive = true }
                        }
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

@Composable
fun CustomBottomNavigationBar(
    items: List<BottomNavItem>,
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
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
            .background(
                color = navBackgroundColor,
                shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)
            )
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