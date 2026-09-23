package com.mmt.guitarlab.ui.metronome

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeDown
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mmt.guitarlab.domain.model.TimeSignature

@Composable
fun MetronomeScreen(viewModel: MetronomeViewModel = hiltViewModel()) {
    val config by viewModel.config.collectAsStateWithLifecycle()
    val beat by viewModel.beat.collectAsStateWithLifecycle()
    val running by viewModel.running.collectAsStateWithLifecycle()

    val pulse by animateFloatAsState(
        targetValue = if (beat?.accent == true) 1.25f else if (beat != null) 1.1f else 1f,
        animationSpec = tween(70),
        label = "beatPulse",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Hero Tempo Display Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            ),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "TEMPO",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 2.sp,
                )

                Spacer(Modifier.height(4.dp))

                // BPM Adjust Row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    OutlinedButton(
                        onClick = { viewModel.setBpm(config.bpm - 5) },
                        modifier = Modifier.size(40.dp),
                        contentPadding = ButtonDefaults.ContentPadding,
                        shape = CircleShape,
                    ) {
                        Text("-5", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }

                    Spacer(Modifier.width(6.dp))

                    FilledIconButton(
                        onClick = { viewModel.setBpm(config.bpm - 1) },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        ),
                    ) {
                        Icon(Icons.Default.Remove, contentDescription = "Decrease BPM")
                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(horizontal = 12.dp),
                    ) {
                        Text(
                            text = "${config.bpm}",
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontSize = 56.sp,
                                fontWeight = FontWeight.ExtraBold,
                            ),
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            text = "BPM · ${config.timeSignature.label}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Medium,
                        )
                    }

                    FilledIconButton(
                        onClick = { viewModel.setBpm(config.bpm + 1) },
                        colors = IconButtonDefaults.filledIconButtonColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        ),
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Increase BPM")
                    }

                    Spacer(Modifier.width(6.dp))

                    OutlinedButton(
                        onClick = { viewModel.setBpm(config.bpm + 5) },
                        modifier = Modifier.size(40.dp),
                        contentPadding = ButtonDefaults.ContentPadding,
                        shape = CircleShape,
                    ) {
                        Text("+5", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(Modifier.height(12.dp))

                // Tap Tempo Button
                Button(
                    onClick = viewModel::onTapTempo,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    ),
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Icon(Icons.Default.TouchApp, contentDescription = "Tap Tempo", modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("TAP TEMPO", fontWeight = FontWeight.Bold)
                }

                Spacer(Modifier.height(12.dp))

                // Beat Visualizer
                BeatDots(
                    beats = config.timeSignature.beatsPerBar,
                    accents = config.timeSignature.accentBeats,
                    current = beat?.beatInBar,
                    pulse = pulse,
                )
            }
        }

        // Action Play/Pause FAB Button
        FilledIconButton(
            onClick = viewModel::toggle,
            modifier = Modifier.size(72.dp),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = if (running) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
            ),
        ) {
            Icon(
                imageVector = if (running) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (running) "Stop" else "Start",
                modifier = Modifier.size(36.dp),
            )
        }

        // Time Signature Selector (Single Horizontal Row)
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "Time Signature",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    TimeSignature.entries.forEach { ts ->
                        FilterChip(
                            selected = config.timeSignature == ts,
                            onClick = { viewModel.setTimeSignature(ts) },
                            label = { Text(ts.label, fontWeight = FontWeight.SemiBold) },
                        )
                    }
                }
            }
        }

        // Volume Slider Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("Click Volume", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    Text("${(config.volume * 100).toInt()}%", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.primary)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.AutoMirrored.Filled.VolumeDown, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    Slider(
                        value = config.volume,
                        onValueChange = viewModel::setVolume,
                        valueRange = 0.1f..1f,
                        modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                    )
                    Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

@Composable
private fun BeatDots(beats: Int, accents: Set<Int>, current: Int?, pulse: Float) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
        repeat(beats) { index ->
            val beatNum = index + 1
            val active = current == beatNum
            val isAccent = beatNum in accents

            val color = when {
                active && isAccent -> MaterialTheme.colorScheme.primary
                active -> MaterialTheme.colorScheme.secondary
                isAccent -> MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
                else -> MaterialTheme.colorScheme.surfaceVariant
            }

            Box(
                modifier = Modifier
                    .size(if (isAccent) 20.dp else 16.dp)
                    .scale(if (active) pulse else 1f)
                    .clip(CircleShape)
                    .background(color),
            )
        }
    }
}
