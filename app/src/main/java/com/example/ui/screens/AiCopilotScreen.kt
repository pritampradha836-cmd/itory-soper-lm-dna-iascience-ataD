package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.DataScienceViewModel

@Composable
fun AiCopilotScreen(viewModel: DataScienceViewModel) {
    val scrollState = rememberScrollState()

    val quickPrompts = listOf(
        "Explain backpropagation mathematically simply" to "Backprop Formula",
        "Write python code for a random forest training" to "Random Forest Code",
        "Suggest deep learning hyperparameters for LSTM" to "LSTM Tuning",
        "Compare Transformer vs CNN models brief" to "Transformer Comparison"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Intro Card
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
                        imageVector = Icons.Default.Star,
                        contentDescription = "AI CoPilot Icon",
                        tint = Color(0xFF4ADE80)
                    )
                    Text(
                        text = "RESEARCH COPILOT",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4ADE80),
                        letterSpacing = 1.5.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Text(
                    text = "Gemini Data Science Companion",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Text(
                    text = "A real Gemini generative model configured to assist with hyperparameter tuning, network structures, model deployment recommendations, or python scripting.",
                    fontSize = 13.sp,
                    color = Color(0xFF94A3B8),
                    lineHeight = 18.sp
                )
            }
        }

        // Quick Prompt Selection
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "SWIFT RESEARCH TEMPLATES",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF94A3B8),
                fontFamily = FontFamily.Monospace
            )
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(quickPrompts) { (query, label) ->
                    Card(
                        modifier = Modifier
                            .clickable {
                                viewModel.aiQuery = query
                                viewModel.askAiAssistant(query)
                            }
                            .testTag("prompt_template_$label"),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F111A)),
                        border = BorderStroke(1.dp, Color(0xFF1E2330)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Box(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = label,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00B6B7)
                            )
                        }
                    }
                }
            }
        }

        // Prompt Input Deck
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF151922)),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFF1E2330))
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = viewModel.aiQuery,
                    onValueChange = { viewModel.aiQuery = it },
                    placeholder = {
                        Text(
                            "e.g., Explain K-Means Clustering Silhouette Score...",
                            color = Color(0xFF64748B),
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ai_query_input"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF00B6B7),
                        unfocusedBorderColor = Color(0xFF1E2330),
                        focusedContainerColor = Color(0xFF0D0F14),
                        unfocusedContainerColor = Color(0xFF0D0F14)
                    )
                )

                Button(
                    onClick = { viewModel.askAiAssistant() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("ai_submit_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF00B6B7),
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(10.dp),
                    enabled = viewModel.aiQuery.isNotBlank() && !viewModel.isAiLoading
                ) {
                    if (viewModel.isAiLoading) {
                        CircularProgressIndicator(
                            color = Color.Black,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.ArrowForward, contentDescription = "Query")
                            Text("Consult Brain Core (Gemini AI)", fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        }

        // Response Render Space
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF07080B)),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, Color(0xFF1D222F))
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Terminal",
                            tint = Color(0xFF4ADE80)
                        )
                        Text(
                            text = "COPILOT INSIGHT",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF4ADE80),
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    // Save AI response to Notebook
                    Button(
                        onClick = { viewModel.saveAiResponseToNotebook() },
                        enabled = viewModel.aiResponse.isNotBlank() && !viewModel.isAiLoading && !viewModel.aiResponse.startsWith("Start by choosing"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF151922),
                            contentColor = Color.White
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(30.dp).testTag("ai_save_note_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Save", modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Add to Labs Log", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Divider(color = Color(0xFF1E2330))

                // Answer Display with styled layout
                if (viewModel.isAiLoading) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        CircularProgressIndicator(color = Color(0xFF00B6B7))
                        Text(
                            text = "Decrypting datasets and model structures...",
                            color = Color(0xFF94A3B8),
                            fontSize = 13.sp
                        )
                    }
                } else {
                    val rawText = viewModel.aiResponse
                    val lines = rawText.split("\n")

                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        var inCodeBlock = false
                        var codeAccumulator = StringBuilder()

                        lines.forEach { line ->
                            val trimmed = line.trim()
                            if (trimmed.startsWith("```")) {
                                if (inCodeBlock) {
                                    // Close current code block
                                    CodeBlock(codeText = codeAccumulator.toString())
                                    codeAccumulator = StringBuilder()
                                    inCodeBlock = false
                                } else {
                                    inCodeBlock = true
                                }
                            } else if (inCodeBlock) {
                                codeAccumulator.append(line).append("\n")
                            } else {
                                if (trimmed.startsWith("##")) {
                                    Text(
                                        text = trimmed.replace("##", "").trim(),
                                        color = Color(0xFF00B6B7),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                                    )
                                } else if (trimmed.startsWith("#")) {
                                    Text(
                                        text = trimmed.replace("#", "").trim(),
                                        color = Color.White,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Black,
                                        modifier = Modifier.padding(top = 10.dp, bottom = 4.dp)
                                    )
                                } else if (trimmed.startsWith("-") || trimmed.startsWith("*")) {
                                    Row(
                                        modifier = Modifier.padding(start = 8.dp),
                                        verticalAlignment = Alignment.Top
                                    ) {
                                        Text("• ", color = Color(0xFF00B6B7), fontSize = 14.sp)
                                        Text(
                                            text = trimmed.substring(1).trim(),
                                            color = Color(0xFFECEFF1),
                                            fontSize = 13.sp,
                                            lineHeight = 18.sp
                                        )
                                    }
                                } else if (trimmed.isNotBlank()) {
                                    Text(
                                        text = line,
                                        color = Color(0xFFECEFF1),
                                        fontSize = 13.sp,
                                        lineHeight = 18.sp
                                    )
                                }
                            }
                        }

                        // Code fallback block if file ends before closure
                        if (inCodeBlock && codeAccumulator.isNotEmpty()) {
                            CodeBlock(codeText = codeAccumulator.toString())
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CodeBlock(codeText: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .background(Color(0xFF0D0F14), RoundedCornerShape(8.dp))
            .border(1.dp, Color(0xFF1E2330), RoundedCornerShape(8.dp))
    ) {
        Text(
            text = codeText.trim(),
            color = Color(0xFFA5F3FC),
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace,
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth()
        )
    }
}
