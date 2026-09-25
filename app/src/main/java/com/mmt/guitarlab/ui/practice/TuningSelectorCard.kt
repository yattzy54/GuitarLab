package com.mmt.guitarlab.ui.practice

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mmt.guitarlab.R
import com.mmt.guitarlab.domain.model.Tuning
import com.mmt.guitarlab.domain.model.TuningNote
import com.mmt.guitarlab.ui.components.Studio3DAccent
import com.mmt.guitarlab.ui.components.Studio3DIconBadge
import com.mmt.guitarlab.ui.components.StudioCard
import com.mmt.guitarlab.ui.theme.ElectricAmber
import com.mmt.guitarlab.ui.theme.ElectricTeal
import com.mmt.guitarlab.ui.theme.GuitarLabTheme
import com.mmt.guitarlab.ui.theme.StudioDarkBg
import com.mmt.guitarlab.ui.theme.StudioTextMuted
import com.mmt.guitarlab.ui.theme.StudioTextPrimary

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

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun TuningSelectorCardPreview() {
    GuitarLabTheme {
        Column(
            modifier = Modifier
                .background(StudioDarkBg)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Состояние 1: Выбран стандартный строй
            TuningSelectorCard(
                selectedTuning = Tuning(
                    id = "standard",
                    name = "Standard E",
                    stringCount = 6,
                    notes = listOf(
                        TuningNote(1, "E", 2, 82.4f, 0),
                        TuningNote(2, "A", 2, 110f, 0),
                        TuningNote(3, "D", 3, 146.8f, 0),
                        TuningNote(4, "G", 3, 196f, 0),
                        TuningNote(5, "B", 3, 246.9f, 0),
                        TuningNote(6, "E", 4, 329.6f, 0),
                    ),
                    category = "Standard",
                    isFavorite = false
                ),
                onClick = {}
            )

            // Состояние 2: Строй не выбран (null)
            TuningSelectorCard(
                selectedTuning = null,
                onClick = {}
            )
        }
    }
}