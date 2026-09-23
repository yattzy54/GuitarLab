package com.mmt.guitarlab.ui.practice

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AudioFile
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

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
            viewModel.loadAudio(context, it, "Audio Track")
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Text("Audio Slow-Downer & A-B Looper", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        // Import Button
        Button(
            onClick = { audioPicker.launch("audio/*") },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Icon(Icons.Default.AudioFile, contentDescription = "Import")
            Spacer(Modifier.width(8.dp))
            Text("Import Audio Track")
        }

        // Track Info Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = trackTitle ?: "No Track Selected",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(formatTime(positionMs), style = MaterialTheme.typography.bodyMedium)
                    Text(formatTime(durationMs), style = MaterialTheme.typography.bodyMedium)
                }
                Slider(
                    value = positionMs.toFloat(),
                    onValueChange = { viewModel.seekTo(it.toLong()) },
                    valueRange = 0f..(durationMs.toFloat().coerceAtLeast(1f)),
                )
            }
        }

        // Play / Pause Button
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            FilledIconButton(
                onClick = viewModel::togglePlayPause,
                modifier = Modifier.size(64.dp),
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Play/Pause",
                    modifier = Modifier.size(32.dp),
                )
            }
        }

        // Speed Controls (Single Horizontal Scrollable Row)
        Column {
            Text("Speed: %.2fx".format(speed), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                val speedPresets = listOf(0.5f, 0.65f, 0.75f, 0.85f, 1.0f, 1.25f)
                speedPresets.forEach { s ->
                    FilterChip(
                        selected = speed == s,
                        onClick = { viewModel.setSpeed(s) },
                        label = { Text("%.2fx".format(s)) },
                    )
                }
            }
        }

        // A-B Looper Controls Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("A-B Loop Markers", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    FilterChip(
                        selected = loopEnabled,
                        onClick = viewModel::toggleLoopEnabled,
                        label = { Text(if (loopEnabled) "Loop ON" else "Loop OFF") },
                        leadingIcon = { Icon(Icons.Default.Loop, contentDescription = null) },
                    )
                }

                Spacer(Modifier.height(4.dp))

                Text("A: ${formatTime(loopA)}   ·   B: ${formatTime(loopB)}", style = MaterialTheme.typography.bodyMedium)

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedButton(onClick = viewModel::setLoopA) {
                        Text("Set Marker A")
                    }
                    OutlinedButton(onClick = viewModel::setLoopB) {
                        Text("Set Marker B")
                    }
                    OutlinedButton(onClick = viewModel::clearLoop) {
                        Text("Clear")
                    }
                }
            }
        }
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d".format(minutes, seconds)
}
