package com.mmt.guitarlab.ui.metronome.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mmt.guitarlab.ui.theme.ElectricAmber
import com.mmt.guitarlab.ui.theme.StudioTextSecondary

@Composable
 fun LabeledSliderRow(
    title: String,
    valueLabel: String,
    value: Float,
    range: ClosedFloatingPointRange<Float>,
    onChange: (Float) -> Unit,
    accentColor: Color,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            title,
            style = MaterialTheme.typography.labelMedium,
            color = StudioTextSecondary,
            fontWeight = FontWeight.Bold,
        )
        Text(
            valueLabel,
            style = MaterialTheme.typography.labelMedium,
            color = accentColor,
            fontWeight = FontWeight.Black,
        )
    }
    Slider(
        value = value,
        onValueChange = onChange,
        valueRange = range,
        colors = SliderDefaults.colors(
            thumbColor = accentColor,
            activeTrackColor = accentColor,
            inactiveTrackColor = Color(0xFF22293B),
        ),
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF0D1117)
@Composable
fun LabeledSliderRowPreview() {
    MaterialTheme {
        // Используем remember, чтобы слайдер был интерактивным в Android Studio (Interactive Mode)
        var sliderValue by remember { mutableStateOf(120f) }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            LabeledSliderRow(
                title = "Tempo",
                valueLabel = "${sliderValue.toInt()} BPM",
                value = sliderValue,
                range = 40f..240f,
                onChange = { sliderValue = it },
                accentColor = ElectricAmber
            )
        }
    }
}