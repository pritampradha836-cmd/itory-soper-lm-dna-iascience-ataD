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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.DataScienceViewModel
import androidx.compose.ui.graphics.PathEffect
import com.example.viewmodel.DecisionNode
import com.example.viewmodel.LabeledPoint

@Composable
fun MachineLearningPlayground(viewModel: DataScienceViewModel) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Select Playground Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF151922)),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFF1E2330))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = "ML Sandbox Icon",
                        tint = Color(0xFF8E2DE2)
                    )
                    Text(
                        text = "ALGORITHM LABS",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8E2DE2),
                        letterSpacing = 1.5.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Text(
                    text = "Classic Machine Learning Sandbox",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                // Selection Tabs
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F111A), RoundedCornerShape(12.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val labs = listOf(
                        "kmeans" to "K-Means",
                        "regression" to "Regression",
                        "decision_tree" to "Decision Tree"
                    )
                    labs.forEach { (id, title) ->
                        val selected = viewModel.mlPlaygroundSelected == id
                        Button(
                            onClick = { viewModel.mlPlaygroundSelected = id },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (selected) Color(0xFF8E2DE2) else Color.Transparent,
                                contentColor = if (selected) Color.White else Color(0xFF94A3B8)
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
                        ) {
                            Text(title, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Display Active Playground
        when (viewModel.mlPlaygroundSelected) {
            "kmeans" -> KMeansSandbox(viewModel)
            "regression" -> LinearRegressionSandbox(viewModel)
            else -> DecisionTreeSandbox(viewModel)
        }
    }
}

@Composable
fun KMeansSandbox(viewModel: DataScienceViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Scatter Plot Canvas
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0D0F14)),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFF1E2330))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "SYNTHETIC CLUSTER PLOT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    fontFamily = FontFamily.Monospace
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .background(Color(0xFF07080B), RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFF151922), RoundedCornerShape(12.dp))
                ) {
                    val points = viewModel.kMeansPoints
                    val centroids = viewModel.kMeansCentroids

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val canvasWidth = size.width
                        val canvasHeight = size.height

                        // Draw Grid lines
                        val gridCount = 5
                        for (i in 1 until gridCount) {
                            val xPos = canvasWidth * i / gridCount
                            val yPos = canvasHeight * i / gridCount
                            drawLine(
                                color = Color(0xFF151922),
                                start = Offset(xPos, 0f),
                                end = Offset(xPos, canvasHeight),
                                strokeWidth = 1f
                            )
                            drawLine(
                                color = Color(0xFF151922),
                                start = Offset(0f, yPos),
                                end = Offset(canvasWidth, yPos),
                                strokeWidth = 1f
                            )
                        }

                        // Plot Points
                        points.forEach { p ->
                            // Scale dimensions
                            val px = (p.x / 100f) * canvasWidth
                            val py = (1f - (p.y / 100f)) * canvasHeight // Invert y for standard Cartesian

                            val clusterColor = when (p.clusterIndex) {
                                0 -> Color(0xFF00B6B7) // Teal
                                1 -> Color(0xFF8B5CF6) // Purple
                                2 -> Color(0xFFF472B6) // Pink
                                else -> Color(0xFF64748B) // Unassigned Slate
                            }

                            drawCircle(
                                color = clusterColor,
                                radius = 10f,
                                center = Offset(px, py)
                            )
                            drawCircle(
                                color = Color.Black,
                                radius = 4f,
                                center = Offset(px, py)
                            )
                        }

                        // Plot Centroids
                        centroids.forEach { c ->
                            val cx = (c.x / 100f) * canvasWidth
                            val cy = (1f - (c.y / 100f)) * canvasHeight

                            val centroidColor = when (c.colorHex) {
                                "#00B6B7" -> Color(0xFF00B6B7)
                                "#A020F0" -> Color(0xFF8B5CF6)
                                else -> Color(0xFFF472B6)
                            }

                            // Draw a larger cross symbol for centroids
                            val sizeOffset = 20f

                            // Outline glow
                            drawCircle(
                                color = Color.White,
                                radius = 22f,
                                center = Offset(cx, cy),
                                alpha = 0.3f
                            )

                            // Cross line 1
                            drawLine(
                                color = centroidColor,
                                start = Offset(cx - sizeOffset, cy - sizeOffset),
                                end = Offset(cx + sizeOffset, cy + sizeOffset),
                                strokeWidth = 6f
                            )
                            // Cross line 2
                            drawLine(
                                color = centroidColor,
                                start = Offset(cx - sizeOffset, cy + sizeOffset),
                                end = Offset(cx + sizeOffset, cy - sizeOffset),
                                strokeWidth = 6f
                            )

                            // Centroid Core
                            drawCircle(
                                color = Color.White,
                                radius = 8f,
                                center = Offset(cx, cy)
                            )
                            drawCircle(
                                color = centroidColor,
                                radius = 5f,
                                center = Offset(cx, cy)
                            )
                        }
                    }
                }

                // Legend
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CentroidLegendItem("Cluster 1", Color(0xFF00B6B7))
                    Spacer(Modifier.width(16.dp))
                    CentroidLegendItem("Cluster 2", Color(0xFF8B5CF6))
                    Spacer(Modifier.width(16.dp))
                    CentroidLegendItem("Cluster 3", Color(0xFFF472B6))
                }
            }
        }

        // Steps & Interactive Control Panel block
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF151922)),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFF1E2330))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "ALGORITHM MONITOR",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF8B5CF6),
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = "Cycles: ${viewModel.kMeansStepCount}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Text(
                    text = viewModel.kMeansStepDescription,
                    color = Color(0xFFECEFF1),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Divider(color = Color(0xFF1E2330))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Reset Button
                    Button(
                        onClick = { viewModel.generateKMeansDatasets() },
                        modifier = Modifier.weight(1f).testTag("kmeans_generate_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F111A)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Regen")
                        Spacer(Modifier.width(4.dp))
                        Text("Regen", fontSize = 11.sp)
                    }

                    // Assign Button
                    Button(
                        onClick = { viewModel.stepKMeansAssignment() },
                        modifier = Modifier.weight(1.3f).testTag("kmeans_assign_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8B5CF6)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Assign")
                        Spacer(Modifier.width(4.dp))
                        Text("1. Assign", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    // Move Centroids Button
                    Button(
                        onClick = { viewModel.stepKMeansCentroidsUpdate() },
                        modifier = Modifier.weight(1.3f).testTag("kmeans_update_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B6B7)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "Move")
                        Spacer(Modifier.width(4.dp))
                        Text("2. Re-Mean", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Button(
                    onClick = { viewModel.saveKMeansSession() },
                    modifier = Modifier.fillMaxWidth().testTag("kmeans_save_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF232832)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Save Run")
                    Spacer(Modifier.width(8.dp))
                    Text("Save Cluster Result to Lab Notes", fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun LinearRegressionSandbox(viewModel: DataScienceViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Scatter and Regression Canvas
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0D0F14)),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFF1E2330))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "GRADIENT FIT COORDINATES",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    fontFamily = FontFamily.Monospace
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .background(Color(0xFF07080B), RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFF151922), RoundedCornerShape(12.dp))
                ) {
                    val points = viewModel.regressionPoints
                    val w = viewModel.regW
                    val b = viewModel.regB

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val canvasWidth = size.width
                        val canvasHeight = size.height

                        // Draw Cartesian Axes
                        drawLine(
                            color = Color(0xFF1E2330),
                            start = Offset(0f, canvasHeight - 2f),
                            end = Offset(canvasWidth, canvasHeight - 2f),
                            strokeWidth = 4f
                        )
                        drawLine(
                            color = Color(0xFF1E2330),
                            start = Offset(2f, 0f),
                            end = Offset(2f, canvasHeight),
                            strokeWidth = 4f
                        )

                        // Draw Grid lines
                        val gridCount = 5
                        for (i in 1 until gridCount) {
                            val xPos = canvasWidth * i / gridCount
                            val yPos = canvasHeight * i / gridCount
                            drawLine(
                                color = Color(0xFF151922),
                                start = Offset(xPos, 0f),
                                end = Offset(xPos, canvasHeight),
                                strokeWidth = 1f
                            )
                            drawLine(
                                color = Color(0xFF151922),
                                start = Offset(0f, yPos),
                                end = Offset(canvasWidth, yPos),
                                strokeWidth = 1f
                            )
                        }

                        // Plot actual training coordinate points
                        points.forEach { p ->
                            val px = (p.x / 100f) * canvasWidth
                            val py = (1f - (p.y / 100f)) * canvasHeight

                            drawCircle(
                                color = Color(0xFFFF1493), // Pink data points
                                radius = 12f,
                                center = Offset(px, py)
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 4f,
                                center = Offset(px, py)
                            )
                        }

                        // Plot Regression Line: y = w * x + b
                        // Pick starting and ending coordinates on grid: x = 0 and x = 100
                        val y0 = w * 0f + b
                        val y100 = w * 100f + b

                        val px0 = (0f / 100f) * canvasWidth
                        val py0 = (1f - (y0 / 100f)) * canvasHeight

                        val px100 = (100f / 100f) * canvasWidth
                        val py100 = (1f - (y100 / 100f)) * canvasHeight

                        drawLine(
                            color = Color(0xFF00B6B7), // Electric Teal regression line
                            start = Offset(px0, py0),
                            end = Offset(px100, py100),
                            strokeWidth = 6f
                        )
                    }

                    // Floating text math parameters
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "Equation Model:",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "y = ${String.format("%.3f", w)}x + ${String.format("%.2f", b)}",
                            color = Color(0xFF00B6B7),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // Descent Metrics Control panel
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF151922)),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFF1E2330))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Main Info Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "COST FUNCTION MONITOR",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF1493),
                            letterSpacing = 1.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "MSE Loss: ${String.format("%.2f", viewModel.regLoss)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "DESCENT STEPS",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8),
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "${viewModel.regStepCount}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                // Hyper-parameter selection
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Learning Rate (α):", color = Color(0xFF94A3B8), fontSize = 12.sp)
                        Text(
                            text = viewModel.regLearningRate.toString(),
                            color = Color(0xFFFF1493),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Slider(
                        value = viewModel.regLearningRate,
                        onValueChange = {
                            viewModel.regLearningRate = it
                            viewModel.calculateRegressionLoss()
                        },
                        valueRange = 0.0001f..0.001f,
                        colors = SliderDefaults.colors(
                            activeTrackColor = Color(0xFFFF1493),
                            inactiveTrackColor = Color(0xFF0D0F14),
                            thumbColor = Color(0xFFFF1493)
                        )
                    )
                }

                // Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Reset Button
                    Button(
                        onClick = { viewModel.resetRegression() },
                        modifier = Modifier.weight(1f).testTag("regression_reset_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF1493)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset Line")
                        Spacer(Modifier.width(4.dp))
                        Text("Reset", fontSize = 12.sp)
                    }

                    // Descent Step Button
                    Button(
                        onClick = { viewModel.executeRegressionGradientStep() },
                        modifier = Modifier.weight(1.5f).testTag("regression_step_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B6B7)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "GD Step")
                        Spacer(Modifier.width(4.dp))
                        Text("Descent Step", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Button(
                    onClick = { viewModel.saveRegressionSession() },
                    modifier = Modifier.fillMaxWidth().testTag("regression_save_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2C3241)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Save Run")
                    Spacer(Modifier.width(8.dp))
                    Text("Save Regression Result to Lab Notes", fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
fun CentroidLegendItem(label: String, color: Color) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(color, RoundedCornerShape(2.dp))
        )
        Text(label, color = Color.White, fontSize = 12.sp)
    }
}

@Composable
fun DecisionTreeSandbox(viewModel: DataScienceViewModel) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // 1. Scatter and Split Boundaries Plot
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0D0F14)),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFF1E2330))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "DECISION BOUNDARY PARTITIONS",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    fontFamily = FontFamily.Monospace
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .background(Color(0xFF07080B), RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFF151922), RoundedCornerShape(12.dp))
                ) {
                    val points = viewModel.treePoints
                    val rootNode = viewModel.treeModelRoot

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val canvasWidth = size.width
                        val canvasHeight = size.height

                        // Draw Grid lines
                        val gridCount = 5
                        for (i in 1 until gridCount) {
                            val xPos = canvasWidth * i / gridCount
                            val yPos = canvasHeight * i / gridCount
                            drawLine(
                                color = Color(0xFF151922),
                                start = Offset(xPos, 0f),
                                end = Offset(xPos, canvasHeight),
                                strokeWidth = 1f
                            )
                            drawLine(
                                color = Color(0xFF151922),
                                start = Offset(0f, yPos),
                                end = Offset(canvasWidth, yPos),
                                strokeWidth = 1f
                            )
                        }

                        // Draw recursive tree decision boundaries if trained
                        if (rootNode != null) {
                            fun drawBoundaries(
                                node: DecisionNode?,
                                xMin: Float,
                                xMax: Float,
                                yMin: Float,
                                yMax: Float
                            ) {
                                if (node == null || node.feature == -1) return

                                val thX = (node.threshold / 100f) * canvasWidth
                                val thY = (1f - (node.threshold / 100f)) * canvasHeight

                                val pMinX = (xMin / 100f) * canvasWidth
                                val pMaxX = (xMax / 100f) * canvasWidth
                                val pMinY = (1f - (yMin / 100f)) * canvasHeight
                                val pMaxY = (1f - (yMax / 100f)) * canvasHeight

                                if (node.feature == 0) {
                                    // Vertical Line (X split)
                                    drawLine(
                                        color = Color(0xFF00B6B7).copy(alpha = 0.6f),
                                        start = Offset(thX, pMinY),
                                        end = Offset(thX, pMaxY),
                                        strokeWidth = 4f,
                                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 6f), 0f)
                                    )
                                    drawBoundaries(node.left, xMin, node.threshold, yMin, yMax)
                                    drawBoundaries(node.right, node.threshold, xMax, yMin, yMax)
                                } else {
                                    // Horizontal Line (Y split)
                                    drawLine(
                                        color = Color(0xFFFF1493).copy(alpha = 0.6f),
                                        start = Offset(pMinX, thY),
                                        end = Offset(pMaxX, thY),
                                        strokeWidth = 4f,
                                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 6f), 0f)
                                    )
                                    drawBoundaries(node.left, xMin, xMax, yMin, node.threshold)
                                    drawBoundaries(node.right, xMin, xMax, node.threshold, yMax)
                                }
                            }
                            drawBoundaries(rootNode, 0f, 100f, 0f, 100f)
                        }

                        // Plot classification labeled coordinate points
                        points.forEach { p ->
                            val px = (p.x / 100f) * canvasWidth
                            val py = (1f - (p.y / 100f)) * canvasHeight

                            val pointColor = if (p.label == 0) Color(0xFF00B6B7) else Color(0xFFFF1493)

                            drawCircle(
                                color = pointColor,
                                radius = 10f,
                                center = Offset(px, py)
                            )
                            drawCircle(
                                color = Color.White,
                                radius = 3f,
                                center = Offset(px, py)
                            )
                        }
                    }
                }

                // Legend
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CentroidLegendItem("Class A (Teal)", Color(0xFF00B6B7))
                    Spacer(Modifier.width(24.dp))
                    CentroidLegendItem("Class B (Pink)", Color(0xFFFF1493))
                }
            }
        }

        // 2. Metrics & Config Panel
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF151922)),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFF1E2330))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header Metrics
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TREE METRICS MONITOR",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF8E2DE2),
                            letterSpacing = 1.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "Accuracy: ${String.format("%.1f", viewModel.treeAccuracy)}%",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "TOTAL NODES",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8),
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "${viewModel.treeStepCount}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Divider(color = Color(0xFF1E2330))

                // Criterion Option Selector
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text("Splitting Criterion:", color = Color(0xFF94A3B8), fontSize = 12.sp)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("Gini Impurity", "Entropy").forEach { crit ->
                            val selected = viewModel.treeCriterion == crit
                            Button(
                                onClick = { viewModel.treeCriterion = crit },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selected) Color(0xFF8E2DE2) else Color(0xFF0F111A),
                                    contentColor = if (selected) Color.White else Color(0xFF94A3B8)
                                ),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(vertical = 4.dp, horizontal = 8.dp)
                            ) {
                                Text(crit, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Hyperparameter Slider 1: Max Depth
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Max Tree Depth:", color = Color(0xFF94A3B8), fontSize = 12.sp)
                        Text(
                            text = viewModel.treeMaxDepth.toInt().toString(),
                            color = Color(0xFF8E2DE2),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Slider(
                        value = viewModel.treeMaxDepth,
                        onValueChange = { viewModel.treeMaxDepth = it },
                        valueRange = 1f..5f,
                        steps = 3,
                        colors = SliderDefaults.colors(
                            activeTrackColor = Color(0xFF8E2DE2),
                            inactiveTrackColor = Color(0xFF0D0F14),
                            thumbColor = Color(0xFF8E2DE2)
                        )
                    )
                }

                // Hyperparameter Slider 2: Min Samples Split
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Min Samples Split:", color = Color(0xFF94A3B8), fontSize = 12.sp)
                        Text(
                            text = viewModel.treeMinSamplesSplit.toInt().toString(),
                            color = Color(0xFF8E2DE2),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Slider(
                        value = viewModel.treeMinSamplesSplit,
                        onValueChange = { viewModel.treeMinSamplesSplit = it },
                        valueRange = 2f..10f,
                        steps = 7,
                        colors = SliderDefaults.colors(
                            activeTrackColor = Color(0xFF8E2DE2),
                            inactiveTrackColor = Color(0xFF0D0F14),
                            thumbColor = Color(0xFF8E2DE2)
                        )
                    )
                }

                Divider(color = Color(0xFF1E2330))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Reset Button
                    Button(
                        onClick = { viewModel.generateDecisionTreePoints() },
                        modifier = Modifier.weight(1f).testTag("tree_reset_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F111A)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Regen Data", modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Regen", fontSize = 12.sp)
                    }

                    // Train Button
                    Button(
                        onClick = { viewModel.trainDecisionTree() },
                        modifier = Modifier.weight(1.5f).testTag("tree_train_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8E2DE2)),
                        shape = RoundedCornerShape(8.dp),
                        enabled = !viewModel.isTreeTraining
                    ) {
                        if (viewModel.isTreeTraining) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Train Tree", modifier = Modifier.size(16.dp))
                        }
                        Spacer(Modifier.width(4.dp))
                        Text(if (viewModel.isTreeTraining) "Training..." else "Train Model", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // 3. Status Logs Log
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0D0F14)),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFF1E2330))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "STATUS & LOG MONITOR",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFFFCC00),
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = viewModel.treeTrainingLog,
                    color = Color(0xFFECEFF1),
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        }

        // 4. Decision Tree Structure diagram
        viewModel.treeModelRoot?.let { root ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF151922)),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFF1E2330))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "COMPILED STRUCTURAL REPRESENTATION",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF94A3B8),
                        fontFamily = FontFamily.Monospace
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF07080B), RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        RenderTreeNode(root)
                    }
                }
            }
        }

        // 5. Save results Button
        Button(
            onClick = { viewModel.saveDecisionTreeSession() },
            modifier = Modifier.fillMaxWidth().testTag("tree_save_btn"),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF232832)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Save Run", modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Save Decision Tree to Lab Notes", fontSize = 13.sp)
        }
    }
}

@Composable
fun RenderTreeNode(node: DecisionNode, depth: Int = 0) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = (depth * 10).dp)
    ) {
        if (node.feature == -1) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .padding(vertical = 2.dp)
                    .background(Color(0xFF0F111A), RoundedCornerShape(4.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(if (node.label == 0) Color(0xFF00B6B7) else Color(0xFFFF1493), RoundedCornerShape(4.dp))
                )
                Text(
                    text = "Leaf: Class ${if (node.label == 0) "A" else "B"} (${node.totalSamples} samples, Imp: ${String.format("%.2f", node.impurity)})",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8),
                    fontFamily = FontFamily.Monospace
                )
            }
        } else {
            Column(modifier = Modifier.padding(vertical = 2.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Split",
                        tint = Color(0xFF8E2DE2),
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "Split [${if (node.feature == 0) "X" else "Y"}] < ${String.format("%.1f", node.threshold)} (Samples: ${node.totalSamples})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontFamily = FontFamily.Monospace
                    )
                }

                node.left?.let {
                    Row(modifier = Modifier.padding(vertical = 1.dp)) {
                        Text(" ├─ True: ", color = Color(0xFF64748B), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        RenderTreeNode(it, depth + 1)
                    }
                }

                node.right?.let {
                    Row(modifier = Modifier.padding(vertical = 1.dp)) {
                        Text(" └─ False: ", color = Color(0xFF64748B), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        RenderTreeNode(it, depth + 1)
                    }
                }
            }
        }
    }
}
