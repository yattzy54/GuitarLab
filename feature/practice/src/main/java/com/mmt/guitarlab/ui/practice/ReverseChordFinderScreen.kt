package com.mmt.guitarlab.ui.practice

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mmt.guitarlab.core.ui.R
import com.mmt.guitarlab.domain.model.FretboardMode
import com.mmt.guitarlab.domain.model.Tuning
import com.mmt.guitarlab.domain.model.TuningNote
import com.mmt.guitarlab.core.ui.components.Studio3DAccent
import com.mmt.guitarlab.core.ui.components.Studio3DIconBadge
import com.mmt.guitarlab.core.ui.components.StudioCard
import com.mmt.guitarlab.core.ui.theme.ElectricGreen
import com.mmt.guitarlab.core.ui.theme.GuitarLabTheme
import com.mmt.guitarlab.core.ui.theme.StudioDarkBg
import com.mmt.guitarlab.core.ui.theme.StudioTextMuted
import com.mmt.guitarlab.core.ui.theme.StudioTextPrimary
import com.mmt.guitarlab.core.ui.theme.StudioTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReverseChordFinderScreen(viewModel: FretboardViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) {
        viewModel.setMode(FretboardMode.REVERSE_LOOKUP)
    }

    val availableTunings by viewModel.availableTunings.collectAsStateWithLifecycle()
    val selectedTuning by viewModel.selectedTuning.collectAsStateWithLifecycle()
    val pressedFrets by viewModel.pressedFrets.collectAsStateWithLifecycle()
    val detectedChords by viewModel.detectedChords.collectAsStateWithLifecycle()

    var showTuningBottomSheet by remember { mutableStateOf(false) }

    ReverseChordFinderContent(
        selectedTuning = selectedTuning,
        pressedFrets = pressedFrets,
        detectedChords = detectedChords,
        onOpenTuningSheet = { showTuningBottomSheet = true },
        onClearFrets = viewModel::clearPressedFrets,
        onFretTapped = { strIdx, fret, midi ->
            viewModel.toggleFret(strIdx, fret, midi)
        }
    )

    if (showTuningBottomSheet) {
        TuningBottomSheet(
            availableTunings = availableTunings,
            selectedTuning = selectedTuning,
            onTuningSelected = { tun -> viewModel.selectTuning(tun.id) },
            onDismiss = { showTuningBottomSheet = false },
        )
    }
}

@Composable
fun ReverseChordFinderContent(
    selectedTuning: Tuning?,
    pressedFrets: Set<FretPosition>,
    detectedChords: List<String>,
    onOpenTuningSheet: () -> Unit,
    onClearFrets: () -> Unit,
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
        TuningSelectorCard(
            selectedTuning = selectedTuning,
            onClick = onOpenTuningSheet,
        )

        Spacer(Modifier.height(16.dp))

        ChordFinderHeaderCard(
            detectedChords = detectedChords,
            pressedFretsCount = pressedFrets.size,
            onClearFrets = onClearFrets,
        )

        Spacer(Modifier.height(18.dp))

        Text(
            text = stringResource(R.string.interactive_fretboard),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = StudioTextMuted,
            letterSpacing = 1.sp,
        )
        Spacer(Modifier.height(10.dp))

        FretboardContainer(
            stringCount = stringCount,
            tuningNotes = tuningNotes,
            pressedFrets = pressedFrets,
            onFretTapped = onFretTapped,
        )

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun ChordFinderHeaderCard(
    detectedChords: List<String>,
    pressedFretsCount: Int,
    onClearFrets: () -> Unit,
) {
    StudioCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = stringResource(R.string.chord_finder_title),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = StudioTextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(4.dp))
                if (detectedChords.isNotEmpty()) {
                    Text(
                        text = stringResource(R.string.chord_finder_detected, detectedChords.joinToString(", ")),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Black,
                        color = ElectricGreen,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                } else {
                    Text(
                        text = if (pressedFretsCount == 0) {
                            stringResource(R.string.chord_finder_hint)
                        } else {
                            stringResource(R.string.chord_finder_selected_count, pressedFretsCount)
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = StudioTextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            Box(
                modifier = Modifier.size(36.dp),
                contentAlignment = Alignment.Center,
            ) {
                if (pressedFretsCount > 0) {
                    Studio3DIconBadge(
                        icon = Icons.Default.Clear,
                        contentDescription = stringResource(R.string.clear),
                        size = 36.dp,
                        accent = Studio3DAccent.RUBY,
                        onClick = onClearFrets,
                    )
                }
            }
        }
    }
}

@Composable
private fun FretboardContainer(
    stringCount: Int,
    tuningNotes: List<TuningNote>,
    pressedFrets: Set<FretPosition>,
    onFretTapped: (Int, Int, Int) -> Unit,
) {
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
                mode = FretboardMode.REVERSE_LOOKUP,
                rootPitchIndex = 0,
                targetIntervals = emptyList(),
                pressedFrets = pressedFrets,
                onFretTapped = onFretTapped,
            )
        }
    }
}

// ==================== PREVIEWS ====================

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun ChordFinderHeaderCardPreview() {
    GuitarLabTheme {
        Column(
            modifier = Modifier
                .background(StudioDarkBg)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            ChordFinderHeaderCard(
                detectedChords = emptyList(),
                pressedFretsCount = 2,
                onClearFrets = {}
            )
            ChordFinderHeaderCard(
                detectedChords = listOf("Cmaj7", "Am9"),
                pressedFretsCount = 4,
                onClearFrets = {}
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun ReverseChordFinderScreenPreview() {
    GuitarLabTheme {
        ReverseChordFinderContent(
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
                category = "Standard"
            ),
            pressedFrets = setOf(
                FretPosition(stringIndex = 0, fret = 3, midiNote = 48),
                FretPosition(stringIndex = 1, fret = 2, midiNote = 55)
            ),
            detectedChords = listOf("Em"),
            onOpenTuningSheet = {},
            onClearFrets = {},
            onFretTapped = { _, _, _ -> }
        )
    }
}