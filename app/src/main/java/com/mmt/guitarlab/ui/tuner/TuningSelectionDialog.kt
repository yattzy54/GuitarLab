package com.mmt.guitarlab.ui.tuner

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mmt.guitarlab.domain.model.Tuning
import com.mmt.guitarlab.domain.model.TuningNote
import com.mmt.guitarlab.ui.theme.ElectricAmber
import com.mmt.guitarlab.ui.theme.GuitarLabTheme
import com.mmt.guitarlab.ui.theme.StudioCardBg
import com.mmt.guitarlab.ui.theme.StudioCardBorder
import com.mmt.guitarlab.ui.theme.StudioCardElevated
import com.mmt.guitarlab.ui.theme.StudioDarkBg
import com.mmt.guitarlab.ui.theme.StudioTextMuted
import com.mmt.guitarlab.ui.theme.StudioTextPrimary
import com.mmt.guitarlab.ui.theme.StudioTextSecondary

@Composable
fun TuningSelectionDialog(
    tunings: List<Tuning>,
    selectedId: String,
    isChromatic: Boolean,
    onToggleChromatic: () -> Unit,
    onSelect: (Tuning) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val categories = remember(tunings) {
        listOf("Favorites") + tunings.map { it.category }.distinct()
    }
    var selectedCategory by remember { mutableStateOf("Standard") }

    val filteredTunings = remember(tunings, selectedCategory) {
        when (selectedCategory) {
            "Favorites" -> tunings.filter { it.isFavorite }
            else -> tunings.filter { it.category == selectedCategory }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = StudioCardBg,
        title = {
            Text(
                "Guitar Tuning Presets",
                fontWeight = FontWeight.Bold,
                color = StudioTextPrimary,
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Chromatic Mode Button inside Guitar tuning presets
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            if (isChromatic) Color(0xFF382600) else StudioCardElevated
                        )
                        .border(
                            1.dp,
                            if (isChromatic) ElectricAmber else StudioCardBorder,
                            RoundedCornerShape(12.dp),
                        )
                        .clickable { onToggleChromatic() }
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = "Chromatic Tuner",
                            tint = if (isChromatic) ElectricAmber else StudioTextSecondary,
                            modifier = Modifier.size(24.dp),
                        )
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Хроматический режим",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isChromatic) ElectricAmber else StudioTextPrimary,
                            )
                            Text(
                                text = "Определение любой ноты без струн",
                                style = MaterialTheme.typography.bodySmall,
                                color = StudioTextSecondary,
                            )
                        }
                    }
                    if (isChromatic) {
                        Text(
                            text = "АКТИВЕН",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = ElectricAmber,
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                ScrollableTabRow(
                    selectedTabIndex = categories.indexOf(selectedCategory).coerceAtLeast(0),
                    edgePadding = 0.dp,
                    containerColor = StudioDarkBg,
                    contentColor = ElectricAmber,
                ) {
                    categories.forEach { cat ->
                        Tab(
                            selected = cat == selectedCategory,
                            onClick = { selectedCategory = cat },
                            text = { Text(cat, fontWeight = FontWeight.SemiBold) },
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                if (filteredTunings.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            "No presets found in this category.",
                            color = StudioTextSecondary,
                        )
                    }
                } else {
                    LazyColumn(modifier = Modifier.height(300.dp)) {
                        items(filteredTunings) { tuning ->
                            val isSelected = tuning.id == selectedId
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) Color(0xFF382600) else StudioCardElevated,
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) ElectricAmber else StudioCardBorder,
                                        RoundedCornerShape(12.dp),
                                    )
                                    .clickable { onSelect(tuning) }
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = tuning.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) ElectricAmber else StudioTextPrimary,
                                    )
                                    Text(
                                        text = tuning.notes.joinToString("  ") { it.noteName },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = StudioTextSecondary,
                                    )
                                }
                                IconButton(
                                    onClick = { onToggleFavorite(tuning.id) },
                                ) {
                                    Icon(
                                        imageVector = if (tuning.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                        contentDescription = "Favorite",
                                        tint = if (tuning.isFavorite) ElectricAmber else StudioTextMuted,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = ElectricAmber, fontWeight = FontWeight.Bold)
            }
        },
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun TuningSelectionDialogPreview() {
    GuitarLabTheme {
        // Создаем тестовые данные для превью
        val sampleTunings = listOf(
            Tuning(
                id = "standard",
                name = "Standard E",
                isFavorite = true,
                notes = listOf(
                    TuningNote(1, "E", 2, 82.4f, midiNote = 0),
                    TuningNote(2, "A", 2, 110.0f, midiNote = 0),
                    TuningNote(3, "D", 3, 146.8f, midiNote = 0),
                    TuningNote(4, "G", 3, 196.0f, midiNote = 0),
                    TuningNote(5, "B", 3, 246.9f, midiNote = 0),
                    TuningNote(6, "E", 4, 329.6f, midiNote = 0)
                ),
                stringCount = 6,
                category = "Standard"
            ),
            Tuning(
                id = "drop_d",
                name = "Drop D",
                isFavorite = false,
                notes = listOf(
                    TuningNote(1, "D", 2, 73.4f, midiNote = 0),
                    TuningNote(2, "A", 2, 110.0f, midiNote = 0),
                    TuningNote(3, "D", 3, 146.8f, midiNote = 0),
                    TuningNote(4, "G", 3, 196.0f, midiNote = 0),
                    TuningNote(5, "B", 3, 246.9f, midiNote = 0),
                    TuningNote(6, "E", 4, 329.6f, midiNote = 0)
                ),
                stringCount = 6,
                category = "Drop"
            ),
            Tuning(
                id = "half_step_down",
                name = "Half Step Down (Eb)",
                isFavorite = false,
                notes = listOf(
                    TuningNote(1, "D♯", 2, 77.8f, midiNote = 0),
                    TuningNote(2, "G♯", 2, 103.8f, midiNote = 0),
                    TuningNote(3, "C♯", 3, 138.6f, midiNote = 0),
                    TuningNote(4, "F♯", 3, 185.0f, midiNote = 0),
                    TuningNote(5, "A♯", 3, 233.1f, midiNote = 0),
                    TuningNote(6, "D♯", 4, 311.1f, midiNote = 0)
                ),
                stringCount = 6,
                category = "Standard"
            )
        )

        Box(
            modifier = Modifier
                .background(StudioDarkBg)
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            TuningSelectionDialog(
                tunings = sampleTunings,
                selectedId = "standard",
                isChromatic = false,
                onToggleChromatic = {},
                onSelect = {},
                onToggleFavorite = {},
                onDismiss = {}
            )
        }
    }
}