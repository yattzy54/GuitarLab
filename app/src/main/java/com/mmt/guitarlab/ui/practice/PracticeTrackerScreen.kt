package com.mmt.guitarlab.ui.practice

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mmt.guitarlab.data.db.PracticeSessionEntity
import com.mmt.guitarlab.ui.components.Studio3DAccent
import com.mmt.guitarlab.ui.components.Studio3DIconBadge
import com.mmt.guitarlab.ui.components.StudioCard
import com.mmt.guitarlab.ui.components.StudioPill
import com.mmt.guitarlab.ui.theme.ElectricAmber
import com.mmt.guitarlab.ui.theme.ElectricGreen
import com.mmt.guitarlab.ui.theme.ElectricRuby
import com.mmt.guitarlab.ui.theme.ElectricTeal
import com.mmt.guitarlab.ui.theme.StudioCardBg
import com.mmt.guitarlab.ui.theme.StudioCardBorder
import com.mmt.guitarlab.ui.theme.StudioCardElevated
import com.mmt.guitarlab.ui.theme.StudioDarkBg
import com.mmt.guitarlab.ui.theme.StudioTextMuted
import com.mmt.guitarlab.ui.theme.StudioTextPrimary
import com.mmt.guitarlab.ui.theme.StudioTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PracticeTrackerScreen(viewModel: PracticeTrackerViewModel = hiltViewModel()) {
    val stats by viewModel.stats.collectAsStateWithLifecycle()
    val sessions by viewModel.sessions.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .padding(horizontal = 18.dp, vertical = 12.dp),
    ) {
        // Top 3D Stats Dashboard Cards (Streak, Total Minutes, Total Sessions)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            // Streak Card
            StudioCard(
                modifier = Modifier.weight(1f),
                accentBorder = if (stats.streakDays > 0) ElectricAmber else null,
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Studio3DIconBadge(
                        icon = Icons.Default.LocalFireDepartment,
                        contentDescription = "Streak",
                        size = 38.dp,
                        accent = Studio3DAccent.AMBER,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "${stats.streakDays}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = ElectricAmber,
                    )
                    Text(
                        text = "Day Streak",
                        style = MaterialTheme.typography.labelSmall,
                        color = StudioTextSecondary,
                    )
                }
            }

            // Total Minutes Card
            StudioCard(
                modifier = Modifier.weight(1f),
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Studio3DIconBadge(
                        icon = Icons.Default.Schedule,
                        contentDescription = "Total Mins",
                        size = 38.dp,
                        accent = Studio3DAccent.TEAL,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "${stats.totalMinutes}m",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = ElectricTeal,
                    )
                    Text(
                        text = "Total Practice",
                        style = MaterialTheme.typography.labelSmall,
                        color = StudioTextSecondary,
                    )
                }
            }

            // Total Sessions Card
            StudioCard(
                modifier = Modifier.weight(1f),
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Studio3DIconBadge(
                        icon = Icons.Default.CheckCircle,
                        contentDescription = "Sessions",
                        size = 38.dp,
                        accent = Studio3DAccent.GREEN,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "${stats.totalSessions}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = ElectricGreen,
                    )
                    Text(
                        text = "Sessions",
                        style = MaterialTheme.typography.labelSmall,
                        color = StudioTextSecondary,
                    )
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Log Practice Session Action Card
        StudioCard(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showAddDialog = true }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Studio3DIconBadge(
                    icon = Icons.Default.Add,
                    contentDescription = "Log",
                    size = 40.dp,
                    accent = Studio3DAccent.AMBER,
                )
                Spacer(Modifier.width(14.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Log Practice Session",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = StudioTextPrimary,
                    )
                    Text(
                        text = "Record drills, exercises, scales and songs",
                        style = MaterialTheme.typography.bodySmall,
                        color = StudioTextSecondary,
                    )
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Practice Log Header
        Text(
            text = "PRACTICE SESSIONS LOG",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = StudioTextMuted,
            letterSpacing = 1.sp,
        )

        Spacer(Modifier.height(10.dp))

        if (sessions.isEmpty()) {
            StudioCard(modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        "No sessions recorded yet.\nTap 'Log Practice Session' above to build your daily streak!",
                        color = StudioTextSecondary,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(sessions) { session ->
                    StudioCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    Text(
                                        text = session.category,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = ElectricAmber,
                                    )
                                    Text(
                                        text = "·  ${session.durationMinutes} min",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = ElectricTeal,
                                    )
                                }
                                if (session.notes.isNotBlank()) {
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = session.notes,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = StudioTextPrimary,
                                    )
                                }
                                Spacer(Modifier.height(4.dp))
                                Text(
                                    text = formatDate(session.dateMillis),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = StudioTextMuted,
                                )
                            }

                            IconButton(onClick = { viewModel.deleteSession(session) }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = StudioTextMuted,
                                    modifier = Modifier.size(20.dp),
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        LogPracticeDialog(
            onSave = { duration, notes, category ->
                viewModel.logSession(duration, notes, category)
                showAddDialog = false
            },
            onDismiss = { showAddDialog = false },
        )
    }
}

@Composable
private fun LogPracticeDialog(
    onSave: (duration: Int, notes: String, category: String) -> Unit,
    onDismiss: () -> Unit,
) {
    var minutesText by remember { mutableStateOf("30") }
    var notes by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Technique") }
    val categories = listOf("Technique", "Repertoire", "Theory & Scales", "Song Practice", "Improv")

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = StudioCardBg,
        title = {
            Text(
                "Log Practice Session",
                fontWeight = FontWeight.Bold,
                color = StudioTextPrimary,
            )
        },
        text = {
            Column {
                Text(
                    "CATEGORY",
                    style = MaterialTheme.typography.labelSmall,
                    color = StudioTextMuted,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    categories.forEach { cat ->
                        StudioPill(
                            text = cat,
                            selected = category == cat,
                            onClick = { category = cat },
                            accentColor = ElectricAmber,
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                OutlinedTextField(
                    value = minutesText,
                    onValueChange = { minutesText = it },
                    label = { Text("Duration (minutes)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricAmber,
                        unfocusedBorderColor = StudioCardBorder,
                        focusedTextColor = StudioTextPrimary,
                        unfocusedTextColor = StudioTextPrimary,
                    ),
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (e.g. Mastered Intro Riff @ 110 BPM)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricTeal,
                        unfocusedBorderColor = StudioCardBorder,
                        focusedTextColor = StudioTextPrimary,
                        unfocusedTextColor = StudioTextPrimary,
                    ),
                )
            }
        },
        confirmButton = {
            StudioPill(
                text = "Save Session",
                selected = true,
                onClick = {
                    val duration = minutesText.toIntOrNull() ?: 0
                    if (duration > 0) onSave(duration, notes, category)
                },
                accentColor = ElectricAmber,
            )
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = StudioTextSecondary)
            }
        },
    )
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy · HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
