package com.mmt.guitarlab.ui.metronome

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
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mmt.guitarlab.domain.model.MetronomeConfig
import com.mmt.guitarlab.domain.model.MetronomeSound
import com.mmt.guitarlab.domain.model.TrainerIntervalKind
import com.mmt.guitarlab.ui.components.Studio3DAccent
import com.mmt.guitarlab.ui.components.Studio3DIconBadge
import com.mmt.guitarlab.ui.components.StudioCard
import com.mmt.guitarlab.ui.components.StudioPill
import com.mmt.guitarlab.ui.theme.ElectricAmber
import com.mmt.guitarlab.ui.theme.ElectricGreen
import com.mmt.guitarlab.ui.theme.ElectricRuby
import com.mmt.guitarlab.ui.theme.ElectricTeal
import com.mmt.guitarlab.ui.theme.StudioCardBorder
import com.mmt.guitarlab.ui.theme.StudioDarkBg
import com.mmt.guitarlab.ui.theme.StudioTextMuted
import com.mmt.guitarlab.ui.theme.StudioTextPrimary
import com.mmt.guitarlab.ui.theme.StudioTextSecondary

@Composable
fun AutoSpeedTrainerScreen(viewModel: MetronomeViewModel = hiltViewModel()) {
    val config by viewModel.config.collectAsStateWithLifecycle()
    val beat by viewModel.beat.collectAsStateWithLifecycle()
    val running by viewModel.running.collectAsStateWithLifecycle()
    val trainer = config.trainer

    val liveBpm = beat?.bpm ?: config.bpm

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Status & Live Speed Progress Card
        StudioCard(
            modifier = Modifier.fillMaxWidth(),
            accentBorder = if (running) ElectricTeal else null,
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Studio3DIconBadge(
                            icon = Icons.AutoMirrored.Filled.TrendingUp,
                            contentDescription = null,
                            size = 40.dp,
                            accent = if (trainer.enabled) Studio3DAccent.TEAL else Studio3DAccent.SLATE,
                        )
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Auto-Speed Trainer",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = StudioTextPrimary,
                            )
                            Text(
                                text = if (running) "RAMPING SPEED" else "READY TO TRAIN",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (running) ElectricTeal else StudioTextSecondary,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.8.sp,
                            )
                        }
                    }

                    // Play / Pause Button
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = CircleShape,
                                ambientColor = if (running) ElectricRuby else ElectricTeal,
                                spotColor = if (running) ElectricRuby else ElectricTeal,
                            )
                            .clip(CircleShape)
                            .background(
                                brush = Brush.verticalGradient(
                                    if (running) {
                                        listOf(Color(0xFFFF5277), Color(0xFFFF2A55), Color(0xFFB80028))
                                    } else {
                                        listOf(Color(0xFF80F5FF), ElectricTeal, Color(0xFF008394))
                                    },
                                ),
                            )
                            .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape)
                            .clickable {
                                if (!trainer.enabled) viewModel.setTrainerEnabled(true)
                                viewModel.toggle()
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = if (running) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (running) "Stop" else "Start",
                            tint = if (running) Color.White else Color(0xFF002227),
                            modifier = Modifier.size(32.dp),
                        )
                    }
                }

                Spacer(Modifier.height(18.dp))

                // PROMINENT LIVE METRONOME SPEED DISPLAY
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "LIVE METRONOME SPEED",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (running) ElectricTeal else StudioTextMuted,
                        letterSpacing = 1.2.sp,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = "$liveBpm BPM",
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Black,
                        fontSize = 44.sp,
                        color = if (running) ElectricTeal else StudioTextPrimary,
                    )
                }

                Spacer(Modifier.height(16.dp))

                // Overall Speed Ramp Progress Bar (start -> current -> target)
                val startBpm = trainer.startBpm
                val targetBpm = trainer.targetBpm
                val totalRamp = (targetBpm - startBpm).let { if (it == 0) 1 else kotlin.math.abs(it) }
                val currentRamp = (liveBpm - startBpm).let { kotlin.math.abs(it) }
                val overallRampProgress = (currentRamp.toFloat() / totalRamp).coerceIn(0f, 1f)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Ramp Progress",
                        style = MaterialTheme.typography.labelSmall,
                        color = StudioTextSecondary,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "$startBpm → $targetBpm BPM",
                        style = MaterialTheme.typography.labelSmall,
                        color = ElectricAmber,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Spacer(Modifier.height(6.dp))

                LinearProgressIndicator(
                    progress = { overallRampProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = ElectricAmber,
                    trackColor = Color(0xFF1E2638),
                )

                Spacer(Modifier.height(14.dp))

                // Next Interval Increment Progress Bar
                LinearProgressIndicator(
                    progress = { beat?.progressToNextJump ?: 0f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = ElectricTeal,
                    trackColor = Color(0xFF1E2638),
                )

                Spacer(Modifier.height(6.dp))

                val eta = when {
                    beat?.beatsUntilJump != null -> {
                        val beatsPerBar = config.timeSignature.beatsPerBar
                        val remainingBeats = beat!!.beatsUntilJump!!
                        val remainingBars = ((remainingBeats + beatsPerBar - 1) / beatsPerBar).coerceAtLeast(1)
                        val barText = if (remainingBars == 1) "1 bar" else "$remainingBars bars"
                        "$barText until next +${trainer.incrementBpm} BPM step"
                    }
                    beat?.millisUntilJump != null -> "${(beat!!.millisUntilJump!! / 1000)}s until next +${trainer.incrementBpm} BPM step"
                    else -> "Press Play to begin auto-speed training"
                }

                Text(
                    text = eta,
                    style = MaterialTheme.typography.bodySmall,
                    color = ElectricTeal,
                    fontWeight = FontWeight.Medium,
                    fontSize = 12.sp,
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Metronome Sound Selection Card
        StudioCard(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
            ) {
                Text(
                    text = "METRONOME SOUND",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = StudioTextMuted,
                    letterSpacing = 1.sp,
                )

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    MetronomeSound.entries.forEach { sound ->
                        val isSelected = config.sound == sound
                        StudioPill(
                            text = sound.label,
                            selected = isSelected,
                            onClick = { viewModel.setSound(sound) },
                            accentColor = ElectricAmber,
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Trainer Settings Card
        StudioCard(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = "SPEED RAMP CONFIGURATION",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = StudioTextMuted,
                    letterSpacing = 1.sp,
                )

                Spacer(Modifier.height(14.dp))

                LabeledSliderRow(
                    title = "Starting Tempo",
                    valueLabel = "${trainer.startBpm} BPM",
                    value = trainer.startBpm.toFloat(),
                    range = MetronomeConfig.MIN_BPM.toFloat()..MetronomeConfig.MAX_BPM.toFloat(),
                    onChange = { viewModel.setTrainerStart(it.toInt()) },
                    accentColor = ElectricTeal,
                )

                Spacer(Modifier.height(14.dp))

                LabeledSliderRow(
                    title = "Target Tempo",
                    valueLabel = "${trainer.targetBpm} BPM",
                    value = trainer.targetBpm.toFloat(),
                    range = MetronomeConfig.MIN_BPM.toFloat()..MetronomeConfig.MAX_BPM.toFloat(),
                    onChange = { viewModel.setTrainerTarget(it.toInt()) },
                    accentColor = ElectricAmber,
                )

                Spacer(Modifier.height(14.dp))

                LabeledSliderRow(
                    title = "Increment per Interval",
                    valueLabel = "+${trainer.incrementBpm} BPM",
                    value = trainer.incrementBpm.toFloat(),
                    range = 1f..12f,
                    onChange = { viewModel.setTrainerIncrement(it.toInt()) },
                    accentColor = ElectricGreen,
                )

                Spacer(Modifier.height(16.dp))

                // Interval Unit Selector: Bars vs Seconds
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Interval Unit",
                        style = MaterialTheme.typography.labelMedium,
                        color = StudioTextSecondary,
                        fontWeight = FontWeight.Bold,
                    )
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        val isSeconds = (trainer.intervalKind == TrainerIntervalKind.SECONDS) || (trainer.intervalKind == TrainerIntervalKind.MINUTES)
                        StudioPill(
                            text = "Bars",
                            selected = trainer.intervalKind == TrainerIntervalKind.BARS,
                            onClick = { viewModel.setTrainerIntervalKind(TrainerIntervalKind.BARS) },
                            accentColor = ElectricTeal,
                        )
                        StudioPill(
                            text = "Seconds",
                            selected = isSeconds,
                            onClick = { viewModel.setTrainerIntervalKind(TrainerIntervalKind.SECONDS) },
                            accentColor = ElectricTeal,
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                val isBarsMode = trainer.intervalKind == TrainerIntervalKind.BARS
                val maxInterval = if (isBarsMode) 16f else 60f
                val intervalLabel = if (isBarsMode) "${trainer.intervalValue} bars" else "${trainer.intervalValue} s"

                LabeledSliderRow(
                    title = if (isBarsMode) "Interval Duration (Bars)" else "Interval Duration (Seconds)",
                    valueLabel = intervalLabel,
                    value = trainer.intervalValue.toFloat().coerceIn(1f, maxInterval),
                    range = 1f..maxInterval,
                    onChange = { viewModel.setTrainerIntervalValue(it.toInt()) },
                    accentColor = ElectricTeal,
                )
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun LabeledSliderRow(
    title: String,
    valueLabel: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onChange: (Float) -> Unit,
    accentColor: Color,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            title,
            style = MaterialTheme.typography.labelMedium,
            color = StudioTextSecondary,
            fontWeight = FontWeight.Bold,
        )
        Text(
            valueLabel,
            style = MaterialTheme.typography.labelMedium,
            color = accentColor,
            fontWeight = FontWeight.Black,
        )
    }
    Slider(
        value = value,
        onValueChange = onChange,
        valueRange = range,
        colors = SliderDefaults.colors(
            thumbColor = accentColor,
            activeTrackColor = accentColor,
            inactiveTrackColor = Color(0xFF22293B),
        ),
    )
}
