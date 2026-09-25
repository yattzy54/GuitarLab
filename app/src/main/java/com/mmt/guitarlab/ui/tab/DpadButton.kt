package com.mmt.guitarlab.ui.tab

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import com.mmt.guitarlab.ui.theme.GuitarLabTheme

@Composable
 fun DpadButton(
    text: String,
    isCenter: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(width = 32.dp, height = 28.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(if (isCenter) Color(0xFFD97706) else Color(0xFF2E3440))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isCenter) Color.Black else Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 12.sp
        )
    }
}

@Preview(showBackground = true)
@Composable
fun DpadButtonPreview() {
    GuitarLabTheme() {
        DpadButton(
            text = "Pad",
            isCenter = true,
            onClick = {}
        )
    }

}