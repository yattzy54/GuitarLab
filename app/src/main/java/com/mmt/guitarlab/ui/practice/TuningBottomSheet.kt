package com.mmt.guitarlab.ui.practice

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Tune
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mmt.guitarlab.domain.model.Tuning
import com.mmt.guitarlab.domain.model.TuningNote
import com.mmt.guitarlab.ui.components.Studio3DAccent
import com.mmt.guitarlab.ui.components.Studio3DIconBadge
import com.mmt.guitarlab.ui.theme.ElectricAmber
import com.mmt.guitarlab.ui.theme.ElectricTeal
import com.mmt.guitarlab.ui.theme.GuitarLabTheme
import com.mmt.guitarlab.ui.theme.StudioCardBorder
import com.mmt.guitarlab.ui.theme.StudioDarkBg
import com.mmt.guitarlab.ui.theme.StudioTextMuted
import com.mmt.guitarlab.ui.theme.StudioTextPrimary
import com.mmt.guitarlab.ui.theme.StudioTextSecondary

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
        TuningSheetContent(
            availableTunings = availableTunings,
            selectedTuning = selectedTuning,
            onTuningSelected = onTuningSelected,
            onDismiss = onDismiss,
        )
    }
}

@Composable
fun TuningSheetContent(
    availableTunings: List<Tuning>,
    selectedTuning: Tuning?,
    onTuningSelected: (Tuning) -> Unit,
    onDismiss: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .padding(bottom = 32.dp),
    ) {
        TuningSheetHeader(onDismiss = onDismiss)

        HorizontalDivider(
            color = StudioCardBorder,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        val grouped = availableTunings.groupBy { it.category }
        grouped.forEach { (category, tunings) ->
            TuningCategorySection(
                category = category,
                tunings = tunings,
                selectedTuning = selectedTuning,
                onTuningSelected = {
                    onTuningSelected(it)
                    onDismiss()
                },
            )
            Spacer(Modifier.height(12.dp))
        }
    }
}

@Composable
private fun TuningSheetHeader(onDismiss: () -> Unit) {
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
                    text = "Выберите строй", // Или stringResource(R.string.tuning_select_title)
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = StudioTextPrimary,
                    letterSpacing = 0.5.sp,
                )
                Text(
                    text = "Доступные гитарные строи", // Или stringResource(R.string.tuning_select_subtitle)
                    style = MaterialTheme.typography.labelSmall,
                    color = StudioTextSecondary,
                )
            }
        }

        Studio3DIconBadge(
            icon = Icons.Default.Close,
            contentDescription = "Закрыть",
            size = 32.dp,
            accent = Studio3DAccent.SLATE,
            onClick = onDismiss,
        )
    }
}

@Composable
private fun TuningCategorySection(
    category: String,
    tunings: List<Tuning>,
    selectedTuning: Tuning?,
    onTuningSelected: (Tuning) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
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
            TuningItemRow(
                tuning = tun,
                isSelected = isSelected,
                onClick = { onTuningSelected(tun) },
            )
            Spacer(Modifier.height(4.dp))
        }
    }
}

@Composable
private fun TuningItemRow(
    tuning: Tuning,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val tunNotes = tuning.notes.joinToString(" ") { it.noteName }

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
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tuning.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                    color = if (isSelected) ElectricTeal else StudioTextPrimary,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "$tunNotes · ${tuning.stringCount} струн",
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

// ==================== PREVIEWS ====================

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun TuningBottomSheetPreview() {
    GuitarLabTheme {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        LaunchedEffect(Unit) {
            sheetState.show()
        }

        Box(
            modifier = Modifier
                .background(StudioDarkBg)
                .fillMaxWidth()
                .height(450.dp)
        ) {
            TuningSheetContent(
                availableTunings = listOf(
                    Tuning(
                        id = "std",
                        name = "Standard E",
                        category = "Popular",
                        stringCount = 6,
                        notes = listOf(
                            TuningNote(1, "E", 2, 82.4f, midiNote = 0),
                            TuningNote(2, "A", 2, 110f, midiNote = 0),
                            TuningNote(3, "D", 3, 146.8f, midiNote = 0),
                            TuningNote(4, "G", 3, 196f, midiNote = 0),
                            TuningNote(5, "B", 3, 246.9f, midiNote = 0),
                            TuningNote(6, "E", 4, 329.6f, midiNote = 0),
                        )
                    ),
                    Tuning(
                        id = "drop",
                        name = "Drop D",
                        category = "Popular",
                        stringCount = 6,
                        notes = listOf(
                            TuningNote(1, "D", 2, 73.4f, midiNote = 0),
                            TuningNote(2, "A", 2, 110f, midiNote = 0),
                            TuningNote(3, "D", 3, 146.8f, midiNote = 0),
                            TuningNote(4, "G", 3, 196f, midiNote = 0),
                            TuningNote(5, "B", 3, 246.9f, midiNote = 0),
                            TuningNote(6, "E", 4, 329.6f, midiNote = 0),
                        )
                    )
                ),
                selectedTuning = Tuning(id = "std", name = "Standard E", category = "Popular", stringCount = 6, notes = emptyList(), isFavorite = false ),
                onTuningSelected = {},
                onDismiss = {}
            )
        }
    }
}