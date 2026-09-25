package com.mmt.guitarlab.ui.tuner

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
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
import com.mmt.guitarlab.ui.theme.ElectricGreen
import com.mmt.guitarlab.ui.theme.GuitarLabTheme
import com.mmt.guitarlab.ui.theme.StudioDarkBg
import com.mmt.guitarlab.ui.theme.StudioTextSecondary
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun StrobeNeedleGauge(
    cents: Float,
    isInTune: Boolean,
    hasSignal: Boolean,
    modifier: Modifier = Modifier,
) {
    val trackColor = Color(0xFF1E2536)
    val tickColor = Color(0xFF424F6C)
    val safeZoneColor = ElectricGreen.copy(alpha = 0.25f)
    val needleColor = when {
        !hasSignal -> Color(0xFF64748B)
        isInTune -> ElectricGreen
        else -> ElectricAmber
    }

    Canvas(modifier = modifier) {
        val sweep = 160f
        val start = 190f
        val stroke = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)

        // Arc track
        drawArc(
            color = trackColor,
            startAngle = start,
            sweepAngle = sweep,
            useCenter = false,
            style = stroke,
        )

        // Green safe zone around center (0 cents)
        val safeSweep = 16f
        drawArc(
            color = safeZoneColor,
            startAngle = 270f - safeSweep / 2f,
            sweepAngle = safeSweep,
            useCenter = false,
            style = stroke,
        )

        val radius = size.minDimension / 1.15f
        val center = Offset(size.width / 2f, size.height * 0.98f)

        // Tick marks (-50 to +50 cents in steps of 10)
        for (c in -50..50 step 10) {
            val t = (c + 50) / 100f
            val angle = Math.toRadians((start + sweep * t).toDouble())
            val inner = radius - (if (c % 25 == 0) 18.dp.toPx() else 10.dp.toPx())
            val outer = radius - 4.dp.toPx()

            val curTickColor = if (c == 0) ElectricGreen else tickColor
            drawLine(
                color = curTickColor,
                start = Offset(
                    center.x + inner * cos(angle).toFloat(),
                    center.y + inner * sin(angle).toFloat(),
                ),
                end = Offset(
                    center.x + outer * cos(angle).toFloat(),
                    center.y + outer * sin(angle).toFloat(),
                ),
                strokeWidth = if (c == 0) 3.5.dp.toPx() else 1.8.dp.toPx(),
                cap = StrokeCap.Round,
            )
        }

        // Animated Needle
        val needleNorm = (cents.coerceIn(-50f, 50f) + 50f) / 100f
        val needleAngle = Math.toRadians((start + sweep * needleNorm).toDouble())

        drawLine(
            color = needleColor,
            start = center,
            end = Offset(
                center.x + (radius - 6.dp.toPx()) * cos(needleAngle).toFloat(),
                center.y + (radius - 6.dp.toPx()) * sin(needleAngle).toFloat(),
            ),
            strokeWidth = 4.5.dp.toPx(),
            cap = StrokeCap.Round,
        )

        // Central pivot cap
        drawCircle(
            color = needleColor,
            radius = 9.dp.toPx(),
            center = center,
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0D1117)
@Composable
fun StrobeNeedleGaugePreview() {
    GuitarLabTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 1. Состояние: В строе (0 центов)
            GaugePreviewItem(title = "In Tune (0 cents)") {
                StrobeNeedleGauge(
                    cents = 0f,
                    isInTune = true,
                    hasSignal = true,
                    modifier = Modifier.width(220.dp).height(130.dp)
                )
            }

            // 2. Состояние: Отклонение (+25 центов)
            GaugePreviewItem(title = "Out of Tune (+25 cents)") {
                StrobeNeedleGauge(
                    cents = 25f,
                    isInTune = false,
                    hasSignal = true,
                    modifier = Modifier.width(220.dp).height(130.dp)
                )
            }

            // 3. Состояние: Нет сигнала
            GaugePreviewItem(title = "No Signal") {
                StrobeNeedleGauge(
                    cents = 0f,
                    isInTune = false,
                    hasSignal = false,
                    modifier = Modifier.width(220.dp).height(130.dp)
                )
            }
        }
    }
}

@Composable
private fun GaugePreviewItem(
    title: String,
    content: @Composable () -> Unit
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            color = Color.LightGray,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 11.sp,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        content()
    }
}