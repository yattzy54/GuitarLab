package com.mmt.guitarlab.ui.metronome.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mmt.guitarlab.domain.model.MetronomeConfig
import com.mmt.guitarlab.domain.model.TrainerConfig
import com.mmt.guitarlab.domain.model.TrainerIntervalKind
import com.mmt.guitarlab.ui.components.StudioCard
import com.mmt.guitarlab.ui.components.StudioPill
import com.mmt.guitarlab.ui.theme.ElectricAmber
import com.mmt.guitarlab.ui.theme.ElectricGreen
import com.mmt.guitarlab.ui.theme.ElectricTeal
import com.mmt.guitarlab.ui.theme.GuitarLabTheme
import com.mmt.guitarlab.ui.theme.StudioDarkBg
import com.mmt.guitarlab.ui.theme.StudioTextMuted
import com.mmt.guitarlab.ui.theme.StudioTextSecondary

@Composable
 fun TrainerSettingsCard(
    trainer: TrainerConfig,
    onSetTrainerStart: (Int) -> Unit,
    onSetTrainerTarget: (Int) -> Unit,
    onSetTrainerIncrement: (Int) -> Unit,
    onSetTrainerIntervalKind: (TrainerIntervalKind) -> Unit,
    onSetTrainerIntervalValue: (Int) -> Unit,
) {
    StudioCard(modifier = Modifier.fillMaxWidth()) {
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
                onChange = { onSetTrainerStart(it.toInt()) },
                accentColor = ElectricTeal,
            )

            Spacer(Modifier.height(14.dp))

            LabeledSliderRow(
                title = "Target Tempo",
                valueLabel = "${trainer.targetBpm} BPM",
                value = trainer.targetBpm.toFloat(),
                range = MetronomeConfig.MIN_BPM.toFloat()..MetronomeConfig.MAX_BPM.toFloat(),
                onChange = { onSetTrainerTarget(it.toInt()) },
                accentColor = ElectricAmber,
            )

            Spacer(Modifier.height(14.dp))

            LabeledSliderRow(
                title = "Increment per Interval",
                valueLabel = "+${trainer.incrementBpm} BPM",
                value = trainer.incrementBpm.toFloat(),
                range = 1f..12f,
                onChange = { onSetTrainerIncrement(it.toInt()) },
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
                        onClick = { onSetTrainerIntervalKind(TrainerIntervalKind.BARS) },
                        accentColor = ElectricTeal,
                    )
                    StudioPill(
                        text = "Seconds",
                        selected = isSeconds,
                        onClick = { onSetTrainerIntervalKind(TrainerIntervalKind.SECONDS) },
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
                onChange = { onSetTrainerIntervalValue(it.toInt()) },
                accentColor = ElectricTeal,
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun TrainerSettingsCardPreview() {
    GuitarLabTheme {
        Column(
            modifier = Modifier
                .background(StudioDarkBg)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            TrainerSettingsCard(
                trainer = TrainerConfig(
                    enabled = true,
                    startBpm = 80,
                    targetBpm = 140,
                    incrementBpm = 2,
                    intervalKind = TrainerIntervalKind.BARS,
                    intervalValue = 4
                ),
                onSetTrainerStart = {},
                onSetTrainerTarget = {},
                onSetTrainerIncrement = {},
                onSetTrainerIntervalKind = {},
                onSetTrainerIntervalValue = {}
            )
        }
    }
}
