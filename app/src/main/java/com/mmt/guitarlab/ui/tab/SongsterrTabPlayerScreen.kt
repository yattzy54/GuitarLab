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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Tune
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mmt.guitarlab.domain.model.InstrumentType
import com.mmt.guitarlab.domain.model.TabMeasure
import com.mmt.guitarlab.domain.model.TabScore
import com.mmt.guitarlab.domain.model.TabTrack
import com.mmt.guitarlab.ui.tab.components.SongsterrBottomControlBar
import com.mmt.guitarlab.ui.tab.components.TabCanvasRenderer
import com.mmt.guitarlab.ui.tab.components.sheets.ChromaticTunerBottomSheet
import com.mmt.guitarlab.ui.tab.components.sheets.MixerBottomSheet
import com.mmt.guitarlab.ui.tab.components.sheets.MoreOptionsBottomSheet
import com.mmt.guitarlab.ui.tab.components.sheets.SongCatalogBottomSheet
import com.mmt.guitarlab.ui.tab.components.sheets.TabSourceBottomSheet
import com.mmt.guitarlab.ui.tab.components.sheets.TempoBottomSheet
import com.mmt.guitarlab.ui.tab.components.sheets.TranspositionBottomSheet
import com.mmt.guitarlab.ui.theme.GuitarLabTheme
import com.mmt.guitarlab.ui.theme.StudioDarkBg
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

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri: Uri? ->
        uri?.let {
            viewModel.loadFromFile(context, it)
            Toast.makeText(context, "Загрузка табулатуры...", Toast.LENGTH_SHORT).show()
        }
    }

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

    var isLoopActive by remember { mutableStateOf(false) }
    var loopRange by remember { mutableStateOf<Pair<Int, Int>?>(Pair(1, 4)) }
    var loopPickingPoint by remember { mutableStateOf<String?>("A") }

    var semitones by remember { mutableIntStateOf(0) }
    var tuningOverrideName by remember { mutableStateOf<String?>(null) }

    var countInEnabled by remember { mutableStateOf(false) }
    var metronomeClickEnabled by remember { mutableStateOf(false) }
    var isCountingIn by remember { mutableStateOf(false) }
    var countInBeat by remember { mutableIntStateOf(1) }

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

    val localDensity = LocalDensity.current

    LaunchedEffect(selectedTrackIndex) {
        if (activeTrack.measures.isNotEmpty()) {
            val stringCount = if (activeTrack.instrumentType == InstrumentType.DRUMS) 5 else activeTrack.stringCount.coerceAtLeast(4)
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

    LaunchedEffect(currentMeasureIndex, isPlaying) {
        if (isPlaying && activeTrack.measures.isNotEmpty()) {
            val stringCount = if (activeTrack.instrumentType == InstrumentType.DRUMS) 5 else activeTrack.stringCount.coerceAtLeast(4)
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

    SongsterrPlayerContent(
        modifier = modifier,
        score = score,
        activeTrack = activeTrack,
        selectedTrackIndex = selectedTrackIndex,
        isPlaying = isPlaying,
        currentMeasureIndex = currentMeasureIndex,
        currentBeatIndex = currentBeatIndex,
        speedMultiplier = speedMultiplier,
        isLoopActive = isLoopActive,
        loopRange = loopRange,
        isCountingIn = isCountingIn,
        countInBeat = countInBeat,
        tuningOverrideName = tuningOverrideName,
        scrollState = scrollState,
        onOpenDrawer = onOpenDrawer,
        onNavigateToEditor = onNavigateToEditor,
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
        onUpdateLoopRange = { newRange ->
            loopRange = newRange
            viewModel.setLoop(newRange.first - 1, newRange.second - 1)
        },
        onDisableLoop = {
            isLoopActive = false
            viewModel.clearLoop()
        },
        onOpenMixer = { isMixerOpen = true },
        onOpenTempoPicker = { isTempoOpen = true },
        onOpenMoreMenu = { isMoreOpen = true },
        onSelectTrack = { viewModel.selectTrack(it) },
        onOpenTabSource = { isTabSourceOpen = true },
        onSeekPlayback = { mIdx, bIdx -> viewModel.seekPlayback(mIdx, bIdx) },
        onMeasureTapped = { mIdx ->
            val barNum = mIdx + 1
            if (isLoopActive) {
                val cur = loopRange ?: Pair(1, 4)
                if (loopPickingPoint == "A") {
                    val newEnd = maxOf(barNum, cur.second)
                    loopRange = Pair(barNum, newEnd)
                    viewModel.setLoop(barNum - 1, newEnd - 1)
                    loopPickingPoint = "B"
                } else {
                    val s = minOf(cur.first, barNum)
                    val e = maxOf(cur.first, barNum)
                    loopRange = Pair(s, e)
                    viewModel.setLoop(s - 1, e - 1)
                    loopPickingPoint = "A"
                }
            }
        }
    )

    // BOTTOM SHEETS
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

    if (isTunerOpen) {
        ChromaticTunerBottomSheet(
            sheetState = tunerSheetState,
            onDismissRequest = { isTunerOpen = false },
            targetTuningName = tuningOverrideName ?: activeTrack.tuningName,
            targetTuningNotes = activeTrack.tuningNotes.ifEmpty { listOf("D4", "A3", "F3", "C3", "G2", "C2") }
        )
    }

    if (isMixerOpen) {
        MixerBottomSheet(
            sheetState = mixerSheetState,
            onDismissRequest = { isMixerOpen = false },
            tracks = score.tracks,
            activeTrackId = activeTrack.id,
            onSelectActiveTrack = { trackId ->
                val idx = score.tracks.indexOfFirst { it.id == trackId }
                if (idx >= 0) viewModel.selectTrack(idx)
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

    if (isTempoOpen) {
        TempoBottomSheet(
            sheetState = tempoSheetState,
            onDismissRequest = { isTempoOpen = false },
            speedRatio = speedMultiplier,
            onSpeedRatioChange = { viewModel.setSpeedMultiplier(it) },
            baseTempoBpm = score.tempo
        )
    }

    if (isMoreOpen) {
        MoreOptionsBottomSheet(
            sheetState = moreSheetState,
            onDismissRequest = { isMoreOpen = false },
            onOpenTuner = { isMoreOpen = false; isTunerOpen = true },
            onOpenTransposition = { isMoreOpen = false; isTranspositionOpen = true },
            onOpenSongCatalog = { isMoreOpen = false; isCatalogOpen = true },
            countInEnabled = countInEnabled,
            onToggleCountIn = { countInEnabled = !countInEnabled },
            metronomeClickEnabled = metronomeClickEnabled,
            onToggleMetronomeClick = { metronomeClickEnabled = !metronomeClickEnabled },
            onOpenEditor = { isMoreOpen = false; onNavigateToEditor() },
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

    TabSourceBottomSheet(
        isOpen = isTabSourceOpen,
        sheetState = tabSourceSheetState,
        onDismiss = { isTabSourceOpen = false },
        onOpenCatalog = { isCatalogOpen = true },
        onOpenFilePicker = {
            filePickerLauncher.launch(arrayOf("*/*", "application/octet-stream", "application/x-guitar-pro", "text/plain"))
        }
    )

    if (isCatalogOpen) {
        SongCatalogBottomSheet(
            sheetState = catalogSheetState,
            onDismissRequest = { isCatalogOpen = false },
            searchResults = searchResults,
            isSearching = isSearchingOnline,
            isLoadingSong = isLoadingOnlineSong,
            onSearch = { viewModel.searchOnlineSongs(it) },
            onSelectSong = { songId ->
                viewModel.loadOnlineSong(songId) { isCatalogOpen = false }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SongsterrPlayerContent(
    score: TabScore,
    activeTrack: TabTrack,
    selectedTrackIndex: Int,
    isPlaying: Boolean,
    currentMeasureIndex: Int,
    currentBeatIndex: Int,
    speedMultiplier: Float,
    isLoopActive: Boolean,
    loopRange: Pair<Int, Int>?,
    isCountingIn: Boolean,
    countInBeat: Int,
    tuningOverrideName: String?,
    scrollState: androidx.compose.foundation.ScrollState,
    modifier: Modifier = Modifier,
    onOpenDrawer: () -> Unit = {},
    onNavigateToEditor: () -> Unit = {},
    onTogglePlay: () -> Unit = {},
    onToggleLoop: () -> Unit = {},
    onUpdateLoopRange: (Pair<Int, Int>) -> Unit = {},
    onDisableLoop: () -> Unit = {},
    onOpenMixer: () -> Unit = {},
    onOpenTempoPicker: () -> Unit = {},
    onOpenMoreMenu: () -> Unit = {},
    onSelectTrack: (Int) -> Unit = {},
    onOpenTabSource: () -> Unit = {},
    onSeekPlayback: (Int, Int) -> Unit = { _, _ -> },
    onMeasureTapped: (Int) -> Unit = {},
) {
    Scaffold(
        containerColor = Color(0xFF0D0F12),
        bottomBar = {
            SongsterrBottomControlBar(
                isPlaying = isPlaying,
                onTogglePlay = onTogglePlay,
                activeTrackName = activeTrack.name,
                speedRatio = speedMultiplier,
                isLoopActive = isLoopActive,
                loopRange = loopRange,
                onToggleLoop = onToggleLoop,
                onOpenMixer = onOpenMixer,
                onOpenTempoPicker = onOpenTempoPicker,
                onOpenMoreMenu = onOpenMoreMenu,
                trackCount = score.tracks.size
            )
        }
    ) { paddingValues ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            StudioTopBar(
                title = score.title,
                artist = score.artist,
                tuning = tuningOverrideName ?: activeTrack.tuningName,
                tempo = score.tempo,
                onOpenDrawer = onOpenDrawer,
                onOpenTabSource = onOpenTabSource,
                onNavigateToEditor = onNavigateToEditor,
            )

            if (score.tracks.size > 1) {
                TrackSwitcherBar(
                    tracks = score.tracks,
                    selectedIndex = selectedTrackIndex,
                    onSelectTrack = onSelectTrack,
                )
            }

            AnimatedVisibility(
                visible = isLoopActive && loopRange != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                LoopingControlBanner(
                    loopRange = loopRange,
                    totalBars = activeTrack.measures.size.coerceAtLeast(1),
                    onUpdateRange = onUpdateLoopRange,
                    onDisableLoop = onDisableLoop,
                )
            }

            if (isCountingIn) {
                CountInBanner(beat = countInBeat)
            }

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
                    onSelectPosition = onSeekPlayback,
                    onMeasureTapped = onMeasureTapped,
                )
            }
        }
    }
}

@Composable
private fun StudioTopBar(
    title: String,
    artist: String,
    tuning: String,
    tempo: Int,
    onOpenDrawer: () -> Unit,
    onOpenTabSource: () -> Unit,
    onNavigateToEditor: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF101319))
            .border(1.dp, Color(0xFF1E2430))
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
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

        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp)
        ) {
            Text(
                text = "$title — $artist",
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
            Text(
                text = "$tuning • $tempo BPM",
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFFBBF24),
                maxLines = 1,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1C222D))
                .border(1.dp, Color(0xFF2E384D), RoundedCornerShape(12.dp))
                .clickable(onClick = onOpenTabSource)
                .padding(horizontal = 9.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LibraryMusic,
                contentDescription = "Каталог",
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

        Row(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF261D0C))
                .border(1.dp, Color(0xFFF59E0B).copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                .clickable(onClick = onNavigateToEditor)
                .padding(horizontal = 9.dp, vertical = 7.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Icon(
                imageVector = Icons.Filled.Tune,
                contentDescription = "TabLab",
                tint = Color(0xFFF59E0B),
                modifier = Modifier.size(15.dp)
            )
            Text(
                text = "TabLab",
                color = Color(0xFFFBBF24),
                fontSize = 11.sp,
                fontWeight = FontWeight.Black
            )
        }
    }
}

@Composable
private fun TrackSwitcherBar(
    tracks: List<TabTrack>,
    selectedIndex: Int,
    onSelectTrack: (Int) -> Unit,
) {
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
        tracks.forEachIndexed { idx, trk ->
            val isSelected = idx == selectedIndex
            val icon = when (trk.instrumentType) {
                InstrumentType.DRUMS -> Icons.Default.MusicNote
                InstrumentType.BASS, InstrumentType.BASS_5 -> Icons.Default.GraphicEq
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
                    .clickable { onSelectTrack(idx) }
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

@Composable
private fun LoopingControlBanner(
    loopRange: Pair<Int, Int>?,
    totalBars: Int,
    onUpdateRange: (Pair<Int, Int>) -> Unit,
    onDisableLoop: () -> Unit,
) {
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
                        onUpdateRange(Pair(newStart, curRange.second))
                    },
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "-1", tint = Color.White, modifier = Modifier.size(12.dp))
                }
                Text("${curRange.first}", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Black)
                IconButton(
                    onClick = {
                        val newStart = (curRange.first + 1).coerceAtMost(curRange.second)
                        onUpdateRange(Pair(newStart, curRange.second))
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
                        onUpdateRange(Pair(curRange.first, newEnd))
                    },
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "-1", tint = Color.White, modifier = Modifier.size(12.dp))
                }
                Text("${curRange.second}", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Black)
                IconButton(
                    onClick = {
                        val newEnd = (curRange.second + 1).coerceAtMost(totalBars)
                        onUpdateRange(Pair(curRange.first, newEnd))
                    },
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "+1", tint = Color.White, modifier = Modifier.size(12.dp))
                }
            }

            IconButton(
                onClick = onDisableLoop,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(Icons.Default.Close, contentDescription = "Выключить цикл", tint = Color(0xFF9CA3AF), modifier = Modifier.size(16.dp))
            }
        }
    }
}

@Composable
private fun CountInBanner(beat: Int) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF10B981))
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "ОТСЧЁТ ПЕРЕД СТАРТОМ: $beat / 4",
            fontSize = 13.sp,
            fontWeight = FontWeight.Black,
            color = Color(0xFF09090B),
            fontFamily = FontFamily.Monospace
        )
    }
}

// ==================== PREVIEWS ====================

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun SongsterrPlayerContentPreview() {
    GuitarLabTheme {
        SongsterrPlayerContent(
            score = TabScore(
                title = "BUZZ OF US",
                artist = "BuzzÖuter",
                tempo = 140,
                tracks = listOf(
                    TabTrack(
                        name = "Lead Guitar",
                        instrumentType = InstrumentType.GUITAR,
                        tuningName = "Standard E",
                        measures = listOf(TabMeasure(1))
                    ),
                    TabTrack(
                        name = "Bass",
                        instrumentType = InstrumentType.BASS,
                        tuningName = "Standard E",
                        measures = listOf(TabMeasure(1))
                    )
                )
            ),
            activeTrack = TabTrack(
                name = "Lead Guitar",
                instrumentType = InstrumentType.BASS,
                tuningName = "Standard E",
                measures = listOf(TabMeasure(1))
            ),
            selectedTrackIndex = 0,
            isPlaying = false,
            currentMeasureIndex = 0,
            currentBeatIndex = 0,
            speedMultiplier = 1.0f,
            isLoopActive = true,
            loopRange = Pair(1, 4),
            isCountingIn = false,
            countInBeat = 1,
            tuningOverrideName = null,
            scrollState = rememberScrollState()
        )
    }
}