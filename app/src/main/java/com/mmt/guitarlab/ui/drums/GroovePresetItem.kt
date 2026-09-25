package com.mmt.guitarlab.ui.drums

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mmt.guitarlab.domain.model.DrumPattern
import com.mmt.guitarlab.ui.theme.ElectricTeal
import com.mmt.guitarlab.ui.theme.GuitarLabTheme
import com.mmt.guitarlab.ui.theme.StudioCardBorder
import com.mmt.guitarlab.ui.theme.StudioCardElevated
import com.mmt.guitarlab.ui.theme.StudioTextMuted
import com.mmt.guitarlab.ui.theme.StudioTextPrimary

@Composable
fun GroovePresetItem(
    pattern: DrumPattern,
    isSelected: Boolean,
    onSelect: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) ElectricTeal.copy(alpha = 0.15f) else StudioCardElevated)
            .border(
                width = 1.dp,
                color = if (isSelected) ElectricTeal else StudioCardBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onSelect)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = pattern.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) ElectricTeal else StudioTextPrimary,
                )
                Spacer(Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(ElectricTeal.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp),
                ) {
                    Text(
                        text = pattern.style.uppercase(),
                        style = MaterialTheme.typography.labelSmall,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = ElectricTeal,
                    )
                }
            }
            Spacer(Modifier.height(2.dp))
            Text(
                text = "BPM: ${pattern.defaultBpm}",
                style = MaterialTheme.typography.bodySmall,
                color = StudioTextMuted,
            )
        }

        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = ElectricTeal,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF12151C)
@Composable
private fun GroovePresetItemPreview() {
    GuitarLabTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            val samplePattern = DrumPattern(
                name = "Heavy Metal Core",
                style = "metal",
                defaultBpm = 140,
                grid = emptyMap(),
                id = "sample_pattern"
            )

            // Обычное состояние
            GroovePresetItem(
                pattern = samplePattern,
                isSelected = false,
                onSelect = {}
            )

            // Выбранное состояние
            GroovePresetItem(
                pattern = samplePattern,
                isSelected = true,
                onSelect = {}
            )
        }
    }
}