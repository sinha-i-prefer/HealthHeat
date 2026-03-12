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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
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
    viewModel: ScannerViewModel,
    onScanSuccess: () -> Unit // 1. Changed this to explicitly accept the success callback
) {
    val cameraPermissionState = rememberPermissionState(Manifest.permission.CAMERA)

    when {
        cameraPermissionState.status.isGranted -> {
            CameraPreviewWithOverlay(
                viewModel = viewModel,
                onScanSuccess = onScanSuccess // 2. Passed it down to the camera preview!
            )
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
private fun CameraPreviewWithOverlay(
    viewModel: ScannerViewModel,
    onScanSuccess: () -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val mainExecutor = remember(context) { ContextCompat.getMainExecutor(context) }

    val apiState by viewModel.apiState
    var lastDetectedBarcode by remember { mutableStateOf("") }

    // When the API succeeds, it triggers navigation automatically.
    LaunchedEffect(apiState) {
        if (apiState is ApiState.Success) {
            onScanSuccess()
        }
    }

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
                    if (apiState is ApiState.Idle) {
                        val barcodes = result.getValue(barcodeScanner)
                        if (!barcodes.isNullOrEmpty()) {
                            val rawValue = barcodes.first().rawValue
                            if (rawValue != null && rawValue != lastDetectedBarcode) {
                                lastDetectedBarcode = rawValue
                                viewModel.lookupBarcode(rawValue)
                            }
                        }
                    }
                }
            )
            bindToLifecycle(lifecycleOwner)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { ctx ->
                PreviewView(ctx).apply {
                    this.controller = cameraController
                }
            }
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            when (val state = apiState) {
                is ApiState.Idle -> {
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
                // 3. Removed ApiState.Success entirely from here
                is ApiState.Error -> {
                    ErrorCard(
                        errorMessage = state.message,
                        onDismiss = {
                            viewModel.resetState()
                            lastDetectedBarcode = "" // Allow scanning again
                        }
                    )
                }
                else -> {}
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

@Composable
private fun PermissionRequestScreen(onRequestPermission: () -> Unit) { /* Existing code */ }
@Composable
private fun PermissionRationaleDialog(onRequestPermission: () -> Unit) { /* Existing code */ }