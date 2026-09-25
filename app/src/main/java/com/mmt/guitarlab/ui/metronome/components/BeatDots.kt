package com.mmt.guitarlab.ui.metronome.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mmt.guitarlab.ui.theme.ElectricAmber
import com.mmt.guitarlab.ui.theme.ElectricTeal

@Composable
 fun BeatDots(beats: Int, accents: Set<Int>, current: Int?, pulse: Float) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(beats) { index ->
            val beatNum = index + 1
            val active = current == beatNum
            val isAccent = beatNum in accents

            val dotColor = when {
                active && isAccent -> ElectricAmber
                active -> ElectricTeal
                isAccent -> ElectricAmber.copy(alpha = 0.35f)
                else -> Color(0xFF263042)
            }

            val dotSize = if (isAccent) 22.dp else 16.dp
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .scale(if (active) pulse else 1f)
                    .clip(CircleShape)
                    .background(dotColor)
                    .border(
                        width = 1.dp,
                        color = if (active) Color.White.copy(alpha = 0.6f) else Color.Transparent,
                        shape = CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                if (isAccent) {
                    Text(
                        text = "1",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        fontWeight = FontWeight.Black,
                        color = if (active) Color(0xFF261800) else ElectricAmber,
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0D1117)
@Composable
fun BeatDotsPreview() {
    MaterialTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Обычное состояние (4/4 такта, акцент на первом ударе, ничего не играет)
            BeatPreviewSection(title = "Default (Paused)") {
                BeatDots(
                    beats = 4,
                    accents = setOf(1),
                    current = null,
                    pulse = 1f
                )
            }

            // 2. Активен первый удар (Акцент, с эффектом пульсации)
            BeatPreviewSection(title = "Playing: Beat 1 (Accent)") {
                BeatDots(
                    beats = 4,
                    accents = setOf(1),
                    current = 1,
                    pulse = 1.2f // Немного увеличивается за счет scale
                )
            }

            // 3. Активен третий удар (Обычный бит)
            BeatPreviewSection(title = "Playing: Beat 3 (Normal)") {
                BeatDots(
                    beats = 4,
                    accents = setOf(1),
                    current = 3,
                    pulse = 1.15f
                )
            }
        }
    }
}

@Composable
private fun BeatPreviewSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            color = Color.LightGray,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 11.sp,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        content()
    }
}