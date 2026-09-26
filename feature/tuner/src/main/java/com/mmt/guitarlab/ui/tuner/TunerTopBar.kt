package com.mmt.guitarlab.ui.tuner

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
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
import com.mmt.guitarlab.domain.model.TuningNote
import com.mmt.guitarlab.core.ui.components.Studio3DAccent
import com.mmt.guitarlab.core.ui.components.Studio3DIconBadge
import com.mmt.guitarlab.core.ui.theme.ElectricAmber
import com.mmt.guitarlab.core.ui.theme.GuitarLabTheme
import com.mmt.guitarlab.core.ui.theme.StudioCardBg
import com.mmt.guitarlab.core.ui.theme.StudioCardBorder
import com.mmt.guitarlab.core.ui.theme.StudioCardElevated
import com.mmt.guitarlab.core.ui.theme.StudioDarkBg
import com.mmt.guitarlab.core.ui.theme.StudioTextPrimary
import com.mmt.guitarlab.core.ui.theme.StudioTextSecondary
import kotlin.math.abs

@Composable
fun TunerTopBar(
    selectedTuningName: String?,
    selectedTuningNotes: List<TuningNote>?,
    isChromaticMode: Boolean,
    a4: Float,
    running: Boolean,
    calibrationDropdownExpanded: Boolean,
    onCalibrationDropdownExpandedChange: (Boolean) -> Unit,
    onSetA4: (Float) -> Unit,
    onToggleMic: () -> Unit,
    onOpenTuningDialog: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    brush = Brush.verticalGradient(
                        if (isChromaticMode) listOf(Color(0xFF2E2413), Color(0xFF1E1609))
                        else listOf(StudioCardElevated, StudioCardBg),
                    ),
                )
                .border(
                    1.dp,
                    if (isChromaticMode) ElectricAmber else StudioCardBorder,
                    RoundedCornerShape(16.dp),
                )
                .clickable { onOpenTuningDialog() }
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Studio3DIconBadge(
                icon = if (isChromaticMode) Icons.Default.MusicNote else Icons.Default.Tune,
                contentDescription = null,
                size = 32.dp,
                accent = Studio3DAccent.AMBER,
            )
            Spacer(Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isChromaticMode) "Хроматический тюнер" else (selectedTuningName ?: "Standard E"),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (isChromaticMode) ElectricAmber else StudioTextPrimary,
                    maxLines = 1,
                )
                Text(
                    text = if (isChromaticMode) "Определение любой ноты" else (selectedTuningNotes?.joinToString(" ") { it.noteName } ?: "E A D G B E"),
                    style = MaterialTheme.typography.labelSmall,
                    color = StudioTextSecondary,
                    maxLines = 1,
                )
            }
        }

        Box {
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(brush = Brush.verticalGradient(listOf(StudioCardElevated, StudioCardBg)))
                    .border(1.dp, StudioCardBorder, RoundedCornerShape(16.dp))
                    .clickable { onCalibrationDropdownExpandedChange(true) }
                    .padding(horizontal = 10.dp, vertical = 11.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = "${a4.toInt()} Hz",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = ElectricAmber,
                )
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null,
                    tint = StudioTextSecondary,
                    modifier = Modifier.size(18.dp),
                )
            }

            DropdownMenu(
                expanded = calibrationDropdownExpanded,
                onDismissRequest = { onCalibrationDropdownExpandedChange(false) },
                modifier = Modifier.background(StudioCardBg),
            ) {
                listOf(432f, 440f, 442f, 444f).forEach { hz ->
                    val isCur = abs(a4 - hz) < 0.5f
                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "${hz.toInt()} Hz",
                                    fontWeight = if (isCur) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isCur) ElectricAmber else StudioTextPrimary,
                                )
                                if (hz == 440f) {
                                    Text(text = " (Стандарт)", style = MaterialTheme.typography.bodySmall, color = StudioTextSecondary)
                                }
                            }
                        },
                        onClick = {
                            onSetA4(hz)
                            onCalibrationDropdownExpandedChange(false)
                        },
                    )
                }
            }
        }

        Studio3DIconBadge(
            icon = if (running) Icons.Default.Mic else Icons.Default.MicOff,
            contentDescription = null,
            size = 46.dp,
            accent = if (running) Studio3DAccent.GREEN else Studio3DAccent.SLATE,
            onClick = onToggleMic,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun TunerTopBarPreview() {
    GuitarLabTheme {
        Column(
            modifier = Modifier
                .background(StudioDarkBg)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Состояние 1: Стандартный строй (Standard E), микрофон включен
            TunerTopBar(
                selectedTuningName = "Standard E",
                selectedTuningNotes = listOf(
                    TuningNote(stringNumber = 1, noteName = "E", octave = 2, targetFrequencyHz = 82.4f, midiNote = 0),
                    TuningNote(stringNumber = 2, noteName = "A", octave = 2, targetFrequencyHz = 110.0f, midiNote = 0),
                    TuningNote(stringNumber = 3, noteName = "D", octave = 3, targetFrequencyHz = 146.8f, midiNote = 0),
                    TuningNote(stringNumber = 4, noteName = "G", octave = 3, targetFrequencyHz = 196.0f, midiNote = 0),
                    TuningNote(stringNumber = 5, noteName = "B", octave = 3, targetFrequencyHz = 246.9f, midiNote = 0),
                    TuningNote(stringNumber = 6, noteName = "E", octave = 4, targetFrequencyHz = 329.6f, midiNote = 0),
                ),
                isChromaticMode = false,
                a4 = 440f,
                running = true,
                calibrationDropdownExpanded = false,
                onCalibrationDropdownExpandedChange = {},
                onSetA4 = {},
                onToggleMic = {},
                onOpenTuningDialog = {},
            )

            // Состояние 2: Хроматический режим, микрофон выключен
            TunerTopBar(
                selectedTuningName = null,
                selectedTuningNotes = null,
                isChromaticMode = true,
                a4 = 442f,
                running = false,
                calibrationDropdownExpanded = false,
                onCalibrationDropdownExpandedChange = {},
                onSetA4 = {},
                onToggleMic = {},
                onOpenTuningDialog = {},
            )
        }
    }
}