package com.mmt.guitarlab.ui.metronome.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mmt.guitarlab.ui.theme.ElectricAmber
import com.mmt.guitarlab.ui.theme.ElectricTeal
import kotlin.math.cos
import kotlin.math.sin

@Composable
 fun TempoCircularWheel(
    bpm: Int,
    running: Boolean,
    accent: Boolean,
    modifier: Modifier = Modifier,
) {
    val trackColor = Color(0xFF1B2130)
    val activeGlow = when {
        running && accent -> ElectricAmber
        running -> ElectricTeal
        else -> Color(0xFF333E56)
    }

    Canvas(modifier = modifier) {
        val stroke = Stroke(width = 10.dp.toPx(), cap = StrokeCap.Round)
        val center = Offset(size.width / 2f, size.height / 2f)
        val radius = size.minDimension / 2f - 12.dp.toPx()

        // Background Track
        drawCircle(
            color = trackColor,
            radius = radius,
            center = center,
            style = stroke,
        )

        // Progress Arc (Normalized between 40 and 240 BPM)
        val norm = ((bpm - 40f) / (240f - 40f)).coerceIn(0f, 1f)
        val sweepAngle = norm * 360f

        drawArc(
            color = activeGlow,
            startAngle = -90f,
            sweepAngle = sweepAngle,
            useCenter = false,
            style = stroke,
        )

        // Draw radial ticks around the wheel
        for (i in 0 until 40) {
            val angle = Math.toRadians((i * (360.0 / 40.0)))
            val inner = radius - 14.dp.toPx()
            val outer = radius - 8.dp.toPx()
            drawLine(
                color = Color(0xFF3B4863).copy(alpha = 0.6f),
                start = Offset(
                    center.x + inner * cos(angle).toFloat(),
                    center.y + inner * sin(angle).toFloat(),
                ),
                end = Offset(
                    center.x + outer * cos(angle).toFloat(),
                    center.y + outer * sin(angle).toFloat(),
                ),
                strokeWidth = 2.dp.toPx(),
                cap = StrokeCap.Round,
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0D1117)
@Composable
fun TempoCircularWheelPreview() {
    MaterialTheme {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Остановлен (Paused / Default)
            WheelPreviewItem(title = "Paused (80 BPM)") {
                TempoCircularWheel(
                    bpm = 80,
                    running = false,
                    accent = false,
                    modifier = Modifier.size(100.dp)
                )
            }

            // 2. Обычный запуск (Running / Teal)
            WheelPreviewItem(title = "Running (120 BPM)") {
                TempoCircularWheel(
                    bpm = 120,
                    running = true,
                    accent = false,
                    modifier = Modifier.size(100.dp)
                )
            }

            // 3. Акцентный такт (Accent / Amber)
            WheelPreviewItem(title = "Accent (160 BPM)") {
                TempoCircularWheel(
                    bpm = 160,
                    running = true,
                    accent = true,
                    modifier = Modifier.size(100.dp)
                )
            }
        }
    }
}

@Composable
private fun WheelPreviewItem(
    title: String,
    content: @Composable () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        content()
        Text(
            text = title,
            color = Color.LightGray,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}