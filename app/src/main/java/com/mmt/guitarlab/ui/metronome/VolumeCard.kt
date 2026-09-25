package com.mmt.guitarlab.ui.metronome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeDown
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mmt.guitarlab.ui.components.StudioCard
import com.mmt.guitarlab.core.ui.theme.ElectricAmber
import com.mmt.guitarlab.core.ui.theme.GuitarLabTheme
import com.mmt.guitarlab.core.ui.theme.StudioCardBorder
import com.mmt.guitarlab.core.ui.theme.StudioDarkBg
import com.mmt.guitarlab.core.ui.theme.StudioTextMuted
import com.mmt.guitarlab.core.ui.theme.StudioTextSecondary

@Composable
fun VolumeCard(
    volume: Float,
    onSetVolume: (Float) -> Unit,
) {
    StudioCard(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "METRONOME VOLUME",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = StudioTextMuted,
                    letterSpacing = 1.sp,
                )
                Text(
                    text = "${(volume * 100).toInt()}%",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = ElectricAmber,
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeDown,
                    contentDescription = null,
                    tint = StudioTextSecondary,
                    modifier = Modifier.size(20.dp),
                )
                Slider(
                    value = volume,
                    onValueChange = onSetVolume,
                    valueRange = 0.1f..1f,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 8.dp),
                    colors = SliderDefaults.colors(
                        thumbColor = ElectricAmber,
                        activeTrackColor = ElectricAmber,
                        inactiveTrackColor = StudioCardBorder,
                    ),
                )
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                    contentDescription = null,
                    tint = ElectricAmber,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun VolumeCardPreview() {
    GuitarLabTheme {
        Column(
            modifier = Modifier
                .background(StudioDarkBg)
                .padding(16.dp),
        ) {
            VolumeCard(volume = 0.75f, onSetVolume = {})
        }
    }
}