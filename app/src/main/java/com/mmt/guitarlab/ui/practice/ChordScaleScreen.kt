package com.mmt.guitarlab.ui.practice

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mmt.guitarlab.domain.model.FretboardMode
import com.mmt.guitarlab.domain.model.MusicTheory
import com.mmt.guitarlab.ui.components.Studio3DAccent
import com.mmt.guitarlab.ui.components.Studio3DIconBadge
import com.mmt.guitarlab.ui.components.StudioCard
import com.mmt.guitarlab.ui.components.StudioPill
import com.mmt.guitarlab.ui.theme.ElectricAmber
import com.mmt.guitarlab.ui.theme.ElectricGreen
import com.mmt.guitarlab.ui.theme.ElectricTeal
import com.mmt.guitarlab.ui.theme.StudioCardBg
import com.mmt.guitarlab.ui.theme.StudioCardBorder
import com.mmt.guitarlab.ui.theme.StudioCardElevated
import com.mmt.guitarlab.ui.theme.StudioDarkBg
import com.mmt.guitarlab.ui.theme.StudioTextMuted
import com.mmt.guitarlab.ui.theme.StudioTextPrimary
import com.mmt.guitarlab.ui.theme.StudioTextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChordScaleScreen(viewModel: FretboardViewModel = hiltViewModel()) {
    androidx.compose.runtime.LaunchedEffect(Unit) {
        viewModel.setMode(FretboardMode.CHORD_SCALE_FINDER)
    }
    val selectedTuning by viewModel.selectedTuning.collectAsStateWithLifecycle()
    val mode by viewModel.mode.collectAsStateWithLifecycle()
    val rootNote by viewModel.rootNote.collectAsStateWithLifecycle()
    val selectedChord by viewModel.selectedChord.collectAsStateWithLifecycle()
    val selectedScale by viewModel.selectedScale.collectAsStateWithLifecycle()
    val pressedFrets by viewModel.pressedFrets.collectAsStateWithLifecycle()
    val detectedChords by viewModel.detectedChords.collectAsStateWithLifecycle()

    val stringCount = selectedTuning?.stringCount ?: 6
    val tuningNotes = selectedTuning?.notes ?: emptyList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {


        if (mode == FretboardMode.CHORD_SCALE_FINDER) {
            // Root Note Picker (Chromatic 12 notes)
            Text(
                text = "ROOT NOTE",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = StudioTextMuted,
                letterSpacing = 1.sp,
            )
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                MusicTheory.noteNames.forEach { note ->
                    val isSelected = rootNote == note
                    StudioPill(
                        text = note,
                        selected = isSelected,
                        onClick = { viewModel.setRootNote(note) },
                        accentColor = ElectricAmber,
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Chord Type Selector
            Text(
                text = "CHORDS",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = StudioTextMuted,
                letterSpacing = 1.sp,
            )
            Spacer(Modifier.height(8.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                MusicTheory.chordFormulas.forEach { chord ->
                    val isSelected = selectedChord?.name == chord.name
                    StudioPill(
                        text = chord.name,
                        selected = isSelected,
                        onClick = { viewModel.selectChord(chord) },
                        accentColor = ElectricAmber,
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Scale Type Selector
            Text(
                text = "SCALES",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = StudioTextMuted,
                letterSpacing = 1.sp,
            )
            Spacer(Modifier.height(8.dp))
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                MusicTheory.scaleFormulas.forEach { scale ->
                    val isSelected = selectedScale?.name == scale.name
                    StudioPill(
                        text = scale.name,
                        selected = isSelected,
                        onClick = { viewModel.selectScale(scale) },
                        accentColor = ElectricTeal,
                    )
                }
            }
        } else {
            // Reverse Chord Lookup Header
            StudioCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Tap any fret to place fingers",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = StudioTextPrimary,
                        )
                        if (detectedChords.isNotEmpty()) {
                            Text(
                                text = "Detected: ${detectedChords.joinToString(", ")}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Black,
                                color = ElectricGreen,
                            )
                        } else {
                            Text(
                                text = "Press 2+ notes to identify chord",
                                style = MaterialTheme.typography.bodySmall,
                                color = StudioTextSecondary,
                            )
                        }
                    }

                    if (pressedFrets.isNotEmpty()) {
                        Studio3DIconBadge(
                            icon = Icons.Default.Clear,
                            contentDescription = "Clear",
                            size = 36.dp,
                            accent = Studio3DAccent.RUBY,
                            onClick = { viewModel.clearPressedFrets() },
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        // Fretboard Visualizer (Realistic Woodgrain Neck + Mother of Pearl Inlays)
        val title = if (mode == FretboardMode.CHORD_SCALE_FINDER) {
            val type = selectedChord?.name ?: selectedScale?.name ?: "Diagram"
            "$rootNote $type Fretboard"
        } else "Interactive Fretboard"

        Text(
            text = title.uppercase(),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = StudioTextMuted,
            letterSpacing = 1.sp,
        )

        Spacer(Modifier.height(10.dp))

        StudioCard(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 16.dp, horizontal = 8.dp),
            ) {
                RealisticFretboardCanvas(
                    stringCount = stringCount,
                    tuningNotes = tuningNotes,
                    mode = mode,
                    rootPitchIndex = MusicTheory.getMidiNoteIndex(rootNote),
                    targetIntervals = selectedChord?.intervals ?: selectedScale?.intervals ?: emptyList(),
                    pressedFrets = pressedFrets,
                    onFretTapped = { strIdx, fret, midi ->
                        if (mode == FretboardMode.REVERSE_LOOKUP) {
                            viewModel.toggleFret(strIdx, fret, midi)
                        }
                    },
                )
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
fun RealisticFretboardCanvas(
    stringCount: Int,
    tuningNotes: List<com.mmt.guitarlab.domain.model.TuningNote>,
    mode: FretboardMode,
    rootPitchIndex: Int,
    targetIntervals: List<Int>,
    pressedFrets: Set<FretPosition>,
    onFretTapped: (stringIndex: Int, fret: Int, midiNote: Int) -> Unit,
) {
    val totalFrets = 15
    val fretWidth = 52.dp
    val stringSpacing = 28.dp
    val textMeasurer = rememberTextMeasurer()

    val totalWidth = fretWidth * (totalFrets + 1) + 40.dp
    val totalHeight = stringSpacing * (stringCount + 1)

    Canvas(
        modifier = Modifier
            .size(width = totalWidth, height = totalHeight)
            .pointerInput(mode, stringCount) {
                detectTapGestures { offset ->
                    val fWidthPx = fretWidth.toPx()
                    val sHeightPx = stringSpacing.toPx()
                    val startXPx = 30.dp.toPx()
                    val startYPx = 16.dp.toPx()

                    val relX = offset.x - startXPx
                    val relY = offset.y - startYPx

                    if (relX >= 0 && relY >= -sHeightPx / 2f) {
                        val fret = (relX / fWidthPx).toInt().coerceIn(0, totalFrets)
                        val stringIdx = ((relY + sHeightPx / 2f) / sHeightPx).toInt().coerceIn(0, stringCount - 1)
                        val baseMidi = tuningNotes.getOrNull(stringIdx)?.midiNote ?: (64 - stringIdx * 5)
                        onFretTapped(stringIdx, fret, baseMidi + fret)
                    }
                }
            },
    ) {
        val fWidth = fretWidth.toPx()
        val sHeight = stringSpacing.toPx()
        val startX = 30.dp.toPx()
        val startY = 16.dp.toPx()
        val neckHeight = (stringCount - 1) * sHeight

        // Dark Studio Ebony Wood Neck Background
        drawRoundRect(
            brush = Brush.verticalGradient(
                listOf(Color(0xFF1E232F), Color(0xFF131720)),
            ),
            topLeft = Offset(startX, startY - 8.dp.toPx()),
            size = Size(totalFrets * fWidth, neckHeight + 16.dp.toPx()),
            cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx()),
        )

        // Mother of Pearl Fret Markers: Single dots at 3, 5, 7, 9, 15; Double dots at 12
        val singleDotFrets = listOf(3, 5, 7, 9, 15)
        singleDotFrets.forEach { f ->
            val cx = startX + (f - 0.5f) * fWidth
            val cy = startY + neckHeight / 2f
            drawCircle(
                color = Color(0xFF8899B0).copy(alpha = 0.45f),
                radius = 5.dp.toPx(),
                center = Offset(cx, cy),
            )
        }
        // Double dot at fret 12
        val cx12 = startX + (12 - 0.5f) * fWidth
        drawCircle(
            color = Color(0xFF8899B0).copy(alpha = 0.45f),
            radius = 4.5.dp.toPx(),
            center = Offset(cx12, startY + neckHeight * 0.28f),
        )
        drawCircle(
            color = Color(0xFF8899B0).copy(alpha = 0.45f),
            radius = 4.5.dp.toPx(),
            center = Offset(cx12, startY + neckHeight * 0.72f),
        )

        // Draw Frets & Nut
        for (fret in 0..totalFrets) {
            val x = startX + fret * fWidth
            val isNut = fret == 0
            drawLine(
                color = if (isNut) Color(0xFFE2E8F0) else Color(0xFF3E4B66),
                start = Offset(x, startY - 8.dp.toPx()),
                end = Offset(x, startY + neckHeight + 8.dp.toPx()),
                strokeWidth = if (isNut) 6.dp.toPx() else 2.5.dp.toPx(),
            )

            // Fret Numbers below neck
            if (fret > 0) {
                drawText(
                    textMeasurer = textMeasurer,
                    text = "$fret",
                    topLeft = Offset(x - fWidth / 2f - 4.dp.toPx(), startY + neckHeight + 12.dp.toPx()),
                    style = TextStyle(
                        color = Color(0xFF64748B),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                    ),
                )
            }
        }

        // Draw Strings & Note Inlays
        for (stringIdx in 0 until stringCount) {
            val y = startY + stringIdx * sHeight
            val baseMidi = tuningNotes.getOrNull(stringIdx)?.midiNote ?: (64 - stringIdx * 5)

            // Steel/Nickel guitar string with thickness variation
            val stringThickness = (stringCount - stringIdx) * 0.65f + 1.2f
            drawLine(
                color = Color(0xFFB0BCCC),
                start = Offset(startX, y),
                end = Offset(startX + totalFrets * fWidth, y),
                strokeWidth = stringThickness.dp.toPx(),
            )

            // Notes on frets
            for (fret in 0..totalFrets) {
                val noteMidi = baseMidi + fret
                val pitchClass = (noteMidi % 12 + 12) % 12
                val noteName = MusicTheory.noteNames[pitchClass]
                val intervalFromRoot = (pitchClass - rootPitchIndex + 12) % 12
                val isTargetNote = targetIntervals.contains(intervalFromRoot)
                val isRoot = intervalFromRoot == 0 && isTargetNote
                val isPressed = mode == FretboardMode.REVERSE_LOOKUP &&
                    pressedFrets.any { it.stringIndex == stringIdx && it.fret == fret }

                val nx = startX + (if (fret == 0) 0f else (fret - 0.5f) * fWidth)

                if ((mode == FretboardMode.CHORD_SCALE_FINDER && isTargetNote) || isPressed) {
                    val dotColor = when {
                        isPressed -> ElectricTeal
                        isRoot -> ElectricAmber
                        else -> Color(0xFF00B4D8)
                    }

                    // Outer halo for root notes
                    if (isRoot) {
                        drawCircle(
                            color = ElectricAmber.copy(alpha = 0.35f),
                            radius = 16.dp.toPx(),
                            center = Offset(nx, y),
                        )
                    }

                    // Note Circle
                    drawCircle(
                        color = dotColor,
                        radius = 12.dp.toPx(),
                        center = Offset(nx, y),
                    )

                    // Text Note Name inside circle
                    drawText(
                        textMeasurer = textMeasurer,
                        text = noteName,
                        topLeft = Offset(nx - 5.dp.toPx(), y - 7.dp.toPx()),
                        style = TextStyle(
                            color = Color(0xFF0C1017),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                        ),
                    )
                }
            }
        }
    }
}
