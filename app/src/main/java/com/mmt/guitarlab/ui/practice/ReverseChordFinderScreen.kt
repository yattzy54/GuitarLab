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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mmt.guitarlab.domain.model.FretboardMode
import com.mmt.guitarlab.ui.components.Studio3DAccent
import com.mmt.guitarlab.ui.components.Studio3DIconBadge
import com.mmt.guitarlab.ui.components.StudioCard
import com.mmt.guitarlab.ui.theme.ElectricGreen
import com.mmt.guitarlab.ui.theme.StudioDarkBg
import com.mmt.guitarlab.ui.theme.StudioTextMuted
import com.mmt.guitarlab.ui.theme.StudioTextPrimary
import com.mmt.guitarlab.ui.theme.StudioTextSecondary

@Composable
fun ReverseChordFinderScreen(viewModel: FretboardViewModel = hiltViewModel()) {
    LaunchedEffect(Unit) {
        viewModel.setMode(FretboardMode.REVERSE_LOOKUP)
    }

    val selectedTuning by viewModel.selectedTuning.collectAsStateWithLifecycle()
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
        // Reverse Chord Lookup Card
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
                        text = "Нажмите на лады, чтобы распознать аккорд",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = StudioTextPrimary,
                    )
                    Spacer(Modifier.height(4.dp))
                    if (detectedChords.isNotEmpty()) {
                        Text(
                            text = "Определен: ${detectedChords.joinToString(", ")}",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Black,
                            color = ElectricGreen,
                        )
                    } else {
                        Text(
                            text = "Зажмите 2+ ноты на грифе (макс. 1 нота на струну)",
                            style = MaterialTheme.typography.bodySmall,
                            color = StudioTextSecondary,
                        )
                    }
                }

                if (pressedFrets.isNotEmpty()) {
                    Studio3DIconBadge(
                        icon = Icons.Default.Clear,
                        contentDescription = "Очистить",
                        size = 36.dp,
                        accent = Studio3DAccent.RUBY,
                        onClick = { viewModel.clearPressedFrets() },
                    )
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        Text(
            text = "ИНТЕРАКТИВНЫЙ ГРИФ",
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
                    mode = FretboardMode.REVERSE_LOOKUP,
                    rootPitchIndex = 0,
                    targetIntervals = emptyList(),
                    pressedFrets = pressedFrets,
                    onFretTapped = { strIdx, fret, midi ->
                        viewModel.toggleFret(strIdx, fret, midi)
                    },
                )
            }
        }

        Spacer(Modifier.height(24.dp))
    }
}
