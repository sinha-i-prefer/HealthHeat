package com.example.healthheatv2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthheatv2.data.ProductCacheEntity
import com.example.healthheatv2.ui.viewmodel.ScannerViewModel
import java.text.SimpleDateFormat
import java.util.*

// Theme Colors
private val SurfaceDark = Color(0xFF131313)
private val PrimaryGold = Color(0xFFFFD79B)
private val OnSurfaceVariant = Color(0xFFD6C4AC)
private val OutlineVariant = Color(0xFF514532)
private val SurfaceContainerLow = Color(0xFF1C1B1B)
private val SurfaceContainer = Color(0xFF201F1F)
private val SurfaceContainerHighest = Color(0xFF353534)

// Badge Colors
private val ErrorText = Color(0xFFFFB4AB)
private val ErrorBg = Color(0xFF93000A).copy(alpha = 0.3f)
private val ErrorBorder = Color(0xFFFFB4AB).copy(alpha = 0.3f)

private val PassText = Color(0xFF78DC77)
private val PassBg = Color(0xFF005D16).copy(alpha = 0.3f)
private val PassBorder = Color(0xFF78DC77).copy(alpha = 0.3f)

private val NeutralText = Color(0xFFFFBA38)
private val NeutralBg = Color(0xFF6B4900).copy(alpha = 0.3f)
private val NeutralBorder = Color(0xFFFFBA38).copy(alpha = 0.3f)

@Composable
fun HistoryScreen(
    viewModel: ScannerViewModel,
    onBackClick: () -> Unit, // Keeping for compatibility, though we use the bottom nav mostly now
    onProductSelected: () -> Unit
) {
    val history by viewModel.searchHistory
    var searchQuery by remember { mutableStateOf("") }

    // Real-time filtering logic
    val filteredHistory = remember(searchQuery, history) {
        if (searchQuery.isBlank()) {
            history
        } else {
            history.filter {
                val nameMatch = it.foodResponse.name?.contains(searchQuery, ignoreCase = true) == true
                val brandMatch = it.foodResponse.brand?.contains(searchQuery, ignoreCase = true) == true
                nameMatch || brandMatch
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceDark)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Top App Bar
        CustomTopBar()

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp, top = 24.dp)
        ) {
            item {
                // Editorial Header
                Text(
                    text = "YOUR ARCHIVE",
                    color = PrimaryGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Scan History",
                    color = Color.White,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-1).sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Review your past choices and track the evolution of your nutritional journey.",
                    color = OnSurfaceVariant,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    modifier = Modifier.fillMaxWidth(0.85f)
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Search & Filter Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Search Bar
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .background(SurfaceContainerLow, CircleShape)
                            .border(1.dp, OutlineVariant.copy(alpha = 0.3f), CircleShape)
                            .padding(horizontal = 16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Filled.Search, contentDescription = "Search", tint = OutlineVariant, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            BasicTextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                textStyle = TextStyle(color = Color.White, fontSize = 14.sp),
                                cursorBrush = SolidColor(PrimaryGold),
                                modifier = Modifier.fillMaxWidth(),
                                decorationBox = { innerTextField ->
                                    if (searchQuery.isEmpty()) {
                                        Text("Search products...", color = OutlineVariant, fontSize = 14.sp)
                                    }
                                    innerTextField()
                                }
                            )
                        }
                    }

                    // Filter Button
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(SurfaceContainerHighest, CircleShape)
                            .border(1.dp, OutlineVariant.copy(alpha = 0.3f), CircleShape)
                            .clickable { /* Future: Open filter dialog */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Filled.Settings, contentDescription = "Filter", tint = OnSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            if (filteredHistory.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 40.dp), contentAlignment = Alignment.Center) {
                        Text("No matching products found.", color = OnSurfaceVariant)
                    }
                }
            } else {
                items(filteredHistory) { cacheEntity ->
                    HistoryItemCard(
                        item = cacheEntity,
                        onClick = {
                            viewModel.loadFromHistory(cacheEntity.foodResponse)
                            onProductSelected()
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
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
private fun HistoryItemCard(item: ProductCacheEntity, onClick: () -> Unit) {
    val product = item.foodResponse

    // Determine colors based on verdict
    val verdictStr = product.verdict?.uppercase() ?: "UNKNOWN"
    val (textColor, bgColor, borderColor) = when {
        verdictStr.contains("SMASH") || verdictStr.contains("PASS") -> Triple(PassText, PassBg, PassBorder)
        verdictStr.contains("BAD") || verdictStr.contains("FAIL") -> Triple(ErrorText, ErrorBg, ErrorBorder)
        else -> Triple(NeutralText, NeutralBg, NeutralBorder)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceContainer, RoundedCornerShape(16.dp))
            .border(1.dp, OutlineVariant.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // Image Placeholder (Use Coil here later for product.imageUrl)
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(SurfaceContainerLow, RoundedCornerShape(12.dp))
                    .border(1.dp, OutlineVariant.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = product.name ?: "Unknown Product",
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = product.brand?.uppercase() ?: "BRAND N/A",
                    color = OnSurfaceVariant,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                // Verdict Badge
                Box(
                    modifier = Modifier
                        .background(bgColor, CircleShape)
                        .border(1.dp, borderColor, CircleShape)
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = verdictStr,
                        color = textColor,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                // Formatted Date
                Text(
                    text = formatTimestamp(item.scannedAt),
                    color = OutlineVariant,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// Helper function to format the timestamp into a readable date
private fun formatTimestamp(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
    return sdf.format(Date(timestamp))
}