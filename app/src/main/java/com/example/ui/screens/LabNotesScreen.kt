package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ModelSession
import com.example.data.model.SavedNote
import com.example.viewmodel.DataScienceViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LabNotesScreen(viewModel: DataScienceViewModel) {
    val scrollState = rememberScrollState()
    val notes by viewModel.savedNotes.collectAsState()
    val sessions by viewModel.modelSessions.collectAsState()

    var activeSubTab by mutableStateOf("notes") // "notes" or "runs"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Lab Notes Header
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
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Notes Icon",
                            tint = Color(0xFFFFCC00)
                        )
                        Text(
                            text = "SCIENCE JOURNAL",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFFCC00),
                            letterSpacing = 1.5.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    if (sessions.isNotEmpty()) {
                        IconButton(
                            onClick = { viewModel.clearAllHistory() },
                            modifier = Modifier.testTag("clear_history_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Clear Session Run History",
                                tint = Color(0xFFFF1493)
                            )
                        }
                    }
                }

                Text(
                    text = "Local Research Notebook",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White
                )

                Text(
                    text = "A sandboxed SQLite database running on Room to store experiment metrics, network equations, gradient outputs, and saved AI insights.",
                    fontSize = 13.sp,
                    color = Color(0xFF94A3B8),
                    lineHeight = 18.sp
                )

                // Sub-tab selectors
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F111A), RoundedCornerShape(10.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { activeSubTab = "notes" },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (activeSubTab == "notes") Color(0xFFFFCC00) else Color.Transparent,
                            contentColor = if (activeSubTab == "notes") Color.Black else Color(0xFF94A3B8)
                        ),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("Log Journal (${notes.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { activeSubTab = "runs" },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (activeSubTab == "runs") Color(0xFFFFCC00) else Color.Transparent,
                            contentColor = if (activeSubTab == "runs") Color.Black else Color(0xFF94A3B8)
                        ),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("Model Runs (${sessions.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Active logs sub view switcher
        if (activeSubTab == "notes") {
            NotesSubView(viewModel = viewModel, notes = notes)
        } else {
            SessionsSubView(sessions = sessions)
        }
    }
}

@Composable
fun NotesSubView(viewModel: DataScienceViewModel, notes: List<SavedNote>) {
    var showAddForm by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Trigger Add Form block
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0D0F14)),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, Color(0xFF1E2330))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                if (!showAddForm) {
                    Button(
                        onClick = { showAddForm = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("show_add_note_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF232832)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Form")
                        Spacer(Modifier.width(8.dp))
                        Text("Add Custom Research Entry", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("New Journal Log Entity", color = Color.White, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text(
                                "Cancel",
                                color = Color(0xFFFF1493),
                                fontSize = 12.sp,
                                modifier = Modifier.clickable { showAddForm = false }
                            )
                        }

                        OutlinedTextField(
                            value = viewModel.newNoteTitle,
                            onValueChange = { viewModel.newNoteTitle = it },
                            placeholder = { Text("Topic name...", color = Color(0xFF64748B)) },
                            modifier = Modifier.fillMaxWidth().testTag("note_title_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFFFCC00),
                                unfocusedBorderColor = Color(0xFF1E2330)
                            )
                        )

                        OutlinedTextField(
                            value = viewModel.newNoteContent,
                            onValueChange = { viewModel.newNoteContent = it },
                            placeholder = { Text("Details or math notes...", color = Color(0xFF64748B)) },
                            modifier = Modifier.fillMaxWidth().height(100.dp).testTag("note_content_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                focusedBorderColor = Color(0xFFFFCC00),
                                unfocusedBorderColor = Color(0xFF1E2330)
                            )
                        )

                        Button(
                            onClick = {
                                viewModel.saveCustomNote()
                                showAddForm = false
                            },
                            enabled = viewModel.newNoteTitle.isNotBlank() && viewModel.newNoteContent.isNotBlank(),
                            modifier = Modifier.fillMaxWidth().testTag("save_note_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFCC00), contentColor = Color.Black)
                        ) {
                            Text("Append Entry", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        // Notes List
        if (notes.isEmpty()) {
            EmptyListPlaceholder("No exploration notes logged yet. Run simulations, consult Gemini copilot, or build a custom notebook here!")
        } else {
            notes.forEach { note ->
                SavedNoteItem(note = note, onDelete = { viewModel.deleteNote(note) })
            }
        }
    }
}

@Composable
fun SessionsSubView(sessions: List<ModelSession>) {
    if (sessions.isEmpty()) {
        EmptyListPlaceholder("Empty Model History. Set input variables, run Linear Regression model gradient fits or train Neural Networks to persist sessions automatically!")
    } else {
        sessions.forEach { sess ->
            ModelSessionItem(session = sess)
        }
    }
}

@Composable
fun SavedNoteItem(note: SavedNote, onDelete: () -> Unit) {
    val dateString = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault()).format(Date(note.timestamp))

    val (badgeText, badgeColor) = when (note.type) {
        "AI" -> "AI INSIGHT" to Color(0xFF4ADE80)
        "ML" -> "MACHINE LEARNING" to Color(0xFF8B5CF6)
        "DL" -> "DEEP LEARNING" to Color(0xFFFF1493)
        else -> "USER NOTE" to Color(0xFFFFCC00)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F111A)),
        border = BorderStroke(1.dp, Color(0xFF1E2330)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .background(badgeColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = badgeText,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = badgeColor,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Text(
                    text = dateString,
                    color = Color(0xFF64748B),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            Text(
                text = note.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = note.content,
                fontSize = 13.sp,
                color = Color(0xFFECEFF1),
                lineHeight = 18.sp
            )

            Divider(color = Color(0xFF1E2330), thickness = 0.5.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(32.dp)
                        .testTag("delete_note_${note.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete entry",
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ModelSessionItem(session: ModelSession) {
    val dateString = SimpleDateFormat("MMM dd • HH:mm", Locale.getDefault()).format(Date(session.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F111A)),
        border = BorderStroke(1.dp, Color(0xFF1E2330)),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(Color(0xFF4ADE80), RoundedCornerShape(4.dp))
            )

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = session.modelName,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Tested: ${session.datasetName} | Iterations: ${session.epochCount}",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = dateString,
                    color = Color(0xFF64748B),
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
                Text(
                    text = "${session.finalMetricName}: ${String.format("%.4f", session.finalMetricValue)}",
                    color = Color(0xFF00B6B7),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

@Composable
fun EmptyListPlaceholder(message: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 32.dp, horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = "Empty File",
            tint = Color(0xFF1E2330),
            modifier = Modifier.size(48.dp)
        )
        Text(
            text = message,
            color = Color(0xFF64748B),
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            lineHeight = 18.sp
        )
    }
}
