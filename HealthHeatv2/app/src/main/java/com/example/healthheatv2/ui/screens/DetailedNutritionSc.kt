package com.example.healthheatv2.ui.screens

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.healthheatv2.network.FoodResponse
import com.example.healthheatv2.ui.viewmodel.ApiState
import com.example.healthheatv2.ui.viewmodel.ScannerViewModel
import kotlin.math.roundToInt

// Colors
private val SurfaceDark = Color(0xFF131313)
private val PrimaryGold = Color(0xFFFFD79B)
private val OnSurfaceVariant = Color(0xFFD6C4AC)
private val OutlineVariant = Color(0xFF514532)
private val SurfaceContainerLow = Color(0xFF1C1B1B)
private val SurfaceContainerHighest = Color(0xFF353534)
private val GlassCardBg = Color(0xFF201F1F).copy(alpha = 0.4f)
private val GlassBorder = Color.White.copy(alpha = 0.05f)

private val HighColor = Color(0xFFFFB4AB)
private val ModerateColor = Color(0xFFFFBA38)
private val LowColor = Color(0xFF78DC77)

@Composable
fun DetailedNutritionScreen(
    viewModel: ScannerViewModel,
    onBackClick: () -> Unit
) {
    val apiState by viewModel.apiState

    if (apiState is ApiState.Success) {
        val product = (apiState as ApiState.Success).data
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(SurfaceDark)
                .padding(horizontal = 24.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Custom Top Bar
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .background(SurfaceContainerHighest, CircleShape)
                        .clickable { onBackClick() }
                        .padding(8.dp)
                ) {
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back", tint = PrimaryGold)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text("NUTRITION IN-DEPTH", color = PrimaryGold, fontSize = 14.sp, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
            }

            Spacer(modifier = Modifier.height(32.dp))

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 1. AI Summary Card
                product.summary?.let { summary ->
                    SummaryCard(summary)
                }

                // 2. Health Assessment (Reasoning & Frequency)
                HealthAssessmentCard(product)

                // 3. Animated Bar Graph (Macros & Sugar)
                AnimatedNutrientBarChart(nutrients = product.nutrients)

                // 4. Nutrient Levels (Traffic Lights)
                NutrientLevelsGrid(levels = product.nutrientLevels)

                // 5. Detailed Text Analysis
                product.nutritionAnalysis?.let { analysis ->
                    AnalysisCard(title = "Energy Profile", text = analysis.energyEstimation, icon = Icons.Filled.ElectricBolt)
                    AnalysisCard(title = "Macro Balance", text = analysis.macronutrientBalance, icon = Icons.Filled.Scale)
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}

@Composable
private fun SummaryCard(summary: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(PrimaryGold.copy(alpha = 0.1f), RoundedCornerShape(24.dp))
            .border(1.dp, PrimaryGold.copy(alpha = 0.3f), RoundedCornerShape(24.dp))
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.AutoAwesome, contentDescription = null, tint = PrimaryGold, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("AI NUTRITION SUMMARY", color = PrimaryGold, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = summary,
            color = Color.White,
            fontSize = 15.sp,
            lineHeight = 24.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun HealthAssessmentCard(product: FoodResponse) {
    val frequency = product.safeConsumptionFrequency ?: "Unknown"
    val reason = product.healthReason ?: "No detailed health reason provided."
    val isGood = product.isGoodForHealth ?: false

    val statusColor = if (isGood) LowColor else HighColor

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(GlassCardBg, RoundedCornerShape(24.dp))
            .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
            .padding(24.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Filled.HealthAndSafety, contentDescription = null, tint = statusColor, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text("CONSUMPTION ADVICE", color = OnSurfaceVariant, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
                Text(frequency.uppercase(), color = statusColor, fontSize = 18.sp, fontWeight = FontWeight.Black, letterSpacing = 1.sp)
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceContainerLow, RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Text(
                text = reason,
                color = OnSurfaceVariant,
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
    }
}

@Composable
private fun AnimatedNutrientBarChart(nutrients: Map<String, Any>?) {
    // Safely extract values
    val carbs = (nutrients?.get("carbohydrates_100g") as? Number)?.toFloat() ?: 0f
    val protein = (nutrients?.get("proteins_100g") as? Number)?.toFloat() ?: 0f
    val fat = (nutrients?.get("fat_100g") as? Number)?.toFloat() ?: 0f
    val sugar = (nutrients?.get("sugars_100g") as? Number)?.toFloat() ?: 0f

    // Find the max value to scale the bars properly (minimum 1f to avoid division by zero)
    val maxVal = maxOf(carbs, protein, fat, sugar, 1f)

    // Setup the animation state
    val animationProgress = remember { Animatable(0f) }

    // Trigger the animation when the composable enters the composition
    LaunchedEffect(Unit) {
        animationProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing)
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(GlassCardBg, RoundedCornerShape(24.dp))
            .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
            .padding(24.dp)
    ) {
        Text("KEY METRICS (PER 100g)", color = OnSurfaceVariant, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp)
        Spacer(modifier = Modifier.height(32.dp))

        // Chart Area
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp), // Fixed height for the chart area
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom // Align bars to the bottom
        ) {
            ChartBar("CARBS", carbs, maxVal, animationProgress.value, Color(0xFF8AB4F8))
            ChartBar("FAT", fat, maxVal, animationProgress.value, Color(0xFFF28B82))
            ChartBar("PROTEIN", protein, maxVal, animationProgress.value, Color(0xFF81C995))
            ChartBar("SUGAR", sugar, maxVal, animationProgress.value, Color(0xFFC58AF9))
        }
    }
}

@Composable
private fun ChartBar(label: String, value: Float, maxVal: Float, progress: Float, color: Color) {
    // Calculate the target percentage relative to the max value
    val targetPercentage = value / maxVal
    // Apply the animation progress
    val currentPercentage = targetPercentage * progress

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Bottom,
        modifier = Modifier.fillMaxHeight()
    ) {
        // Value Text (fades and moves in with the bar)
        if (progress > 0.1f) {
            Text(
                text = "${value.roundToInt()}g",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        // The Bar
        Box(
            modifier = Modifier
                .width(40.dp)
                // Height is total available height * calculated percentage
                .fillMaxHeight(currentPercentage.coerceIn(0.01f, 1f))
                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .background(color)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Axis Label
        Text(
            text = label,
            color = OnSurfaceVariant,
            fontSize = 10.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 1.sp
        )
    }
}

@Composable
private fun NutrientLevelsGrid(levels: Map<String, String>?) {
    if (levels == null) return

    Column {
        Text("TRAFFIC LIGHT SYSTEM", color = OnSurfaceVariant, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 2.sp, modifier = Modifier.padding(horizontal = 8.dp))
        Spacer(modifier = Modifier.height(12.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            LevelCard(modifier = Modifier.weight(1f), label = "SUGAR", level = levels["sugars"])
            LevelCard(modifier = Modifier.weight(1f), label = "FAT", level = levels["fat"])
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            LevelCard(modifier = Modifier.weight(1f), label = "SAT. FAT", level = levels["saturated-fat"])
            LevelCard(modifier = Modifier.weight(1f), label = "SALT", level = levels["salt"])
        }
    }
}

@Composable
private fun LevelCard(modifier: Modifier, label: String, level: String?) {
    val lvl = level?.lowercase() ?: "unknown"
    val color = when (lvl) {
        "high" -> HighColor
        "moderate" -> ModerateColor
        "low" -> LowColor
        else -> OutlineVariant
    }

    Box(
        modifier = modifier
            .background(SurfaceContainerLow, RoundedCornerShape(16.dp))
            .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Column {
            Text(label, color = OnSurfaceVariant, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(10.dp).background(color, CircleShape))
                Spacer(modifier = Modifier.width(8.dp))
                Text((level ?: "N/A").uppercase(), color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Black)
            }
        }
    }
}

@Composable
private fun AnalysisCard(title: String, text: String?, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    if (text.isNullOrBlank()) return
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(GlassCardBg, RoundedCornerShape(16.dp))
            .border(1.dp, GlassBorder, RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Icon(icon, contentDescription = null, tint = PrimaryGold, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(title.uppercase(), color = PrimaryGold, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text, color = OnSurfaceVariant, fontSize = 13.sp, lineHeight = 18.sp)
        }
    }
}