package com.mmt.guitarlab.ui.metronome

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mmt.guitarlab.domain.model.MetronomeConfig
import com.mmt.guitarlab.domain.model.TrainerIntervalKind

@Composable
fun AutoSpeedTrainerScreen(viewModel: MetronomeViewModel = hiltViewModel()) {
    val config by viewModel.config.collectAsStateWithLifecycle()
    val beat by viewModel.beat.collectAsStateWithLifecycle()
    val running by viewModel.running.collectAsStateWithLifecycle()
    val trainer = config.trainer

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Status Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text("Auto-Speed Trainer", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(
                            text = if (trainer.enabled) "Active · ${config.bpm} BPM" else "Trainer Disabled",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                    Switch(
                        checked = trainer.enabled,
                        onCheckedChange = viewModel::setTrainerEnabled,
                    )
                }

                if (trainer.enabled) {
                    Spacer(Modifier.height(12.dp))
                    LinearProgressIndicator(
                        progress = { beat?.progressToNextJump ?: 0f },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    )
                    Spacer(Modifier.height(6.dp))
                    val eta = when {
                        beat?.beatsUntilJump != null -> "${beat!!.beatsUntilJump} beats to next speed increment"
                        beat?.millisUntilJump != null -> "${(beat!!.millisUntilJump!! / 1000)}s to next speed increment"
                        else -> "Press play to start speed trainer"
                    }
                    Text(
                        text = eta,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }

        // Start / Pause FAB
        FilledIconButton(
            onClick = {
                if (!trainer.enabled) viewModel.setTrainerEnabled(true)
                viewModel.toggle()
            },
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

        // Configuration Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                LabeledSliderRow(
                    title = "Start Tempo",
                    valueLabel = "${trainer.startBpm} BPM",
                    value = trainer.startBpm.toFloat(),
                    range = MetronomeConfig.MIN_BPM.toFloat()..MetronomeConfig.MAX_BPM.toFloat(),
                    onChange = { viewModel.setTrainerStart(it.toInt()) },
                )

                LabeledSliderRow(
                    title = "Target Tempo",
                    valueLabel = "${trainer.targetBpm} BPM",
                    value = trainer.targetBpm.toFloat(),
                    range = MetronomeConfig.MIN_BPM.toFloat()..MetronomeConfig.MAX_BPM.toFloat(),
                    onChange = { viewModel.setTrainerTarget(it.toInt()) },
                )

                LabeledSliderRow(
                    title = "Increment",
                    valueLabel = "+${trainer.incrementBpm} BPM",
                    value = trainer.incrementBpm.toFloat(),
                    range = 1f..12f,
                    onChange = { viewModel.setTrainerIncrement(it.toInt()) },
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Interval Unit", style = MaterialTheme.typography.labelMedium)
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        FilterChip(
                            selected = trainer.intervalKind == TrainerIntervalKind.BARS,
                            onClick = { viewModel.setTrainerIntervalKind(TrainerIntervalKind.BARS) },
                            label = { Text("Bars") },
                        )
                        FilterChip(
                            selected = trainer.intervalKind == TrainerIntervalKind.MINUTES,
                            onClick = { viewModel.setTrainerIntervalKind(TrainerIntervalKind.MINUTES) },
                            label = { Text("Minutes") },
                        )
                    }
                }

                LabeledSliderRow(
                    title = if (trainer.intervalKind == TrainerIntervalKind.BARS) "Interval (Bars)" else "Interval (Mins)",
                    valueLabel = "${trainer.intervalValue}",
                    value = trainer.intervalValue.toFloat(),
                    range = 1f..16f,
                    onChange = { viewModel.setTrainerIntervalValue(it.toInt()) },
                )
            }
        }
    }
}

@Composable
private fun LabeledSliderRow(
    title: String,
    valueLabel: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onChange: (Float) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(title, style = MaterialTheme.typography.labelMedium)
        Text(valueLabel, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
    }
    Slider(
        value = value,
        onValueChange = onChange,
        valueRange = range,
    )
}
