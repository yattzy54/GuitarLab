package com.mmt.guitarlab.ui.metronome.components

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mmt.guitarlab.domain.model.MetronomeSound
import com.mmt.guitarlab.ui.components.StudioCard
import com.mmt.guitarlab.ui.components.StudioPill
import com.mmt.guitarlab.core.ui.theme.ElectricAmber
import com.mmt.guitarlab.core.ui.theme.GuitarLabTheme
import com.mmt.guitarlab.core.ui.theme.StudioDarkBg
import com.mmt.guitarlab.core.ui.theme.StudioTextMuted

@Composable
 fun SoundSelectionCard(
    selectedSound: MetronomeSound,
    onSetSound: (MetronomeSound) -> Unit,
) {
    StudioCard(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
        ) {
            Text(
                text = "METRONOME SOUND",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = StudioTextMuted,
                letterSpacing = 1.sp,
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                MetronomeSound.entries.forEach { sound ->
                    val isSelected = selectedSound == sound
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

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun SoundSelectionCardPreview() {
    GuitarLabTheme {
        Column(
            modifier = Modifier
                .background(StudioDarkBg)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SoundSelectionCard(
                selectedSound = MetronomeSound.WOODBLOCK,
                onSetSound = {}
            )
        }
    }
}
