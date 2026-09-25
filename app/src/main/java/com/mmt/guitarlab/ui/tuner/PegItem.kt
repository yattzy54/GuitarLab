package com.mmt.guitarlab.ui.tuner

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mmt.guitarlab.domain.model.TuningNote
import com.mmt.guitarlab.core.ui.theme.ElectricAmber
import com.mmt.guitarlab.core.ui.theme.GuitarLabTheme
import com.mmt.guitarlab.core.ui.theme.StudioCardBorder
import com.mmt.guitarlab.core.ui.theme.StudioTextMuted
import com.mmt.guitarlab.core.ui.theme.StudioTextPrimary

@Composable
fun PegItem(
    note: TuningNote,
    isSelected: Boolean,
    isInTune: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    val (bgColors, borderColor, textColor) = when {
        isInTune -> Triple(
            listOf(Color(0xFF00E676), Color(0xFF00A850)),
            Color(0xFF69F0AE),
            Color(0xFF0D1117),
        )
        isSelected -> Triple(
            listOf(ElectricAmber, Color(0xFFC47D00)),
            Color(0xFFFFD166),
            Color(0xFF1A1200),
        )
        else -> Triple(
            listOf(Color(0xFF1E2433), Color(0xFF141824)),
            StudioCardBorder,
            StudioTextPrimary,
        )
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(brush = Brush.verticalGradient(bgColors))
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 10.dp, horizontal = 2.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "${note.stringNumber}",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                color = if (isSelected) textColor.copy(alpha = 0.75f) else StudioTextMuted,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "${note.noteName}${note.octave}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Black,
                color = textColor,
                maxLines = 1,
            )
        }
    }
}

// Заглушка для модели данных (если нужно для сборки превью)
// val sampleNote = TuningNote(stringNumber = 6, noteName = "E", octave = 2)

@Preview(showBackground = true, backgroundColor = 0xFF0D1117)
@Composable
fun PegItemPreviewGroup() {
    GuitarLabTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 1. Обычное состояние (не выбрано, не в строе)
            PegItem(
                //note = TuningNote(stringNumber = 6, noteName = "E", octave = 2),
            note = TuningNote(
                stringNumber = 6,
                noteName = "E",
                octave = 2,
                targetFrequencyHz = 329.63f,
                midiNote = 64,
            ),
                isSelected = false,
                isInTune = false,
                modifier = Modifier.width(60.dp),
                onClick = {}
            )

            // 2. Выбранное состояние
            PegItem(
                note = TuningNote(
                    stringNumber = 5,
                    noteName = "A",
                    octave = 2,
                    targetFrequencyHz = 110.0f,
                    midiNote = 69,
                ),
                isSelected = true,
                isInTune = false,
                modifier = Modifier.width(60.dp),
                onClick = {}
            )

            // 3. Состояние «В строе» (In Tune)
            PegItem(
                note = TuningNote(
                    stringNumber = 1,
                    noteName = "E",
                    octave = 4,
                    targetFrequencyHz = 329.63f,
                    midiNote = 64,
                ),
                isSelected = false,
                isInTune = true,
                modifier = Modifier.width(60.dp),
                onClick = {}
            )
        }
    }
}