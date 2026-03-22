package com.example.healthheatv2.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.rounded.AddCircle
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthheatv2.data.ProductCacheEntity
import com.example.healthheatv2.ui.viewmodel.ScannerViewModel

// Define the custom colors from your Tailwind config
private val SurfaceDark = Color(0xFF131313)
private val PrimaryGold = Color(0xFFFFD79B)
private val PrimaryContainer = Color(0xFFFFB300)
private val SurfaceContainerLowest = Color(0xFF0E0E0E)
private val SurfaceContainerLow = Color(0xFF1C1B1B)
private val SurfaceContainer = Color(0xFF201F1F)
private val SurfaceContainerHighest = Color(0xFF353534)
private val OnSurfaceVariant = Color(0xFFD6C4AC)
private val OutlineVariant = Color(0xFF514532)

@Composable
fun SearchHubScreen(
    viewModel: ScannerViewModel,
    onScanClick: () -> Unit,
    onManualEntryClick: () -> Unit,
    onViewAllHistoryClick: () -> Unit,
    onProductSelected: () -> Unit
) {
    // Read the history directly from the Room database
    val history by viewModel.searchHistory

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(SurfaceDark)
            .padding(horizontal = 24.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // 1. Top Navigation Bar
        CustomTopBar()

        Spacer(modifier = Modifier.height(32.dp))

        // 2. Hero Editorial Header
        HeroHeader()

        Spacer(modifier = Modifier.height(32.dp))

        // 3. Main Actions Bento Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ActionCard(
                modifier = Modifier.weight(1f),
                title = "Scan Barcode\nwith Camera",
                subtitle = "Instant analysis via lens",
                icon = Icons.Rounded.PlayArrow,
                actionIcon = Icons.Filled.ArrowForward,
                isPrimary = true,
                onClick = onScanClick
            )
            ActionCard(
                modifier = Modifier.weight(1f),
                title = "Enter Barcode\nManually",
                subtitle = "Type digits from package",
                icon = Icons.Rounded.AddCircle,
                actionIcon = Icons.Filled.Edit,
                isPrimary = false,
                onClick = onManualEntryClick
            )
        }

        Spacer(modifier = Modifier.height(48.dp))

        // 4. Recent Activity Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Bottom
        ) {
            Text(
                text = "Recent Scans",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "VIEW ALL",
                color = PrimaryGold,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp,
                modifier = Modifier.clickable { onViewAllHistoryClick() }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 5. Recent Scans List
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceContainerLow, RoundedCornerShape(32.dp))
                .padding(8.dp)
        ) {
            if (history.isEmpty()) {
                Text(
                    text = "Your recent scans will appear here.",
                    color = OnSurfaceVariant,
                    modifier = Modifier.padding(16.dp)
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Only show the 2 most recent scans on the dashboard
                    history.take(2).forEach { cacheEntity ->
                        RecentScanItem(
                            item = cacheEntity,
                            onClick = {
                                viewModel.loadFromHistory(cacheEntity.foodResponse)
                                onProductSelected()
                            }
                        )
                    }
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
            Icon(
                imageVector = Icons.Filled.Menu,
                contentDescription = "Menu",
                tint = PrimaryGold,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = "HEALTHEAT",
                color = PrimaryGold,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
        }
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(SurfaceContainerHighest, CircleShape)
                .border(1.dp, OutlineVariant.copy(alpha = 0.5f), CircleShape)
        )
    }
}

@Composable
private fun HeroHeader() {
    Column {
        Text(
            text = "NUTRITION INTELLIGENCE",
            color = PrimaryGold,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = buildAnnotatedString {
                append("Check your\n")
                withStyle(style = SpanStyle(color = PrimaryGold, fontStyle = FontStyle.Italic)) {
                    append("Fuel.")
                }
            },
            color = Color.White,
            fontSize = 48.sp,
            fontWeight = FontWeight.ExtraBold,
            lineHeight = 52.sp
        )
    }
}

@Composable
private fun ActionCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    actionIcon: ImageVector,
    isPrimary: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isPrimary) SurfaceContainerLow else SurfaceContainer
    val iconContainerColor = if (isPrimary) SurfaceContainerHighest else SurfaceContainerHighest
    val iconColor = if (isPrimary) PrimaryGold else Color.LightGray

    Box(
        modifier = modifier
            .aspectRatio(0.8f) // Creates that tall rectangular "bento" look
            .background(backgroundColor, RoundedCornerShape(24.dp))
            .border(1.dp, if (isPrimary) Color.Transparent else OutlineVariant.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
            .clickable { onClick() }
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = Modifier
                    .background(iconContainerColor, RoundedCornerShape(16.dp))
                    .padding(12.dp)
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(32.dp))
            }

            Column {
                Text(title, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold, lineHeight = 24.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(subtitle, color = OnSurfaceVariant, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .border(1.dp, OutlineVariant.copy(alpha = 0.5f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(actionIcon, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
private fun RecentScanItem(item: ProductCacheEntity, onClick: () -> Unit) {
    val product = item.foodResponse

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SurfaceContainer, RoundedCornerShape(24.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .background(SurfaceContainerLowest, RoundedCornerShape(16.dp))
        )
        // Note: You can replace the Box above with an AsyncImage from the Coil library later to show the actual product.imageUrl

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = product.name ?: "Unknown Product", color = Color.White, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(text = product.brand ?: "Brand N/A", color = OnSurfaceVariant, fontSize = 12.sp)
        }

        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = product.verdict?.uppercase() ?: "N/A",
                color = if (product.verdict == "SMASH") Color(0xFF78DC77) else PrimaryGold,
                fontSize = 10.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
            Text(
                text = product.healthScore?.toString() ?: "--",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}