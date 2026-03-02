package com.example.healthheatv2.ui.screens

import android.Manifest
import androidx.camera.core.ImageAnalysis
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.healthheatv2.network.FoodResponse
import com.example.healthheatv2.ui.viewmodel.ApiState
import com.example.healthheatv2.ui.viewmodel.ScannerViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun BarcodeScannerScreen(
    viewModel: ScannerViewModel, // Pass the ViewModel in
    onBarcodeDetected: (String) -> Unit = {} // Keep for navigation if needed later
) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    when {
        cameraPermissionState.status.isGranted -> {
            CameraPreviewWithOverlay(viewModel = viewModel)
        }
        cameraPermissionState.status.shouldShowRationale -> {
            PermissionRationaleDialog(
                onRequestPermission = { cameraPermissionState.launchPermissionRequest() }
            )
        }
        else -> {
            LaunchedEffect(Unit) {
                cameraPermissionState.launchPermissionRequest()
            }
            PermissionRequestScreen(
                onRequestPermission = { cameraPermissionState.launchPermissionRequest() }
            )
        }
    }
}

@Composable
private fun CameraPreviewWithOverlay(viewModel: ScannerViewModel) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val mainExecutor = remember(context) { ContextCompat.getMainExecutor(context) }

    // Observe the API state from the ViewModel
    val apiState by viewModel.apiState

    // Track the last detected barcode to avoid spamming the backend
    var lastDetectedBarcode by remember { mutableStateOf("") }

    val cameraController = remember {
        LifecycleCameraController(context).apply {
            val options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
                .build()
            val barcodeScanner = BarcodeScanning.getClient(options)

            setImageAnalysisAnalyzer(
                mainExecutor,
                MlKitAnalyzer(
                    listOf(barcodeScanner),
                    ImageAnalysis.COORDINATE_SYSTEM_VIEW_REFERENCED,
                    mainExecutor
                ) { result ->
                    val barcodes = result.getValue(barcodeScanner)
                    if (!barcodes.isNullOrEmpty()) {
                        val rawValue = barcodes.first().rawValue
                        // Only trigger if we aren't already looking up this exact barcode
                        if (rawValue != null && rawValue != lastDetectedBarcode) {
                            lastDetectedBarcode = rawValue
                            viewModel.lookupBarcode(rawValue) // Trigger the API POST request!
                        }
                    }
                }
            )
            bindToLifecycle(lifecycleOwner)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 1. The Camera Background
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                PreviewView(ctx).apply {
                    this.controller = cameraController
                }
            }
        )

        // 2. The UI Overlay (Loading, Success, or Error)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            when (val state = apiState) {
                is ApiState.Idle -> {
                    // Show nothing, or a scanning reticle/instruction
                    Text(
                        text = "Point camera at a barcode",
                        color = Color.White,
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                            .padding(16.dp)
                    )
                }
                is ApiState.Loading -> {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
                is ApiState.Success -> {
                    ProductDetailsCard(
                        product = state.data,
                        onDismiss = {
                            viewModel.resetState()
                            lastDetectedBarcode = "" // Allow scanning again
                        }
                    )
                }
                is ApiState.Error -> {
                    ErrorCard(
                        errorMessage = state.message,
                        onDismiss = {
                            viewModel.resetState()
                            lastDetectedBarcode = "" // Allow scanning again
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ProductDetailsCard(product: FoodResponse, onDismiss: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = product.name ?: "Unknown Product", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(text = "Brand: ${product.brand ?: "N/A"}", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(8.dp))
            Divider()
            Spacer(modifier = Modifier.height(8.dp))

            Text(text = "Verdict: ${product.verdict ?: "N/A"}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Text(text = product.reasoning ?: "No reasoning provided.", style = MaterialTheme.typography.bodyMedium)

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                Text("Scan Another")
            }
        }
    }
}

@Composable
fun ErrorCard(errorMessage: String, onDismiss: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Oops!", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onErrorContainer)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = errorMessage, textAlign = TextAlign.Center, color = MaterialTheme.colorScheme.onErrorContainer)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)) {
                Text("Try Again")
            }
        }
    }
}

// ... Keep your existing PermissionRationaleDialog and PermissionRequestScreen down here ...
@Composable
private fun PermissionRequestScreen(onRequestPermission: () -> Unit) { /* Existing code */ }
@Composable
private fun PermissionRationaleDialog(onRequestPermission: () -> Unit) { /* Existing code */ }