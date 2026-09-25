package com.mmt.guitarlab.ui.metronome.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mmt.guitarlab.domain.model.MetronomeBeat
import com.mmt.guitarlab.domain.model.MetronomeConfig
import com.mmt.guitarlab.domain.model.TrainerConfig
import com.mmt.guitarlab.ui.components.Studio3DAccent
import com.mmt.guitarlab.ui.components.Studio3DIconBadge
import com.mmt.guitarlab.ui.components.StudioCard
import com.mmt.guitarlab.core.ui.theme.ElectricAmber
import com.mmt.guitarlab.core.ui.theme.ElectricTeal
import com.mmt.guitarlab.core.ui.theme.GuitarLabTheme
import com.mmt.guitarlab.core.ui.theme.StudioDarkBg
import com.mmt.guitarlab.core.ui.theme.StudioTextMuted
import com.mmt.guitarlab.core.ui.theme.StudioTextPrimary
import com.mmt.guitarlab.core.ui.theme.StudioTextSecondary

@Composable
 fun LiveSpeedProgressCard(
    config: MetronomeConfig,
    beat: MetronomeBeat?,
    running: Boolean,
    onTogglePlay: () -> Unit,
) {
    val trainer = config.trainer
    val liveBpm = beat?.bpm ?: config.bpm

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

                PlayPauseButton(running = running, onClick = onTogglePlay)
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

            // Overall Speed Ramp Progress Bar
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
                    val remainingBeats = beat.beatsUntilJump
                    val remainingBars = ((remainingBeats + beatsPerBar - 1) / beatsPerBar).coerceAtLeast(1)
                    val barText = if (remainingBars == 1) "1 bar" else "$remainingBars bars"
                    "$barText until next +${trainer.incrementBpm} BPM step"
                }
                beat?.millisUntilJump != null -> "${(beat.millisUntilJump / 1000)}s until next +${trainer.incrementBpm} BPM step"
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
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun LiveSpeedProgressCardPreview() {
    GuitarLabTheme {
        Column(
            modifier = Modifier
                .background(StudioDarkBg)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Состояние: Остановлен (Ready)
            LiveSpeedProgressCard(
                config = MetronomeConfig(
                    bpm = 100,
                    trainer = TrainerConfig(
                        enabled = true,
                        startBpm = 80,
                        targetBpm = 140,
                        incrementBpm = 2
                    )
                ),
                beat = null,
                running = false,
                onTogglePlay = {}
            )

            // Состояние: Активен (Ramping Speed)
            LiveSpeedProgressCard(
                config = MetronomeConfig(
                    bpm = 100,
                    trainer = TrainerConfig(
                        enabled = true,
                        startBpm = 80,
                        targetBpm = 140,
                        incrementBpm = 2
                    )
                ),
                beat = MetronomeBeat(
                    beatInBar = 1,
                    barIndex = 2L,
                    accent = true,
                    bpm = 102,
                    progressToNextJump = 0.65f,
                    beatsUntilJump = 2,
                    millisUntilJump = null
                ),
                running = true,
                onTogglePlay = {}
            )
        }
    }
}