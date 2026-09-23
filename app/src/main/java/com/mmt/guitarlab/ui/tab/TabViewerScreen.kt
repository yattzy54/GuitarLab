package com.mmt.guitarlab.ui.tab

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.TabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mmt.guitarlab.data.db.TabProjectEntity
import com.mmt.guitarlab.domain.model.InstrumentType
import com.mmt.guitarlab.domain.model.NoteDuration
import com.mmt.guitarlab.domain.model.NoteEffect
import com.mmt.guitarlab.domain.model.TabMeasure
import com.mmt.guitarlab.domain.model.TabScore

@Composable
fun TabViewerScreen(viewModel: TabViewModel = hiltViewModel()) {
    val score by viewModel.score.collectAsStateWithLifecycle()
    val zoomScale by viewModel.zoomScale.collectAsStateWithLifecycle()
    val statusMessage by viewModel.statusMessage.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val currentMeasureIndex by viewModel.currentMeasureIndex.collectAsStateWithLifecycle()
    val currentBeatIndex by viewModel.currentBeatIndex.collectAsStateWithLifecycle()
    val speedMultiplier by viewModel.speedMultiplier.collectAsStateWithLifecycle()

    val selectedTrackIndex by viewModel.selectedTrackIndex.collectAsStateWithLifecycle()
    val selectedMeasureIndex by viewModel.selectedMeasureIndex.collectAsStateWithLifecycle()
    val selectedBeatIndex by viewModel.selectedBeatIndex.collectAsStateWithLifecycle()
    val selectedStringIndex by viewModel.selectedStringIndex.collectAsStateWithLifecycle()

    val activeEffect by viewModel.activeEffect.collectAsStateWithLifecycle()
    val activeDuration by viewModel.activeDuration.collectAsStateWithLifecycle()
    val savedProjects by viewModel.savedProjects.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var showPasteDialog by remember { mutableStateOf(false) }
    var showAddTrackDialog by remember { mutableStateOf(false) }
    var showMixerDialog by remember { mutableStateOf(false) }
    var showProjectsDialog by remember { mutableStateOf(false) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri: Uri? ->
        uri?.let { viewModel.loadFromFile(context, it) }
    }

    val saveFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("text/plain"),
    ) { uri: Uri? ->
        uri?.let { viewModel.saveScore(context, it) }
    }

    LaunchedEffect(statusMessage) {
        statusMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearStatus()
        }
    }

    var selectedTabMode by remember { mutableStateOf(0) }

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTabMode,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary,
        ) {
            Tab(
                selected = selectedTabMode == 0,
                onClick = { selectedTabMode = 0 },
                text = { Text("Songsterr Player", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.PlayArrow, contentDescription = null) },
            )
            Tab(
                selected = selectedTabMode == 1,
                onClick = { selectedTabMode = 1 },
                text = { Text("Tab Editor & Studio", fontWeight = FontWeight.Bold) },
                icon = { Icon(Icons.Default.Tune, contentDescription = null) },
            )
        }

        if (selectedTabMode == 0) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                SongsterrTabPlayerScreen(viewModel = viewModel)
            }
        } else {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
        ) {
            // Main Action Toolbar (Single Horizontal Row)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(
                    onClick = { filePickerLauncher.launch(arrayOf("*/*")) },
                ) {
                    Icon(Icons.Default.FileOpen, contentDescription = "Open")
                    Spacer(Modifier.width(4.dp))
                    Text("Open")
                }

                OutlinedButton(onClick = { showProjectsDialog = true }) {
                    Icon(Icons.Default.Folder, contentDescription = "Projects")
                    Spacer(Modifier.width(4.dp))
                    Text("Projects")
                }

                OutlinedButton(
                    onClick = {
                        val fileName = "${score?.title ?: "Tab"}.txt"
                        saveFileLauncher.launch(fileName)
                    },
                ) {
                    Icon(Icons.Default.Save, contentDescription = "Export File")
                    Spacer(Modifier.width(4.dp))
                    Text("Export")
                }

                OutlinedButton(onClick = { showPasteDialog = true }) {
                    Text("Paste ASCII")
                }

                IconButton(onClick = { viewModel.setZoom(zoomScale - 0.2f) }) {
                    Icon(Icons.Default.ZoomOut, contentDescription = "Zoom Out")
                }
                IconButton(onClick = { viewModel.setZoom(zoomScale + 0.2f) }) {
                    Icon(Icons.Default.ZoomIn, contentDescription = "Zoom In")
                }
                IconButton(onClick = { viewModel.exportToMidi(context) }) {
                    Icon(Icons.Default.MusicNote, contentDescription = "Export MIDI")
                }
                IconButton(onClick = { viewModel.shareScore(context) }) {
                    Icon(Icons.Default.Share, contentDescription = "Share")
                }
            }

            Spacer(Modifier.height(8.dp))

            // Playback Transport & Speed Bar
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                shape = RoundedCornerShape(16.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FilledIconButton(onClick = viewModel::togglePlay) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play/Pause",
                            )
                        }
                        Spacer(Modifier.width(6.dp))
                        IconButton(onClick = viewModel::stopPlayback) {
                            Icon(Icons.Default.Stop, contentDescription = "Stop")
                        }
                        Spacer(Modifier.width(6.dp))
                        IconButton(onClick = { showMixerDialog = true }) {
                            Icon(Icons.Default.Tune, contentDescription = "Mixer")
                        }
                    }

                    // Speed Multiplier Chips
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        listOf(0.5f, 0.75f, 0.9f, 1.0f, 1.25f, 1.5f).forEach { spd ->
                            FilterChip(
                                selected = speedMultiplier == spd,
                                onClick = { viewModel.setSpeedMultiplier(spd) },
                                label = { Text("%.2fx".format(spd)) },
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(8.dp))

            // Multi-Instrument Track Selector & Mixer Controls
            score?.let { s ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    ScrollableTabRow(
                        selectedTabIndex = selectedTrackIndex.coerceIn(0, s.tracks.lastIndex),
                        edgePadding = 0.dp,
                        modifier = Modifier.weight(1f),
                    ) {
                        s.tracks.forEachIndexed { idx, track ->
                            Tab(
                                selected = selectedTrackIndex == idx,
                                onClick = { viewModel.selectTrack(idx) },
                                text = { Text("${track.name} (${track.instrumentType.displayName})") },
                            )
                        }
                    }

                    val activeTrack = s.tracks.getOrNull(selectedTrackIndex)
                    if (activeTrack != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { viewModel.toggleMuteTrack(selectedTrackIndex) }) {
                                Icon(
                                    imageVector = if (activeTrack.isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                                    contentDescription = "Mute",
                                    tint = if (activeTrack.isMuted) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                                )
                            }
                            FilterChip(
                                selected = activeTrack.isSolo,
                                onClick = { viewModel.toggleSoloTrack(selectedTrackIndex) },
                                label = { Text("Solo") },
                            )
                            IconButton(onClick = { showAddTrackDialog = true }) {
                                Icon(Icons.Default.Add, contentDescription = "Add Track")
                            }
                        }
                    }
                }
                Spacer(Modifier.height(6.dp))
            }

            // Songsterr Mobile Style Paginated Vertical Score View
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainerLowest, shape = RoundedCornerShape(12.dp))
                    .padding(8.dp),
            ) {
                score?.let { currentScore ->
                    if (!currentScore.rawAsciiContent.isNullOrBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .horizontalScroll(rememberScrollState())
                                .verticalScroll(rememberScrollState()),
                        ) {
                            Text(
                                text = currentScore.rawAsciiContent,
                                style = TextStyle(
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = (14 * zoomScale).sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                ),
                                modifier = Modifier.padding(12.dp),
                            )
                        }
                    } else {
                        SongsterrPaginatedTabScore(
                            score = currentScore,
                            trackIndex = selectedTrackIndex,
                            zoomScale = zoomScale,
                            isPlaying = isPlaying,
                            playbackMeasure = currentMeasureIndex,
                            playbackBeat = currentBeatIndex,
                            selectedMeasure = selectedMeasureIndex,
                            selectedBeat = selectedBeatIndex,
                            selectedString = selectedStringIndex,
                            onCellSelected = viewModel::selectCell,
                        )
                    }
                } ?: Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text("No Tab Loaded. Open a .gp, .gpx, .gp5, .gp4, .gp3 file.")
                }
            }

            Spacer(Modifier.height(8.dp))

            // Editing Toolbar: Duration & Articulation / Effects Toolbars
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh),
                shape = RoundedCornerShape(16.dp),
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    // Note Duration Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Duration:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        NoteDuration.entries.forEach { dur ->
                            FilterChip(
                                selected = activeDuration == dur,
                                onClick = { viewModel.setActiveDuration(dur) },
                                label = { Text(dur.label) },
                            )
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    // Note Effects Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text("Effect:", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        NoteEffect.entries.forEach { fx ->
                            FilterChip(
                                selected = activeEffect == fx,
                                onClick = { viewModel.setActiveEffect(fx) },
                                label = { Text(fx.displayName) },
                            )
                        }
                    }

                    Spacer(Modifier.height(4.dp))

                    // Fret Numpad Row
                    val activeInst = score?.tracks?.getOrNull(selectedTrackIndex)?.instrumentType ?: InstrumentType.GUITAR
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = if (activeInst == InstrumentType.DRUMS) "Drum Hit:" else "Fret:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                        )

                        if (activeInst == InstrumentType.DRUMS) {
                            listOf(0 to "Normal", 1 to "Accent", 2 to "Rim").forEach { (hitVal, label) ->
                                OutlinedButton(
                                    onClick = { viewModel.updateFretAtSelectedCell(hitVal) },
                                    modifier = Modifier.height(36.dp),
                                ) {
                                    Text(label, fontWeight = FontWeight.Bold)
                                }
                            }
                        } else {
                            val frets = listOf(0, 1, 2, 3, 5, 7, 8, 10, 12, 14, 15, 17, 19, 21, 24)
                            frets.forEach { f ->
                                OutlinedButton(
                                    onClick = { viewModel.updateFretAtSelectedCell(f) },
                                    modifier = Modifier.height(36.dp),
                                ) {
                                    Text("$f", fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        IconButton(onClick = { viewModel.updateFretAtSelectedCell(null) }) {
                            Icon(Icons.AutoMirrored.Filled.Backspace, contentDescription = "Clear Note")
                        }

                        OutlinedButton(onClick = viewModel::addBeat) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Text("Beat")
                        }

                        OutlinedButton(onClick = viewModel::addMeasure) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Text("Measure")
                        }
                    }
                }
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
            }
        }
    }

    if (showMixerDialog) {
        score?.let { s ->
            MixerDialog(
                score = s,
                onVolumeChange = viewModel::setTrackVolume,
                onPanChange = viewModel::setTrackPan,
                onMuteToggle = viewModel::toggleMuteTrack,
                onSoloToggle = viewModel::toggleSoloTrack,
                onDismiss = { showMixerDialog = false },
            )
        }
    }

    if (showProjectsDialog) {
        ProjectsDialog(
            projects = savedProjects,
            onSelect = {
                viewModel.loadProjectFromDb(it.id)
                showProjectsDialog = false
            },
            onDelete = viewModel::deleteProjectFromDb,
            onDismiss = { showProjectsDialog = false },
        )
    }

    if (showPasteDialog) {
        PasteTabDialog(
            onParse = { text, title ->
                viewModel.loadAsciiText(text, title)
                showPasteDialog = false
            },
            onDismiss = { showPasteDialog = false },
        )
    }

    if (showAddTrackDialog) {
        AddTrackDialog(
            onAdd = { name, type ->
                viewModel.addTrack(name, type)
                showAddTrackDialog = false
            },
            onDismiss = { showAddTrackDialog = false },
        )
    }
}

@Composable
private fun MixerDialog(
    score: TabScore,
    onVolumeChange: (index: Int, volume: Float) -> Unit,
    onPanChange: (index: Int, pan: Float) -> Unit,
    onMuteToggle: (index: Int) -> Unit,
    onSoloToggle: (index: Int) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Multi-Track Audio Mixer", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
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

                            Text("Pan: ${if (track.pan < 0) "L ${(track.pan * -100).toInt()}%" else if (track.pan > 0) "R ${(track.pan * 100).toInt()}%" else "Center"}", style = MaterialTheme.typography.labelSmall)
                            Slider(
                                value = track.pan,
                                onValueChange = { onPanChange(idx, it) },
                                valueRange = -1f..1f,
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },
    )
}

@Composable
private fun ProjectsDialog(
    projects: List<TabProjectEntity>,
    onSelect: (TabProjectEntity) -> Unit,
    onDelete: (TabProjectEntity) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Saved Local Projects", fontWeight = FontWeight.Bold) },
        text = {
            if (projects.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                    Text("No saved projects found. Edit or open a tab to auto-save!")
                }
            } else {
                LazyColumn(modifier = Modifier.height(280.dp)) {
                    items(projects) { project ->
                        Card(
                            onClick = { onSelect(project) },
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(project.title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                    Text(project.artist, style = MaterialTheme.typography.bodySmall)
                                }
                                IconButton(onClick = { onDelete(project) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        },
    )
}

@Composable
private fun SongsterrPaginatedTabScore(
    score: TabScore,
    trackIndex: Int,
    zoomScale: Float,
    isPlaying: Boolean,
    playbackMeasure: Int,
    playbackBeat: Int,
    selectedMeasure: Int,
    selectedBeat: Int,
    selectedString: Int,
    onCellSelected: (measureIdx: Int, beatIdx: Int, stringIdx: Int) -> Unit,
) {
    val activeTrack = score.tracks.getOrNull(trackIndex) ?: score.tracks.firstOrNull()
    val measures = activeTrack?.measures ?: emptyList()
    val stringLabels = activeTrack?.stringLabels ?: InstrumentType.GUITAR.defaultStringLabels
    val stringCount = activeTrack?.stringCount ?: 6

    val measuresPerLine = (2 / zoomScale).toInt().coerceIn(1, 4)
    val measureChunkedLines = remember(measures, measuresPerLine) {
        measures.chunked(measuresPerLine)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        itemsIndexed(measureChunkedLines) { lineIndex, measureChunk ->
            val startMeasureIndex = lineIndex * measuresPerLine

            SystemRowCanvas(
                measures = measureChunk,
                startMeasureIndex = startMeasureIndex,
                stringCount = stringCount,
                stringLabels = stringLabels,
                zoomScale = zoomScale,
                isPlaying = isPlaying,
                playbackMeasure = playbackMeasure,
                playbackBeat = playbackBeat,
                selectedMeasure = selectedMeasure,
                selectedBeat = selectedBeat,
                selectedString = selectedString,
                onCellSelected = onCellSelected,
            )
        }
    }
}

@Composable
private fun SystemRowCanvas(
    measures: List<TabMeasure>,
    startMeasureIndex: Int,
    stringCount: Int,
    stringLabels: List<String>,
    zoomScale: Float,
    isPlaying: Boolean,
    playbackMeasure: Int,
    playbackBeat: Int,
    selectedMeasure: Int,
    selectedBeat: Int,
    selectedString: Int,
    onCellSelected: (measureIdx: Int, beatIdx: Int, stringIdx: Int) -> Unit,
) {
    val textMeasurer = rememberTextMeasurer()
    val stringSpacing = (18 * zoomScale).dp
    val colWidth = (28 * zoomScale).dp

    val onSurface = MaterialTheme.colorScheme.onSurface
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val lineTrackColor = MaterialTheme.colorScheme.outlineVariant
    val surfaceColor = MaterialTheme.colorScheme.surface
    val selectionBgColor = MaterialTheme.colorScheme.primaryContainer

    val totalBeatsInLine = measures.sumOf { it.beats.size }
    val canvasWidth = ((totalBeatsInLine + measures.size * 2 + 4) * colWidth.value).coerceAtLeast(340f)
    val canvasHeight = ((stringCount + 1.5f) * stringSpacing.value).coerceAtLeast(140f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
    ) {
        Canvas(
            modifier = Modifier
                .width(canvasWidth.dp)
                .height(canvasHeight.dp)
                .padding(vertical = 8.dp)
                .pointerInput(measures, zoomScale) {
                    detectTapGestures { offset ->
                        val startX = 40f * zoomScale
                        val startY = 24f * zoomScale
                        val spacing = stringSpacing.toPx()
                        val colW = colWidth.toPx()

                        val tappedString = ((offset.y - startY + spacing / 2f) / spacing).toInt().coerceIn(0, stringCount - 1)

                        var currX = startX + 30f * zoomScale
                        measures.forEachIndexed { relativeMIdx, measure ->
                            val globalMIdx = startMeasureIndex + relativeMIdx
                            currX += 15f * zoomScale
                            measure.beats.forEachIndexed { bIdx, _ ->
                                if (offset.x >= currX - 5f && offset.x <= currX + colW + 5f) {
                                    onCellSelected(globalMIdx, bIdx, tappedString)
                                    return@detectTapGestures
                                }
                                currX += colW
                            }
                            currX += 15f * zoomScale
                        }
                    }
                },
        ) {
            val startX = 40f * zoomScale
            val startY = 24f * zoomScale
            val spacing = stringSpacing.toPx()

            for (i in 0 until stringCount) {
                val y = startY + i * spacing
                drawLine(
                    color = lineTrackColor,
                    start = Offset(startX, y),
                    end = Offset(size.width - 10f, y),
                    strokeWidth = 2f * zoomScale,
                )
                val label = stringLabels.getOrElse(i) { "E" }
                drawText(
                    textMeasurer = textMeasurer,
                    text = label,
                    topLeft = Offset(8f * zoomScale, y - 10f * zoomScale),
                    style = TextStyle(color = onSurface, fontSize = (11 * zoomScale).sp, fontWeight = FontWeight.Bold),
                )
            }

            var xOffset = startX + 30f * zoomScale

            measures.forEachIndexed { relativeMIdx, measure ->
                val globalMIdx = startMeasureIndex + relativeMIdx

                drawLine(
                    color = primaryColor,
                    start = Offset(xOffset, startY),
                    end = Offset(xOffset, startY + (stringCount - 1) * spacing),
                    strokeWidth = 3f * zoomScale,
                )
                drawText(
                    textMeasurer = textMeasurer,
                    text = "M${measure.number}",
                    topLeft = Offset(xOffset + 2f, startY - 20f * zoomScale),
                    style = TextStyle(color = primaryColor, fontSize = (10 * zoomScale).sp, fontWeight = FontWeight.Bold),
                )

                xOffset += 15f * zoomScale

                measure.beats.forEachIndexed { bIdx, beat ->
                    val isPlaybackActive = isPlaying && playbackMeasure == globalMIdx && playbackBeat == bIdx
                    val isSelectedCell = selectedMeasure == globalMIdx && selectedBeat == bIdx

                    if (isPlaybackActive) {
                        drawLine(
                            color = secondaryColor,
                            start = Offset(xOffset, startY - 8f),
                            end = Offset(xOffset, startY + (stringCount - 1) * spacing + 8f),
                            strokeWidth = 6f * zoomScale,
                        )
                    }

                    if (isSelectedCell) {
                        val selY = startY + selectedString.coerceIn(0, stringCount - 1) * spacing
                        drawCircle(
                            color = selectionBgColor,
                            radius = 13f * zoomScale,
                            center = Offset(xOffset, selY),
                        )
                    }

                    beat.notes.forEach { note ->
                        val stringIdx = note.stringIndex.coerceIn(0, stringCount - 1)
                        val y = startY + stringIdx * spacing
                        val fretText = note.displayLabel

                        drawCircle(
                            color = surfaceColor,
                            radius = 10f * zoomScale,
                            center = Offset(xOffset, y),
                        )

                        drawText(
                            textMeasurer = textMeasurer,
                            text = fretText,
                            topLeft = Offset(xOffset - 5f * zoomScale, y - 8f * zoomScale),
                            style = TextStyle(
                                color = if (isPlaybackActive) secondaryColor else primaryColor,
                                fontSize = (12 * zoomScale).sp,
                                fontWeight = FontWeight.Bold,
                            ),
                        )
                    }
                    xOffset += colWidth.toPx()
                }
                xOffset += 15f * zoomScale
            }
        }
    }
}

@Composable
private fun AddTrackDialog(
    onAdd: (name: String, type: InstrumentType) -> Unit,
    onDismiss: () -> Unit,
) {
    var trackName by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(InstrumentType.GUITAR) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Instrument Track") },
        text = {
            Column {
                OutlinedTextField(
                    value = trackName,
                    onValueChange = { trackName = it },
                    label = { Text("Track Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(12.dp))
                Text("Instrument Type", style = MaterialTheme.typography.labelMedium)
                Spacer(Modifier.height(6.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    InstrumentType.entries.forEach { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            label = { Text(type.displayName) },
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onAdd(trackName, selectedType) },
            ) {
                Text("Add Track")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}

@Composable
private fun PasteTabDialog(
    onParse: (text: String, title: String) -> Unit,
    onDismiss: () -> Unit,
) {
    var title by remember { mutableStateOf("Pasted Tab") }
    var asciiText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Paste ASCII Tab") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Tab Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = asciiText,
                    onValueChange = { asciiText = it },
                    label = { Text("Tab Text (e|---0---1---|)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    textStyle = TextStyle(fontFamily = FontFamily.Monospace),
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (asciiText.isNotBlank()) {
                        onParse(asciiText, title)
                    }
                },
            ) {
                Text("Parse Tab")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}
