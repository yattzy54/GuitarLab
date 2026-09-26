package com.mmt.guitarlab.ui.practice

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mmt.guitarlab.core.ui.components.Studio3DAccent
import com.mmt.guitarlab.core.ui.components.Studio3DIconBadge
import com.mmt.guitarlab.core.ui.components.StudioCard
import com.mmt.guitarlab.core.ui.components.StudioPill
import com.mmt.guitarlab.core.ui.theme.ElectricAmber
import com.mmt.guitarlab.core.ui.theme.ElectricGreen
import com.mmt.guitarlab.core.ui.theme.ElectricRuby
import com.mmt.guitarlab.core.ui.theme.ElectricTeal
import com.mmt.guitarlab.core.ui.theme.GuitarLabTheme
import com.mmt.guitarlab.core.ui.theme.StudioDarkBg
import com.mmt.guitarlab.core.ui.theme.StudioTextMuted
import com.mmt.guitarlab.core.ui.theme.StudioTextPrimary
import com.mmt.guitarlab.core.ui.theme.StudioTextSecondary

@Composable
fun SlowDownerScreen(viewModel: SlowDownerViewModel = hiltViewModel()) {
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val positionMs by viewModel.positionMs.collectAsStateWithLifecycle()
    val durationMs by viewModel.durationMs.collectAsStateWithLifecycle()
    val speed by viewModel.speed.collectAsStateWithLifecycle()
    val loopA by viewModel.loopA.collectAsStateWithLifecycle()
    val loopB by viewModel.loopB.collectAsStateWithLifecycle()
    val loopEnabled by viewModel.loopEnabled.collectAsStateWithLifecycle()
    val trackTitle by viewModel.trackTitle.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val audioPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->
        uri?.let {
            viewModel.loadAudio(context, it, "Guitar Backing Track")
        }
    }

    SlowDownerContent(
        isPlaying = isPlaying,
        positionMs = positionMs,
        durationMs = durationMs,
        speed = speed,
        loopA = loopA,
        loopB = loopB,
        loopEnabled = loopEnabled,
        trackTitle = trackTitle,
        onImportClick = { audioPicker.launch("audio/*") },
        onSeek = viewModel::seekTo,
        onTogglePlayPause = viewModel::togglePlayPause,
        onSetSpeed = viewModel::setSpeed,
        onToggleLoop = viewModel::toggleLoopEnabled,
        onSetLoopA = viewModel::setLoopA,
        onSetLoopB = viewModel::setLoopB,
        onClearLoop = viewModel::clearLoop,
    )
}

@Composable
fun SlowDownerContent(
    isPlaying: Boolean,
    positionMs: Long,
    durationMs: Long,
    speed: Float,
    loopA: Long,
    loopB: Long,
    loopEnabled: Boolean,
    trackTitle: String?,
    onImportClick: () -> Unit,
    onSeek: (Long) -> Unit,
    onTogglePlayPause: () -> Unit,
    onSetSpeed: (Float) -> Unit,
    onToggleLoop: () -> Unit,
    onSetLoopA: () -> Unit,
    onSetLoopB: () -> Unit,
    onClearLoop: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 12.dp),
    ) {
        SlowDownerHeaderCard(
            trackTitle = trackTitle,
            onImportClick = onImportClick,
        )

        Spacer(Modifier.height(16.dp))

        SlowDownerWaveformCard(
            positionMs = positionMs,
            durationMs = durationMs,
            loopA = loopA,
            loopB = loopB,
            onSeek = onSeek,
        )

        Spacer(Modifier.height(20.dp))

        SlowDownerTransportControls(
            isPlaying = isPlaying,
            onRewind = { onSeek((positionMs - 5000).coerceAtLeast(0)) },
            onForward = { onSeek((positionMs + 5000).coerceAtMost(durationMs)) },
            onTogglePlayPause = onTogglePlayPause,
        )

        Spacer(Modifier.height(24.dp))

        SlowDownerSpeedCard(
            speed = speed,
            onSetSpeed = onSetSpeed,
        )

        Spacer(Modifier.height(16.dp))

        SlowDownerLooperCard(
            loopEnabled = loopEnabled,
            onToggleLoop = onToggleLoop,
            onSetLoopA = onSetLoopA,
            onSetLoopB = onSetLoopB,
            onClearLoop = onClearLoop,
        )

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun SlowDownerHeaderCard(
    trackTitle: String?,
    onImportClick: () -> Unit,
) {
    StudioCard(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onImportClick() }
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Studio3DIconBadge(
                icon = Icons.Default.AudioFile,
                contentDescription = "Import",
                size = 44.dp,
                accent = Studio3DAccent.TEAL,
            )
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = trackTitle ?: "Tap to Import Audio / Song",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = StudioTextPrimary,
                )
                Text(
                    text = if (trackTitle != null) "MP3, WAV, AAC Loaded" else "Select audio file from your device",
                    style = MaterialTheme.typography.bodySmall,
                    color = StudioTextSecondary,
                )
            }
        }
    }
}

@Composable
private fun SlowDownerWaveformCard(
    positionMs: Long,
    durationMs: Long,
    loopA: Long,
    loopB: Long,
    onSeek: (Long) -> Unit,
) {
    StudioCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    formatTime(positionMs),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = ElectricTeal,
                )
                Text(
                    formatTime(durationMs),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = StudioTextSecondary,
                )
            }

            Spacer(Modifier.height(8.dp))

            Slider(
                value = positionMs.toFloat(),
                onValueChange = { onSeek(it.toLong()) },
                valueRange = 0f..(durationMs.toFloat().coerceAtLeast(1f)),
                colors = SliderDefaults.colors(
                    thumbColor = ElectricTeal,
                    activeTrackColor = ElectricTeal,
                    inactiveTrackColor = Color(0xFF222B3D),
                ),
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(
                    text = "A: ${formatTime(loopA)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (loopA > 0) ElectricAmber else StudioTextMuted,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "B: ${formatTime(loopB)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (loopB > 0) ElectricAmber else StudioTextMuted,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun SlowDownerTransportControls(
    isPlaying: Boolean,
    onRewind: () -> Unit,
    onForward: () -> Unit,
    onTogglePlayPause: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Studio3DIconBadge(
            icon = Icons.Default.FastRewind,
            contentDescription = "-5s",
            size = 48.dp,
            accent = Studio3DAccent.SLATE,
            onClick = onRewind,
        )

        Spacer(Modifier.width(24.dp))

        Box(
            modifier = Modifier
                .size(74.dp)
                .shadow(
                    elevation = 12.dp,
                    shape = CircleShape,
                    ambientColor = if (isPlaying) ElectricRuby else ElectricTeal,
                    spotColor = if (isPlaying) ElectricRuby else ElectricTeal,
                )
                .clip(CircleShape)
                .background(
                    brush = Brush.verticalGradient(
                        if (isPlaying) {
                            listOf(Color(0xFFFF5277), Color(0xFFFF2A55), Color(0xFFB80028))
                        } else {
                            listOf(Color(0xFF80F5FF), ElectricTeal, Color(0xFF008394))
                        },
                    ),
                )
                .border(1.5.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                .clickable { onTogglePlayPause() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                tint = if (isPlaying) Color.White else Color(0xFF002227),
                modifier = Modifier.size(38.dp),
            )
        }

        Spacer(Modifier.width(24.dp))

        Studio3DIconBadge(
            icon = Icons.Default.FastForward,
            contentDescription = "+5s",
            size = 48.dp,
            accent = Studio3DAccent.SLATE,
            onClick = onForward,
        )
    }
}

@Composable
private fun SlowDownerSpeedCard(
    speed: Float,
    onSetSpeed: (Float) -> Unit,
) {
    StudioCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "PLAYBACK SPEED",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = StudioTextMuted,
                    letterSpacing = 1.sp,
                )
                Text(
                    text = String.format("%.2fx", speed),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = ElectricAmber,
                )
            }

            Spacer(Modifier.height(10.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                val presets = listOf(0.5f, 0.65f, 0.75f, 0.85f, 1.0f, 1.25f, 1.5f)
                presets.forEach { s ->
                    StudioPill(
                        text = String.format("%.2fx", s),
                        selected = kotlin.math.abs(speed - s) < 0.02f,
                        onClick = { onSetSpeed(s) },
                        accentColor = ElectricAmber,
                    )
                }
            }
        }
    }
}

@Composable
private fun SlowDownerLooperCard(
    loopEnabled: Boolean,
    onToggleLoop: () -> Unit,
    onSetLoopA: () -> Unit,
    onSetLoopB: () -> Unit,
    onClearLoop: () -> Unit,
) {
    StudioCard(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Studio3DIconBadge(
                        icon = Icons.Default.Loop,
                        contentDescription = null,
                        size = 36.dp,
                        accent = if (loopEnabled) Studio3DAccent.GREEN else Studio3DAccent.SLATE,
                    )
                    Spacer(Modifier.width(10.dp))
                    Text(
                        text = "A-B Practice Looper",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = StudioTextPrimary,
                    )
                }

                StudioPill(
                    text = if (loopEnabled) "Loop ON" else "Loop OFF",
                    selected = loopEnabled,
                    onClick = onToggleLoop,
                    accentColor = ElectricGreen,
                )
            }

            Spacer(Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                StudioPill(
                    text = "Set Point A",
                    selected = false,
                    onClick = onSetLoopA,
                    modifier = Modifier.weight(1f),
                    accentColor = ElectricAmber,
                )
                StudioPill(
                    text = "Set Point B",
                    selected = false,
                    onClick = onSetLoopB,
                    modifier = Modifier.weight(1f),
                    accentColor = ElectricAmber,
                )
                StudioPill(
                    text = "Clear",
                    selected = false,
                    onClick = onClearLoop,
                    leadingIcon = Icons.Default.Clear,
                    modifier = Modifier.weight(0.8f),
                    accentColor = ElectricRuby,
                )
            }
        }
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}

// ==================== PREVIEWS ====================

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun SlowDownerScreenPreview() {
    GuitarLabTheme {
        SlowDownerContent(
            isPlaying = true,
            positionMs = 45000L,
            durationMs = 180000L,
            speed = 0.75f,
            loopA = 30000L,
            loopB = 60000L,
            loopEnabled = true,
            trackTitle = "Acoustic Backing Track.mp3",
            onImportClick = {},
            onSeek = {},
            onTogglePlayPause = {},
            onSetSpeed = {},
            onToggleLoop = {},
            onSetLoopA = {},
            onSetLoopB = {},
            onClearLoop = {},
        )
    }
}