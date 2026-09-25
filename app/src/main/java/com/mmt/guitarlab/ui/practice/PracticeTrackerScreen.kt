package com.mmt.guitarlab.ui.practice

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mmt.guitarlab.data.db.PracticeSessionEntity
import com.mmt.guitarlab.ui.components.Studio3DAccent
import com.mmt.guitarlab.ui.components.Studio3DIconBadge
import com.mmt.guitarlab.ui.components.StudioCard
import com.mmt.guitarlab.ui.theme.ElectricAmber
import com.mmt.guitarlab.ui.theme.ElectricGreen
import com.mmt.guitarlab.ui.theme.ElectricTeal
import com.mmt.guitarlab.ui.theme.GuitarLabTheme
import com.mmt.guitarlab.ui.theme.StudioDarkBg
import com.mmt.guitarlab.ui.theme.StudioTextMuted
import com.mmt.guitarlab.ui.theme.StudioTextPrimary
import com.mmt.guitarlab.ui.theme.StudioTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class PracticeStatsUiModel(
    val streakDays: Int = 0,
    val totalMinutes: Int = 0,
    val totalSessions: Int = 0,
)

@Composable
fun PracticeTrackerScreen(viewModel: PracticeTrackerViewModel = hiltViewModel()) {
    val statsEntity by viewModel.stats.collectAsStateWithLifecycle()
    val sessions by viewModel.sessions.collectAsStateWithLifecycle()
    var showAddDialog by remember { mutableStateOf(false) }

    val statsModel = PracticeStatsUiModel(
        streakDays = statsEntity.streakDays,
        totalMinutes = statsEntity.totalMinutes,
        totalSessions = statsEntity.totalSessions,
    )

    PracticeTrackerContent(
        stats = statsModel,
        sessions = sessions,
        onLogSessionClick = { showAddDialog = true },
        onDeleteSession = viewModel::deleteSession,
    )

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
fun PracticeTrackerContent(
    stats: PracticeStatsUiModel,
    sessions: List<PracticeSessionEntity>,
    onLogSessionClick: () -> Unit,
    onDeleteSession: (PracticeSessionEntity) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .padding(horizontal = 18.dp, vertical = 12.dp),
    ) {
        // Top 3D Stats Dashboard Cards
        PracticeStatsDashboard(stats = stats)

        Spacer(Modifier.height(16.dp))

        // Log Practice Session Action Card
        PracticeLogActionCard(onClick = onLogSessionClick)

        Spacer(Modifier.height(20.dp))

        // Practice Log Header & Content
        Text(
            text = "PRACTICE SESSIONS LOG",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = StudioTextMuted,
            letterSpacing = 1.sp,
        )

        Spacer(Modifier.height(10.dp))

        PracticeSessionsList(
            sessions = sessions,
            onDeleteSession = onDeleteSession,
        )
    }
}

@Composable
private fun PracticeStatsDashboard(stats: PracticeStatsUiModel) {
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
}

@Composable
private fun PracticeLogActionCard(onClick: () -> Unit) {
    StudioCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
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
}

@Composable
private fun PracticeSessionsList(
    sessions: List<PracticeSessionEntity>,
    onDeleteSession: (PracticeSessionEntity) -> Unit,
) {
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
        // Убран .weight(1f), теперь LazyColumn адаптируется по высоте
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(sessions, key = { it.id }) { session ->
                PracticeSessionItem(
                    session = session,
                    onDelete = { onDeleteSession(session) }
                )
            }
        }
    }
}

@Composable
private fun PracticeSessionItem(
    session: PracticeSessionEntity,
    onDelete: () -> Unit,
) {
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

            IconButton(onClick = onDelete) {
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

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy · HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

// ==================== PREVIEWS ====================

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun PracticeStatsDashboardPreview() {
    GuitarLabTheme {
        Column(
            modifier = Modifier
                .background(StudioDarkBg)
                .padding(16.dp)
        ) {
            PracticeStatsDashboard(
                stats = PracticeStatsUiModel(
                    streakDays = 5,
                    totalMinutes = 180,
                    totalSessions = 12
                )
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun PracticeTrackerScreenPreview() {
    GuitarLabTheme {
        PracticeTrackerContent(
            stats = PracticeStatsUiModel(
                streakDays = 3,
                totalMinutes = 90,
                totalSessions = 4
            ),
            sessions = listOf(
                PracticeSessionEntity(
                    id = 1L,
                    category = "Scales",
                    durationMinutes = 30,
                    notes = "Practiced pentatonic box 1 at 120 BPM",
                    dateMillis = System.currentTimeMillis()
                ),
                PracticeSessionEntity(
                    id = 2L,
                    category = "Repertoire",
                    durationMinutes = 45,
                    notes = "Worked on solo sections",
                    dateMillis = System.currentTimeMillis() - 86400000L
                )
            ),
            onLogSessionClick = {},
            onDeleteSession = {}
        )
    }
}