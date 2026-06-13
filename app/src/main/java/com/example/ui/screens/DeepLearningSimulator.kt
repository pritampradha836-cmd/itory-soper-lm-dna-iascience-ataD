package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.DataScienceViewModel

@Composable
fun DeepLearningSimulator(viewModel: DataScienceViewModel) {
    val scrollState = rememberScrollState()
    val w1State = viewModel.w1
    val w2State = viewModel.w2
    val hActivations = viewModel.activationHidden
    val outActivation = viewModel.activationOutput
    val x1 = viewModel.inputX1
    val x2 = viewModel.inputX2

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Main Tech Header Card
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
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Neural Net Icon",
                        tint = Color(0xFF00B6B7)
                    )
                    Text(
                        text = "DEEP LEARNING PLAYGROUND",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF00B6B7),
                        letterSpacing = 1.5.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Text(
                    text = "Interactive Backpropagation Engine",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Text(
                    text = "A real multi-layer feed-forward artificial neural network (2-3-1 structure) training live in your browser. Alter the hyper-parameters and watch gradient convergence step-by-step.",
                    fontSize = 13.sp,
                    color = Color(0xFF94A3B8),
                    lineHeight = 18.sp
                )
            }
        }

        // Network Topology Visual Canvas Section
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0D0F14)),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFF1E2330))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "LIVE TOPOLOGY GRAPH",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    fontFamily = FontFamily.Monospace
                )

                // The Draw Canvas
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                        .background(Color(0xFF07080B), RoundedCornerShape(12.dp))
                        .border(1.dp, Color(0xFF151922), RoundedCornerShape(12.dp))
                ) {
                    // Neural Network Topology Drawing

                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val canvasWidth = size.width
                        val canvasHeight = size.height

                        // Coordinates
                        // Left layer: 2 Neurons
                        val xInput = canvasWidth * 0.15f
                        val yInputs = floatArrayOf(canvasHeight * 0.33f, canvasHeight * 0.67f)

                        // Middle Layer: 3 Neurons
                        val xHidden = canvasWidth * 0.5f
                        val yHiddens = floatArrayOf(canvasHeight * 0.22f, canvasHeight * 0.5f, canvasHeight * 0.78f)

                        // Right Layer: 1 Neuron
                        val xOutput = canvasWidth * 0.85f
                        val yOutput = canvasHeight * 0.5f

                        // Draw Connections between Input and Hidden
                        for (i in 0..1) {
                            for (j in 0..2) {
                                val weightVal = w1State[i][j]
                                val lineColor = if (weightVal >= 0) Color(0xFF00B6B7) else Color(0xFFFF1493)
                                val thickness = (Math.abs(weightVal) * 3f + 1f).coerceAtMost(8f)
                                drawLine(
                                    color = lineColor.copy(alpha = 0.5f + (Math.abs(weightVal) / 2f).coerceAtMost(0.5f)),
                                    start = Offset(xInput, yInputs[i]),
                                    end = Offset(xHidden, yHiddens[j]),
                                    strokeWidth = thickness,
                                    cap = StrokeCap.Round
                                )
                            }
                        }

                        // Draw Connections between Hidden and Output
                        for (j in 0..2) {
                            val weightVal = w2State[j]
                            val lineColor = if (weightVal >= 0) Color(0xFF00B6B7) else Color(0xFFFF1493)
                            val thickness = (Math.abs(weightVal) * 3f + 1f).coerceAtMost(8f)
                            drawLine(
                                color = lineColor.copy(alpha = 0.5f + (Math.abs(weightVal) / 2f).coerceAtMost(0.5f)),
                                start = Offset(xHidden, yHiddens[j]),
                                end = Offset(xOutput, yOutput),
                                strokeWidth = thickness,
                                cap = StrokeCap.Round
                            )
                        }

                        // Draw Input Neurons
                        val inputVals = floatArrayOf(x1, x2)
                        for (i in 0..1) {
                            val act = inputVals[i]
                            // Inner fill matching activation
                            drawCircle(
                                color = Color(0xFF1E293B),
                                radius = 22f,
                                center = Offset(xInput, yInputs[i])
                            )
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(Color(0xFF38BDF8), Color(0xFF0284C7)),
                                    center = Offset(xInput, yInputs[i]),
                                    radius = 20f * act + 4f
                                ),
                                radius = 20f * act + 4f,
                                center = Offset(xInput, yInputs[i]),
                                alpha = 0.4f + 0.6f * act
                            )
                            drawCircle(
                                color = Color(0xFF0284C7),
                                radius = 22f,
                                center = Offset(xInput, yInputs[i]),
                                style = androidx.compose.ui.graphics.drawscope.Stroke(2f)
                            )
                        }

                        // Draw Hidden Neurons
                        for (j in 0..2) {
                            val act = hActivations[j]
                            drawCircle(
                                color = Color(0xFF1E293B),
                                radius = 22f,
                                center = Offset(xHidden, yHiddens[j])
                            )
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(Color(0xFFC084FC), Color(0xFF8B5CF6)),
                                    center = Offset(xHidden, yHiddens[j]),
                                    radius = 20f * act + 4f
                                ),
                                radius = 20f * act + 4f,
                                center = Offset(xHidden, yHiddens[j]),
                                alpha = 0.4f + 0.6f * act
                            )
                            drawCircle(
                                color = Color(0xFF8B5CF6),
                                radius = 22f,
                                center = Offset(xHidden, yHiddens[j]),
                                style = androidx.compose.ui.graphics.drawscope.Stroke(2f)
                            )
                        }

                        // Draw Output Neuron
                        drawCircle(
                            color = Color(0xFF1E293B),
                            radius = 24f,
                            center = Offset(xOutput, yOutput)
                        )
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(Color(0xFFF472B6), Color(0xFFE11D48)),
                                center = Offset(xOutput, yOutput),
                                radius = 22f * outActivation + 4f
                            ),
                            radius = 22f * outActivation + 4f,
                            center = Offset(xOutput, yOutput),
                            alpha = 0.4f + 0.6f * outActivation
                        )
                        drawCircle(
                            color = Color(0xFFE11D48),
                            radius = 24f,
                            center = Offset(xOutput, yOutput),
                            style = androidx.compose.ui.graphics.drawscope.Stroke(2.5f)
                        )
                    }

                    // Floating text parameters inside canvas
                    Text(
                        text = "Inputs (X1,X2)",
                        color = Color(0xFF94A3B8),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(start = 8.dp, bottom = 8.dp)
                    )

                    Text(
                        text = "Hidden Activation",
                        color = Color(0xFFC084FC),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 8.dp)
                    )

                    Text(
                        text = "Output",
                        color = Color(0xFFF472B6),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(end = 8.dp, bottom = 8.dp)
                    )

                    // Display Node Activations
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(8.dp)
                    ) {
                        Text("X1 = ${String.format("%.2f", x1)}", color = Color(0xFF38BDF8), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                        Text("X2 = ${String.format("%.2f", x2)}", color = Color(0xFF38BDF8), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                    }

                    Column(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Hidden Hx", color = Color(0xFFC084FC), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            hActivations.forEachIndexed { i, act ->
                                Text("h${i+1}:${String.format("%.2f", act)}", color = Color.White, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                            }
                        }
                    }

                    Column(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Text("Prediction", color = Color(0xFFF472B6), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text("y_pred = ${String.format("%.4f", outActivation)}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, fontFamily = FontFamily.Monospace)
                    }
                }

                // Input Simulation Sliders (changes inputs on the fly and fires forward pass)
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Drag Input Sliders to test Feed-Forward Prediction instantly:",
                        fontSize = 12.sp,
                        color = Color(0xFF94A3B8),
                        fontWeight = FontWeight.Medium
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text("Input X1: ${String.format("%.2f", x1)}", color = Color(0xFF38BDF8), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            }
                            Slider(
                                value = x1,
                                onValueChange = {
                                    viewModel.inputX1 = it
                                    viewModel.runForwardPropagation()
                                },
                                valueRange = 0f..1f,
                                modifier = Modifier.height(28.dp)
                            )
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text("Input X2: ${String.format("%.2f", x2)}", color = Color(0xFF38BDF8), fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            }
                            Slider(
                                value = x2,
                                onValueChange = {
                                    viewModel.inputX2 = it
                                    viewModel.runForwardPropagation()
                                },
                                valueRange = 0f..1f,
                                modifier = Modifier.height(28.dp)
                            )
                        }
                    }
                }
            }
        }

        // Training Control Dashboard Card
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "TRAINING MONITOR",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFF1493),
                            letterSpacing = 1.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "Epoch: ${viewModel.nnEpoch}",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // Target function Selector
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .background(Color(0xFF0F111A), RoundedCornerShape(8.dp))
                            .padding(4.dp)
                    ) {
                        listOf("XOR Gate", "AND Gate").forEach { label ->
                            val selected = viewModel.nnDatasetName == label
                            Button(
                                onClick = {
                                    viewModel.nnDatasetName = label
                                    viewModel.resetNeuralNetwork()
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selected) Color(0xFF00B6B7) else Color.Transparent,
                                    contentColor = if (selected) Color.Black else Color.White
                                ),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                shape = RoundedCornerShape(6.dp),
                                modifier = Modifier.height(30.dp)
                            ) {
                                Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Loss Display
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0D0F14), RoundedCornerShape(12.dp))
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Global Training Error (MSE Loss)",
                            color = Color(0xFF94A3B8),
                            fontSize = 11.sp
                        )
                        Text(
                            text = String.format("%.6f", viewModel.nnLoss),
                            color = if (viewModel.nnLoss < 0.01) Color(0xFF4ADE80) else Color(0xFFFF1493),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .border(2.dp, Color(0xFF1E2330), RoundedCornerShape(27.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            progress = { (1f - viewModel.nnLoss.toFloat()).coerceIn(0f, 1f) },
                            modifier = Modifier.fillMaxSize(),
                            color = Color(0xFF00B6B7),
                            strokeWidth = 4.dp,
                            trackColor = Color(0xFF151922)
                        )
                        Text(
                            text = "${((1.0 - viewModel.nnLoss).coerceIn(0.0, 1.0) * 100).toInt()}%",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Adjust Learning Rate
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Learning Rate (η):", color = Color(0xFF94A3B8), fontSize = 12.sp)
                        Text(
                            text = viewModel.nnLearningRate.toString(),
                            color = Color(0xFF00B6B7),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Slider(
                        value = viewModel.nnLearningRate,
                        onValueChange = { viewModel.nnLearningRate = it },
                        valueRange = 0.01f..1.0f,
                        colors = SliderDefaults.colors(
                            activeTrackColor = Color(0xFF00B6B7),
                            inactiveTrackColor = Color(0xFF0D0F14),
                            thumbColor = Color(0xFF00B6B7)
                        )
                    )
                }

                // Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Reset Button
                    OutlinedButton(
                        onClick = { viewModel.resetNeuralNetwork() },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("reset_nn_button"),
                        border = BorderStroke(1.dp, Color(0xFFFF1493)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFFFF1493)
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset")
                        Spacer(Modifier.width(4.dp))
                        Text("Reset", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    // Single Step Button
                    OutlinedButton(
                        onClick = { viewModel.executeBackpropagationStep() },
                        modifier = Modifier
                            .weight(1.2f)
                            .testTag("step_nn_button"),
                        border = BorderStroke(1.dp, Color(0xFF00B6B7)),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF00B6B7)
                        ),
                        shape = RoundedCornerShape(10.dp),
                        enabled = !viewModel.isNnTraining
                    ) {
                        Icon(Icons.Default.ArrowForward, contentDescription = "Step")
                        Spacer(Modifier.width(4.dp))
                        Text("Step SGD", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    // Train Toggle Button
                    val isTraining = viewModel.isNnTraining
                    Button(
                        onClick = { viewModel.toggleNnTraining() },
                        modifier = Modifier
                            .weight(1.5f)
                            .testTag("train_nn_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isTraining) Color(0xFFFF1493) else Color(0xFF00B6B7),
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = if (isTraining) Icons.Default.Close else Icons.Default.PlayArrow,
                            contentDescription = "Train"
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = if (isTraining) "Pause" else "Train Live",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                // Log Training Metrics to Local Database
                Button(
                    onClick = { viewModel.saveNnTrainingRun() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("save_nn_run_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2C3241),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Log Session")
                    Spacer(Modifier.width(8.dp))
                    Text("Save Run to Lab Notes", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
