package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Point2D
import com.example.viewmodel.DataScienceViewModel
import kotlin.math.sqrt

@Composable
fun ChartLabScreen(viewModel: DataScienceViewModel) {
    val scrollState = rememberScrollState()
    var activeChartType by remember { mutableStateOf("scatter") } // "scatter", "histogram", or "table"

    // Distribution summaries
    val points = viewModel.visualPoints
    val xMean = if (points.isEmpty()) 0.0f else points.map { it.x }.average().toFloat()
    val yMean = if (points.isEmpty()) 0.0f else points.map { it.y }.average().toFloat()
    
    // Variance and standard deviation
    val xVariance = if (points.size < 2) 0f else points.map { (it.x - xMean) * (it.x - xMean) }.sum() / (points.size - 1)
    val yVariance = if (points.size < 2) 0f else points.map { (it.y - yMean) * (it.y - yMean) }.sum() / (points.size - 1)
    val stdDevX = sqrt(xVariance)
    val stdDevY = sqrt(yVariance)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Platform Hero Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF151922)),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFF1E2330))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color(0xFFFF1493), RoundedCornerShape(4.dp))
                        )
                        Text(
                            text = "VISUAL SYNTHESIS LAB",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF1493),
                            letterSpacing = 1.5.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    // Log action
                    IconButton(
                        onClick = { viewModel.saveDatasetSessionToLabJournal() },
                        modifier = Modifier
                            .testTag("log_chart_action")
                            .size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Log Chart configuration",
                            tint = Color(0xFF4ADE80)
                        )
                    }
                }

                Text(
                    text = "High-Fidelity Native Charts",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Text(
                    text = "Observe Gaussian, continuous, correlated, or clustered data generated natively on Canvas parameters. Toggle scatter grids or bin densities.",
                    fontSize = 13.sp,
                    color = Color(0xFF94A3B8),
                    lineHeight = 18.sp
                )

                // Sub-tab switcher between Scatter Plot, Histogram, and Preview Table
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F111A), RoundedCornerShape(10.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { activeChartType = "scatter" },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_chart_scatter"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (activeChartType == "scatter") Color(0xFFFF1493) else Color.Transparent,
                            contentColor = if (activeChartType == "scatter") Color.Black else Color(0xFF94A3B8)
                        ),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
                    ) {
                        Text("Scatter Plot", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { activeChartType = "histogram" },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_chart_histogram"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (activeChartType == "histogram") Color(0xFFFF1493) else Color.Transparent,
                            contentColor = if (activeChartType == "histogram") Color.Black else Color(0xFF94A3B8)
                        ),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
                    ) {
                        Text("Histogram", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { activeChartType = "table" },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("btn_chart_table"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (activeChartType == "table") Color(0xFFFF1493) else Color.Transparent,
                            contentColor = if (activeChartType == "table") Color.Black else Color(0xFF94A3B8)
                        ),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
                    ) {
                        Text("Preview Table", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        val viewportHeight = if (activeChartType == "table") 390.dp else 300.dp

        // Active Chart Render Viewport
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(viewportHeight),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF040608)),
            border = BorderStroke(1.dp, Color(0xFF151922)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(if (activeChartType == "table") 0.dp else 16.dp),
                contentAlignment = Alignment.Center
            ) {
                if (points.isEmpty()) {
                    Text(
                        text = "Generating dynamic simulation coords...",
                        color = Color(0xFF64748B),
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                } else {
                    when (activeChartType) {
                        "scatter" -> {
                            CustomScatterPlotChart(
                                points = points,
                                xMean = xMean,
                                yMean = yMean,
                                colorAccent = if (viewModel.visualDatasetType == "Gaussian") Color(0xFF00B6B7) else Color(0xFF8B5CF6)
                            )
                        }
                        "histogram" -> {
                            CustomHistogramChart(
                                points = points,
                                binCount = viewModel.histogramBinsCount.toInt().coerceIn(5, 25)
                            )
                        }
                        "table" -> {
                            SyntheticDatasetPreviewTable(
                                points = points,
                                datasetType = viewModel.visualDatasetType
                            )
                        }
                    }
                }
            }
        }

        // Configuration Slider Parameters deck
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0D0F14)),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFF1E2330))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "SIMULATION ENGINE PROPERTIES",
                    color = Color(0xFF94A3B8),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                // Row of Distribution Presets
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Mathematical Generator", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val types = listOf(
                            "Gaussian" to "Normal",
                            "Uniform" to "Uniform",
                            "Correlation" to "Linear",
                            "Clusters" to "Dual Blob"
                        )
                        types.forEach { (typeKey, label) ->
                            val isSelected = viewModel.visualDatasetType == typeKey
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(
                                        if (isSelected) Color(0xFFFF1493).copy(alpha = 0.15f) else Color(0xFF151922),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) Color(0xFFFF1493) else Color(0xFF1E2330),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable {
                                        viewModel.visualDatasetType = typeKey
                                        viewModel.generateVisualSyntheticData()
                                    }
                                    .padding(vertical = 10.dp)
                                    .testTag("preset_$typeKey"),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color(0xFFFF1493) else Color(0xFF94A3B8)
                                )
                            }
                        }
                    }
                }

                Divider(color = Color(0xFF1E2330))

                // Points Count Slider
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Sample Size (N)", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = "${viewModel.visualPointCount.toInt()} points",
                            color = Color(0xFF00B6B7),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Slider(
                        value = viewModel.visualPointCount,
                        onValueChange = {
                            viewModel.visualPointCount = it
                            viewModel.generateVisualSyntheticData()
                        },
                        valueRange = 20f..200f,
                        steps = 18,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFFFF1493),
                            activeTrackColor = Color(0xFFFF1493),
                            inactiveTrackColor = Color(0xFF1E2330)
                        ),
                        modifier = Modifier.testTag("slider_sample_size")
                    )
                }

                // Dispersion Standard Deviation Slider
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Dispersion (Noise Variance)", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(
                            text = String.format("%.1fσ", viewModel.visualParamNoise / 10f),
                            color = Color(0xFF00B6B7),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Slider(
                        value = viewModel.visualParamNoise,
                        onValueChange = {
                            viewModel.visualParamNoise = it
                            viewModel.generateVisualSyntheticData()
                        },
                        valueRange = 1f..30f,
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFFFF1493),
                            activeTrackColor = Color(0xFFFF1493),
                            inactiveTrackColor = Color(0xFF1E2330)
                        ),
                        modifier = Modifier.testTag("slider_variance")
                    )
                }

                // Histogram Spec: Displayed only when active chart is histogram
                AnimatedVisibility(visible = activeChartType == "histogram") {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Histogram Bin Baskets (k)", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(
                                text = "${viewModel.histogramBinsCount.toInt()} bins",
                                color = Color(0xFFFFCC00),
                                fontSize = 12.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Slider(
                            value = viewModel.histogramBinsCount,
                            onValueChange = { viewModel.histogramBinsCount = it },
                            valueRange = 5f..25f,
                            steps = 20,
                            colors = SliderDefaults.colors(
                                thumbColor = Color(0xFFFFCC00),
                                activeTrackColor = Color(0xFFFFCC00),
                                inactiveTrackColor = Color(0xFF1E2330)
                            ),
                            modifier = Modifier.testTag("slider_bins")
                        )
                    }
                }

                // Manual refresh button
                Button(
                    onClick = { viewModel.generateVisualSyntheticData() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("btn_refresh_dataset"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF151922),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(imageVector = Icons.Default.Refresh, contentDescription = "Regenerate")
                    Spacer(Modifier.width(8.dp))
                    Text("Regenerate Random Distribution", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        // Statistical Analytics Deck
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F111A)),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFF1E2330))
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Analysis",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "LATENT MATHEMATICAL METRICS",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Divider(color = Color(0xFF1E2330))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Mean Coordinate", color = Color(0xFF64748B), fontSize = 11.sp)
                        Text(
                            text = "(${String.format("%.2f", xMean)}, ${String.format("%.2f", yMean)})",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("Standard Deviation X", color = Color(0xFF64748B), fontSize = 11.sp)
                        Text(
                            text = "${String.format("%.4f", stdDevX)} σ",
                            color = Color(0xFF00B6B7),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Variance Y Axis", color = Color(0xFF64748B), fontSize = 11.sp)
                        Text(
                            text = String.format("%.3f", yVariance),
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("System Entropy Estimate", color = Color(0xFF64748B), fontSize = 11.sp)
                        val entropy = if (points.isEmpty()) 0.0 else Math.log(points.size.toDouble() * stdDevX * stdDevY + 1.0)
                        Text(
                            text = "${String.format("%.3f", entropy)} H(x)",
                            color = Color(0xFFE91E63),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun CustomScatterPlotChart(
    points: List<Point2D>,
    xMean: Float,
    yMean: Float,
    colorAccent: Color
) {
    val textMeasurer = rememberTextMeasurer()
    val tickStyle = remember {
        androidx.compose.ui.text.TextStyle(
            fontSize = 10.sp,
            color = Color(0xFF64748B),
            fontFamily = FontFamily.Monospace
        )
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        val width = size.width
        val height = size.height

        val paddingLeft = 40.dp.toPx()
        val paddingRight = 10.dp.toPx()
        val paddingTop = 10.dp.toPx()
        val paddingBottom = 40.dp.toPx()

        val plotWidth = width - paddingLeft - paddingRight
        val plotHeight = height - paddingTop - paddingBottom

        // Draw elegant grid lines
        val axisLineColor = Color(0xFF1E2330)
        val gridLineColor = Color(0xFF0F111A)

        // 4 Grid rows/columns
        for (i in 0..4) {
            val ratio = i / 4f
            // Vertical grids
            val xPos = paddingLeft + ratio * plotWidth
            drawLine(
                color = gridLineColor,
                start = Offset(xPos, paddingTop),
                end = Offset(xPos, paddingTop + plotHeight),
                strokeWidth = 1f
            )

            // Horizontal grids
            val yPos = paddingTop + ratio * plotHeight
            drawLine(
                color = gridLineColor,
                start = Offset(paddingLeft, yPos),
                end = Offset(paddingLeft + plotWidth, yPos),
                strokeWidth = 1f
            )
        }

        // Draw axes lines
        drawLine(
            color = axisLineColor,
            start = Offset(paddingLeft, paddingTop),
            end = Offset(paddingLeft, paddingTop + plotHeight),
            strokeWidth = 2f
        )
        drawLine(
            color = axisLineColor,
            start = Offset(paddingLeft, paddingTop + plotHeight),
            end = Offset(paddingLeft + plotWidth, paddingTop + plotHeight),
            strokeWidth = 2f
        )

        // Axis ticks text labels
        for (i in 0..2) {
            val ratio = i / 2f
            val xValueString = "${(ratio * 100).toInt()}"
            val yValueString = "${(ratio * 100).toInt()}"

            // X ticks txt
            val xPos = paddingLeft + ratio * plotWidth
            drawText(
                textMeasurer = textMeasurer,
                text = xValueString,
                style = tickStyle,
                topLeft = Offset(xPos - 12f, paddingTop + plotHeight + 6f)
            )

            // Y ticks txt
            val yPos = paddingTop + (1f - ratio) * plotHeight
            drawText(
                textMeasurer = textMeasurer,
                text = yValueString,
                style = tickStyle,
                topLeft = Offset(paddingLeft - 28f, yPos - 6f)
            )
        }

        // Draw Mean lines (Dashed statistical reference markers)
        val meanXPixel = paddingLeft + (xMean / 100f) * plotWidth
        val meanYPixel = paddingTop + (1f - (yMean / 100f)) * plotHeight

        drawLine(
            color = Color(0xFF334155),
            start = Offset(meanXPixel, paddingTop),
            end = Offset(meanXPixel, paddingTop + plotHeight),
            strokeWidth = 1.5f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
        )

        drawLine(
            color = Color(0xFF334155),
            start = Offset(paddingLeft, meanYPixel),
            end = Offset(paddingLeft + plotWidth, meanYPixel),
            strokeWidth = 1.5f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
        )

        // Draw Scatter coordinates
        points.forEach { pt ->
            // Map 0..100 metrics to visual coordinates
            val mapX = paddingLeft + (pt.x / 100f).coerceIn(0f, 1f) * plotWidth
            val mapY = paddingTop + (1f - (pt.y / 100f).coerceIn(0f, 1f)) * plotHeight

            // Base dot glow ring
            drawCircle(
                color = colorAccent.copy(alpha = 0.2f),
                radius = 7.dp.toPx(),
                center = Offset(mapX, mapY)
            )

            // Actual core point dot
            drawCircle(
                color = colorAccent,
                radius = 3.dp.toPx(),
                center = Offset(mapX, mapY)
            )
        }
    }
}

@OptIn(ExperimentalTextApi::class)
@Composable
fun CustomHistogramChart(
    points: List<Point2D>,
    binCount: Int
) {
    val textMeasurer = rememberTextMeasurer()
    val labelStyle = remember {
        androidx.compose.ui.text.TextStyle(
            fontSize = 9.sp, 
            color = Color(0xFF475569), 
            fontFamily = FontFamily.Monospace
        )
    }
    val valueStyle = remember {
        androidx.compose.ui.text.TextStyle(
            fontSize = 10.sp, 
            color = Color.White, 
            fontWeight = FontWeight.Bold, 
            fontFamily = FontFamily.Monospace
        )
    }
    val axisLabelStyle = remember {
        androidx.compose.ui.text.TextStyle(
            fontSize = 9.sp, 
            color = Color(0xFF64748B), 
            fontFamily = FontFamily.Monospace
        )
    }

    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {
        val width = size.width
        val height = size.height

        val paddingLeft = 40.dp.toPx()
        val paddingRight = 10.dp.toPx()
        val paddingTop = 20.dp.toPx()
        val paddingBottom = 40.dp.toPx()

        val plotWidth = width - paddingLeft - paddingRight
        val plotHeight = height - paddingTop - paddingBottom

        val axisLineColor = Color(0xFF1E2330)

        // Bin frequency calculator: Divide X coordinates 0..100 into standard interval bins
        val bins = IntArray(binCount) { 0 }
        points.forEach { pt ->
            val binIndex = ((pt.x / 100f) * binCount).toInt().coerceIn(0, binCount - 1)
            bins[binIndex]++
        }

        val maxBinFrequency = bins.maxOrNull()?.coerceAtLeast(1) ?: 1

        // Horizontal guidelines representing counts
        val gridLineColor = Color(0xFF0F111A)
        val guidelinesCount = 3
        for (i in 1..guidelinesCount) {
            val ratio = i / guidelinesCount.toFloat()
            val yPos = paddingTop + (1f - ratio) * plotHeight
            drawLine(
                color = gridLineColor,
                start = Offset(paddingLeft, yPos),
                end = Offset(paddingLeft + plotWidth, yPos),
                strokeWidth = 1f
            )

            val countLabel = "${(ratio * maxBinFrequency).toInt()}"
            drawText(
                textMeasurer = textMeasurer,
                text = countLabel,
                style = labelStyle,
                topLeft = Offset(paddingLeft - 22f, yPos - 6f)
            )
        }

        // Draw axes lines
        drawLine(
            color = axisLineColor,
            start = Offset(paddingLeft, paddingTop),
            end = Offset(paddingLeft, paddingTop + plotHeight),
            strokeWidth = 2f
        )
        drawLine(
            color = axisLineColor,
            start = Offset(paddingLeft, paddingTop + plotHeight),
            end = Offset(paddingLeft + plotWidth, paddingTop + plotHeight),
            strokeWidth = 2f
        )

        // Render each bar
        val barSpacing = 4f
        val calculatedBarWidth = (plotWidth / binCount) - barSpacing

        for (b in 0 until binCount) {
            val freq = bins[b]
            if (freq <= 0) continue

            val barHeightRatio = freq / maxBinFrequency.toFloat()
            val drawBarHeight = barHeightRatio * plotHeight

            val startX = paddingLeft + b * (plotWidth / binCount) + (barSpacing / 2)
            val startY = paddingTop + plotHeight - drawBarHeight

            // Rect shape with beautiful multi-color vertical gradient
            val gradientBrush = Brush.verticalGradient(
                colors = listOf(
                    Color(0xFFFF1493), // Intense Fuchsia magenta
                    Color(0xFF8B5CF6), // Bright Violet
                )
            )

            drawRoundRect(
                brush = gradientBrush,
                topLeft = Offset(startX, startY),
                size = Size(calculatedBarWidth, drawBarHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
            )

            // Render thin stroke highlights for better visual contrast
            drawRoundRect(
                color = Color.White.copy(alpha = 0.4f),
                topLeft = Offset(startX, startY),
                size = Size(calculatedBarWidth, drawBarHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f),
                style = Stroke(width = 1f)
            )

            // Frequency text counter on top of the bar for clarity
            if (calculatedBarWidth > 16f) { // Only render if bar has readable width
                val freqString = "$freq"
                drawText(
                    textMeasurer = textMeasurer,
                    text = freqString,
                    style = valueStyle,
                    topLeft = Offset(startX + (calculatedBarWidth / 2f) - 6f, startY - 16f)
                )
            }
        }

        // Render label for bottom bins
        for (b in listOf(0, binCount / 2, binCount - 1)) {
            val ratio = b / (binCount - 1).toFloat()
            val tickLabel = "${(ratio * 100).toInt()}"
            val startX = paddingLeft + b * (plotWidth / binCount) + (calculatedBarWidth / 2)

            drawText(
                textMeasurer = textMeasurer,
                text = tickLabel,
                style = axisLabelStyle,
                topLeft = Offset(startX - 8f, paddingTop + plotHeight + 6f)
            )
        }
    }
}

@Composable
fun SyntheticDatasetPreviewTable(
    points: List<com.example.data.model.Point2D>,
    datasetType: String
) {
    var sortBy by remember { mutableStateOf("index") } // "index", "x_asc", "x_desc", "y_asc", "y_desc"
    var showHighXOnly by remember { mutableStateOf(false) }
    var currentPage by remember { mutableStateOf(0) }
    val pageSize = 6

    // Reset page if points count or filters change
    LaunchedEffect(points, showHighXOnly, sortBy) {
        currentPage = 0
    }

    // Classify coordinate to subgroup helper
    fun classifyGroup(pt: com.example.data.model.Point2D): String {
        return when (datasetType) {
            "Gaussian" -> {
                // Calculate distance to origin or mean
                val dist = sqrt((pt.x - 50.0).let { it * it } + (pt.y - 50.0).let { it * it })
                if (dist < 15.0) "Core (1σ)" else if (dist < 31.0) "Mid (2σ)" else "Outer Boundary"
            }
            "Clusters" -> {
                val distToC1 = sqrt((pt.x - 30.0).let { it * it } + (pt.y - 30.0).let { it * it })
                val distToC2 = sqrt((pt.x - 70.0).let { it * it } + (pt.y - 70.0).let { it * it })
                if (distToC1 < distToC2) "Blob Alpha" else "Blob Beta"
            }
            "Correlation" -> {
                if (pt.x < 35.0f) "Low Band" else if (pt.x > 65.0f) "High Band" else "Mid Band"
            }
            else -> { // Uniform
                val qX = if (pt.x >= 50.0f) "E" else "W"
                val qY = if (pt.y >= 50.0f) "N" else "S"
                "Sector $qY$qX"
            }
        }
    }

    // 1. Process points with index mapping
    val indexedPoints = points.mapIndexed { idx, pt -> 
        Triple(idx + 1, pt, classifyGroup(pt)) 
    }

    // 1.1 Apply Filter
    val filteredPoints = indexedPoints.filter { (_, pt, _) ->
        if (showHighXOnly) pt.x >= 50f else true
    }

    // 1.2 Apply Sort
    val sortedPoints = when (sortBy) {
        "x_asc" -> filteredPoints.sortedBy { it.second.x }
        "x_desc" -> filteredPoints.sortedByDescending { it.second.x }
        "y_asc" -> filteredPoints.sortedBy { it.second.y }
        "y_desc" -> filteredPoints.sortedByDescending { it.second.y }
        else -> filteredPoints // "index" (already in order)
    }

    // 1.3 Apply Pagination
    val totalRecords = sortedPoints.size
    val totalPages = kotlin.math.max(1, (totalRecords + pageSize - 1) / pageSize)
    val currentPageClamped = currentPage.coerceIn(0, totalPages - 1)
    
    val startIndex = currentPageClamped * pageSize
    val endIndex = kotlin.math.min(startIndex + pageSize, totalRecords)
    val pageItems = if (totalRecords > 0) sortedPoints.subList(startIndex, endIndex) else emptyList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF07080B))
    ) {
        // Table Header Panel with Filter Controls
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0F111A))
                .border(0.5.dp, Color(0xFF1E2330))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Sort badges or menu
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "SORT:",
                    fontSize = 9.sp,
                    color = Color(0xFF64748B),
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                
                val sortModes = listOf("index" to "Def", "x_asc" to "X↑", "x_desc" to "X↓", "y_asc" to "Y↑")
                sortModes.forEach { (mode, label) ->
                    val isSelected = sortBy == mode
                    Box(
                        modifier = Modifier
                            .background(
                                if (isSelected) Color(0xFFFF1493).copy(alpha = 0.2f) else Color.Transparent,
                                RoundedCornerShape(4.dp)
                            )
                            .border(
                                1.dp,
                                if (isSelected) Color(0xFFFF1493) else Color(0xFF1E2330),
                                RoundedCornerShape(4.dp)
                            )
                            .clickable { sortBy = mode }
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color(0xFFFF1493) else Color(0xFF94A3B8),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Quick Filter Switch (e.g. X >= 50)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            if (showHighXOnly) Color(0xFF00B6B7).copy(alpha = 0.2f) else Color.Transparent,
                            RoundedCornerShape(4.dp)
                        )
                        .border(
                            1.dp,
                            if (showHighXOnly) Color(0xFF00B6B7) else Color(0xFF1E2330),
                            RoundedCornerShape(4.dp)
                        )
                        .clickable { showHighXOnly = !showHighXOnly }
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "X ≥ 50",
                        color = if (showHighXOnly) Color(0xFF00B6B7) else Color(0xFF64748B),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Table Grid headers
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF151922))
                .border(0.5.dp, Color(0xFF1E2330))
                .padding(vertical = 8.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ID",
                modifier = Modifier.weight(0.12f),
                color = Color(0xFF94A3B8),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "FEATURE X",
                modifier = Modifier.weight(0.28f),
                color = Color(0xFF94A3B8),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "FEATURE Y",
                modifier = Modifier.weight(0.28f),
                color = Color(0xFF94A3B8),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = "CLUSTER",
                modifier = Modifier.weight(0.32f),
                color = Color(0xFF94A3B8),
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
            )
        }

        // Data Rows container
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (pageItems.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No records match constraints.",
                        color = Color(0xFF64748B),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }
            } else {
                pageItems.forEach { (id, pt, group) ->
                    val isAltRow = id % 2 != 0
                    val rowBgColor = if (isAltRow) Color(0xFF0F111A) else Color(0xFF07080B)
                    
                    val badgeColor = when {
                        group.contains("Alpha") || group.contains("Core") || group.contains("Low") -> Color(0xFF00B6B7)
                        group.contains("Beta") || group.contains("Outer") || group.contains("High") -> Color(0xFFFF1493)
                        else -> Color(0xFF8B5CF6)
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(rowBgColor)
                            .border(0.5.dp, Color(0xFF1E2330).copy(alpha = 0.5f))
                            .padding(vertical = 10.dp, horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // ID Col
                        Text(
                            text = "#$id",
                            modifier = Modifier.weight(0.12f),
                            color = Color(0xFF64748B),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        // Feature X Col
                        Text(
                            text = String.format("%.4f", pt.x),
                            modifier = Modifier.weight(0.28f),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        // Feature Y Col
                        Text(
                            text = String.format("%.4f", pt.y),
                            modifier = Modifier.weight(0.28f),
                            color = Color.White,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        // Sub-group col (badge)
                        Row(
                            modifier = Modifier.weight(0.32f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(badgeColor, RoundedCornerShape(3.dp))
                            )
                            Text(
                                text = group,
                                color = badgeColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Table Pagination controls footer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0F111A))
                .border(0.5.dp, Color(0xFF1E2330))
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "${totalRecords} items | ${startIndex + 1}-${endIndex}",
                fontSize = 10.sp,
                color = Color(0xFF64748B),
                fontFamily = FontFamily.Monospace
            )

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Prev button
                TextButton(
                    onClick = { if (currentPageClamped > 0) currentPage-- },
                    enabled = currentPageClamped > 0,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFFFF1493),
                        disabledContentColor = Color(0xFF334155)
                    )
                ) {
                    Text("Prev", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Text(
                    text = "${currentPageClamped + 1} / $totalPages",
                    fontSize = 11.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                // Next button
                TextButton(
                    onClick = { if (currentPageClamped < totalPages - 1) currentPage++ },
                    enabled = currentPageClamped < totalPages - 1,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = Color(0xFFFF1493),
                        disabledContentColor = Color(0xFF334155)
                    )
                ) {
                    Text("Next", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
