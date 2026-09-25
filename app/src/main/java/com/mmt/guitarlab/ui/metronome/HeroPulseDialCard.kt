package com.mmt.guitarlab.ui.metronome

import androidx.compose.foundation.background
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mmt.guitarlab.domain.model.MetronomeBeat
import com.mmt.guitarlab.domain.model.MetronomeConfig
import com.mmt.guitarlab.domain.model.TimeSignature
import com.mmt.guitarlab.ui.components.StudioCard
import com.mmt.guitarlab.ui.metronome.components.BeatDots
import com.mmt.guitarlab.ui.metronome.components.TempoCircularWheel
import com.mmt.guitarlab.ui.metronome.components.TempoStepButton
import com.mmt.guitarlab.core.ui.theme.ElectricAmber
import com.mmt.guitarlab.core.ui.theme.ElectricTeal
import com.mmt.guitarlab.core.ui.theme.GuitarLabTheme
import com.mmt.guitarlab.core.ui.theme.StudioDarkBg
import com.mmt.guitarlab.core.ui.theme.StudioTextPrimary
import com.mmt.guitarlab.core.ui.theme.StudioTextSecondary

@Composable
fun HeroPulseDialCard(
    config: MetronomeConfig,
    beat: MetronomeBeat?,
    running: Boolean,
    pulse: Float,
    onSetBpm: (Int) -> Unit,
) {
    val tempoName = when (config.bpm) {
        in 40..59 -> "Largo"
        in 60..65 -> "Larghetto"
        in 66..75 -> "Adagio"
        in 76..107 -> "Andante"
        in 108..119 -> "Moderato"
        in 120..155 -> "Allegro"
        in 156..199 -> "Vivace"
        else -> "Presto"
    }

    StudioCard(
        modifier = Modifier.fillMaxWidth(),
        accentBorder = if (running && beat?.accent == true) ElectricAmber else null,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Beat Dots (1, 2, 3, 4...)
            BeatDots(
                beats = config.timeSignature.beatsPerBar,
                accents = config.timeSignature.accentBeats,
                current = beat?.beatInBar,
                pulse = pulse,
            )

            Spacer(Modifier.height(18.dp))

            // Big Glowing Pulse Wheel & BPM Display
            Box(
                modifier = Modifier
                    .size(190.dp)
                    .scale(if (running) pulse else 1f),
                contentAlignment = Alignment.Center,
            ) {
                TempoCircularWheel(
                    bpm = config.bpm,
                    running = running,
                    accent = beat?.accent == true,
                    modifier = Modifier.fillMaxSize(),
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "${config.bpm}",
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 64.sp,
                            fontWeight = FontWeight.Black,
                        ),
                        color = if (running && beat?.accent == true) ElectricAmber else StudioTextPrimary,
                    )
                    Text(
                        text = "BPM · $tempoName",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (running) ElectricTeal else StudioTextSecondary,
                        letterSpacing = 1.2.sp,
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Fine Adjustment Buttons (-5, -1, +1, +5)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                TempoStepButton(label = "-5", onClick = { onSetBpm(config.bpm - 5) })
                TempoStepButton(label = "-1", onClick = { onSetBpm(config.bpm - 1) })
                TempoStepButton(label = "+1", onClick = { onSetBpm(config.bpm + 1) })
                TempoStepButton(label = "+5", onClick = { onSetBpm(config.bpm + 5) })
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun HeroPulseDialCardPreview() {
    GuitarLabTheme {
        Column(
            modifier = Modifier
                .background(StudioDarkBg)
                .padding(16.dp),
        ) {
            HeroPulseDialCard(
                config = MetronomeConfig(bpm = 120, timeSignature = TimeSignature.FOUR_FOUR),
                beat = MetronomeBeat(beatInBar = 1, barIndex = 1L, accent = true, bpm = 120, progressToNextJump = 0f, beatsUntilJump = null, millisUntilJump = null),
                running = true,
                pulse = 1.28f,
                onSetBpm = {},
            )
        }
    }
}