package com.mmt.guitarlab.ui.practice

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
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

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ChordScaleScreen(viewModel: FretboardViewModel = hiltViewModel()) {
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
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        Text("Fretboard Reference & Lookup", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))

        // Mode Switch Tabs
        PrimaryTabRow(
            selectedTabIndex = if (mode == FretboardMode.CHORD_SCALE_FINDER) 0 else 1,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Tab(
                selected = mode == FretboardMode.CHORD_SCALE_FINDER,
                onClick = { viewModel.setMode(FretboardMode.CHORD_SCALE_FINDER) },
                text = { Text("Chord & Scale Shapes") },
            )
            Tab(
                selected = mode == FretboardMode.REVERSE_LOOKUP,
                onClick = { viewModel.setMode(FretboardMode.REVERSE_LOOKUP) },
                text = { Text("Reverse Lookup") },
            )
        }

        Spacer(Modifier.height(16.dp))

        if (mode == FretboardMode.CHORD_SCALE_FINDER) {
            // Root Note Picker
            Text("Root Note", style = MaterialTheme.typography.labelLarge)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 6.dp),
            ) {
                MusicTheory.noteNames.forEach { note ->
                    FilterChip(
                        selected = note == rootNote,
                        onClick = { viewModel.setRootNote(note) },
                        label = { Text(note) },
                        modifier = Modifier.padding(end = 4.dp),
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            // Chord / Scale Pickers (Single Horizontal Scrollable Rows)
            Text("Chords", style = MaterialTheme.typography.labelLarge)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                MusicTheory.chordFormulas.forEach { chord ->
                    FilterChip(
                        selected = selectedChord?.name == chord.name,
                        onClick = { viewModel.selectChord(chord) },
                        label = { Text(chord.name) },
                    )
                }
            }

            Spacer(Modifier.height(8.dp))

            Text("Scales", style = MaterialTheme.typography.labelLarge)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                MusicTheory.scaleFormulas.forEach { scale ->
                    FilterChip(
                        selected = selectedScale?.name == scale.name,
                        onClick = { viewModel.selectScale(scale) },
                        label = { Text(scale.name) },
                    )
                }
            }
        } else {
            // Reverse Lookup Info Header
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        "Tap notes on the fretboard below to construct a chord shape.",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = if (detectedChords.isEmpty()) {
                            "Identified Chords: (Select notes on fretboard)"
                        } else {
                            "Identified Chords: ${detectedChords.joinToString(", ")}"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                    Spacer(Modifier.height(8.dp))
                    OutlinedButton(
                        onClick = viewModel::clearPressedFrets,
                        modifier = Modifier.align(Alignment.End),
                    ) {
                        Text("Clear Notes")
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Virtual Interactive Fretboard
        Text(
            text = "Active Tuning: ${selectedTuning?.name ?: "Standard E"}",
            style = MaterialTheme.typography.titleSmall,
        )
        Spacer(Modifier.height(8.dp))

        VirtualFretboardCanvas(
            stringCount = stringCount,
            tuningNotes = tuningNotes,
            rootNote = rootNote,
            selectedChordIntervals = selectedChord?.intervals,
            selectedScaleIntervals = selectedScale?.intervals,
            mode = mode,
            pressedFrets = pressedFrets,
            onFretClick = viewModel::toggleFret,
        )
    }
}

@Composable
private fun VirtualFretboardCanvas(
    stringCount: Int,
    tuningNotes: List<com.mmt.guitarlab.domain.model.TuningNote>,
    rootNote: String,
    selectedChordIntervals: List<Int>?,
    selectedScaleIntervals: List<Int>?,
    mode: FretboardMode,
    pressedFrets: Set<FretPosition>,
    onFretClick: (stringIdx: Int, fret: Int, midiNote: Int) -> Unit,
) {
    val totalFrets = 15
    val fretWidth = 54.dp
    val stringHeight = 32.dp

    val textMeasurer = rememberTextMeasurer()
    val rootPitchIndex = MusicTheory.getMidiNoteIndex(rootNote)
    val targetIntervals = selectedChordIntervals ?: selectedScaleIntervals ?: emptyList()

    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val onSurface = MaterialTheme.colorScheme.onSurface
    val onPrimaryColor = MaterialTheme.colorScheme.onPrimary
    val fretboardBg = MaterialTheme.colorScheme.surfaceContainerHigh

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(fretboardBg)
            .horizontalScroll(rememberScrollState())
            .padding(12.dp),
    ) {
        val widthPx = (totalFrets + 1) * fretWidth.value
        val heightPx = (stringCount + 1) * stringHeight.value

        Canvas(
            modifier = Modifier
                .width(widthPx.dp)
                .height(heightPx.dp)
                .pointerInput(mode, stringCount, tuningNotes) {
                    detectTapGestures { offset ->
                        val startX = 50f
                        val startY = 30f
                        val fWidth = fretWidth.toPx()
                        val sHeight = stringHeight.toPx()

                        val stringIdx = ((offset.y - startY + sHeight / 2f) / sHeight).toInt().coerceIn(0, stringCount - 1)
                        val fret = ((offset.x - startX + fWidth / 2f) / fWidth).toInt().coerceIn(0, totalFrets)

                        val tuningNote = tuningNotes.getOrNull(stringIdx)
                        val baseMidi = tuningNote?.midiNote ?: (64 - stringIdx * 5)
                        val noteMidi = baseMidi + fret

                        onFretClick(stringIdx, fret, noteMidi)
                    }
                },
        ) {
            val startX = 50f
            val startY = 30f
            val fWidth = fretWidth.toPx()
            val sHeight = stringHeight.toPx()

            // Draw Frets (Vertical lines)
            for (fret in 0..totalFrets) {
                val x = startX + fret * fWidth
                drawLine(
                    color = if (fret == 0) primaryColor else surfaceVariant,
                    start = Offset(x, startY),
                    end = Offset(x, startY + (stringCount - 1) * sHeight),
                    strokeWidth = if (fret == 0) 8f else 3f,
                )
                // Fret numbers
                drawText(
                    textMeasurer = textMeasurer,
                    text = fret.toString(),
                    topLeft = Offset(x + fWidth / 2f - 6f, startY + (stringCount - 1) * sHeight + 8f),
                    style = TextStyle(color = onSurface.copy(alpha = 0.7f), fontSize = 11.sp),
                )
            }

            // Draw Fretboard Inlays (Single dots at 3, 5, 7, 9, 12)
            val inlays = listOf(3, 5, 7, 9, 12)
            inlays.forEach { fret ->
                val x = startX + (fret - 0.5f) * fWidth
                val y = startY + (stringCount - 1) * sHeight / 2f
                drawCircle(color = surfaceVariant.copy(alpha = 0.5f), radius = 6f, center = Offset(x, y))
            }

            // Draw Strings & Note Dots
            for (stringIdx in 0 until stringCount) {
                val y = startY + stringIdx * sHeight
                val tuningNote = tuningNotes.getOrNull(stringIdx)
                val baseMidi = tuningNote?.midiNote ?: (64 - stringIdx * 5)

                // String line
                drawLine(
                    color = onSurface.copy(alpha = 0.4f),
                    start = Offset(startX, y),
                    end = Offset(startX + totalFrets * fWidth, y),
                    strokeWidth = (stringIdx + 1) * 1.2f,
                )

                // Draw Notes on each fret
                for (fret in 0..totalFrets) {
                    val noteMidi = baseMidi + fret
                    val pitchClass = (noteMidi % 12 + 12) % 12
                    val noteName = MusicTheory.noteNames[pitchClass]

                    val intervalFromRoot = (pitchClass - rootPitchIndex + 12) % 12
                    val isTargetNote = targetIntervals.contains(intervalFromRoot)
                    val isRoot = intervalFromRoot == 0 && isTargetNote

                    val isPressedInReverse = mode == FretboardMode.REVERSE_LOOKUP &&
                        pressedFrets.any { it.stringIndex == stringIdx && it.fret == fret }

                    val x = startX + (if (fret == 0) 0f else (fret - 0.5f) * fWidth)

                    if ((mode == FretboardMode.CHORD_SCALE_FINDER && isTargetNote) || isPressedInReverse) {
                        val dotColor = when {
                            isPressedInReverse -> secondaryColor
                            isRoot -> primaryColor
                            else -> secondaryColor
                        }

                        drawCircle(
                            color = dotColor,
                            radius = 14f,
                            center = Offset(x, y),
                        )

                        drawText(
                            textMeasurer = textMeasurer,
                            text = noteName,
                            topLeft = Offset(x - 8f, y - 8f),
                            style = TextStyle(
                                color = onPrimaryColor,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                            ),
                        )
                    }
                }
            }
        }
    }
}
