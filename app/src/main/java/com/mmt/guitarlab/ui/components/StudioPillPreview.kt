package com.mmt.guitarlab.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview(showBackground = true, backgroundColor = 0xFF0D1117)
@Composable
fun StudioPillPreview() {
    MaterialTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 1. Неактивное состояние
            StudioPill(
                text = "Standard",
                selected = false,
                onClick = {}
            )

            // 2. Активное состояние (с дефолтным акцентным цветом ElectricAmber)
            StudioPill(
                text = "Drop D",
                selected = true,
                onClick = {}
            )

            // 3. Активное состояние с иконкой
            StudioPill(
                text = "Custom",
                selected = true,
                leadingIcon = Icons.Default.Star,
                onClick = {}
            )
        }
    }
}