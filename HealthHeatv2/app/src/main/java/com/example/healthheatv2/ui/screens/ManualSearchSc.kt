package com.example.healthheatv2.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.healthheatv2.ui.viewmodel.ApiState
import com.example.healthheatv2.ui.viewmodel.ScannerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManualSearchScreen(
    viewModel: ScannerViewModel,
    onBackClick: () -> Unit,
    onSearchSuccess: () -> Unit
) {
    var barcodeInput by remember { mutableStateOf("") }
    val apiState by viewModel.apiState

    // Automatically navigate when the API call succeeds
    LaunchedEffect(apiState) {
        if (apiState is ApiState.Success) {
            onSearchSuccess()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Manual Entry") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Enter the barcode number printed on the product packaging.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = barcodeInput,
                onValueChange = { barcodeInput = it },
                label = { Text("Barcode Number") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (barcodeInput.isNotBlank()) {
                        viewModel.lookupBarcode(barcodeInput)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = barcodeInput.isNotBlank() && apiState !is ApiState.Loading
            ) {
                Text("Search Product")
            }

            Spacer(modifier = Modifier.height(32.dp))

            // UI Feedback for Loading or Error
            when (val state = apiState) {
                is ApiState.Loading -> {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Searching database...")
                }
                is ApiState.Error -> {
                    ErrorCard(
                        errorMessage = state.message,
                        onDismiss = { viewModel.resetState() }
                    )
                }
                else -> {}
            }
        }
    }
}