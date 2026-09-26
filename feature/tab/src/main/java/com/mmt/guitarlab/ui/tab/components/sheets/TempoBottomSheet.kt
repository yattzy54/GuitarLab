package com.mmt.guitarlab.ui.tab.components.sheets

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mmt.guitarlab.core.ui.theme.GuitarLabTheme
import com.mmt.guitarlab.core.ui.theme.StudioDarkBg

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TempoBottomSheet(
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    speedRatio: Float,
    onSpeedRatioChange: (Float) -> Unit,
    baseTempoBpm: Int,
    modifier: Modifier = Modifier,
) {
    val presets = listOf(0.50f, 0.75f, 0.90f, 1.00f, 1.10f, 1.25f)
    val effectiveBpm = (baseTempoBpm * speedRatio).toInt()

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = Color(0xFF12151A),
        contentColor = Color.White,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Темп и скорость воспроизведения",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Large Speed & Effective BPM Indicator
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "${(speedRatio * 100).toInt()}%",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFFFBBF24)
                )
                Text(
                    text = "($effectiveBpm BPM)",
                    fontSize = 18.sp,
                    fontFamily = FontFamily.Monospace,
                    color = Color(0xFF9CA3AF),
                    modifier = Modifier.padding(bottom = 6.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Slider(
                value = speedRatio,
                onValueChange = onSpeedRatioChange,
                valueRange = 0.25f..1.50f,
                steps = 24,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFFF59E0B),
                    activeTrackColor = Color(0xFFF59E0B),
                    inactiveTrackColor = Color(0xFF374151)
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Быстрые настройки скорости:",
                fontSize = 12.sp,
                color = Color(0xFF9CA3AF),
                modifier = Modifier.align(Alignment.Start)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                presets.forEach { preset ->
                    val isSelected = kotlin.math.abs(speedRatio - preset) < 0.02f
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) Color(0xFFF59E0B) else Color(0xFF1E222A))
                            .border(
                                width = 1.dp,
                                color = if (isSelected) Color(0xFFF59E0B) else Color(0xFF2E333D),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { onSpeedRatioChange(preset) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${(preset * 100).toInt()}%",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = if (isSelected) Color(0xFF09090B) else Color.White
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun TempoBottomSheetPreview() {
    GuitarLabTheme {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        Box(
            modifier = Modifier
                .background(StudioDarkBg)
                .fillMaxWidth()
                .height(400.dp)
        ) {
            TempoBottomSheet(
                sheetState = sheetState,
                onDismissRequest = {},
                speedRatio = 0.75f,
                onSpeedRatioChange = {},
                baseTempoBpm = 120,
            )
        }
    }
}