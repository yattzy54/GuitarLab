package com.mmt.guitarlab.ui.tuner

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mmt.guitarlab.ui.tab.DrawerItemButton
import com.mmt.guitarlab.ui.theme.GuitarLabTheme

@Composable
private fun HeadstockCenterGraphic(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        // Draw Headstock silhouette
        drawRoundRect(
            color = Color(0xFF1A1F2C),
            topLeft = Offset(w * 0.15f, 0f),
            size = androidx.compose.ui.geometry.Size(w * 0.7f, h),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f),
        )
        // Center guitar logo line
        drawLine(
            color = Color(0xFFFFB703).copy(alpha = 0.6f),
            start = Offset(w * 0.5f, 20f),
            end = Offset(w * 0.5f, h - 20f),
            strokeWidth = 2f,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212) // Темный фон для контраста
@Composable
fun HeadstockCenterGraphicPreview() {
    GuitarLabTheme() {
        HeadstockCenterGraphic(
            modifier = Modifier.size(100.dp, 200.dp) // Задаем размеры для примера
        )
    }
}