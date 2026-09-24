package com.mmt.guitarlab.ui.tab

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mmt.guitarlab.ui.tab.components.SongsterrBottomControlBar
import com.mmt.guitarlab.ui.tab.components.TabCanvasRenderer
import com.mmt.guitarlab.ui.tab.components.sheets.ChromaticTunerBottomSheet
import com.mmt.guitarlab.ui.tab.components.sheets.MixerBottomSheet
import com.mmt.guitarlab.ui.tab.components.sheets.MoreOptionsBottomSheet
import com.mmt.guitarlab.ui.tab.components.sheets.SongCatalogBottomSheet
import androidx.compose.material.icons.filled.Menu
import com.mmt.guitarlab.ui.tab.components.sheets.TabSourceBottomSheet

import androidx.compose.material.icons.filled.LibraryMusic
import com.mmt.guitarlab.ui.tab.components.sheets.TempoBottomSheet
import com.mmt.guitarlab.ui.tab.components.sheets.TranspositionBottomSheet
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongsterrTabPlayerScreen(
    viewModel: TabViewModel,
    modifier: Modifier = Modifier,
    onOpenDrawer: () -> Unit = {},
    onNavigateToEditor: () -> Unit = {},
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val scoreState by viewModel.score.collectAsState()
    val score = scoreState ?: return

    val selectedTrackIndex by viewModel.selectedTrackIndex.collectAsState()
    val activeTrack = score.tracks.getOrNull(selectedTrackIndex) ?: score.tracks.first()

    // File picker to load tabs (.gp, .gp5, .gpx, .gp3, text) from device storage
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri: Uri? ->
        uri?.let {
            viewModel.loadFromFile(context, it)
            Toast.makeText(context, "Загрузка табулатуры...", Toast.LENGTH_SHORT).show()
        }
    }

    // Real audio playback states bound to TabPlaybackEngine
    val isPlaying by viewModel.isPlaying.collectAsState()
    val currentMeasureIndex by viewModel.currentMeasureIndex.collectAsState()
    val currentBeatIndex by viewModel.currentBeatIndex.collectAsState()
    val speedMultiplier by viewModel.speedMultiplier.collectAsState()

    val searchResults by viewModel.searchResults.collectAsState()
    val isSearchingOnline by viewModel.isSearchingOnline.collectAsState()
    val isLoadingOnlineSong by viewModel.isLoadingOnlineSong.collectAsState()

        var isTabSourceOpen by remember { mutableStateOf(false) }
    val tabSourceSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var isCatalogOpen by remember { mutableStateOf(false) }
    val catalogSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Interactive Loop state
    var isLoopActive by remember { mutableStateOf(false) }
    var loopRange by remember { mutableStateOf<Pair<Int, Int>?>(Pair(1, 4)) }
    var loopPickingPoint by remember { mutableStateOf<String?>("A") } // "A" then "B"

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

    // Responsive viewport tracking: auto-scrolls on instrument change and playback progress
    val localDensity = LocalDensity.current

    // Immediate scroll when active track is changed or when song is loaded
    LaunchedEffect(selectedTrackIndex) {
        if (activeTrack.measures.isNotEmpty()) {
            val stringCount = if (activeTrack.instrumentType == com.mmt.guitarlab.domain.model.InstrumentType.DRUMS) 5 else activeTrack.stringCount.coerceAtLeast(4)
            val stringSpacing = with(localDensity) { 24.dp.toPx() }
            val topPadding = with(localDensity) { 40.dp.toPx() }
            val stemHeight = with(localDensity) { 28.dp.toPx() }
            val measureBottomPadding = with(localDensity) { 20.dp.toPx() }
            val measureTotalHeightPx = topPadding + (stringCount - 1) * stringSpacing + stemHeight + measureBottomPadding

            val activeBarTop = 16f + currentMeasureIndex * measureTotalHeightPx
            val viewportHeight = scrollState.viewportSize.toFloat()
            if (viewportHeight > 0) {
                val targetScroll = (activeBarTop - viewportHeight * 0.2f).toInt().coerceAtLeast(0)
                scrollState.animateScrollTo(targetScroll)
            } else {
                scrollState.scrollTo(activeBarTop.toInt().coerceAtLeast(0))
            }
        }
    }

    // Auto-scrolls as playback advances to subsequent measures
    LaunchedEffect(currentMeasureIndex, isPlaying) {
        if (isPlaying && activeTrack.measures.isNotEmpty()) {
            val stringCount = if (activeTrack.instrumentType == com.mmt.guitarlab.domain.model.InstrumentType.DRUMS) 5 else activeTrack.stringCount.coerceAtLeast(4)
            val stringSpacing = with(localDensity) { 24.dp.toPx() }
            val topPadding = with(localDensity) { 40.dp.toPx() }
            val stemHeight = with(localDensity) { 28.dp.toPx() }
            val measureBottomPadding = with(localDensity) { 20.dp.toPx() }
            val measureTotalHeightPx = topPadding + (stringCount - 1) * stringSpacing + stemHeight + measureBottomPadding

            val activeBarTop = 16f + currentMeasureIndex * measureTotalHeightPx
            val activeBarBottom = activeBarTop + measureTotalHeightPx

            val viewportHeight = scrollState.viewportSize.toFloat()
            if (viewportHeight > 0) {
                val currentScroll = scrollState.value.toFloat()
                val bottomSafetyMargin = with(localDensity) { 130.dp.toPx() }
                if (activeBarBottom > currentScroll + viewportHeight - bottomSafetyMargin) {
                    val targetScroll = (activeBarBottom - viewportHeight + bottomSafetyMargin).toInt()
                    scrollState.animateScrollTo(targetScroll.coerceAtLeast(0))
                } else if (activeBarTop < currentScroll + 20f) {
                    val targetScroll = (activeBarTop - 20f).toInt().coerceAtLeast(0)
                    scrollState.animateScrollTo(targetScroll)
                }
            } else {
                val targetScroll = activeBarTop.toInt().coerceAtLeast(0)
                scrollState.animateScrollTo(targetScroll)
            }
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
                isLoopActive = isLoopActive,
                loopRange = loopRange,
                onToggleLoop = {
                    isLoopActive = !isLoopActive
                    if (isLoopActive) {
                        val currentBar = currentMeasureIndex + 1
                        val total = activeTrack.measures.size.coerceAtLeast(1)
                        val endBar = (currentBar + 3).coerceAtMost(total)
                        val range = loopRange ?: Pair(currentBar, endBar)
                        loopRange = range
                        viewModel.setLoop(range.first - 1, range.second - 1)
                        loopPickingPoint = "A"
                        Toast.makeText(
                            context,
                            "Зацикливание включено: такты ${range.first}–${range.second}. Нажмите на такт в табах чтобы выбрать отрезок.",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        viewModel.clearLoop()
                        loopPickingPoint = null
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
            // 1. INTEGRATED STUDIO TOOLBAR: Drawer Menu + Song Title & Artist + Catalog/Load Action Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF101319))
                    .border(1.dp, Color(0xFF1E2430))
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Menu Drawer Button
                IconButton(
                    onClick = onOpenDrawer,
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Open Studio Menu",
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Song Title & Artist in Toolbar
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp)
                ) {
                    Text(
                        text = "${score.title} — ${score.artist}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${tuningOverrideName ?: activeTrack.tuningName} • ${score.tempo} BPM",
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFFBBF24),
                        maxLines = 1,
                        overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                    )
                }

                // Button opening TabSourceBottomSheet (Catalog & Load)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1C222D))
                        .border(1.dp, Color(0xFF2E384D), RoundedCornerShape(12.dp))
                        .clickable { isTabSourceOpen = true }
                        .padding(horizontal = 9.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LibraryMusic,
                        contentDescription = "Выбрать песню или загрузить",
                        tint = Color(0xFFFBBF24),
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "Каталог",
                        color = Color(0xFFF3F4F6),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // TuxGuitar Studio Button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF261D0C))
                        .border(1.dp, Color(0xFFF59E0B).copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                        .clickable { onNavigateToEditor() }
                        .padding(horizontal = 9.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = "TuxGuitar Studio",
                        tint = Color(0xFFF59E0B),
                        modifier = Modifier.size(15.dp)
                    )
                    Text(
                        text = "TuxGuitar",
                        color = Color(0xFFFBBF24),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            // QUICK INSTRUMENT SWITCHER BAR
            if (score.tracks.size > 1) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF11141B))
                        .border(1.dp, Color(0xFF1E2636))
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    score.tracks.forEachIndexed { idx, trk ->
                        val isSelected = idx == selectedTrackIndex
                        val icon = when (trk.instrumentType) {
                            com.mmt.guitarlab.domain.model.InstrumentType.DRUMS -> Icons.Default.MusicNote
                            com.mmt.guitarlab.domain.model.InstrumentType.BASS,
                            com.mmt.guitarlab.domain.model.InstrumentType.BASS_5 -> Icons.Default.GraphicEq
                            else -> Icons.Default.MusicNote
                        }
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) Color(0xFF1B382B) else Color(0xFF181D26))
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) Color(0xFF10B981) else Color(0xFF2B3648),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    viewModel.selectTrack(idx)
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = icon,
                                contentDescription = trk.name,
                                tint = if (isSelected) Color(0xFF34D399) else Color(0xFF94A3B8),
                                modifier = Modifier.size(14.dp)
                            )
                            Text(
                                text = trk.name,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else Color(0xFF94A3B8)
                            )
                        }
                    }
                }
            }

            // 2. INTERACTIVE LOOP SELECTION BANNER (when loop is active)
            AnimatedVisibility(
                visible = isLoopActive && loopRange != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                val totalBars = activeTrack.measures.size.coerceAtLeast(1)
                val curRange = loopRange ?: Pair(1, 4)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E1A11))
                        .border(1.dp, Color(0x66F59E0B))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Repeat,
                            contentDescription = null,
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Цикл: Такты ${curRange.first}–${curRange.second}",
                            color = Color(0xFFFBBF24),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // Stepper controls for Start (A) and End (B) bars
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Bar A Stepper
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(Color(0xFF2C2518), RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFF4A3B22), RoundedCornerShape(8.dp))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text("A:", fontSize = 10.sp, color = Color(0xFF9CA3AF), fontWeight = FontWeight.Bold)
                            IconButton(
                                onClick = {
                                    val newStart = (curRange.first - 1).coerceAtLeast(1)
                                    loopRange = Pair(newStart, curRange.second)
                                    viewModel.setLoop(newStart - 1, curRange.second - 1)
                                },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "-1", tint = Color.White, modifier = Modifier.size(12.dp))
                            }
                            Text("${curRange.first}", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Black)
                            IconButton(
                                onClick = {
                                    val newStart = (curRange.first + 1).coerceAtMost(curRange.second)
                                    loopRange = Pair(newStart, curRange.second)
                                    viewModel.setLoop(newStart - 1, curRange.second - 1)
                                },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "+1", tint = Color.White, modifier = Modifier.size(12.dp))
                            }
                        }

                        // Bar B Stepper
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(Color(0xFF2C2518), RoundedCornerShape(8.dp))
                                .border(1.dp, Color(0xFF4A3B22), RoundedCornerShape(8.dp))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text("B:", fontSize = 10.sp, color = Color(0xFF9CA3AF), fontWeight = FontWeight.Bold)
                            IconButton(
                                onClick = {
                                    val newEnd = (curRange.second - 1).coerceAtLeast(curRange.first)
                                    loopRange = Pair(curRange.first, newEnd)
                                    viewModel.setLoop(curRange.first - 1, newEnd - 1)
                                },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "-1", tint = Color.White, modifier = Modifier.size(12.dp))
                            }
                            Text("${curRange.second}", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Black)
                            IconButton(
                                onClick = {
                                    val newEnd = (curRange.second + 1).coerceAtMost(totalBars)
                                    loopRange = Pair(curRange.first, newEnd)
                                    viewModel.setLoop(curRange.first - 1, newEnd - 1)
                                },
                                modifier = Modifier.size(20.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "+1", tint = Color.White, modifier = Modifier.size(12.dp))
                            }
                        }

                        // Close Loop button
                        IconButton(
                            onClick = {
                                isLoopActive = false
                                viewModel.clearLoop()
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Выключить цикл", tint = Color(0xFF9CA3AF), modifier = Modifier.size(16.dp))
                        }
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

            // 3. TAB CANVAS (with smooth vertical scrolling & interactive measure/loop tapping)
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
                    },
                    onMeasureTapped = { mIdx ->
                        val barNum = mIdx + 1
                        if (isLoopActive) {
                            val cur = loopRange ?: Pair(1, 4)
                            if (loopPickingPoint == "A") {
                                val newEnd = maxOf(barNum, cur.second)
                                loopRange = Pair(barNum, newEnd)
                                viewModel.setLoop(barNum - 1, newEnd - 1)
                                loopPickingPoint = "B"
                                Toast.makeText(
                                    context,
                                    "Старт цикла (A) установлен на такт $barNum. Нажмите такт для конца (B).",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                val s = minOf(cur.first, barNum)
                                val e = maxOf(cur.first, barNum)
                                loopRange = Pair(s, e)
                                viewModel.setLoop(s - 1, e - 1)
                                loopPickingPoint = "A"
                                Toast.makeText(
                                    context,
                                    "Отрезок зациклен: такты $s–$e",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
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
                isCatalogOpen = true
            },
            countInEnabled = countInEnabled,
            onToggleCountIn = {
                countInEnabled = !countInEnabled
            },
            metronomeClickEnabled = metronomeClickEnabled,
            onToggleMetronomeClick = {
                metronomeClickEnabled = !metronomeClickEnabled
            },
            onOpenEditor = {
                isMoreOpen = false
                onNavigateToEditor()
            },
            onCopyTab = {
                isMoreOpen = false
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText(
                    "GuitarLab Tab",
                    "${score.title} - ${score.artist}\nTrack: ${activeTrack.name} (${activeTrack.tuningName})"
                )
                clipboard.setPrimaryClip(clip)
                Toast.makeText(context, "Информация о табулатуре скопирована в буфер", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // 5.5 Tab Source Sheet
    TabSourceBottomSheet(
    isOpen = isTabSourceOpen,
    sheetState = tabSourceSheetState,
    onDismiss = { isTabSourceOpen = false },
    onOpenCatalog = { isCatalogOpen = true },
    onOpenFilePicker = {
    filePickerLauncher.launch(
    arrayOf(
    "*/*",
    "application/octet-stream",
    "application/x-guitar-pro",
    "text/plain"
    )
    )
    }
    )
    
    // 6. Song Catalog Sheet
    if (isCatalogOpen) {
        SongCatalogBottomSheet(
            sheetState = catalogSheetState,
            onDismissRequest = { isCatalogOpen = false },
            searchResults = searchResults,
            isSearching = isSearchingOnline,
            isLoadingSong = isLoadingOnlineSong,
            onSearch = { query ->
                viewModel.searchOnlineSongs(query)
            },
            onSelectSong = { songId ->
                viewModel.loadOnlineSong(songId) {
                    isCatalogOpen = false
                }
            }
        )
    }
}
