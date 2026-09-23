package com.mmt.guitarlab.ui.drums

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mmt.guitarlab.domain.model.DrumPattern
import com.mmt.guitarlab.domain.model.DrumSound
import com.mmt.guitarlab.ui.components.Studio3DAccent
import com.mmt.guitarlab.ui.components.Studio3DIconBadge
import com.mmt.guitarlab.ui.theme.ElectricAmber
import com.mmt.guitarlab.ui.theme.ElectricTeal
import com.mmt.guitarlab.ui.theme.StudioCardBg
import com.mmt.guitarlab.ui.theme.StudioCardBorder
import com.mmt.guitarlab.ui.theme.StudioCardElevated
import com.mmt.guitarlab.ui.theme.StudioDarkBg
import com.mmt.guitarlab.ui.theme.StudioTextMuted
import com.mmt.guitarlab.ui.theme.StudioTextPrimary
import com.mmt.guitarlab.ui.theme.StudioTextSecondary

@Composable
fun DrumsScreen(
    viewModel: DrumsViewModel = hiltViewModel(),
) {
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val currentStep by viewModel.currentStep.collectAsStateWithLifecycle()
    val bpm by viewModel.bpm.collectAsStateWithLifecycle()
    val volume by viewModel.volume.collectAsStateWithLifecycle()
    val swing by viewModel.swing.collectAsStateWithLifecycle()
    val pattern by viewModel.pattern.collectAsStateWithLifecycle()
    val patterns = viewModel.availablePatterns

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Studio3DIconBadge(
                icon = Icons.Default.Album,
                contentDescription = "Drum Grooves",
                size = 46.dp,
                accent = Studio3DAccent.AMBER,
            )
            Spacer(Modifier.width(14.dp))
            Column {
                Text(
                    text = "Drum Grooves",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = StudioTextPrimary,
                )
                Text(
                    text = "Rhythm Practice & Jam Engine",
                    style = MaterialTheme.typography.labelSmall,
                    color = ElectricTeal,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Preset style chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            patterns.forEach { pat ->
                val selected = pat.id == pattern.id
                FilterChip(
                    selected = selected,
                    onClick = { viewModel.selectPattern(pat) },
                    label = {
                        Text(
                            text = pat.name,
                            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ElectricAmber,
                        selectedLabelColor = Color.Black,
                        containerColor = StudioCardElevated,
                        labelColor = StudioTextSecondary,
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = if (selected) ElectricAmber else StudioCardBorder,
                        enabled = true,
                        selected = selected,
                    ),
                    shape = RoundedCornerShape(12.dp),
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // Big Play/Stop & BPM Display Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(StudioCardBg)
                .border(1.dp, StudioCardBorder, RoundedCornerShape(20.dp))
                .padding(20.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Text(
                            text = pattern.style.uppercase(),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = ElectricAmber,
                            letterSpacing = 1.sp,
                        )
                        Text(
                            text = "$bpm BPM",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Black,
                            color = StudioTextPrimary,
                        )
                    }

                    // Big glowing play button
                    val playBg = if (isPlaying) ElectricAmber else Color(0xFF2A2A2A)
                    val playIconColor = if (isPlaying) Color.Black else StudioTextPrimary
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .shadow(if (isPlaying) 16.dp else 4.dp, CircleShape, spotColor = ElectricAmber)
                            .clip(CircleShape)
                            .background(playBg)
                            .border(2.dp, if (isPlaying) ElectricAmber else StudioCardBorder, CircleShape)
                            .clickable { viewModel.togglePlay() },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Stop" else "Play",
                            tint = playIconColor,
                            modifier = Modifier.size(36.dp),
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                // BPM adjustment controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    IconButton(
                        onClick = { viewModel.adjustBpm(-5) },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(StudioCardElevated),
                    ) {
                        Icon(Icons.Default.Remove, "Decrease BPM", tint = StudioTextPrimary)
                    }

                    Slider(
                        value = bpm.toFloat(),
                        onValueChange = { viewModel.setBpm(it.toInt()) },
                        valueRange = 30f..300f,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 12.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = ElectricAmber,
                            activeTrackColor = ElectricAmber,
                            inactiveTrackColor = StudioCardBorder,
                        ),
                    )

                    IconButton(
                        onClick = { viewModel.adjustBpm(5) },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(StudioCardElevated),
                    ) {
                        Icon(Icons.Default.Add, "Increase BPM", tint = StudioTextPrimary)
                    }

                    Spacer(Modifier.width(8.dp))

                    // Tap Tempo button
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(StudioCardElevated)
                            .border(1.dp, StudioCardBorder, RoundedCornerShape(12.dp))
                            .clickable { viewModel.onTapTempo() }
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.TouchApp, null, tint = ElectricTeal, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "TAP",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = StudioTextPrimary,
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // 16-step sequencer matrix card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(StudioCardBg)
                .border(1.dp, StudioCardBorder, RoundedCornerShape(20.dp))
                .padding(16.dp),
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "16-STEP SEQUENCER",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = StudioTextMuted,
                        letterSpacing = 1.sp,
                    )
                    Text(
                        text = "Tap cell to toggle beat",
                        style = MaterialTheme.typography.labelSmall,
                        color = StudioTextSecondary,
                    )
                }

                Spacer(Modifier.height(12.dp))

                val displayedSounds = listOf(
                    DrumSound.CRASH,
                    DrumSound.HIHAT_OPEN,
                    DrumSound.HIHAT_CLOSED,
                    DrumSound.TOM_LOW,
                    DrumSound.SNARE,
                    DrumSound.KICK,
                )

                // Step headers (1..16)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(modifier = Modifier.width(52.dp))
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        for (i in 0 until 16) {
                            val isCur = isPlaying && currentStep == i
                            val isBeat = i % 4 == 0
                            Text(
                                text = if (isBeat) "${(i / 4) + 1}" else "·",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isCur || isBeat) FontWeight.Bold else FontWeight.Normal,
                                color = if (isCur) ElectricAmber else if (isBeat) StudioTextPrimary else StudioTextMuted,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f),
                                fontSize = if (isBeat) 11.sp else 9.sp,
                            )
                        }
                    }
                }

                Spacer(Modifier.height(6.dp))

                // Drum rows
                displayedSounds.forEach { sound ->
                    val rowHits = pattern.grid[sound] ?: BooleanArray(16) { false }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // Sound label / preview
                        Text(
                            text = sound.shortName,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = StudioTextSecondary,
                            modifier = Modifier
                                .width(52.dp)
                                .clickable { viewModel.previewSound(sound) },
                        )

                        // 16 step boxes
                        Row(
                            modifier = Modifier.weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(3.dp),
                        ) {
                            for (step in 0 until 16) {
                                val active = rowHits.getOrNull(step) == true
                                val isCur = isPlaying && currentStep == step
                                val isDownbeat = step % 4 == 0

                                val cellColor = when {
                                    isCur && active -> ElectricAmber
                                    active -> Color(0xFFD97706) // Darker Amber
                                    isCur -> Color(0xFF4A3B18)
                                    isDownbeat -> Color(0xFF242424)
                                    else -> Color(0xFF1A1A1A)
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(24.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(cellColor)
                                        .border(
                                            1.dp,
                                            if (isCur) ElectricAmber else StudioCardBorder,
                                            RoundedCornerShape(4.dp)
                                        )
                                        .clickable { viewModel.toggleStep(sound, step) },
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Sound audition pads
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(StudioCardBg)
                .border(1.dp, StudioCardBorder, RoundedCornerShape(20.dp))
                .padding(16.dp),
        ) {
            Column {
                Text(
                    text = "AUDITION PADS",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = StudioTextMuted,
                    letterSpacing = 1.sp,
                )
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    val pads = listOf(
                        DrumSound.KICK to "KICK",
                        DrumSound.SNARE to "SNARE",
                        DrumSound.HIHAT_CLOSED to "HI-HAT",
                        DrumSound.TOM_LOW to "TOM",
                        DrumSound.CRASH to "CRASH",
                    )
                    pads.forEach { (sound, label) ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(56.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(StudioCardElevated)
                                .border(1.dp, StudioCardBorder, RoundedCornerShape(12.dp))
                                .clickable { viewModel.previewSound(sound) },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = ElectricAmber,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Swing & Volume Sliders Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(StudioCardBg)
                .border(1.dp, StudioCardBorder, RoundedCornerShape(20.dp))
                .padding(16.dp),
        ) {
            Column {
                // Swing
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "Groove / Swing",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = StudioTextPrimary,
                    )
                    Text(
                        text = "${(swing * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = ElectricTeal,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Slider(
                    value = swing,
                    onValueChange = { viewModel.setSwing(it) },
                    valueRange = -0.3f..0.3f,
                    colors = SliderDefaults.colors(
                        thumbColor = ElectricTeal,
                        activeTrackColor = ElectricTeal,
                        inactiveTrackColor = StudioCardBorder,
                    ),
                )

                Spacer(Modifier.height(8.dp))

                // Volume
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = "Master Volume",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.SemiBold,
                        color = StudioTextPrimary,
                    )
                    Text(
                        text = "${(volume * 100).toInt()}%",
                        style = MaterialTheme.typography.bodySmall,
                        color = ElectricAmber,
                        fontWeight = FontWeight.Bold,
                    )
                }
                Slider(
                    value = volume,
                    onValueChange = { viewModel.setVolume(it) },
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(
                        thumbColor = ElectricAmber,
                        activeTrackColor = ElectricAmber,
                        inactiveTrackColor = StudioCardBorder,
                    ),
                )
            }
        }
    }
}
