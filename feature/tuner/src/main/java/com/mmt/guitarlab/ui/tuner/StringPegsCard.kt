package com.mmt.guitarlab.ui.tuner

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mmt.guitarlab.domain.model.Tuning
import com.mmt.guitarlab.domain.model.TuningNote
import com.mmt.guitarlab.core.ui.components.StudioCard
import com.mmt.guitarlab.core.ui.theme.ElectricAmber
import com.mmt.guitarlab.core.ui.theme.ElectricTeal

@Composable
fun StringPegsCard(
    selectedTuning: Tuning?,
    activeTuningNote: TuningNote?,
    lockedNoteIndex: Int?,
    isInTune: Boolean,
    onSelectNoteLock: (Int) -> Unit,
    onResetLock: () -> Unit,
) {
    StudioCard(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (lockedNoteIndex == null) "Auto Detection Active" else "Peg Lock Mode",
                    style = MaterialTheme.typography.labelSmall,
                    color = ElectricTeal,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                )
                if (lockedNoteIndex != null) {
                    Text(
                        text = "Switch to Auto",
                        style = MaterialTheme.typography.labelSmall,
                        color = ElectricAmber,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onResetLock() },
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            val notes = selectedTuning?.notes ?: emptyList()
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                notes.forEachIndexed { index, note ->
                    val isSelected = (lockedNoteIndex == index) ||
                            (lockedNoteIndex == null && activeTuningNote?.stringNumber == note.stringNumber)

                    PegItem(
                        note = note,
                        isSelected = isSelected,
                        isInTune = isSelected && isInTune,
                        modifier = Modifier.weight(1f),
                        onClick = { onSelectNoteLock(index) },
                    )
                }
            }
        }
    }
}