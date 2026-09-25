package com.mmt.guitarlab.ui.tab

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mmt.guitarlab.data.db.TabProjectEntity
import com.mmt.guitarlab.domain.model.InstrumentType
import com.mmt.guitarlab.domain.model.NoteDuration
import com.mmt.guitarlab.domain.model.TabMeasure
import com.mmt.guitarlab.domain.model.TabScore
import com.mmt.guitarlab.domain.model.TabTrack
import com.mmt.guitarlab.domain.model.TuxGuitarSoundBank
import com.mmt.guitarlab.ui.components.StudioCard
import com.mmt.guitarlab.ui.tab.components.AddTrackDialog
import com.mmt.guitarlab.core.ui.theme.GuitarLabTheme
import com.mmt.guitarlab.core.ui.theme.StudioDarkBg

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabEditorScreen(viewModel: TabViewModel = hiltViewModel()) {
    val score by viewModel.score.collectAsStateWithLifecycle()
    val zoomScale by viewModel.zoomScale.collectAsStateWithLifecycle()
    val statusMessage by viewModel.statusMessage.collectAsStateWithLifecycle()
    val isPlaying by viewModel.isPlaying.collectAsStateWithLifecycle()
    val currentMeasureIndex by viewModel.currentMeasureIndex.collectAsStateWithLifecycle()
    val currentBeatIndex by viewModel.currentBeatIndex.collectAsStateWithLifecycle()

    val selectedTrackIndex by viewModel.selectedTrackIndex.collectAsStateWithLifecycle()
    val selectedMeasureIndex by viewModel.selectedMeasureIndex.collectAsStateWithLifecycle()
    val selectedBeatIndex by viewModel.selectedBeatIndex.collectAsStateWithLifecycle()
    val selectedStringIndex by viewModel.selectedStringIndex.collectAsStateWithLifecycle()

    val activeDuration by viewModel.activeDuration.collectAsStateWithLifecycle()
    val soundBank by viewModel.soundBank.collectAsStateWithLifecycle()
    val savedProjects by viewModel.savedProjects.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }

    var showLeftDrawer by remember { mutableStateOf(false) }
    var drawerTab by remember { mutableIntStateOf(0) }
    var showTabKeyboard by remember { mutableStateOf(true) }
    var showFretboard by remember { mutableStateOf(false) }
    var showPiano by remember { mutableStateOf(false) }
    var showOverflowMenu by remember { mutableStateOf(false) }

    var showSongInfoDialog by remember { mutableStateOf(false) }
    var showMixerDialog by remember { mutableStateOf(false) }
    var showSoundBankDialog by remember { mutableStateOf(false) }
    var showAddTrackDialog by remember { mutableStateOf(false) }
    var showProjectsDialog by remember { mutableStateOf(false) }
    var showPasteDialog by remember { mutableStateOf(false) }
    var showTempoDialog by remember { mutableStateOf(false) }

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

    BackHandler(enabled = showLeftDrawer) {
        showLeftDrawer = false
    }

    val activeTrack = score?.tracks?.getOrNull(selectedTrackIndex) ?: score?.tracks?.firstOrNull()

    TabEditorContent(
        score = score,
        activeTrack = activeTrack,
        zoomScale = zoomScale,
        isPlaying = isPlaying,
        currentMeasureIndex = currentMeasureIndex,
        currentBeatIndex = currentBeatIndex,
        selectedTrackIndex = selectedTrackIndex,
        selectedMeasureIndex = selectedMeasureIndex,
        selectedBeatIndex = selectedBeatIndex,
        selectedStringIndex = selectedStringIndex,
        activeDuration = activeDuration,
        showTabKeyboard = showTabKeyboard,
        showFretboard = showFretboard,
        showPiano = showPiano,
        showOverflowMenu = showOverflowMenu,
        snackbarHostState = snackbarHostState,
        onToggleDrawer = { showLeftDrawer = !showLeftDrawer },
        onToggleKeyboard = { showTabKeyboard = !showTabKeyboard },
        onTogglePlay = viewModel::togglePlay,
        onStopPlayback = viewModel::stopPlayback,
        onToggleFretboard = { showFretboard = !showFretboard },
        onTogglePiano = { showPiano = !showPiano },
        onToggleOverflow = { showOverflowMenu = !showOverflowMenu },
        onDismissOverflow = { showOverflowMenu = false },
        onOpenSongInfo = { showSongInfoDialog = true },
        onOpenMixer = { showMixerDialog = true },
        onOpenSoundBank = { showSoundBankDialog = true },
        onOpenTempo = { showTempoDialog = true },
        onZoomIn = { viewModel.setZoom(zoomScale + 0.2f) },
        onZoomOut = { viewModel.setZoom(zoomScale - 0.2f) },
        onAddMeasure = viewModel::addMeasure,
        onExportMidi = { viewModel.exportToMidi(context) },
        onShareScore = { viewModel.shareScore(context) },
        onSelectCell = viewModel::selectCell,
        onUpdateFret = viewModel::updateFretAtSelectedCell,
        onAddBeat = viewModel::addBeat,
        onCycleDuration = viewModel::cycleDuration,
        onMoveUp = viewModel::moveCaretUp,
        onMoveDown = viewModel::moveCaretDown,
        onMoveLeft = viewModel::moveCaretLeft,
        onMoveRight = viewModel::moveCaretRight,
    )

    if (showLeftDrawer) {
        TuxGuitarDrawerView(
            drawerTab = drawerTab,
            onTabSelected = { drawerTab = it },
            tracks = score?.tracks ?: emptyList(),
            selectedTrackIndex = selectedTrackIndex,
            savedProjectsCount = savedProjects.size,
            onDismiss = { showLeftDrawer = false },
            onOpenFile = {
                showLeftDrawer = false
                filePickerLauncher.launch(arrayOf("*/*"))
            },
            onOpenProjects = {
                showLeftDrawer = false
                showProjectsDialog = true
            },
            onSaveFile = {
                showLeftDrawer = false
                saveFileLauncher.launch("${score?.title ?: "Tab"}.txt")
            },
            onExportMidi = {
                showLeftDrawer = false
                viewModel.exportToMidi(context)
            },
            onOpenPaste = {
                showLeftDrawer = false
                showPasteDialog = true
            },
            onShare = {
                showLeftDrawer = false
                viewModel.shareScore(context)
            },
            onSelectTrack = viewModel::selectTrack,
            onToggleMute = viewModel::toggleMuteTrack,
            onToggleSolo = viewModel::toggleSoloTrack,
            onAddTrackClick = {
                showLeftDrawer = false
                showAddTrackDialog = true
            },
            onOpenMixer = {
                showLeftDrawer = false
                showMixerDialog = true
            },
            onOpenSongInfo = {
                showLeftDrawer = false
                showSongInfoDialog = true
            }
        )
    }

    // Dialogs
    if (showSongInfoDialog) {
        val currentScore = score
        var titleText by remember { mutableStateOf(currentScore?.title ?: "") }
        var artistText by remember { mutableStateOf(currentScore?.artist ?: "") }
        var tempoText by remember { mutableStateOf((currentScore?.tempo ?: 120).toString()) }

        AlertDialog(
            onDismissRequest = { showSongInfoDialog = false },
            title = { Text("TuxGuitar Song Information", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = titleText,
                        onValueChange = { titleText = it },
                        label = { Text("Title") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = artistText,
                        onValueChange = { artistText = it },
                        label = { Text("Artist") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = tempoText,
                        onValueChange = { tempoText = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Tempo (BPM)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val bpm = tempoText.toIntOrNull() ?: 120
                        viewModel.updateSongInfo(titleText, artistText, bpm)
                        showSongInfoDialog = false
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showSongInfoDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showTempoDialog) {
        var tempoVal by remember { mutableStateOf(score?.tempo ?: 120) }
        AlertDialog(
            onDismissRequest = { showTempoDialog = false },
            title = { Text("Change Tempo", fontWeight = FontWeight.Bold) },
            text = {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("$tempoVal BPM", fontSize = 24.sp, fontWeight = FontWeight.Black)
                    Slider(
                        value = tempoVal.toFloat(),
                        onValueChange = { tempoVal = it.toInt() },
                        valueRange = 40f..260f
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.updateSongInfo(score?.title ?: "", score?.artist ?: "", tempoVal)
                    showTempoDialog = false
                }) {
                    Text("Apply")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTempoDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showMixerDialog) {
        score?.let { s ->
            MixerDialog(
                score = s,
                currentSoundBank = soundBank,
                onOpenSoundBank = {
                    showMixerDialog = false
                    showSoundBankDialog = true
                },
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

    if (showSoundBankDialog) {
        SoundBankDialog(
            currentBank = soundBank,
            onSelectBank = { bank ->
                viewModel.setSoundBank(bank)
                showSoundBankDialog = false
            },
            onDismiss = { showSoundBankDialog = false },
        )
    }
}

@Composable
fun TabEditorContent(
    score: TabScore?,
    activeTrack: TabTrack?,
    zoomScale: Float,
    isPlaying: Boolean,
    currentMeasureIndex: Int,
    currentBeatIndex: Int,
    selectedTrackIndex: Int,
    selectedMeasureIndex: Int,
    selectedBeatIndex: Int,
    selectedStringIndex: Int,
    activeDuration: NoteDuration,
    showTabKeyboard: Boolean,
    showFretboard: Boolean,
    showPiano: Boolean,
    showOverflowMenu: Boolean,
    snackbarHostState: SnackbarHostState,
    onToggleDrawer: () -> Unit,
    onToggleKeyboard: () -> Unit,
    onTogglePlay: () -> Unit,
    onStopPlayback: () -> Unit,
    onToggleFretboard: () -> Unit,
    onTogglePiano: () -> Unit,
    onToggleOverflow: () -> Unit,
    onDismissOverflow: () -> Unit,
    onOpenSongInfo: () -> Unit,
    onOpenMixer: () -> Unit,
    onOpenSoundBank: () -> Unit,
    onOpenTempo: () -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onAddMeasure: () -> Unit,
    onExportMidi: () -> Unit,
    onShareScore: () -> Unit,
    onSelectCell: (Int, Int, Int) -> Unit,
    onUpdateFret: (Int?) -> Unit,
    onAddBeat: () -> Unit,
    onCycleDuration: (Int) -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onMoveLeft: () -> Unit,
    onMoveRight: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF14171D))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TabEditorTopBar(
                score = score,
                activeTrack = activeTrack,
                showTabKeyboard = showTabKeyboard,
                showFretboard = showFretboard,
                isPlaying = isPlaying,
                showOverflowMenu = showOverflowMenu,
                onToggleDrawer = onToggleDrawer,
                onToggleKeyboard = onToggleKeyboard,
                onTogglePlay = onTogglePlay,
                onStopPlayback = onStopPlayback,
                onToggleFretboard = onToggleFretboard,
                onToggleOverflow = onToggleOverflow,
                onDismissOverflow = onDismissOverflow,
                onOpenSongInfo = onOpenSongInfo,
                onOpenMixer = onOpenMixer,
                onOpenSoundBank = onOpenSoundBank,
                onTogglePiano = onTogglePiano,
                onOpenTempo = onOpenTempo,
                onZoomIn = onZoomIn,
                onZoomOut = onZoomOut,
                onAddMeasure = onAddMeasure,
                onExportMidi = onExportMidi,
                onShareScore = onShareScore,
            )

            AnimatedVisibility(visible = showFretboard) {
                TuxGuitarFretboardView(
                    activeTrack = activeTrack,
                    selectedMeasure = selectedMeasureIndex,
                    selectedBeat = selectedBeatIndex,
                    selectedString = selectedStringIndex,
                    onFretClicked = { fret, stringIdx ->
                        onSelectCell(selectedMeasureIndex, selectedBeatIndex, stringIdx)
                        onUpdateFret(fret)
                    }
                )
            }

            AnimatedVisibility(visible = showPiano) {
                TuxGuitarPianoView(
                    onKeyClicked = { pitch ->
                        onUpdateFret(pitch % 12)
                    }
                )
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(Color(0xFF0F1217))
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
                                    color = Color(0xFFE5E9F0),
                                ),
                                modifier = Modifier.padding(12.dp),
                            )
                        }
                    } else {
                        TuxGuitarScoreView(
                            score = currentScore,
                            trackIndex = selectedTrackIndex,
                            zoomScale = zoomScale,
                            isPlaying = isPlaying,
                            playbackMeasure = currentMeasureIndex,
                            playbackBeat = currentBeatIndex,
                            selectedMeasure = selectedMeasureIndex,
                            selectedBeat = selectedBeatIndex,
                            selectedString = selectedStringIndex,
                            onCellSelected = onSelectCell,
                        )
                    }
                }
            }

            AnimatedVisibility(
                visible = showTabKeyboard,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            ) {
                TuxGuitarTabKeyboard(
                    activeDuration = activeDuration,
                    onNumberClick = onUpdateFret,
                    onInsertClick = onAddBeat,
                    onDeleteClick = { onUpdateFret(null) },
                    onIncrementDuration = { onCycleDuration(-1) },
                    onDecrementDuration = { onCycleDuration(1) },
                    onUpClick = onMoveUp,
                    onDownClick = onMoveDown,
                    onLeftClick = onMoveLeft,
                    onRightClick = onMoveRight,
                    onSelectClick = onTogglePlay,
                )
            }
        }

        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}

@Composable
private fun TabEditorTopBar(
    score: TabScore?,
    activeTrack: TabTrack?,
    showTabKeyboard: Boolean,
    showFretboard: Boolean,
    isPlaying: Boolean,
    showOverflowMenu: Boolean,
    onToggleDrawer: () -> Unit,
    onToggleKeyboard: () -> Unit,
    onTogglePlay: () -> Unit,
    onStopPlayback: () -> Unit,
    onToggleFretboard: () -> Unit,
    onToggleOverflow: () -> Unit,
    onDismissOverflow: () -> Unit,
    onOpenSongInfo: () -> Unit,
    onOpenMixer: () -> Unit,
    onOpenSoundBank: () -> Unit,
    onTogglePiano: () -> Unit,
    onOpenTempo: () -> Unit,
    onZoomIn: () -> Unit,
    onZoomOut: () -> Unit,
    onAddMeasure: () -> Unit,
    onExportMidi: () -> Unit,
    onShareScore: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(Color(0xFF1E232B))
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            IconButton(
                onClick = onToggleDrawer,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "TuxGuitar Main Drawer",
                    tint = Color(0xFFFBBF24)
                )
            }

            Column(
                modifier = Modifier
                    .padding(start = 4.dp)
                    .clickable(onClick = onOpenSongInfo)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = score?.title ?: "TabLab Studio",
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Spacer(Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(0xFF2E3440))
                            .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = activeTrack?.instrumentType?.displayName ?: "Guitar",
                            color = Color(0xFFD8DEE9),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
                Text(
                    text = "${score?.artist ?: "TuxGuitar"} • ♩=${score?.tempo ?: 120}",
                    color = Color(0xFF9CA3AF),
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onToggleKeyboard,
                modifier = Modifier.size(38.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Toggle TGTabKeyboard",
                    tint = if (showTabKeyboard) Color(0xFFF59E0B) else Color(0xFF9CA3AF)
                )
            }

            IconButton(
                onClick = onTogglePlay,
                modifier = Modifier.size(38.dp)
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Play/Pause",
                    tint = if (isPlaying) Color(0xFF10B981) else Color.White
                )
            }

            IconButton(
                onClick = onStopPlayback,
                modifier = Modifier.size(38.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Stop,
                    contentDescription = "Stop",
                    tint = Color(0xFFEF4444)
                )
            }

            IconButton(
                onClick = onToggleFretboard,
                modifier = Modifier.size(38.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.GridOn,
                    contentDescription = "Toggle Fretboard",
                    tint = if (showFretboard) Color(0xFFF59E0B) else Color(0xFF9CA3AF)
                )
            }

            Box {
                IconButton(
                    onClick = onToggleOverflow,
                    modifier = Modifier.size(38.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Menu",
                        tint = Color.White
                    )
                }

                DropdownMenu(
                    expanded = showOverflowMenu,
                    onDismissRequest = onDismissOverflow,
                    modifier = Modifier.background(Color(0xFF242933))
                ) {
                    DropdownMenuItem(
                        text = { Text("Song Info...", color = Color.White) },
                        onClick = { onDismissOverflow(); onOpenSongInfo() },
                        leadingIcon = { Icon(Icons.Default.Info, null, tint = Color(0xFFF59E0B)) }
                    )
                    DropdownMenuItem(
                        text = { Text("Multi-Track Audio Mixer...", color = Color.White) },
                        onClick = { onDismissOverflow(); onOpenMixer() },
                        leadingIcon = { Icon(Icons.Default.Tune, null, tint = Color(0xFFF59E0B)) }
                    )
                    DropdownMenuItem(
                        text = { Text("TuxGuitar SoundBank (Gervill)...", color = Color.White) },
                        onClick = { onDismissOverflow(); onOpenSoundBank() },
                        leadingIcon = { Icon(Icons.Default.GraphicEq, null, tint = Color(0xFFF59E0B)) }
                    )
                    DropdownMenuItem(
                        text = { Text("Toggle Piano View", color = Color.White) },
                        onClick = { onDismissOverflow(); onTogglePiano() },
                        leadingIcon = { Icon(Icons.Default.MusicNote, null, tint = Color(0xFFF59E0B)) }
                    )
                    DropdownMenuItem(
                        text = { Text("Tempo (BPM)...", color = Color.White) },
                        onClick = { onDismissOverflow(); onOpenTempo() },
                        leadingIcon = { Icon(Icons.Default.Tune, null, tint = Color(0xFFF59E0B)) }
                    )
                    DropdownMenuItem(
                        text = { Text("Zoom In (+)", color = Color.White) },
                        onClick = { onDismissOverflow(); onZoomIn() },
                        leadingIcon = { Icon(Icons.Default.ZoomIn, null, tint = Color.White) }
                    )
                    DropdownMenuItem(
                        text = { Text("Zoom Out (-)", color = Color.White) },
                        onClick = { onDismissOverflow(); onZoomOut() },
                        leadingIcon = { Icon(Icons.Default.ZoomOut, null, tint = Color.White) }
                    )
                    DropdownMenuItem(
                        text = { Text("Add Measure", color = Color.White) },
                        onClick = { onDismissOverflow(); onAddMeasure() },
                        leadingIcon = { Icon(Icons.Default.Add, null, tint = Color.White) }
                    )
                    DropdownMenuItem(
                        text = { Text("Export MIDI File", color = Color.White) },
                        onClick = { onDismissOverflow(); onExportMidi() },
                        leadingIcon = { Icon(Icons.Default.MusicNote, null, tint = Color.White) }
                    )
                    DropdownMenuItem(
                        text = { Text("Share Tab", color = Color.White) },
                        onClick = { onDismissOverflow(); onShareScore() },
                        leadingIcon = { Icon(Icons.Default.Share, null, tint = Color.White) }
                    )
                }
            }
        }
    }
}

@Composable
private fun TuxGuitarDrawerView(
    drawerTab: Int,
    onTabSelected: (Int) -> Unit,
    tracks: List<TabTrack>,
    selectedTrackIndex: Int,
    savedProjectsCount: Int,
    onDismiss: () -> Unit,
    onOpenFile: () -> Unit,
    onOpenProjects: () -> Unit,
    onSaveFile: () -> Unit,
    onExportMidi: () -> Unit,
    onOpenPaste: () -> Unit,
    onShare: () -> Unit,
    onSelectTrack: (Int) -> Unit,
    onToggleMute: (Int) -> Unit,
    onToggleSolo: (Int) -> Unit,
    onAddTrackClick: () -> Unit,
    onOpenMixer: () -> Unit,
    onOpenSongInfo: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.55f))
            .clickable(onClick = onDismiss)
    ) {
        Card(
            modifier = Modifier
                .fillMaxHeight()
                .width(280.dp)
                .clickable(enabled = false) {},
            shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF1E232B))
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF14171D))
                        .padding(top = 10.dp, start = 12.dp, end = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TuxGuitar Menu",
                        color = Color(0xFFFBBF24),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, null, tint = Color(0xFF9CA3AF))
                    }
                }

                TabRow(
                    selectedTabIndex = drawerTab,
                    containerColor = Color(0xFF14171D),
                    contentColor = Color(0xFFFBBF24),
                ) {
                    Tab(
                        selected = drawerTab == 0,
                        onClick = { onTabSelected(0) },
                        text = { Text("FILE", fontWeight = FontWeight.Bold) }
                    )
                    Tab(
                        selected = drawerTab == 1,
                        onClick = { onTabSelected(1) },
                        text = { Text("TRACKS (${tracks.size})", fontWeight = FontWeight.Bold) }
                    )
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    if (drawerTab == 0) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            DrawerItemButton(
                                icon = Icons.Default.FileOpen,
                                title = "Open Tab File (.gp, .gp5, .gpx, .ptb)",
                                onClick = onOpenFile
                            )
                            DrawerItemButton(
                                icon = Icons.Default.Folder,
                                title = "Saved Projects ($savedProjectsCount)",
                                onClick = onOpenProjects
                            )
                            DrawerItemButton(
                                icon = Icons.Default.Save,
                                title = "Export / Save File",
                                onClick = onSaveFile
                            )
                            DrawerItemButton(
                                icon = Icons.Default.MusicNote,
                                title = "Export to MIDI",
                                onClick = onExportMidi
                            )
                            DrawerItemButton(
                                icon = Icons.Default.Edit,
                                title = "Paste ASCII / Text Tab",
                                onClick = onOpenPaste
                            )
                            DrawerItemButton(
                                icon = Icons.Default.Share,
                                title = "Share Tab",
                                onClick = onShare
                            )
                        }
                    } else {
                        Column(modifier = Modifier.fillMaxSize()) {
                            LazyColumn(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                itemsIndexed(tracks) { idx, track ->
                                    val isSelected = selectedTrackIndex == idx
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onSelectTrack(idx) },
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (isSelected) Color(0xFF2C323E) else Color(0xFF1E232B)
                                        ),
                                        border = if (isSelected) BorderStroke(1.dp, Color(0xFFF59E0B)) else null
                                    ) {
                                        Column(modifier = Modifier.padding(10.dp)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween,
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = "${idx + 1}. ${track.name}",
                                                        color = Color.White,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 13.sp
                                                    )
                                                    Text(
                                                        text = track.instrumentType.displayName,
                                                        color = Color(0xFF9CA3AF),
                                                        fontSize = 11.sp
                                                    )
                                                }
                                                Row {
                                                    FilterChip(
                                                        selected = track.isMuted,
                                                        onClick = { onToggleMute(idx) },
                                                        label = { Text("M", fontSize = 10.sp) },
                                                        modifier = Modifier.size(32.dp)
                                                    )
                                                    Spacer(Modifier.width(4.dp))
                                                    FilterChip(
                                                        selected = track.isSolo,
                                                        onClick = { onToggleSolo(idx) },
                                                        label = { Text("S", fontSize = 10.sp) },
                                                        modifier = Modifier.size(32.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(Modifier.height(8.dp))
                            Button(
                                onClick = onAddTrackClick,
                                modifier = Modifier.fillMaxWidth(),
                                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFD97706)
                                )
                            ) {
                                Icon(Icons.Default.Add, null)
                                Spacer(Modifier.width(6.dp))
                                Text("Add Track")
                            }
                        }
                    }
                }

                HorizontalDivider(color = Color(0xFF2E3440))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF14171D))
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onOpenMixer) {
                        Icon(Icons.Default.Tune, contentDescription = "Mixer", tint = Color(0xFFF59E0B))
                    }
                    Text(
                        text = "TuxGuitar Engine",
                        color = Color(0xFF6B7280),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    IconButton(onClick = onOpenSongInfo) {
                        Icon(Icons.Default.Info, contentDescription = "Song Info", tint = Color(0xFFF59E0B))
                    }
                }
            }
        }
    }
}

// ==================== PREVIEWS ====================

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun TabEditorContentPreview() {
    GuitarLabTheme {
        TabEditorContent(
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
                    )
                )
            ),
            activeTrack = TabTrack(
                name = "Lead Guitar",
                instrumentType = InstrumentType.GUITAR_7,
                tuningName = "Standard E",
                measures = listOf(TabMeasure(1))
            ),
            zoomScale = 1.0f,
            isPlaying = false,
            currentMeasureIndex = 0,
            currentBeatIndex = 0,
            selectedTrackIndex = 0,
            selectedMeasureIndex = 0,
            selectedBeatIndex = 0,
            selectedStringIndex = 0,
            activeDuration = NoteDuration.QUARTER,
            showTabKeyboard = true,
            showFretboard = false,
            showPiano = false,
            showOverflowMenu = false,
            snackbarHostState = remember { SnackbarHostState() },
            onToggleDrawer = {},
            onToggleKeyboard = {},
            onTogglePlay = {},
            onStopPlayback = {},
            onToggleFretboard = {},
            onTogglePiano = {},
            onToggleOverflow = {},
            onDismissOverflow = {},
            onOpenSongInfo = {},
            onOpenMixer = {},
            onOpenSoundBank = {},
            onOpenTempo = {},
            onZoomIn = {},
            onZoomOut = {},
            onAddMeasure = {},
            onExportMidi = {},
            onShareScore = {},
            onSelectCell = { _, _, _ -> },
            onUpdateFret = {},
            onAddBeat = {},
            onCycleDuration = {},
            onMoveUp = {},
            onMoveDown = {},
            onMoveLeft = {},
            onMoveRight = {},
        )
    }
}