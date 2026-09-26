package com.mmt.guitarlab.ui.tab

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mmt.guitarlab.domain.model.InstrumentType
import com.mmt.guitarlab.domain.model.TabScore
import com.mmt.guitarlab.domain.model.TabTrack
import com.mmt.guitarlab.domain.model.TuxGuitarSoundBank
import com.mmt.guitarlab.core.ui.theme.ElectricAmber
import com.mmt.guitarlab.core.ui.theme.GuitarLabTheme
import com.mmt.guitarlab.core.ui.theme.StudioCardBg
import com.mmt.guitarlab.core.ui.theme.StudioDarkBg
import com.mmt.guitarlab.core.ui.theme.StudioTextPrimary
import com.mmt.guitarlab.core.ui.theme.StudioTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
 fun MixerDialog(
    score: TabScore,
    currentSoundBank: TuxGuitarSoundBank,
    onOpenSoundBank: () -> Unit,
    onVolumeChange: (index: Int, volume: Float) -> Unit,
    onPanChange: (index: Int, pan: Float) -> Unit,
    onMuteToggle: (index: Int) -> Unit,
    onSoloToggle: (index: Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = StudioCardBg,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "TuxGuitar Audio Mixer",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = StudioTextPrimary,
                )
                TextButton(onClick = onDismiss) {
                    Text("Close", color = ElectricAmber, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(16.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp)
                        .clickable(onClick = onOpenSoundBank),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF242933)),
                    border = BorderStroke(1.dp, Color(0xFFF59E0B)),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text(
                                text = "Gervill SoundBank / Timbre",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF9CA3AF),
                            )
                            Text(
                                text = currentSoundBank.displayName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFBBF24),
                            )
                        }
                        Icon(Icons.Default.GraphicEq, contentDescription = null, tint = Color(0xFFF59E0B))
                    }
                }

                score.tracks.forEachIndexed { idx, track ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = "${track.name} (${track.instrumentType.displayName})",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                )
                                Row {
                                    FilterChip(
                                        selected = track.isMuted,
                                        onClick = { onMuteToggle(idx) },
                                        label = { Text("M") },
                                    )
                                    Spacer(Modifier.width(4.dp))
                                    FilterChip(
                                        selected = track.isSolo,
                                        onClick = { onSoloToggle(idx) },
                                        label = { Text("S") },
                                    )
                                }
                            }

                            Spacer(Modifier.height(4.dp))

                            Text("Volume: ${(track.volume * 100).toInt()}%", style = MaterialTheme.typography.labelSmall)
                            Slider(
                                value = track.volume,
                                onValueChange = { onVolumeChange(idx, it) },
                                valueRange = 0f..1f,
                            )

                            Text(
                                "Pan: ${if (track.pan < 0) "L ${(track.pan * -100).toInt()}%" else if (track.pan > 0) "R ${(track.pan * 100).toInt()}%" else "Center"}",
                                style = MaterialTheme.typography.labelSmall
                            )
                            Slider(
                                value = track.pan,
                                onValueChange = { onPanChange(idx, it) },
                                valueRange = -1f..1f,
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun MixerDialogPreview() {
    GuitarLabTheme {
        Box(
            modifier = Modifier
                .background(StudioDarkBg)
                .fillMaxWidth()
                .height(600.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            MixerDialog(
                score = TabScore(
                    tracks = listOf(
                        TabTrack(
                            name = "Lead Guitar",
                            instrumentType = InstrumentType.GUITAR,
                            volume = 0.8f,
                            pan = 0.2f,
                            isMuted = false,
                            isSolo = true
                        ),
                        TabTrack(
                            name = "Bass",
                            instrumentType = InstrumentType.BASS_5,
                            volume = 0.9f,
                            pan = 0.0f,
                            isMuted = false,
                            isSolo = false
                        )
                    )
                ),
                currentSoundBank = TuxGuitarSoundBank.MODERN_METAL,
                onOpenSoundBank = {},
                onVolumeChange = { _, _ -> },
                onPanChange = { _, _ -> },
                onMuteToggle = {},
                onSoloToggle = {},
                onDismiss = {}
            )
        }
    }
}
