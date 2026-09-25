package com.mmt.guitarlab.ui.tuner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mmt.guitarlab.domain.model.DetectedPitch
import com.mmt.guitarlab.domain.model.TuningNote
import com.mmt.guitarlab.ui.components.StudioCard
import com.mmt.guitarlab.ui.theme.ElectricAmber
import com.mmt.guitarlab.ui.theme.ElectricGreen
import com.mmt.guitarlab.ui.theme.ElectricTeal
import com.mmt.guitarlab.ui.theme.StudioTextMuted
import com.mmt.guitarlab.ui.theme.StudioTextPrimary
import com.mmt.guitarlab.ui.theme.StudioTextSecondary
import java.util.Locale
import kotlin.math.abs

@Composable
fun PrecisionTunerGaugeCard(
    pitch: DetectedPitch?,
    running: Boolean,
    isInTune: Boolean,
    currentCents: Float,
    centsAnimated: Float,
    isChromaticMode: Boolean,
    activeTuningNote: TuningNote?,
) {
    StudioCard(
        modifier = Modifier.fillMaxWidth(),
        accentBorder = if (isInTune) ElectricGreen else null,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val statusText = when {
                !running -> "Микрофон отключен"
                pitch == null || pitch.clarity < 0.4f -> "Дёрните струну..."
                isInTune -> "ИДЕАЛЬНО В СТРОЙ (В НОТУ)"
                currentCents < -3f -> String.format(Locale.US, "%.1f цент. НИЖЕ (Тяните вверх ↑)", abs(currentCents))
                else -> String.format(Locale.US, "%.1f цент. ВЫШЕ (Ослабьте вниз ↓)", currentCents)
            }

            val statusColor = when {
                !running -> StudioTextMuted
                pitch == null -> StudioTextSecondary
                isInTune -> ElectricGreen
                else -> ElectricAmber
            }

            Text(
                text = statusText,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = statusColor,
                letterSpacing = 1.sp,
            )

            Spacer(Modifier.height(14.dp))

            StrobeNeedleGauge(
                cents = centsAnimated,
                isInTune = isInTune,
                hasSignal = pitch != null && pitch.clarity >= 0.4f,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
            )

            Spacer(Modifier.height(10.dp))

            val displayNote = if (isChromaticMode) (pitch?.noteName ?: "--") else (activeTuningNote?.noteName ?: (pitch?.noteName ?: "--"))
            val displayOctave = if (isChromaticMode) (pitch?.octave?.toString() ?: "") else (activeTuningNote?.octave?.toString() ?: (pitch?.octave?.toString() ?: ""))
            val detectedHz = pitch?.frequencyHz ?: 0f
            val targetHz = if (isChromaticMode) 0f else (activeTuningNote?.targetFrequencyHz ?: 0f)

            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = displayNote,
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 68.sp,
                        fontWeight = FontWeight.Black,
                    ),
                    color = if (isInTune) ElectricGreen else StudioTextPrimary,
                )
                if (displayOctave.isNotEmpty()) {
                    Text(
                        text = displayOctave,
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = if (isInTune) ElectricGreen else ElectricTeal,
                        modifier = Modifier.padding(bottom = 12.dp, start = 4.dp),
                    )
                }
            }

            Row(
                modifier = Modifier.padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Text(
                    text = if (detectedHz > 0) String.format(Locale.US, "%.1f Hz", detectedHz) else "--.- Hz",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = StudioTextSecondary,
                )
                if (targetHz > 0 && detectedHz > 0) {
                    val delta = detectedHz - targetHz
                    val sign = if (delta > 0) "+" else ""
                    Text(
                        text = String.format(Locale.US, "%s%.1f Hz", sign, delta),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isInTune) ElectricGreen else ElectricAmber,
                    )
                }
            }
        }
    }
}