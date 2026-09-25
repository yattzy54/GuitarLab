package com.mmt.guitarlab.ui.drums

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
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mmt.guitarlab.domain.model.DrumKit
import com.mmt.guitarlab.domain.model.DrumPattern
import com.mmt.guitarlab.domain.model.DrumSound
import com.mmt.guitarlab.ui.components.Studio3DAccent
import com.mmt.guitarlab.ui.components.Studio3DIconBadge
import com.mmt.guitarlab.ui.components.StudioCard
import com.mmt.guitarlab.ui.theme.ElectricAmber
import com.mmt.guitarlab.ui.theme.ElectricGreen
import com.mmt.guitarlab.ui.theme.ElectricRuby
import com.mmt.guitarlab.ui.theme.ElectricTeal
import com.mmt.guitarlab.ui.theme.GuitarLabTheme
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
    val currentKit by viewModel.drumKit.collectAsStateWithLifecycle()
    val patterns = viewModel.availablePatterns

    DrumsContent(
        isPlaying = isPlaying,
        currentStep = currentStep,
        bpm = bpm,
        volume = volume,
        swing = swing,
        pattern = pattern,
        currentKit = currentKit,
        patterns = patterns,
        onTogglePlay = viewModel::togglePlay,
        onAdjustBpm = viewModel::adjustBpm,
        onSetBpm = viewModel::setBpm,
        onTapTempo = viewModel::onTapTempo,
        onPreviewSound = viewModel::previewSound,
        onToggleStep = viewModel::toggleStep,
        onClearPattern = viewModel::clearPattern,
        onRandomizePattern = viewModel::randomizePattern,
        onResetPattern = viewModel::resetPattern,
        onSetSwing = viewModel::setSwing,
        onSetVolume = viewModel::setVolume,
        onSetDrumKit = viewModel::setDrumKit,
        onSelectPattern = viewModel::selectPattern,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrumsContent(
    isPlaying: Boolean,
    currentStep: Int,
    bpm: Int,
    volume: Float,
    swing: Float,
    pattern: DrumPattern,
    currentKit: DrumKit,
    patterns: List<DrumPattern>,
    onTogglePlay: () -> Unit = {},
    onAdjustBpm: (Int) -> Unit = {},
    onSetBpm: (Int) -> Unit = {},
    onTapTempo: () -> Unit = {},
    onPreviewSound: (DrumSound) -> Unit = {},
    onToggleStep: (DrumSound, Int) -> Unit = { _, _ -> },
    onClearPattern: () -> Unit = {},
    onRandomizePattern: () -> Unit = {},
    onResetPattern: () -> Unit = {},
    onSetSwing: (Float) -> Unit = {},
    onSetVolume: (Float) -> Unit = {},
    onSetDrumKit: (DrumKit) -> Unit = {},
    onSelectPattern: (DrumPattern) -> Unit = {},
) {
    var showKitBottomSheet by remember { mutableStateOf(false) }
    var showPresetBottomSheet by remember { mutableStateOf(false) }

    val kitSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val presetSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
                    text = "Drum Machine",
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

        Spacer(Modifier.height(14.dp))

        // DRUM KIT & PRESET SELECTOR CARDS (Bottom Sheet Triggers)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            // Kit Selector Card
            StudioCard(
                modifier = Modifier
                    .weight(1f)
                    .clickable { showKitBottomSheet = true },
                accentBorder = ElectricAmber.copy(alpha = 0.5f),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = currentKit.iconEmoji,
                        fontSize = 22.sp,
                    )
                    Spacer(Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "DRUM KIT",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = ElectricAmber,
                            fontSize = 9.5.sp,
                            letterSpacing = 0.8.sp,
                        )
                        Text(
                            text = currentKit.displayName,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = StudioTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Change Kit",
                        tint = StudioTextMuted,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }

            // Preset Selector Card
            StudioCard(
                modifier = Modifier
                    .weight(1f)
                    .clickable { showPresetBottomSheet = true },
                accentBorder = ElectricTeal.copy(alpha = 0.5f),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(ElectricTeal.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = ElectricTeal,
                            modifier = Modifier.size(18.dp),
                        )
                    }
                    Spacer(Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "GROOVE PRESET",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = ElectricTeal,
                            fontSize = 9.5.sp,
                            letterSpacing = 0.8.sp,
                        )
                        Text(
                            text = pattern.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = StudioTextPrimary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Browse Presets",
                        tint = StudioTextMuted,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        // Transport & BPM Control Card
        StudioCard(
            modifier = Modifier.fillMaxWidth(),
            accentBorder = if (isPlaying) ElectricAmber else StudioCardBorder,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(ElectricAmber.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp),
                            ) {
                                Text(
                                    text = pattern.style.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = ElectricAmber,
                                    fontSize = 10.sp,
                                )
                            }
                            if (swing != 0f) {
                                Spacer(Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(ElectricTeal.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp),
                                ) {
                                    Text(
                                        text = "SWING ${(swing * 100).toInt()}%",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = ElectricTeal,
                                        fontSize = 10.sp,
                                    )
                                }
                            }
                        }
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "$bpm BPM",
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Black,
                            color = StudioTextPrimary,
                        )
                    }

                    // Big glowing play/stop button
                    val playBg = if (isPlaying) ElectricAmber else StudioCardElevated
                    val playIconColor = if (isPlaying) Color.Black else StudioTextPrimary
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .shadow(if (isPlaying) 16.dp else 4.dp, CircleShape, spotColor = ElectricAmber)
                            .clip(CircleShape)
                            .background(playBg)
                            .border(2.dp, if (isPlaying) ElectricAmber else StudioCardBorder, CircleShape)
                            .clickable { onTogglePlay() },
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

                Spacer(Modifier.height(14.dp))

                // BPM adjustment controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    IconButton(
                        onClick = { onAdjustBpm(-5) },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(StudioCardElevated),
                    ) {
                        Icon(Icons.Default.Remove, "Decrease BPM", tint = StudioTextPrimary)
                    }

                    Slider(
                        value = bpm.toFloat(),
                        onValueChange = { onSetBpm(it.toInt()) },
                        valueRange = 30f..280f,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = 10.dp),
                        colors = SliderDefaults.colors(
                            thumbColor = ElectricAmber,
                            activeTrackColor = ElectricAmber,
                            inactiveTrackColor = StudioCardBorder,
                        ),
                    )

                    IconButton(
                        onClick = { onAdjustBpm(5) },
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
                            .clickable { onTapTempo() }
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

                Spacer(Modifier.height(10.dp))

                // Quick BPM presets
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    listOf(60, 80, 100, 120, 140, 160, 180).forEach { quickBpm ->
                        val isSelected = bpm == quickBpm
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) ElectricAmber.copy(alpha = 0.25f) else StudioCardElevated)
                                .border(
                                    1.dp,
                                    if (isSelected) ElectricAmber else StudioCardBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { onSetBpm(quickBpm) }
                                .padding(horizontal = 10.dp, vertical = 5.dp),
                        ) {
                            Text(
                                text = "$quickBpm",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) ElectricAmber else StudioTextSecondary,
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // 16-step sequencer matrix card
        StudioCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = ElectricAmber,
                            modifier = Modifier.size(18.dp),
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "16-STEP SEQUENCER",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = StudioTextMuted,
                            letterSpacing = 1.sp,
                        )
                    }
                    Text(
                        text = "Tap cell to toggle beat",
                        style = MaterialTheme.typography.labelSmall,
                        color = StudioTextSecondary,
                        fontSize = 11.sp,
                    )
                }

                Spacer(Modifier.height(12.dp))

                val displayedSounds = listOf(
                    DrumSound.CRASH to ("CR" to Color(0xFFA855F7)),
                    DrumSound.HIHAT_OPEN to ("OH" to Color(0xFF00E676)),
                    DrumSound.HIHAT_CLOSED to ("CH" to ElectricTeal),
                    DrumSound.TOM_LOW to ("TOM" to Color(0xFF3B82F6)),
                    DrumSound.SNARE to ("SN" to ElectricRuby),
                    DrumSound.KICK to ("KICK" to ElectricAmber),
                )

                // Step headers (1..16)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(modifier = Modifier.width(48.dp))
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
                displayedSounds.forEach { (sound, info) ->
                    val (label, soundColor) = info
                    val rowHits = pattern.grid[sound] ?: BooleanArray(16) { false }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        // Sound label / preview
                        Text(
                            text = label,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = soundColor,
                            modifier = Modifier
                                .width(48.dp)
                                .clickable { onPreviewSound(sound) },
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
                                    isCur && active -> soundColor
                                    active -> soundColor.copy(alpha = 0.75f)
                                    isCur -> soundColor.copy(alpha = 0.25f)
                                    isDownbeat -> Color(0xFF242B3A)
                                    else -> Color(0xFF161A24)
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(26.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(cellColor)
                                        .border(
                                            width = if (isCur) 1.5.dp else 1.dp,
                                            color = if (isCur) soundColor else StudioCardBorder,
                                            shape = RoundedCornerShape(4.dp)
                                        )
                                        .clickable { onToggleStep(sound, step) },
                                )
                            }
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Sequencer Action Bar (Clear, Randomize, Reset)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    // Clear
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(StudioCardElevated)
                            .border(1.dp, StudioCardBorder, RoundedCornerShape(10.dp))
                            .clickable { onClearPattern() }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Clear, null, tint = StudioTextMuted, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "Clear",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = StudioTextSecondary,
                            )
                        }
                    }

                    // Randomize
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(StudioCardElevated)
                            .border(1.dp, StudioCardBorder, RoundedCornerShape(10.dp))
                            .clickable { onRandomizePattern() }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, null, tint = ElectricAmber, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "Random",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = ElectricAmber,
                            )
                        }
                    }

                    // Reset
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(StudioCardElevated)
                            .border(1.dp, StudioCardBorder, RoundedCornerShape(10.dp))
                            .clickable { onResetPattern() }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Refresh, null, tint = ElectricTeal, modifier = Modifier.size(14.dp))
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = "Reset",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = ElectricTeal,
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Audition Pads / Finger Drumming
        StudioCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "AUDITION PADS",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = StudioTextMuted,
                    letterSpacing = 1.sp,
                )
                Spacer(Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    val pads = listOf(
                        DrumSound.KICK to ("KICK" to ElectricAmber),
                        DrumSound.SNARE to ("SNARE" to ElectricRuby),
                        DrumSound.HIHAT_CLOSED to ("HAT" to ElectricTeal),
                        DrumSound.TOM_LOW to ("TOM" to Color(0xFF3B82F6)),
                        DrumSound.CRASH to ("CRASH" to Color(0xFFA855F7)),
                    )
                    pads.forEach { (sound, info) ->
                        val (label, color) = info
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(StudioCardElevated)
                                .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .clickable { onPreviewSound(sound) },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = color,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        // Groove & Master Controls Card
        StudioCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Groove / Swing Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Groove / Swing",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = StudioTextPrimary,
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = if (swing == 0f) "(Straight)" else if (swing > 0) "(Shuffle)" else "(Push)",
                            style = MaterialTheme.typography.labelSmall,
                            color = StudioTextMuted,
                        )
                    }
                    Text(
                        text = "${(swing * 100).toInt()}%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ElectricTeal,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Slider(
                    value = swing,
                    onValueChange = { onSetSwing(it) },
                    valueRange = -0.3f..0.5f,
                    colors = SliderDefaults.colors(
                        thumbColor = ElectricTeal,
                        activeTrackColor = ElectricTeal,
                        inactiveTrackColor = StudioCardBorder,
                    ),
                )

                // Quick Swing presets
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    val swingPresets = listOf(
                        "Straight" to 0f,
                        "Light" to 0.18f,
                        "Triplet" to 0.33f,
                        "Heavy" to 0.48f,
                    )
                    swingPresets.forEach { (label, swVal) ->
                        val isSel = kotlin.math.abs(swing - swVal) < 0.05f
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) ElectricTeal.copy(alpha = 0.22f) else StudioCardElevated)
                                .border(
                                    1.dp,
                                    if (isSel) ElectricTeal else StudioCardBorder,
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { onSetSwing(swVal) }
                                .padding(vertical = 6.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSel) ElectricTeal else StudioTextSecondary,
                                fontSize = 10.5.sp,
                            )
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Master Volume
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.AutoMirrored.Filled.VolumeUp, null, tint = ElectricAmber, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = "Master Volume",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = StudioTextPrimary,
                        )
                    }
                    Text(
                        text = "${(volume * 100).toInt()}%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = ElectricAmber,
                        fontWeight = FontWeight.Bold,
                    )
                }

                Slider(
                    value = volume,
                    onValueChange = { onSetVolume(it) },
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

    // MODAL BOTTOM SHEET 1: DRUM KIT SELECTOR
    if (showKitBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showKitBottomSheet = false },
            sheetState = kitSheetState,
            containerColor = StudioCardBg,
            contentColor = StudioTextPrimary,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 28.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Studio3DIconBadge(
                        icon = Icons.Default.Album,
                        contentDescription = null,
                        size = 40.dp,
                        accent = Studio3DAccent.AMBER,
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "SELECT DRUM KIT",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = StudioTextPrimary,
                        )
                        Text(
                            text = "Choose acoustic & electronic drum sound profiles",
                            style = MaterialTheme.typography.labelSmall,
                            color = StudioTextSecondary,
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    DrumKit.entries.forEach { kit ->
                        val isSelected = kit == currentKit
                        val borderCol = if (isSelected) ElectricAmber else StudioCardBorder
                        val bgCol = if (isSelected) ElectricAmber.copy(alpha = 0.12f) else StudioCardElevated

                        StudioCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSetDrumKit(kit)
                                    showKitBottomSheet = false
                                },
                            accentBorder = borderCol,
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(bgCol)
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = kit.iconEmoji,
                                    fontSize = 28.sp,
                                )
                                Spacer(Modifier.width(14.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = kit.displayName,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = StudioTextPrimary,
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(StudioCardBorder)
                                                .padding(horizontal = 6.dp, vertical = 2.dp),
                                        ) {
                                            Text(
                                                text = kit.styleCategory,
                                                style = MaterialTheme.typography.labelSmall,
                                                color = StudioTextSecondary,
                                                fontSize = 9.5.sp,
                                            )
                                        }
                                    }
                                    Spacer(Modifier.height(2.dp))
                                    Text(
                                        text = kit.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = StudioTextMuted,
                                        fontSize = 12.sp,
                                    )
                                }

                                Spacer(Modifier.width(8.dp))

                                // Audition sample button
                                IconButton(
                                    onClick = {
                                        onSetDrumKit(kit)
                                        onPreviewSound(DrumSound.KICK)
                                        onPreviewSound(DrumSound.SNARE)
                                    },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(StudioCardBorder),
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                                        contentDescription = "Audition Kit",
                                        tint = ElectricAmber,
                                        modifier = Modifier.size(18.dp),
                                    )
                                }

                                if (isSelected) {
                                    Spacer(Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = ElectricAmber,
                                        modifier = Modifier.size(22.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // MODAL BOTTOM SHEET 2: PRESET GROOVE SELECTOR
    if (showPresetBottomSheet) {
        var selectedStyleFilter by remember { mutableStateOf("All") }

        ModalBottomSheet(
            onDismissRequest = { showPresetBottomSheet = false },
            sheetState = presetSheetState,
            containerColor = StudioCardBg,
            contentColor = StudioTextPrimary,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .padding(bottom = 28.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Studio3DIconBadge(
                        icon = Icons.Default.MusicNote,
                        contentDescription = null,
                        size = 40.dp,
                        accent = Studio3DAccent.TEAL,
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "GROOVE PRESET CATALOG",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = StudioTextPrimary,
                        )
                        Text(
                            text = "Choose drum rhythm patterns for practice & jamming",
                            style = MaterialTheme.typography.labelSmall,
                            color = StudioTextSecondary,
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Style Filters
                val styleCategories = listOf("All", "Rock", "Metal", "Blues", "Funk", "Jazz", "Reggae", "Latin", "Pop", "Hip Hop")
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    styleCategories.forEach { style ->
                        val isSel = selectedStyleFilter == style
                        FilterChip(
                            selected = isSel,
                            onClick = { selectedStyleFilter = style },
                            label = { Text(style) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ElectricTeal,
                                selectedLabelColor = Color.Black,
                                containerColor = StudioCardElevated,
                                labelColor = StudioTextSecondary,
                            ),
                            border = FilterChipDefaults.filterChipBorder(
                                borderColor = if (isSel) ElectricTeal else StudioCardBorder,
                                enabled = true,
                                selected = isSel,
                            ),
                            shape = RoundedCornerShape(10.dp),
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                val filteredPatterns = remember(selectedStyleFilter, patterns) {
                    if (selectedStyleFilter == "All") patterns
                    else patterns.filter { it.style.equals(selectedStyleFilter, ignoreCase = true) }
                }

                Column(
                    modifier = Modifier
                        .height(380.dp)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    filteredPatterns.forEach { pat ->
                        val isSelected = pat.id == pattern.id
                        val borderCol = if (isSelected) ElectricTeal else StudioCardBorder
                        val bgCol = if (isSelected) ElectricTeal.copy(alpha = 0.12f) else StudioCardElevated

                        StudioCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSelectPattern(pat)
                                    showPresetBottomSheet = false
                                },
                            accentBorder = borderCol,
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(bgCol)
                                    .padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = pat.name,
                                            style = MaterialTheme.typography.titleSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = StudioTextPrimary,
                                        )
                                        Spacer(Modifier.width(8.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(ElectricTeal.copy(alpha = 0.2f))
                                                .padding(horizontal = 6.dp, vertical = 2.dp),
                                        ) {
                                            Text(
                                                text = pat.style,
                                                style = MaterialTheme.typography.labelSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = ElectricTeal,
                                                fontSize = 9.5.sp,
                                            )
                                        }
                                    }

                                    Spacer(Modifier.height(4.dp))

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Speed,
                                            contentDescription = null,
                                            tint = StudioTextMuted,
                                            modifier = Modifier.size(13.dp),
                                        )
                                        Spacer(Modifier.width(4.dp))
                                        Text(
                                            text = "Default: ${pat.defaultBpm} BPM",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = StudioTextMuted,
                                            fontSize = 11.5.sp,
                                        )
                                    }
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = ElectricTeal,
                                        modifier = Modifier.size(22.dp),
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DrumsScreenPreview() {
    GuitarLabTheme {
        DrumsContent(
            isPlaying = false,
            currentStep = 0,
            bpm = 120,
            volume = 0.85f,
            swing = 0.15f,
            pattern = DrumPattern.DEFAULT_PATTERNS.first(),
            currentKit = DrumKit.ROCK,
            patterns = DrumPattern.DEFAULT_PATTERNS,
        )
    }
}
