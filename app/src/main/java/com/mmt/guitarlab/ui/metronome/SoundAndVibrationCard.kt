package com.mmt.guitarlab.ui.metronome

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mmt.guitarlab.domain.model.MetronomeConfig
import com.mmt.guitarlab.domain.model.MetronomeSound
import com.mmt.guitarlab.ui.components.Studio3DAccent
import com.mmt.guitarlab.ui.components.Studio3DIconBadge
import com.mmt.guitarlab.ui.components.StudioCard
import com.mmt.guitarlab.ui.components.StudioPill
import com.mmt.guitarlab.core.ui.theme.ElectricAmber
import com.mmt.guitarlab.core.ui.theme.ElectricTeal
import com.mmt.guitarlab.core.ui.theme.GuitarLabTheme
import com.mmt.guitarlab.core.ui.theme.StudioCardBorder
import com.mmt.guitarlab.core.ui.theme.StudioDarkBg
import com.mmt.guitarlab.core.ui.theme.StudioTextMuted
import com.mmt.guitarlab.core.ui.theme.StudioTextPrimary
import com.mmt.guitarlab.core.ui.theme.StudioTextSecondary

@Composable
fun SoundAndVibrationCard(
    config: MetronomeConfig,
    onSetVibrateOnly: (Boolean) -> Unit,
    onSetSound: (MetronomeSound) -> Unit,
) {
    StudioCard(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            // Vibration Mode Toggle Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Studio3DIconBadge(
                        icon = Icons.Default.Vibration,
                        contentDescription = "Вибрация",
                        size = 32.dp,
                        accent = if (config.vibrateOnly) Studio3DAccent.TEAL else Studio3DAccent.SLATE,
                    )
                    Column {
                        Text(
                            text = "РЕЖИМ ВИБРАЦИИ",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = StudioTextPrimary,
                        )
                        Text(
                            text = if (config.vibrateOnly) "Тактильные удары вместо звука" else "Звуковые клики включены",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (config.vibrateOnly) ElectricTeal else StudioTextSecondary,
                        )
                    }
                }

                Switch(
                    checked = config.vibrateOnly,
                    onCheckedChange = { onSetVibrateOnly(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = ElectricTeal,
                        checkedTrackColor = Color(0xFF134E4A),
                        uncheckedThumbColor = StudioTextMuted,
                        uncheckedTrackColor = StudioCardBorder,
                    ),
                )
            }

            if (!config.vibrateOnly) {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "ВЫБОР ЗВУКА МЕТРОНОМА",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = StudioTextMuted,
                    letterSpacing = 1.sp,
                )
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    MetronomeSound.entries.forEach { sound ->
                        val isSelected = config.sound == sound
                        StudioPill(
                            text = sound.label,
                            selected = isSelected,
                            onClick = { onSetSound(sound) },
                            accentColor = ElectricAmber,
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun SoundAndVibrationCardPreview() {
    GuitarLabTheme {
        Column(
            modifier = Modifier
                .background(StudioDarkBg)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            SoundAndVibrationCard(
                config = MetronomeConfig(vibrateOnly = false, sound = MetronomeSound.WOODBLOCK),
                onSetVibrateOnly = {},
                onSetSound = {},
            )
            SoundAndVibrationCard(
                config = MetronomeConfig(vibrateOnly = true),
                onSetVibrateOnly = {},
                onSetSound = {},
            )
        }
    }
}