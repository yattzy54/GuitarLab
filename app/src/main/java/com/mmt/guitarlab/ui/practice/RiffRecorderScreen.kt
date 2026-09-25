package com.mmt.guitarlab.ui.practice

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mmt.guitarlab.data.db.RiffRecordEntity
import com.mmt.guitarlab.ui.components.Studio3DAccent
import com.mmt.guitarlab.ui.components.Studio3DIconBadge
import com.mmt.guitarlab.ui.components.StudioCard
import com.mmt.guitarlab.ui.theme.ElectricAmber
import com.mmt.guitarlab.ui.theme.ElectricRuby
import com.mmt.guitarlab.ui.theme.ElectricTeal
import com.mmt.guitarlab.ui.theme.GuitarLabTheme
import com.mmt.guitarlab.ui.theme.StudioCardBorder
import com.mmt.guitarlab.ui.theme.StudioDarkBg
import com.mmt.guitarlab.ui.theme.StudioTextMuted
import com.mmt.guitarlab.ui.theme.StudioTextPrimary
import com.mmt.guitarlab.ui.theme.StudioTextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun RiffRecorderScreen(viewModel: RiffRecorderViewModel = hiltViewModel()) {
    val isRecording by viewModel.isRecording.collectAsStateWithLifecycle()
    val recordingDurationMs by viewModel.recordingDurationMs.collectAsStateWithLifecycle()
    val riffList by viewModel.riffList.collectAsStateWithLifecycle()
    val activeTuningName by viewModel.activeTuningName.collectAsStateWithLifecycle()
    val playingRiffId by viewModel.playingRiffId.collectAsStateWithLifecycle()

    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var bpmText by remember { mutableStateOf("120") }

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
                    PackageManager.PERMISSION_GRANTED,
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        hasPermission = granted
    }

    RiffRecorderContent(
        isRecording = isRecording,
        recordingDurationMs = recordingDurationMs,
        riffList = riffList,
        activeTuningName = activeTuningName,
        playingRiffId = playingRiffId,
        title = title,
        onTitleChange = { title = it },
        bpmText = bpmText,
        onBpmChange = { bpmText = it },
        onStartRecording = {
            if (hasPermission) {
                viewModel.startRecording(context, title)
            } else {
                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            }
        },
        onStopRecording = {
            val bpm = bpmText.toIntOrNull() ?: 120
            viewModel.stopRecording(title, bpm)
        },
        onPlayRiff = viewModel::playRiff,
        onDeleteRiff = viewModel::deleteRiff,
    )
}

@Composable
fun RiffRecorderContent(
    isRecording: Boolean,
    recordingDurationMs: Long,
    riffList: List<RiffRecordEntity>,
    activeTuningName: String,
    playingRiffId: Long?,
    title: String,
    onTitleChange: (String) -> Unit,
    bpmText: String,
    onBpmChange: (String) -> Unit,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
    onPlayRiff: (RiffRecordEntity) -> Unit,
    onDeleteRiff: (RiffRecordEntity) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .padding(horizontal = 18.dp, vertical = 12.dp),
    ) {
        RecorderDeckCard(
            isRecording = isRecording,
            recordingDurationMs = recordingDurationMs,
            activeTuningName = activeTuningName,
            title = title,
            onTitleChange = onTitleChange,
            bpmText = bpmText,
            onBpmChange = onBpmChange,
            onStartRecording = onStartRecording,
            onStopRecording = onStopRecording,
        )

        Spacer(Modifier.height(20.dp))

        // Saved Memos Header
        Text(
            text = "SAVED RIFF MEMOS (${riffList.size})",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = StudioTextMuted,
            letterSpacing = 1.sp,
        )

        Spacer(Modifier.height(10.dp))

        RiffMemoList(
            riffList = riffList,
            playingRiffId = playingRiffId,
            onPlayRiff = onPlayRiff,
            onDeleteRiff = onDeleteRiff,
        )
    }
}

@Composable
private fun RecorderDeckCard(
    isRecording: Boolean,
    recordingDurationMs: Long,
    activeTuningName: String,
    title: String,
    onTitleChange: (String) -> Unit,
    bpmText: String,
    onBpmChange: (String) -> Unit,
    onStartRecording: () -> Unit,
    onStopRecording: () -> Unit,
) {
    val pulseTransition = rememberInfiniteTransition(label = "recordingPulse")
    val pulseScale by pulseTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulseScale",
    )

    StudioCard(
        modifier = Modifier.fillMaxWidth(),
        accentBorder = if (isRecording) ElectricRuby else null,
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Inputs: Title & BPM
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = onTitleChange,
                    label = { Text("Riff Title") },
                    placeholder = { Text("e.g. Heavy Blues Riff") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricTeal,
                        unfocusedBorderColor = StudioCardBorder,
                        focusedTextColor = StudioTextPrimary,
                        unfocusedTextColor = StudioTextPrimary,
                    ),
                )
                OutlinedTextField(
                    value = bpmText,
                    onValueChange = onBpmChange,
                    label = { Text("BPM") },
                    modifier = Modifier.width(96.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricAmber,
                        unfocusedBorderColor = StudioCardBorder,
                        focusedTextColor = StudioTextPrimary,
                        unfocusedTextColor = StudioTextPrimary,
                    ),
                )
            }

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Tuning: $activeTuningName",
                    style = MaterialTheme.typography.bodySmall,
                    color = StudioTextSecondary,
                )
                Text(
                    text = if (isRecording) "RECORDING IN PROGRESS" else "STANDBY",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isRecording) ElectricRuby else ElectricTeal,
                    letterSpacing = 1.sp,
                )
            }

            Spacer(Modifier.height(16.dp))

            // Big Timer Display
            Text(
                text = formatDuration(recordingDurationMs),
                style = MaterialTheme.typography.displayMedium.copy(
                    fontSize = 48.sp,
                    fontWeight = FontWeight.Black,
                ),
                color = if (isRecording) ElectricRuby else StudioTextPrimary,
            )

            Spacer(Modifier.height(16.dp))

            // Record / Stop Hero Button
            if (isRecording) {
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .scale(pulseScale)
                        .shadow(
                            elevation = 16.dp,
                            shape = CircleShape,
                            ambientColor = ElectricRuby,
                            spotColor = ElectricRuby,
                        )
                        .clip(CircleShape)
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(Color(0xFFFF5277), Color(0xFFFF2A55), Color(0xFFB80028)),
                            ),
                        )
                        .border(2.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                        .clickable { onStopRecording() },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.Stop,
                        contentDescription = "Stop",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp),
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(76.dp)
                        .shadow(
                            elevation = 12.dp,
                            shape = CircleShape,
                            ambientColor = ElectricRuby,
                            spotColor = ElectricRuby,
                        )
                        .clip(CircleShape)
                        .background(
                            brush = Brush.verticalGradient(
                                listOf(Color(0xFFFF6B8B), Color(0xFFFF3366), Color(0xFFAD0030)),
                            ),
                        )
                        .border(1.5.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                        .clickable { onStartRecording() },
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.Default.FiberManualRecord,
                        contentDescription = "Record",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun RiffMemoList(
    riffList: List<RiffRecordEntity>,
    playingRiffId: Long?,
    onPlayRiff: (RiffRecordEntity) -> Unit,
    onDeleteRiff: (RiffRecordEntity) -> Unit,
) {
    if (riffList.isEmpty()) {
        StudioCard(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    "No guitar riff recordings yet.\nTap the record button above to capture ideas!",
                    color = StudioTextSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(riffList, key = { it.id }) { riff ->
                val isPlayingThis = playingRiffId == riff.id

                RiffMemoItem(
                    riff = riff,
                    isPlayingThis = isPlayingThis,
                    onPlay = { onPlayRiff(riff) },
                    onDelete = { onDeleteRiff(riff) },
                )
            }
        }
    }
}

@Composable
private fun RiffMemoItem(
    riff: RiffRecordEntity,
    isPlayingThis: Boolean,
    onPlay: () -> Unit,
    onDelete: () -> Unit,
) {
    StudioCard(
        modifier = Modifier.fillMaxWidth(),
        accentBorder = if (isPlayingThis) ElectricAmber else null,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Studio3DIconBadge(
                icon = if (isPlayingThis) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = "Play/Pause",
                size = 42.dp,
                accent = if (isPlayingThis) Studio3DAccent.AMBER else Studio3DAccent.SLATE,
                onClick = onPlay,
            )

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = riff.title.ifBlank { "Untitled Riff" },
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = StudioTextPrimary,
                )
                Text(
                    text = "${riff.tuningName}  ·  ${riff.bpm} BPM  ·  ${formatDuration(riff.durationMs)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = ElectricTeal,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = formatDate(riff.timestamp),
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

private fun formatDuration(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}

private fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy · HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

// ==================== PREVIEWS ====================

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun RiffRecorderScreenPreview() {
    GuitarLabTheme {
        RiffRecorderContent(
            isRecording = false,
            recordingDurationMs = 12000L,
            riffList = listOf(
                RiffRecordEntity(1L, "Heavy Blues Riff", "Standard E", 120, 25000L, 10, ""),
                RiffRecordEntity(2L, "Acoustic Intro", "Drop D", 90, 42000L, 10, ""),
            ),
            activeTuningName = "Standard E",
            playingRiffId = 1L,
            title = "",
            onTitleChange = {},
            bpmText = "120",
            onBpmChange = {},
            onStartRecording = {},
            onStopRecording = {},
            onPlayRiff = {},
            onDeleteRiff = {},
        )
    }
}