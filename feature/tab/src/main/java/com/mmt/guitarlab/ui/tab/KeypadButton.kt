package com.mmt.guitarlab.ui.tab

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mmt.guitarlab.core.ui.theme.GuitarLabTheme
import com.mmt.guitarlab.core.ui.theme.StudioDarkBg

@Composable
 fun KeypadButton(
    text: String,
    isSpecial: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(width = 32.dp, height = 30.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSpecial) Color(0xFF3B4252) else Color(0xFF2E3440))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSpecial) Color(0xFFFBBF24) else Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = if (text.length > 1) 10.sp else 13.sp
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun KeypadButtonPreview() {
    GuitarLabTheme {
        Row(
            modifier = Modifier
                .background(StudioDarkBg)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Обычная кнопка
            KeypadButton(
                text = "5",
                isSpecial = false,
                onClick = {}
            )

            // Специальная кнопка
            KeypadButton(
                text = "C",
                isSpecial = true,
                onClick = {}
            )
        }
    }
}