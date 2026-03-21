package com.example.healthheatv2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthheatv2.network.IngredientAnalysis
import com.example.healthheatv2.ui.viewmodel.ApiState
import com.example.healthheatv2.ui.viewmodel.ScannerViewModel

// Theme Colors matching your Tailwind config
private val SurfaceDark = Color(0xFF131313)
private val PrimaryGold = Color(0xFFFFD79B)
private val PrimaryContainer = Color(0xFFFFB300)
private val OnSurfaceVariant = Color(0xFFD6C4AC)
private val OutlineVariant = Color(0xFF514532)
private val SurfaceContainerLow = Color(0xFF1C1B1B)
private val SurfaceContainerHighest = Color(0xFF353534)
private val GlassCardBg = Color(0xFF201F1F).copy(alpha = 0.4f)
private val GlassBorder = Color.White.copy(alpha = 0.05f)

// Status Colors
private val TertiaryPass = Color(0xFF78DC77)
private val ErrorBad = Color(0xFFFFB4AB)
private val NeutralDim = Color(0xFFFFBA38)

@Composable
fun ProductScreen(
    viewModel: ScannerViewModel,
    onScanAnother: () -> Unit
) {
    val apiState by viewModel.apiState

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceDark)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Top Navigation Bar
        CustomTopBar()

        Spacer(modifier = Modifier.height(24.dp))

        if (apiState is ApiState.Success) {
            val product = (apiState as ApiState.Success).data

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 32.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 1. Hero Section
                item {
                    HeroSection(
                        brandName = product.brand ?: "UNKNOWN BRAND",
                        productName = product.name ?: "Unknown Product"
                    )
                }

                // 2. Verdict Bento Grid
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        VerdictCard(
                            modifier = Modifier.weight(1f),
                            verdict = product.verdict ?: "N/A"
                        )
                        ScoreCard(
                            modifier = Modifier.weight(1f),
                            score = product.healthScore ?: 0
                        )
                    }
                }

                // 3. Ingredients Forensic Analysis
                val ingredientsList = product.ingredientsAnalysis ?: emptyList()
                if (ingredientsList.isNotEmpty()) {
                    item {
                        Text(
                            text = "INGREDIENTS FORENSIC ANALYSIS",
                            color = OnSurfaceVariant,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    items(ingredientsList) { ingredient ->
                        IngredientCard(ingredient)
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }

                // 4. Scan Another CTA
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onScanAnother,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PrimaryContainer,
                            contentColor = Color(0xFF6B4900)
                        ),
                        shape = CircleShape
                    ) {
                        Icon(Icons.Filled.QrCodeScanner, contentDescription = null)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "SCAN ANOTHER PRODUCT",
                            fontWeight = FontWeight.Black,
                            letterSpacing = 2.sp,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        } else {
            // Fallback if accessed without data
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No product data available.", color = OnSurfaceVariant)
            }
        }
    }
}

@Composable
private fun CustomTopBar() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.Menu, contentDescription = "Menu", tint = PrimaryGold, modifier = Modifier.size(28.dp))
            Spacer(modifier = Modifier.width(16.dp))
            Text("HEALTHEAT", color = PrimaryGold, fontSize = 18.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
        }
        Box(
            modifier = Modifier.size(36.dp).background(SurfaceContainerHighest, CircleShape).border(1.dp, OutlineVariant.copy(alpha = 0.5f), CircleShape)
        )
    }
}

@Composable
private fun HeroSection(brandName: String, productName: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(4f / 3f)
            .clip(RoundedCornerShape(32.dp))
            .background(SurfaceContainerLow)
    ) {
        // Placeholder for the actual image.
        // Replace this Box with Coil's AsyncImage when you're ready to load from network
        Box(modifier = Modifier.fillMaxSize().background(Color.DarkGray))

        // Gradient Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, SurfaceDark.copy(alpha = 0.4f), SurfaceDark),
                        startY = 0f,
                        endY = Float.POSITIVE_INFINITY
                    )
                )
        )

        // Text Content
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(32.dp)
        ) {
            Text(
                text = brandName.uppercase(),
                color = PrimaryGold.copy(alpha = 0.8f),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = productName,
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                lineHeight = 36.sp
            )
        }
    }
}

@Composable
private fun VerdictCard(modifier: Modifier = Modifier, verdict: String) {
    val isPass = verdict.uppercase().contains("PASS") || verdict.uppercase().contains("SMASH")
    val verdictColor = if (isPass) TertiaryPass else ErrorBad
    val glowColor = verdictColor.copy(alpha = 0.3f)

    Box(
        modifier = modifier
            .height(240.dp)
            .background(GlassCardBg, RoundedCornerShape(32.dp))
            .border(1.dp, GlassBorder, RoundedCornerShape(32.dp))
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "HEALTH VERDICT",
                color = OnSurfaceVariant,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Column {
                Text(
                    text = if (isPass) "PASS" else "FAIL",
                    color = verdictColor,
                    fontSize = 46.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-2).sp,
                    style = TextStyle(shadow = Shadow(color = glowColor, blurRadius = 40f))
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(verdictColor.copy(alpha = 0.1f), CircleShape)
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        if (isPass) Icons.Filled.CheckCircle else Icons.Filled.Warning,
                        contentDescription = null,
                        tint = verdictColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isPass) "METABOLIC MATCH" else "AVOID",
                        color = verdictColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ScoreCard(modifier: Modifier = Modifier, score: Int) {
    Box(
        modifier = modifier
            .height(240.dp)
            .background(SurfaceContainerLow, RoundedCornerShape(32.dp))
            .border(1.dp, GlassBorder, RoundedCornerShape(32.dp))
            .padding(24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "NUTRITIONAL PURITY",
                color = OnSurfaceVariant,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = score.toString(),
                            color = PrimaryGold,
                            fontSize = 56.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-2).sp
                        )
                        Text(
                            text = "/ 100",
                            color = PrimaryGold.copy(alpha = 0.4f),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(start = 4.dp)
                        )
                    }
                    Icon(
                        Icons.Filled.Analytics,
                        contentDescription = null,
                        tint = PrimaryGold.copy(alpha = 0.5f),
                        modifier = Modifier.size(32.dp).padding(bottom = 8.dp)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                LinearProgressIndicator(
                    progress = { score / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(CircleShape),
                    color = PrimaryGold,
                    trackColor = SurfaceContainerHighest,
                )
            }
        }
    }
}

@Composable
private fun IngredientCard(ingredient: IngredientAnalysis) {
    val status = ingredient.status?.uppercase() ?: "NEUTRAL"
    val (statusColor, icon) = when (status) {
        "GOOD" -> TertiaryPass to Icons.Filled.CheckCircle
        "BAD" -> ErrorBad to Icons.Filled.Warning
        else -> NeutralDim to Icons.Filled.Info
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(GlassCardBg, RoundedCornerShape(16.dp))
            .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
    ) {
        // The thick colored left border
        Box(
            modifier = Modifier
                .width(4.dp)
                .height(IntrinsicSize.Min) // Matches the height of the row
                .background(statusColor.copy(alpha = 0.8f))
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = ingredient.name ?: "Unknown",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .background(statusColor.copy(alpha = 0.9f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = status,
                            color = SurfaceDark,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = ingredient.reason ?: "No detailed analysis available.",
                    color = OnSurfaceVariant,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 18.sp
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = statusColor.copy(alpha = 0.4f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}