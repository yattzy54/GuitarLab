package com.mmt.guitarlab.ui.tab

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mmt.guitarlab.domain.model.TabScore
import com.mmt.guitarlab.domain.model.TabTrack
import com.mmt.guitarlab.ui.tab.components.AudioSourceMode
import com.mmt.guitarlab.ui.tab.components.SongsterrBottomControlBar
import com.mmt.guitarlab.ui.tab.components.TabCanvasRenderer
import com.mmt.guitarlab.ui.tab.components.sheets.ChromaticTunerBottomSheet
import com.mmt.guitarlab.ui.tab.components.sheets.MixerBottomSheet
import com.mmt.guitarlab.ui.tab.components.sheets.MoreOptionsBottomSheet
import com.mmt.guitarlab.ui.tab.components.sheets.TempoBottomSheet
import com.mmt.guitarlab.ui.tab.components.sheets.TranspositionBottomSheet
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongsterrTabPlayerScreen(
    viewModel: TabViewModel,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val scoreState by viewModel.score.collectAsState()
    val score = scoreState ?: return

    var activeTrackId by remember { mutableStateOf(score.tracks.firstOrNull()?.id ?: "") }
    val activeTrack = score.tracks.find { it.id == activeTrackId } ?: score.tracks.first()

    // Playback state
    var isPlaying by remember { mutableStateOf(false) }
    var speedRatio by remember { mutableFloatStateOf(1.0f) }
    var audioSource by remember { mutableStateOf(AudioSourceMode.SYNTH) }
    var currentMeasureIndex by remember { mutableIntStateOf(0) }
    var currentBeatIndex by remember { mutableIntStateOf(0) }

    // Loop state
    var isLoopActive by remember { mutableStateOf(false) }
    var loopRange by remember { mutableStateOf<Pair<Int, Int>?>(Pair(1, 4)) }

    // Transposition state
    var semitones by remember { mutableIntStateOf(0) }
    var tuningOverrideName by remember { mutableStateOf<String?>(null) }

    // Count in & metronome
    var countInEnabled by remember { mutableStateOf(true) }
    var metronomeClickEnabled by remember { mutableStateOf(false) }
    var isCountingIn by remember { mutableStateOf(false) }
    var countInBeat by remember { mutableIntStateOf(1) }

    // Bottom Sheets states
    var isTranspositionOpen by remember { mutableStateOf(false) }
    val transpositionSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var isTunerOpen by remember { mutableStateOf(false) }
    val tunerSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var isMixerOpen by remember { mutableStateOf(false) }
    val mixerSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var isTempoOpen by remember { mutableStateOf(false) }
    val tempoSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var isMoreOpen by remember { mutableStateOf(false) }
    val moreSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Playback Coroutine
    LaunchedEffect(isPlaying, isCountingIn, speedRatio, isLoopActive, loopRange) {
        if (isPlaying && !isCountingIn) {
            val stepDelayMs = ((60000 / (score.tempo * speedRatio)) * 0.5f).toLong().coerceAtLeast(60L)
            while (isActive && isPlaying) {
                delay(stepDelayMs)

                var mIdx = currentMeasureIndex
                var bIdx = currentBeatIndex

                val measure = activeTrack.measures.getOrNull(mIdx)
                if (measure != null && measure.beats.isNotEmpty()) {
                    bIdx++
                    if (bIdx >= measure.beats.size) {
                        bIdx = 0
                        mIdx++
                        if (isLoopActive && loopRange != null) {
                            val loopStart = loopRange!!.first - 1
                            val loopEnd = loopRange!!.second - 1
                            if (mIdx > loopEnd || mIdx < loopStart) {
                                mIdx = loopStart
                            }
                        } else if (mIdx >= activeTrack.measures.size) {
                            mIdx = 0
                        }
                    }
                } else {
                    mIdx = (mIdx + 1) % activeTrack.measures.size.coerceAtLeast(1)
                    bIdx = 0
                }

                currentMeasureIndex = mIdx
                currentBeatIndex = bIdx
            }
        }
    }

    Scaffold(
        containerColor = Color(0xFF0D0F12),
        bottomBar = {
            SongsterrBottomControlBar(
                isPlaying = isPlaying,
                onTogglePlay = {
                    if (isPlaying || isCountingIn) {
                        isPlaying = false
                        isCountingIn = false
                    } else {
                        if (countInEnabled && currentMeasureIndex == 0 && currentBeatIndex == 0) {
                            scope.launch {
                                isCountingIn = true
                                for (beat in 1..4) {
                                    countInBeat = beat
                                    delay((60000 / (score.tempo * speedRatio)).toLong())
                                }
                                isCountingIn = false
                                isPlaying = true
                            }
                        } else {
                            isPlaying = true
                        }
                    }
                },
                speedRatio = speedRatio,
                audioSource = audioSource,
                onToggleAudioSource = { audioSource = it },
                isLoopActive = isLoopActive,
                onToggleLoop = {
                    isLoopActive = !isLoopActive
                    if (isLoopActive && loopRange == null) {
                        loopRange = Pair(1, 4)
                    }
                },
                onOpenMixer = { isMixerOpen = true },
                onOpenTempoPicker = { isTempoOpen = true },
                onOpenMoreMenu = { isMoreOpen = true },
                trackCount = score.tracks.size
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 1. TOP INFORMATION HEADER
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF12151A))
                    .border(1.dp, Color(0xFF1F242D))
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x26F59E0B))
                            .border(1.dp, Color(0x66F59E0B), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MusicNote,
                            contentDescription = null,
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${score.title} — ${score.artist}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black,
                                color = Color.White
                            )
                            Icon(
                                imageVector = Icons.Default.KeyboardArrowDown,
                                contentDescription = null,
                                tint = Color(0xFF9CA3AF),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Text(
                            text = "РЕВИЗИЯ ОТ: ${score.revisionDate} • ${activeTrack.name} (${tuningOverrideName ?: activeTrack.tuningName})",
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFFBBF24)
                        )
                    }
                }

                // Quick Tracks Button
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1E222A))
                        .border(1.dp, Color(0xFF2E333D), RoundedCornerShape(12.dp))
                        .clickable { isMixerOpen = true }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Партии (${score.tracks.size})",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE5E7EB)
                        )
                    }
                }
            }

            // Count-in Banner
            if (isCountingIn) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF10B981))
                        .padding(vertical = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "ОТСЧЁТ ПЕРЕД СТАРТОМ: $countInBeat / 4",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF09090B),
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // 2. TAB CANVAS (Maximized Canvas with scroll)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                TabCanvasRenderer(
                    track = activeTrack,
                    currentMeasureIndex = currentMeasureIndex,
                    currentBeatIndex = currentBeatIndex,
                    isPlaying = isPlaying,
                    loopRange = if (isLoopActive) loopRange else null,
                    onSelectPosition = { mIdx, bIdx ->
                        currentMeasureIndex = mIdx
                        currentBeatIndex = bIdx
                    }
                )
            }
        }
    }

    // MODAL BOTTOM SHEETS
    // 1. Transposition Sheet
    if (isTranspositionOpen) {
        TranspositionBottomSheet(
            sheetState = transpositionSheetState,
            onDismissRequest = { isTranspositionOpen = false },
            semitones = semitones,
            onSemitonesChange = { semitones = it },
            currentTuningName = tuningOverrideName ?: activeTrack.tuningName,
            onApplyPreset = {
                tuningOverrideName = it
                isTranspositionOpen = false
            }
        )
    }

    // 2. Chromatic Tuner Sheet
    if (isTunerOpen) {
        ChromaticTunerBottomSheet(
            sheetState = tunerSheetState,
            onDismissRequest = { isTunerOpen = false },
            targetTuningName = tuningOverrideName ?: activeTrack.tuningName,
            targetTuningNotes = activeTrack.tuningNotes.ifEmpty { listOf("D4", "A3", "F3", "C3", "G2", "C2") }
        )
    }

    // 3. Mixer Sheet
    if (isMixerOpen) {
        MixerBottomSheet(
            sheetState = mixerSheetState,
            onDismissRequest = { isMixerOpen = false },
            tracks = score.tracks,
            activeTrackId = activeTrackId,
            onSelectActiveTrack = {
                activeTrackId = it
                isMixerOpen = false
            },
            onVolumeChange = { _, _ -> },
            onToggleMute = { _ -> },
            onToggleSolo = { _ -> }
        )
    }

    // 4. Tempo Sheet
    if (isTempoOpen) {
        TempoBottomSheet(
            sheetState = tempoSheetState,
            onDismissRequest = { isTempoOpen = false },
            speedRatio = speedRatio,
            onSpeedRatioChange = { speedRatio = it },
            baseTempoBpm = score.tempo
        )
    }

    // 5. More Options Sheet
    if (isMoreOpen) {
        MoreOptionsBottomSheet(
            sheetState = moreSheetState,
            onDismissRequest = { isMoreOpen = false },
            onOpenTuner = {
                isMoreOpen = false
                isTunerOpen = true
            },
            onOpenTransposition = {
                isMoreOpen = false
                isTranspositionOpen = true
            },
            onOpenSongCatalog = {
                isMoreOpen = false
                Toast.makeText(context, "Каталог песен доступен в меню", Toast.LENGTH_SHORT).show()
            },
            countInEnabled = countInEnabled,
            onToggleCountIn = { countInEnabled = !countInEnabled },
            metronomeClickEnabled = metronomeClickEnabled,
            onToggleMetronomeClick = { metronomeClickEnabled = !metronomeClickEnabled },
            onCopyTab = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("GuitarLab Tab", "${score.title} - ${score.artist}\nTrack: ${activeTrack.name} (${activeTrack.tuningName})")
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "Табулатура скопирована в буфер обмена!", Toast.LENGTH_SHORT).show()
                isMoreOpen = false
            }
        )
    }
}
