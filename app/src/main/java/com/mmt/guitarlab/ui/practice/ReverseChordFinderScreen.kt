package com.mmt.guitarlab.ui.practice

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mmt.guitarlab.R
import com.mmt.guitarlab.domain.model.FretboardMode
import com.mmt.guitarlab.domain.model.Tuning
import com.mmt.guitarlab.ui.components.Studio3DAccent
import com.mmt.guitarlab.ui.components.Studio3DIconBadge
import com.mmt.guitarlab.ui.components.StudioCard
import com.mmt.guitarlab.ui.theme.ElectricAmber
import com.mmt.guitarlab.ui.theme.ElectricGreen
import com.mmt.guitarlab.ui.theme.ElectricTeal
import com.mmt.guitarlab.ui.theme.StudioCardBorder
import com.mmt.guitarlab.ui.theme.StudioDarkBg
import com.mmt.guitarlab.ui.theme.StudioTextMuted
import com.mmt.guitarlab.ui.theme.StudioTextPrimary
import com.mmt.guitarlab.ui.theme.StudioTextSecondary

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

    val stringCount = selectedTuning?.stringCount ?: 6
    val tuningNotes = selectedTuning?.notes ?: emptyList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        // Guitar Tuning Selector Button
        TuningSelectorCard(
            selectedTuning = selectedTuning,
            onClick = { showTuningBottomSheet = true },
        )

        Spacer(Modifier.height(16.dp))

        // Fixed-size Chord Finder Card
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
                            text = if (pressedFrets.isEmpty()) {
                                stringResource(R.string.chord_finder_hint)
                            } else {
                                stringResource(R.string.chord_finder_selected_count, pressedFrets.size)
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
                    if (pressedFrets.isNotEmpty()) {
                        Studio3DIconBadge(
                            icon = Icons.Default.Clear,
                            contentDescription = stringResource(R.string.clear),
                            size = 36.dp,
                            accent = Studio3DAccent.RUBY,
                            onClick = { viewModel.clearPressedFrets() },
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        Text(
            text = stringResource(R.string.interactive_fretboard),
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
fun TuningSelectorCard(
    selectedTuning: Tuning?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val notesSummary = selectedTuning?.notes?.joinToString(" ") { it.noteName } ?: ""
    val stringsText = stringResource(R.string.strings_count, selectedTuning?.stringCount ?: 6)

    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.guitar_tuning),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = StudioTextMuted,
            letterSpacing = 1.sp,
        )
        Spacer(Modifier.height(8.dp))

        StudioCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
            accentBorder = ElectricTeal.copy(alpha = 0.5f),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f),
                ) {
                    Studio3DIconBadge(
                        icon = Icons.Default.Tune,
                        contentDescription = stringResource(R.string.guitar_tuning),
                        size = 38.dp,
                        accent = Studio3DAccent.TEAL,
                    )

                    Column {
                        Text(
                            text = selectedTuning?.name ?: stringResource(R.string.select_tuning),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = StudioTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        if (notesSummary.isNotEmpty()) {
                            Spacer(Modifier.height(2.dp))
                            Text(
                                text = "$notesSummary ($stringsText)",
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = ElectricAmber,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }

                Icon(
                    imageVector = Icons.Default.UnfoldMore,
                    contentDescription = stringResource(R.string.select_tuning),
                    tint = ElectricTeal,
                    modifier = Modifier.size(24.dp),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TuningBottomSheet(
    availableTunings: List<Tuning>,
    selectedTuning: Tuning?,
    onTuningSelected: (Tuning) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF141923),
        scrimColor = Color.Black.copy(alpha = 0.65f),
        dragHandle = {
            BottomSheetDefaults.DragHandle(
                color = StudioCardBorder,
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(bottom = 32.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Studio3DIconBadge(
                        icon = Icons.Default.Tune,
                        contentDescription = null,
                        size = 36.dp,
                        accent = Studio3DAccent.TEAL,
                    )
                    Column {
                        Text(
                            text = stringResource(R.string.tuning_select_title),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = StudioTextPrimary,
                            letterSpacing = 0.5.sp,
                        )
                        Text(
                            text = stringResource(R.string.tuning_select_subtitle),
                            style = MaterialTheme.typography.labelSmall,
                            color = StudioTextSecondary,
                        )
                    }
                }

                Studio3DIconBadge(
                    icon = Icons.Default.Close,
                    contentDescription = stringResource(R.string.close),
                    size = 32.dp,
                    accent = Studio3DAccent.SLATE,
                    onClick = onDismiss,
                )
            }

            HorizontalDivider(
                color = StudioCardBorder,
                modifier = Modifier.padding(bottom = 16.dp),
            )

            val grouped = availableTunings.groupBy { it.category }
            grouped.forEach { (category, tunings) ->
                Text(
                    text = category.uppercase(),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = StudioTextMuted,
                    letterSpacing = 1.2.sp,
                    modifier = Modifier.padding(vertical = 8.dp),
                )

                tunings.forEach { tun ->
                    val isSelected = selectedTuning?.id == tun.id
                    val tunNotes = tun.notes.joinToString(" ") { it.noteName }
                    val stringsText = stringResource(R.string.strings_count, tun.stringCount)

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                if (isSelected) {
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF003840), Color(0xFF002228)),
                                    )
                                } else {
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF1E2536), Color(0xFF171D2B)),
                                    )
                                },
                            )
                            .border(
                                width = 1.2.dp,
                                color = if (isSelected) ElectricTeal else StudioCardBorder.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(14.dp),
                            )
                            .clickable {
                                onTuningSelected(tun)
                                onDismiss()
                            }
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = tun.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                                    color = if (isSelected) ElectricTeal else StudioTextPrimary,
                                )
                                Spacer(Modifier.height(2.dp))
                                Text(
                                    text = "$tunNotes · $stringsText",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Medium,
                                    color = if (isSelected) ElectricAmber else StudioTextSecondary,
                                )
                            }

                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(ElectricTeal),
                                    contentAlignment = Alignment.Center,
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = Color(0xFF002026),
                                        modifier = Modifier.size(18.dp),
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(Modifier.height(12.dp))
            }
        }
    }
}
