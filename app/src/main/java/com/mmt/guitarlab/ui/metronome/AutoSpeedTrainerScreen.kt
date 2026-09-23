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
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TrendingUp
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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Status & Progress Card
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
                            icon = Icons.Default.TrendingUp,
                            contentDescription = null,
                            size = 40.dp,
                            accent = if (trainer.enabled) Studio3DAccent.TEAL else Studio3DAccent.SLATE,
                        )
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                "Auto-Speed Trainer",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = StudioTextPrimary,
                            )
                            Text(
                                text = if (running) "Running · ${config.bpm} BPM" else "Ready · ${config.bpm} BPM",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (running) ElectricTeal else StudioTextSecondary,
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                    }

                    // Play / Pause Button in place of switch
                    Box(
                        modifier = Modifier
                            .size(54.dp)
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
                            modifier = Modifier.size(30.dp),
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))
                if (true) {

                    // Progress to Next Increment
                    LinearProgressIndicator(
                        progress = { beat?.progressToNextJump ?: 0f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = ElectricTeal,
                        trackColor = Color(0xFF1E2638),
                    )

                    Spacer(Modifier.height(8.dp))

                    val eta = when {
                        beat?.beatsUntilJump != null -> "${beat!!.beatsUntilJump} beats until next +${trainer.incrementBpm} BPM step"
                        beat?.millisUntilJump != null -> "${(beat!!.millisUntilJump!! / 1000)}s until next +${trainer.incrementBpm} BPM step"
                        else -> "Press Play to begin speed ramp training"
                    }

                    Text(
                        text = eta,
                        style = MaterialTheme.typography.bodySmall,
                        color = ElectricAmber,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }



        Spacer(Modifier.height(20.dp))

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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        "Interval Unit",
                        style = MaterialTheme.typography.labelMedium,
                        color = StudioTextSecondary,
                        fontWeight = FontWeight.Bold,
                    )
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        StudioPill(
                            text = "Bars",
                            selected = trainer.intervalKind == TrainerIntervalKind.BARS,
                            onClick = { viewModel.setTrainerIntervalKind(TrainerIntervalKind.BARS) },
                            accentColor = ElectricTeal,
                        )
                        StudioPill(
                            text = "Minutes",
                            selected = trainer.intervalKind == TrainerIntervalKind.MINUTES,
                            onClick = { viewModel.setTrainerIntervalKind(TrainerIntervalKind.MINUTES) },
                            accentColor = ElectricTeal,
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                LabeledSliderRow(
                    title = if (trainer.intervalKind == TrainerIntervalKind.BARS) "Interval Duration (Bars)" else "Interval Duration (Minutes)",
                    valueLabel = "${trainer.intervalValue}",
                    value = trainer.intervalValue.toFloat(),
                    range = 1f..16f,
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
