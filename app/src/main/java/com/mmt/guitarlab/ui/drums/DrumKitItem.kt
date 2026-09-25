package com.mmt.guitarlab.ui.drums

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import com.mmt.guitarlab.domain.model.DrumKit
import com.mmt.guitarlab.ui.theme.ElectricAmber
import com.mmt.guitarlab.ui.theme.ElectricTeal
import com.mmt.guitarlab.ui.theme.GuitarLabTheme
import com.mmt.guitarlab.ui.theme.StudioCardBorder
import com.mmt.guitarlab.ui.theme.StudioCardElevated
import com.mmt.guitarlab.ui.theme.StudioTextMuted
import com.mmt.guitarlab.ui.theme.StudioTextPrimary

@Composable
 fun DrumKitItem(
    kit: DrumKit,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onPreview: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) ElectricAmber.copy(alpha = 0.15f) else StudioCardElevated)
            .border(
                width = 1.dp,
                color = if (isSelected) ElectricAmber else StudioCardBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onSelect)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = kit.iconEmoji, fontSize = 28.sp)
        Spacer(Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = kit.displayName,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) ElectricAmber else StudioTextPrimary,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = kit.description,
                style = MaterialTheme.typography.bodySmall,
                color = StudioTextMuted,
            )
        }

        // Preview button for the kit
        IconButton(
            onClick = onPreview,
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(StudioCardBorder.copy(alpha = 0.3f)),
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "Preview Kit",
                tint = ElectricTeal,
                modifier = Modifier.size(16.dp),
            )
        }

        if (isSelected) {
            Spacer(Modifier.width(8.dp))
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Selected",
                tint = ElectricAmber,
                modifier = Modifier.size(20.dp),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF12151C)
@Composable
private fun DrumKitItemPreview() {
    GuitarLabTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            // Обычное состояние
            DrumKitItem(
                kit = DrumKit.ROCK,
                isSelected = false,
                onSelect = {},
                onPreview = {}
            )

            // Выбранное состояние
            DrumKitItem(
                kit = DrumKit.METAL,
                isSelected = true,
                onSelect = {},
                onPreview = {}
            )
        }
    }
}