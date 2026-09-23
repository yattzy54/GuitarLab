package com.mmt.guitarlab.ui.tab

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MusicNote
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
import com.mmt.guitarlab.ui.tab.components.AudioSourceMode
import com.mmt.guitarlab.ui.tab.components.SongsterrBottomControlBar
import com.mmt.guitarlab.ui.tab.components.TabCanvasRenderer
import com.mmt.guitarlab.ui.tab.components.sheets.ChromaticTunerBottomSheet
import com.mmt.guitarlab.ui.tab.components.sheets.MixerBottomSheet
import com.mmt.guitarlab.ui.tab.components.sheets.MoreOptionsBottomSheet
import com.mmt.guitarlab.ui.tab.components.sheets.TempoBottomSheet
import com.mmt.guitarlab.ui.tab.components.sheets.TranspositionBottomSheet
import kotlinx.coroutines.delay
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

    val selectedTrackIndex by viewModel.selectedTrackIndex.collectAsState()
    val activeTrack = score.tracks.getOrNull(selectedTrackIndex) ?: score.tracks.first()

    // Real audio playback states bound to TabPlaybackEngine
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentMeasureIndex by viewModel.currentMeasureIndex.collectAsState()
    val currentBeatIndex by viewModel.currentBeatIndex.collectAsState()
    val speedMultiplier by viewModel.speedMultiplier.collectAsState()

    var audioSource by remember { mutableStateOf(AudioSourceMode.SYNTH) }

    // Loop state
    var isLoopActive by remember { mutableStateOf(false) }
    var loopRange by remember { mutableStateOf<Pair<Int, Int>?>(Pair(1, 4)) }

    // Transposition state
    var semitones by remember { mutableIntStateOf(0) }
    var tuningOverrideName by remember { mutableStateOf<String?>(null) }

    // Count-in & metronome
    var countInEnabled by remember { mutableStateOf(false) }
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

    // Smooth auto-scroll during playback
    LaunchedEffect(currentMeasureIndex, isPlaying) {
        if (isPlaying && currentMeasureIndex > 0) {
            val approxMeasureHeightPx = 280
            val targetScroll = (currentMeasureIndex * approxMeasureHeightPx).coerceAtLeast(0)
            scrollState.animateScrollTo(targetScroll)
        }
    }

    Scaffold(
        containerColor = Color(0xFF0D0F12),
        bottomBar = {
            SongsterrBottomControlBar(
                isPlaying = isPlaying,
                onTogglePlay = {
                    if (isPlaying) {
                        viewModel.togglePlay()
                    } else {
                        if (countInEnabled && currentMeasureIndex == 0 && currentBeatIndex == 0) {
                            scope.launch {
                                isCountingIn = true
                                for (beat in 1..4) {
                                    countInBeat = beat
                                    val beatDelay = (60000 / (score.tempo * speedMultiplier)).toLong()
                                    delay(beatDelay.coerceIn(150, 1000))
                                }
                                isCountingIn = false
                                viewModel.togglePlay()
                            }
                        } else {
                            viewModel.togglePlay()
                        }
                    }
                },
                activeTrackName = activeTrack.name,
                speedRatio = speedMultiplier,
                audioSource = audioSource,
                onToggleAudioSource = { audioSource = it },
                isLoopActive = isLoopActive,
                onToggleLoop = {
                    isLoopActive = !isLoopActive
                    if (isLoopActive) {
                        val range = loopRange ?: Pair(1, 4)
                        viewModel.setLoop(range.first - 1, range.second - 1)
                    } else {
                        viewModel.clearLoop()
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
            // 1. TOP INFORMATION HEADER (Clean, no redundant track button)
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

            // 2. TAB CANVAS (Maximized 60 FPS Canvas with smooth scrolling)
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
                        viewModel.seekPlayback(mIdx, bIdx)
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
            activeTrackId = activeTrack.id,
            onSelectActiveTrack = { trackId ->
                val idx = score.tracks.indexOfFirst { it.id == trackId }
                if (idx >= 0) {
                    viewModel.selectTrack(idx)
                }
                isMixerOpen = false
            },
            onVolumeChange = { trackId, vol ->
                val idx = score.tracks.indexOfFirst { it.id == trackId }
                if (idx >= 0) viewModel.setTrackVolume(idx, vol)
            },
            onToggleMute = { trackId ->
                val idx = score.tracks.indexOfFirst { it.id == trackId }
                if (idx >= 0) viewModel.toggleMuteTrack(idx)
            },
            onToggleSolo = { trackId ->
                val idx = score.tracks.indexOfFirst { it.id == trackId }
                if (idx >= 0) viewModel.toggleSoloTrack(idx)
            }
        )
    }

    // 4. Tempo Sheet
    if (isTempoOpen) {
        TempoBottomSheet(
            sheetState = tempoSheetState,
            onDismissRequest = { isTempoOpen = false },
            speedRatio = speedMultiplier,
            onSpeedRatioChange = { newRatio ->
                viewModel.setSpeedMultiplier(newRatio)
            },
            baseTempoBpm = score.tempo
        )
    }

    // 5. More Options Sheet
    if (isMoreOpen) {
        MoreOptionsBottomSheet(
            sheetState = moreSheetState,
            onDismissRequest = { isMoreOpen = false },
            isMutedTrack = activeTrack.isMuted,
            onToggleMuteTrack = {
                val idx = score.tracks.indexOfFirst { it.id == activeTrack.id }
                if (idx >= 0) viewModel.toggleMuteTrack(idx)
            },
            isSoloTrack = activeTrack.isSolo,
            onToggleSoloTrack = {
                val idx = score.tracks.indexOfFirst { it.id == activeTrack.id }
                if (idx >= 0) viewModel.toggleSoloTrack(idx)
            },
            onOpenTransposition = {
                isMoreOpen = false
                isTranspositionOpen = true
            },
            onOpenTuner = {
                isMoreOpen = false
                isTunerOpen = true
            },
            countInEnabled = countInEnabled,
            onToggleCountIn = { countInEnabled = it },
            metronomeClickEnabled = metronomeClickEnabled,
            onToggleMetronomeClick = { metronomeClickEnabled = it },
            onExportAudio = {
                isMoreOpen = false
                Toast.makeText(context, "Экспорт аудио дорожки в разработке", Toast.LENGTH_SHORT).show()
            },
            onShareTab = {
                isMoreOpen = false
                val sendIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, "Смотри разбор песни ${score.title} — ${score.artist} в GuitarLab!")
                    type = "text/plain"
                }
                context.startActivity(Intent.createChooser(sendIntent, "Поделиться табулатурой"))
            },
            onCopyLink = {
                isMoreOpen = false
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("GuitarLab Tab", "${score.title} - ${score.artist}\nTrack: ${activeTrack.name} (${activeTrack.tuningName})")
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "Информация о табулатуре скопирована", Toast.LENGTH_SHORT).show()
            }
        )
    }
}
