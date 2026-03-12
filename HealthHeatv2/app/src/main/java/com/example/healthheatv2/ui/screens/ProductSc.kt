package com.example.healthheatv2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.healthheatv2.ui.viewmodel.ApiState
import com.example.healthheatv2.ui.viewmodel.ScannerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductScreen(
    viewModel: ScannerViewModel,
    onScanAnother: () -> Unit
) {
    val apiState by viewModel.apiState

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product Details") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        },
        bottomBar = {
            Button(
                onClick = onScanAnother,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .height(56.dp)
            ) {
                Text("Scan Another Product", fontWeight = FontWeight.Bold)
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            if (apiState is ApiState.Success) {
                val product = (apiState as ApiState.Success).data

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = product.name ?: "Unknown Product",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Brand: ${product.brand ?: "N/A"}",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Verdict", style = MaterialTheme.typography.titleMedium)
                                    Text(
                                        text = product.verdict ?: "N/A",
                                        style = MaterialTheme.typography.headlineSmall,
                                        fontWeight = FontWeight.Black,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Divider(modifier = Modifier.padding(vertical = 8.dp))
                                Text("Health Score: ${product.healthScore ?: "N/A"}/100", fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(product.summary ?: "No summary available.")
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                        Text("Ingredients Analysis", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // Loop through the ingredients analysis array safely
                    val ingredientsList = product.ingredientsAnalysis ?: emptyList()
                    items(ingredientsList) { ingredient ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = ingredient.name ?: "Unknown", fontWeight = FontWeight.Bold)
                                    Text(
                                        text = ingredient.status ?: "",
                                        color = if (ingredient.status == "Bad") Color.Red else if (ingredient.status == "Good") Color(0xFF008000) else Color.Gray,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = ingredient.reason ?: "", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            } else {
                // Failsafe in case the user navigates here without a successful scan
                Text(
                    text = "No product data available.",
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}