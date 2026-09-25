package com.mmt.guitarlab.ui.practice

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mmt.guitarlab.domain.model.ChordFormula
import com.mmt.guitarlab.domain.model.FretboardMode
import com.mmt.guitarlab.domain.model.MusicTheory
import com.mmt.guitarlab.domain.model.ScaleFormula
import com.mmt.guitarlab.domain.model.Tuning
import com.mmt.guitarlab.domain.model.TuningNote
import com.mmt.guitarlab.ui.components.Studio3DAccent
import com.mmt.guitarlab.ui.components.Studio3DIconBadge
import com.mmt.guitarlab.ui.components.StudioCard
import com.mmt.guitarlab.ui.components.StudioPill
import com.mmt.guitarlab.ui.theme.ElectricAmber
import com.mmt.guitarlab.ui.theme.ElectricGreen
import com.mmt.guitarlab.ui.theme.ElectricTeal
import com.mmt.guitarlab.ui.theme.GuitarLabTheme
import com.mmt.guitarlab.ui.theme.StudioDarkBg
import com.mmt.guitarlab.ui.theme.StudioTextMuted
import com.mmt.guitarlab.ui.theme.StudioTextPrimary
import com.mmt.guitarlab.ui.theme.StudioTextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChordScaleScreen(viewModel: FretboardViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) {
        viewModel.setMode(FretboardMode.CHORD_SCALE_FINDER)
    }

    val selectedTuning by viewModel.selectedTuning.collectAsStateWithLifecycle()
    val mode by viewModel.mode.collectAsStateWithLifecycle()
    val rootNote by viewModel.rootNote.collectAsStateWithLifecycle()
    val selectedChord by viewModel.selectedChord.collectAsStateWithLifecycle()
    val selectedScale by viewModel.selectedScale.collectAsStateWithLifecycle()
    val pressedFrets by viewModel.pressedFrets.collectAsStateWithLifecycle()
    val detectedChords by viewModel.detectedChords.collectAsStateWithLifecycle()
    val availableTunings by viewModel.availableTunings.collectAsStateWithLifecycle()

    ChordScaleContent(
        selectedTuning = selectedTuning,
        availableTunings = availableTunings,
        mode = mode,
        rootNote = rootNote,
        selectedChord = selectedChord,
        selectedScale = selectedScale,
        pressedFrets = pressedFrets,
        detectedChords = detectedChords,
        onSelectTuning = viewModel::selectTuning,
        onSelectRootNote = viewModel::setRootNote,
        onSelectChord = viewModel::selectChord,
        onSelectScale = viewModel::selectScale,
        onClearPressedFrets = viewModel::clearPressedFrets,
        onFretTapped = { strIdx, fret, midi ->
            if (mode == FretboardMode.REVERSE_LOOKUP) {
                viewModel.toggleFret(strIdx, fret, midi)
            }
        }
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChordScaleContent(
    selectedTuning: Tuning?,
    availableTunings: List<Tuning>,
    mode: FretboardMode,
    rootNote: String,
    selectedChord: ChordFormula?,
    selectedScale: ScaleFormula?,
    pressedFrets: Set<FretPosition>,
    detectedChords: List<String>,
    onSelectTuning: (String) -> Unit,
    onSelectRootNote: (String) -> Unit,
    onSelectChord: (ChordFormula) -> Unit,
    onSelectScale: (ScaleFormula) -> Unit,
    onClearPressedFrets: () -> Unit,
    onFretTapped: (Int, Int, Int) -> Unit,
) {
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
            // Tuning Selector
            SectionTitle(title = "GUITAR TUNING")
            Spacer(Modifier.height(8.dp))
            TuningPickerRow(
                availableTunings = availableTunings,
                selectedTuningId = selectedTuning?.id,
                onSelectTuning = onSelectTuning,
            )
            Spacer(Modifier.height(16.dp))

            // Root Note Picker
            SectionTitle(title = "ROOT NOTE")
            Spacer(Modifier.height(8.dp))
            RootNotePickerRow(
                rootNote = rootNote,
                onSelectRootNote = onSelectRootNote,
            )

            Spacer(Modifier.height(16.dp))

            // Chord Type Selector
            SectionTitle(title = "CHORDS")
            Spacer(Modifier.height(8.dp))
            ChordFormulasFlowRow(
                selectedChordName = selectedChord?.name,
                onSelectChord = onSelectChord,
            )

            Spacer(Modifier.height(16.dp))

            // Scale Type Selector
            SectionTitle(title = "SCALES")
            Spacer(Modifier.height(8.dp))
            ScaleFormulasFlowRow(
                selectedScaleName = selectedScale?.name,
                onSelectScale = onSelectScale,
            )
        } else {
            // Reverse Chord Lookup Header
            ReverseLookupHeaderCard(
                detectedChords = detectedChords,
                hasPressedFrets = pressedFrets.isNotEmpty(),
                onClear = onClearPressedFrets,
            )
        }

        Spacer(Modifier.height(18.dp))

        // Fretboard Visualizer Title
        val title = if (mode == FretboardMode.CHORD_SCALE_FINDER) {
            val type = selectedChord?.name ?: selectedScale?.name ?: "Diagram"
            "$rootNote $type Fretboard"
        } else {
            "Interactive Fretboard"
        }

        SectionTitle(title = title.uppercase())
        Spacer(Modifier.height(10.dp))

        ChordScaleFretboardContainer(
            stringCount = stringCount,
            tuningNotes = tuningNotes,
            mode = mode,
            rootNote = rootNote,
            selectedChord = selectedChord,
            selectedScale = selectedScale,
            pressedFrets = pressedFrets,
            onFretTapped = onFretTapped,
        )

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        color = StudioTextMuted,
        letterSpacing = 1.sp,
    )
}

@Composable
private fun TuningPickerRow(
    availableTunings: List<Tuning>,
    selectedTuningId: String?,
    onSelectTuning: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        availableTunings.forEach { tun ->
            val isSelected = selectedTuningId == tun.id
            StudioPill(
                text = tun.name,
                selected = isSelected,
                onClick = { onSelectTuning(tun.id) },
                accentColor = ElectricTeal,
            )
        }
    }
}

@Composable
private fun RootNotePickerRow(
    rootNote: String,
    onSelectRootNote: (String) -> Unit,
) {
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
                onClick = { onSelectRootNote(note) },
                accentColor = ElectricAmber,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChordFormulasFlowRow(
    selectedChordName: String?,
    onSelectChord: (ChordFormula) -> Unit,
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        MusicTheory.chordFormulas.forEach { chord ->
            val isSelected = selectedChordName == chord.name
            StudioPill(
                text = chord.name,
                selected = isSelected,
                onClick = { onSelectChord(chord) },
                accentColor = ElectricAmber,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ScaleFormulasFlowRow(
    selectedScaleName: String?,
    onSelectScale: (ScaleFormula) -> Unit,
) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        MusicTheory.scaleFormulas.forEach { scale ->
            val isSelected = selectedScaleName == scale.name
            StudioPill(
                text = scale.name,
                selected = isSelected,
                onClick = { onSelectScale(scale) },
                accentColor = ElectricTeal,
            )
        }
    }
}

@Composable
private fun ReverseLookupHeaderCard(
    detectedChords: List<String>,
    hasPressedFrets: Boolean,
    onClear: () -> Unit,
) {
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

            if (hasPressedFrets) {
                Studio3DIconBadge(
                    icon = Icons.Default.Clear,
                    contentDescription = "Clear",
                    size = 36.dp,
                    accent = Studio3DAccent.RUBY,
                    onClick = onClear,
                )
            }
        }
    }
}

@Composable
private fun ChordScaleFretboardContainer(
    stringCount: Int,
    tuningNotes: List<TuningNote>,
    mode: FretboardMode,
    rootNote: String,
    selectedChord: ChordFormula?,
    selectedScale: ScaleFormula?,
    pressedFrets: Set<FretPosition>,
    onFretTapped: (Int, Int, Int) -> Unit,
) {
    StudioCard(modifier = Modifier.fillMaxWidth()) {
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
                onFretTapped = onFretTapped,
            )
        }
    }
}

// ==================== PREVIEWS ====================

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun ChordScaleContentPreview() {
    GuitarLabTheme {
        ChordScaleContent(
            selectedTuning = Tuning(
                id = "standard",
                name = "Standard E",
                notes = listOf(
                    TuningNote(1, "E", 2, 82.4f, midiNote = 0),
                    TuningNote(2, "A", 2, 110.0f, midiNote = 0),
                    TuningNote(3, "D", 3, 146.8f, midiNote = 0),
                    TuningNote(4, "G", 3, 196.0f, midiNote = 0),
                    TuningNote(5, "B", 3, 246.9f, midiNote = 0),
                    TuningNote(6, "E", 4, 329.6f, midiNote = 0),
                ),
                stringCount = 6,
                category = "Standard",
                isFavorite = false
            ),
            availableTunings = listOf(
                Tuning(
                    id = "standard",
                    name = "Standard E",
                    notes = listOf(
                        TuningNote(1, "E", 2, 82.4f, midiNote = 0),
                        TuningNote(2, "A", 2, 110.0f, midiNote = 0),
                        TuningNote(3, "D", 3, 146.8f, midiNote = 0),
                        TuningNote(4, "G", 3, 196.0f, midiNote = 0),
                        TuningNote(5, "B", 3, 246.9f, midiNote = 0),
                        TuningNote(6, "E", 4, 329.6f, midiNote = 0),
                    ),
                    stringCount = 6,
                    category = "Standard",
                    isFavorite = false
                ),
                Tuning(
                    id = "standard",
                    name = "Standard E",
                    notes = listOf(
                        TuningNote(1, "E", 2, 82.4f, midiNote = 0),
                        TuningNote(2, "A", 2, 110.0f, midiNote = 0),
                        TuningNote(3, "D", 3, 146.8f, midiNote = 0),
                        TuningNote(4, "G", 3, 196.0f, midiNote = 0),
                        TuningNote(5, "B", 3, 246.9f, midiNote = 0),
                        TuningNote(6, "E", 4, 329.6f, midiNote = 0),
                    ),
                    stringCount = 6,
                    category = "Standard",
                    isFavorite = false
                ),
            ),
            mode = FretboardMode.CHORD_SCALE_FINDER,
            rootNote = "C",
            selectedChord = ChordFormula("Major", listOf(0, 4, 7)),
            selectedScale = null,
            pressedFrets = emptySet(),
            detectedChords = emptyList(),
            onSelectTuning = {},
            onSelectRootNote = {},
            onSelectChord = {},
            onSelectScale = {},
            onClearPressedFrets = {},
            onFretTapped = { _, _, _ -> }
        )
    }
}