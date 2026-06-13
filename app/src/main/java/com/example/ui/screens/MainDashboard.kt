package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Refresh
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
fun MainDashboard(viewModel: DataScienceViewModel) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color(0xFF07080B), // Pure deep dark science canvas
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F111A))
                    .windowInsetsPadding(WindowInsets.safeDrawing)
                    .padding(horizontal = 16.dp, vertical = 12.dp)
                    .border(0.5.dp, Color(0xFF1E2330), RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "DATA SCIENCE HUB",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF00B6B7),
                            letterSpacing = 2.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .background(Color(0xFF4ADE80), RoundedCornerShape(3.dp))
                            )
                            Text(
                                text = "Mathematical Sandbox v1.0",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Interactive Status Chip
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF151922)),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.padding(2.dp)
                    ) {
                        Text(
                            text = "AI ACTIVATED",
                            color = Color(0xFF4ADE80),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        },
        bottomBar = {
            // Modern bottom dynamic navigation satisfying safeDrawing and navigationBars padding
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .border(0.5.dp, Color(0xFF1E2330)),
                containerColor = Color(0xFF0F111A), // slate-charcoal bottom theme
                tonalElevation = 4.dp
            ) {
                val tabs = listOf(
                    Triple("deep_learning", "Deep Learning", Icons.Default.Settings),
                    Triple("machine_learning", "Algo Lab", Icons.Default.Build),
                    Triple("chart_lab", "Visual Lab", Icons.Default.Refresh),
                    Triple("ai_copilot", "AI Copilot", Icons.Default.Star),
                    Triple("lab_notes", "Notes Log", Icons.Default.Edit)
                )

                tabs.forEach { (tabId, label, icon) ->
                    val isSelected = viewModel.selectedTab == tabId
                    val activeColor = when (tabId) {
                        "deep_learning" -> Color(0xFFFF1493) // Intense Pink
                        "machine_learning" -> Color(0xFF8B5CF6) // Elegant Violet
                        "chart_lab" -> Color(0xFF00B6B7) // Electric Teal
                        "ai_copilot" -> Color(0xFF4ADE80) // Emerald Green
                        else -> Color(0xFFFFCC00) // Amber Yellow
                    }

                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.selectedTab = tabId },
                        modifier = Modifier.testTag("tab_item_$tabId"),
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                tint = if (isSelected) activeColor else Color(0xFF64748B)
                            )
                        },
                        label = {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.White else Color(0xFF64748B)
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = activeColor.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFF07080B))
        ) {
            when (viewModel.selectedTab) {
                "deep_learning" -> DeepLearningSimulator(viewModel)
                "machine_learning" -> MachineLearningPlayground(viewModel)
                "chart_lab" -> ChartLabScreen(viewModel)
                "ai_copilot" -> AiCopilotScreen(viewModel)
                "lab_notes" -> LabNotesScreen(viewModel)
            }
        }
    }
}
