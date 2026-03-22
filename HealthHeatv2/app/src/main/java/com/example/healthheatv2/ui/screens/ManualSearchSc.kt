package com.example.healthheatv2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthheatv2.ui.viewmodel.ApiState
import com.example.healthheatv2.ui.viewmodel.ScannerViewModel

// Theme Colors
private val SurfaceDark = Color(0xFF131313)
private val PrimaryGold = Color(0xFFFFD79B)
private val PrimaryContainer = Color(0xFFFFB300)
private val OnPrimaryFixed = Color(0xFF281900)
private val OnSurfaceVariant = Color(0xFFD6C4AC)
private val OutlineVariant = Color(0xFF514532)
private val SurfaceContainerLow = Color(0xFF1C1B1B)
private val SurfaceContainer = Color(0xFF201F1F)
private val SurfaceContainerHighest = Color(0xFF353534)

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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceDark)
    ) {
        // Bottom Background Graphic (Asymmetric Bleed)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 32.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            Box(
                modifier = Modifier
                    .size(256.dp)
                    .offset(x = 64.dp, y = 32.dp)
                    .background(SurfaceContainerHighest, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Using QrCodeScanner as a placeholder for the "nutrition" icon in the mockup
                Icon(
                    imageVector = Icons.Filled.QrCodeScanner,
                    contentDescription = null,
                    tint = SurfaceContainer.copy(alpha = 0.5f),
                    modifier = Modifier.size(120.dp)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Custom Top Bar with Back Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(SurfaceContainerHighest, CircleShape)
                        .clickable { onBackClick() }
                        .padding(8.dp)
                ) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = PrimaryGold)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text("HEALTHEAT", color = PrimaryGold, fontSize = 18.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Hero Section
            Text(
                text = "Barcode Entry",
                color = Color.White,
                fontSize = 48.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-1).sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Enter the barcode number printed on the product packaging.",
                color = OnSurfaceVariant,
                fontSize = 18.sp,
                lineHeight = 26.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Form Section
            Box(modifier = Modifier.fillMaxWidth()) {
                // Glow Effect behind the input box
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 16.dp, y = (-16).dp)
                        .size(96.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(PrimaryContainer.copy(alpha = 0.15f), Color.Transparent)
                            ),
                            shape = CircleShape
                        )
                )

                // Input Container
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SurfaceContainerLow, RoundedCornerShape(24.dp))
                        .padding(24.dp)
                ) {
                    Text(
                        text = "BARCODE NUMBER",
                        color = PrimaryGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // Custom styled text field
                    BasicTextField(
                        value = barcodeInput,
                        onValueChange = { input ->
                            // Only allow numbers
                            if (input.all { it.isDigit() }) {
                                barcodeInput = input
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        textStyle = TextStyle(
                            color = PrimaryGold,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 4.sp
                        ),
                        cursorBrush = SolidColor(PrimaryGold),
                        singleLine = true,
                        decorationBox = { innerTextField ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(SurfaceContainer, RoundedCornerShape(16.dp))
                                    .padding(vertical = 20.dp, horizontal = 24.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(modifier = Modifier.weight(1f)) {
                                    if (barcodeInput.isEmpty()) {
                                        Text(
                                            text = "0000 0000 0000",
                                            color = OnSurfaceVariant.copy(alpha = 0.3f),
                                            fontSize = 24.sp,
                                            fontWeight = FontWeight.Bold,
                                            letterSpacing = 4.sp
                                        )
                                    }
                                    innerTextField()
                                }
                                Icon(
                                    imageVector = Icons.Filled.QrCodeScanner,
                                    contentDescription = null,
                                    tint = OnSurfaceVariant.copy(alpha = 0.4f)
                                )
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Primary Action Button (or Loading Indicator)
            if (apiState is ApiState.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .background(PrimaryContainer.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = OnPrimaryFixed, modifier = Modifier.size(24.dp))
                }
            } else {
                Button(
                    onClick = {
                        if (barcodeInput.isNotBlank()) {
                            viewModel.lookupBarcode(barcodeInput)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = PrimaryContainer,
                        contentColor = OnPrimaryFixed
                    ),
                    shape = CircleShape,
                    enabled = barcodeInput.isNotBlank()
                ) {
                    Text(
                        text = "Search Product",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(Icons.Filled.ArrowForward, contentDescription = null)
                }
            }

            // Error Display (If API fails)
            if (apiState is ApiState.Error) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = (apiState as ApiState.Error).message,
                    color = Color(0xFFFFB4AB),
                    fontSize = 14.sp,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Secondary Guidance Info Box
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceDark.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .border(1.dp, OutlineVariant.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                    .padding(20.dp),
            ) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = null,
                    tint = PrimaryGold,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Scanning the barcode directly with your camera is usually faster. If the camera isn't focusing, please type the full 12 or 13 digit code here.",
                    color = OnSurfaceVariant,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
            }
        }
    }
}