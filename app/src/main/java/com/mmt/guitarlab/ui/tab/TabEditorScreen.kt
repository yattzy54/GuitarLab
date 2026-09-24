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
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Folder
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
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

/**
 * Authentic TuxGuitar Android Interface:
 * Faithfully mirrors the layout and user experience of TuxGuitar-android (phiresky/tuxguitar):
 * 1. Classic TuxGuitar Action Bar (Drawer menu, Keyboard toggle, Transport Play/Stop, Fretboard toggle, Overflow menus)
 * 2. Left TGMainDrawer with [FILE] and [TRACKS] tabs + bottom [Mixer] & [Song Info] footer
 * 3. TGSongView Tablature Canvas with TuxGuitar's iconic red caret cursor & measure layout
 * 4. Iconic TGTabKeyboard bottom dock (3x4 numeric keypad, Duration ▲/▼ selector, and D-pad arrows)
 * 5. Integrated 24-fret guitar fretboard & piano overlays
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabEditorScreen(viewModel: TabViewModel = hiltViewModel()) {
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

    // TuxGuitar UI State
    var showLeftDrawer by remember { mutableStateOf(false) }
    var drawerTab by remember { mutableIntStateOf(0) } // 0: FILE, 1: TRACKS
    var showTabKeyboard by remember { mutableStateOf(true) }
    var showFretboard by remember { mutableStateOf(false) }
    var showPiano by remember { mutableStateOf(false) }
    var showOverflowMenu by remember { mutableStateOf(false) }

    // Dialogs
    var showSongInfoDialog by remember { mutableStateOf(false) }
    var showMixerDialog by remember { mutableStateOf(false) }
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

    // Handle back button when drawer is open
    BackHandler(enabled = showLeftDrawer) {
        showLeftDrawer = false
    }

    val activeTrack = score?.tracks?.getOrNull(selectedTrackIndex) ?: score?.tracks?.firstOrNull()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF14171D))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // ==========================================
            // 1. TOP ACTION BAR (Classic TuxGuitar Android style)
            // ==========================================
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .background(Color(0xFF1E232B))
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Drawer Toggle Button (Hamburger) + Title
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    IconButton(
                        onClick = { showLeftDrawer = true },
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
                            .clickable { showSongInfoDialog = true }
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

                // Right: Action Icons (Keyboard Toggle, Transport Play/Stop, Fretboard, Overflow)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Keyboard toggle button (TuxGuitar's edit icon)
                    IconButton(
                        onClick = { showTabKeyboard = !showTabKeyboard },
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Toggle TGTabKeyboard",
                            tint = if (showTabKeyboard) Color(0xFFF59E0B) else Color(0xFF9CA3AF)
                        )
                    }

                    // Play / Pause Transport
                    IconButton(
                        onClick = viewModel::togglePlay,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Play/Pause",
                            tint = if (isPlaying) Color(0xFF10B981) else Color.White
                        )
                    }

                    // Stop Transport
                    IconButton(
                        onClick = viewModel::stopPlayback,
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Stop,
                            contentDescription = "Stop",
                            tint = Color(0xFFEF4444)
                        )
                    }

                    // Fretboard Toggle
                    IconButton(
                        onClick = { showFretboard = !showFretboard },
                        modifier = Modifier.size(38.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.GridOn,
                            contentDescription = "Toggle Fretboard",
                            tint = if (showFretboard) Color(0xFFF59E0B) else Color(0xFF9CA3AF)
                        )
                    }

                    // Overflow Menu (TuxGuitar Menus)
                    Box {
                        IconButton(
                            onClick = { showOverflowMenu = true },
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
                            onDismissRequest = { showOverflowMenu = false },
                            modifier = Modifier.background(Color(0xFF242933))
                        ) {
                            DropdownMenuItem(
                                text = { Text("Song Info...", color = Color.White) },
                                onClick = {
                                    showOverflowMenu = false
                                    showSongInfoDialog = true
                                },
                                leadingIcon = { Icon(Icons.Default.Info, null, tint = Color(0xFFF59E0B)) }
                            )
                            DropdownMenuItem(
                                text = { Text("Multi-Track Audio Mixer...", color = Color.White) },
                                onClick = {
                                    showOverflowMenu = false
                                    showMixerDialog = true
                                },
                                leadingIcon = { Icon(Icons.Default.Tune, null, tint = Color(0xFFF59E0B)) }
                            )
                            DropdownMenuItem(
                                text = { Text("Toggle Piano View", color = Color.White) },
                                onClick = {
                                    showOverflowMenu = false
                                    showPiano = !showPiano
                                },
                                leadingIcon = { Icon(Icons.Default.MusicNote, null, tint = Color(0xFFF59E0B)) }
                            )
                            DropdownMenuItem(
                                text = { Text("Tempo (BPM)...", color = Color.White) },
                                onClick = {
                                    showOverflowMenu = false
                                    showTempoDialog = true
                                },
                                leadingIcon = { Icon(Icons.Default.Tune, null, tint = Color(0xFFF59E0B)) }
                            )
                            DropdownMenuItem(
                                text = { Text("Zoom In (+)", color = Color.White) },
                                onClick = {
                                    viewModel.setZoom(zoomScale + 0.2f)
                                    showOverflowMenu = false
                                },
                                leadingIcon = { Icon(Icons.Default.ZoomIn, null, tint = Color.White) }
                            )
                            DropdownMenuItem(
                                text = { Text("Zoom Out (-)", color = Color.White) },
                                onClick = {
                                    viewModel.setZoom(zoomScale - 0.2f)
                                    showOverflowMenu = false
                                },
                                leadingIcon = { Icon(Icons.Default.ZoomOut, null, tint = Color.White) }
                            )
                            DropdownMenuItem(
                                text = { Text("Add Measure", color = Color.White) },
                                onClick = {
                                    viewModel.addMeasure()
                                    showOverflowMenu = false
                                },
                                leadingIcon = { Icon(Icons.Default.Add, null, tint = Color.White) }
                            )
                            DropdownMenuItem(
                                text = { Text("Export MIDI File", color = Color.White) },
                                onClick = {
                                    showOverflowMenu = false
                                    viewModel.exportToMidi(context)
                                },
                                leadingIcon = { Icon(Icons.Default.MusicNote, null, tint = Color.White) }
                            )
                            DropdownMenuItem(
                                text = { Text("Share Tab", color = Color.White) },
                                onClick = {
                                    showOverflowMenu = false
                                    viewModel.shareScore(context)
                                },
                                leadingIcon = { Icon(Icons.Default.Share, null, tint = Color.White) }
                            )
                        }
                    }
                }
            }

            // ==========================================
            // 2. OPTIONAL VIEW OVERLAYS (Fretboard & Piano)
            // ==========================================
            AnimatedVisibility(visible = showFretboard) {
                TuxGuitarFretboardView(
                    activeTrack = activeTrack,
                    selectedMeasure = selectedMeasureIndex,
                    selectedBeat = selectedBeatIndex,
                    selectedString = selectedStringIndex,
                    onFretClicked = { fret ->
                        viewModel.updateFretAtSelectedCell(fret)
                    }
                )
            }

            AnimatedVisibility(visible = showPiano) {
                TuxGuitarPianoView(
                    onKeyClicked = { pitch ->
                        viewModel.updateFretAtSelectedCell(pitch % 12)
                    }
                )
            }

            // ==========================================
            // 3. MAIN BODY: TABLATURE CANVAS (TGSongView)
            // ==========================================
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
                            onCellSelected = viewModel::selectCell,
                        )
                    }
                } ?: Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "TuxGuitar TabLab Studio",
                            color = Color(0xFFFBBF24),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "Откройте файл .gp, .gp5, .gp4, .gp3 или проект из бокового меню",
                            color = Color(0xFF9CA3AF),
                            fontSize = 13.sp
                        )
                        Spacer(Modifier.height(14.dp))
                        Button(
                            onClick = { showLeftDrawer = true },
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFD97706)
                            )
                        ) {
                            Icon(Icons.Default.Menu, null)
                            Spacer(Modifier.width(6.dp))
                            Text("Открыть меню (Drawer)")
                        }
                    }
                }
            }

            // ==========================================
            // 4. THE ICONIC TGTABKEYBOARD (view_tab_keyboard.xml)
            // ==========================================
            AnimatedVisibility(
                visible = showTabKeyboard,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
            ) {
                TuxGuitarTabKeyboard(
                    activeDuration = activeDuration,
                    onNumberClick = { num ->
                        viewModel.updateFretAtSelectedCell(num)
                    },
                    onInsertClick = {
                        viewModel.addBeat()
                    },
                    onDeleteClick = {
                        viewModel.updateFretAtSelectedCell(null)
                    },
                    onIncrementDuration = {
                        viewModel.cycleDuration(-1) // to shorter note
                    },
                    onDecrementDuration = {
                        viewModel.cycleDuration(1)  // to longer note
                    },
                    onUpClick = {
                        viewModel.moveCaretUp()
                    },
                    onDownClick = {
                        viewModel.moveCaretDown()
                    },
                    onLeftClick = {
                        viewModel.moveCaretLeft()
                    },
                    onRightClick = {
                        viewModel.moveCaretRight()
                    },
                    onSelectClick = {
                        // Retrigger active fret or toggle play
                        viewModel.togglePlay()
                    }
                )
            }
        }

        // ==========================================
        // 5. LEFT DRAWER (view_main_drawer.xml)
        // ==========================================
        if (showLeftDrawer) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.55f))
                    .clickable { showLeftDrawer = false }
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(280.dp)
                        .clickable(enabled = false) {}, // prevent clicks from closing
                    shape = RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E232B))
                ) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Header with Tabs: [ FILE ] and [ TRACKS ]
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
                            IconButton(onClick = { showLeftDrawer = false }) {
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
                                onClick = { drawerTab = 0 },
                                text = { Text("FILE", fontWeight = FontWeight.Bold) }
                            )
                            Tab(
                                selected = drawerTab == 1,
                                onClick = { drawerTab = 1 },
                                text = { Text("TRACKS (${score?.tracks?.size ?: 0})", fontWeight = FontWeight.Bold) }
                            )
                        }

                        // Tab Content
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            if (drawerTab == 0) {
                                // FILE TAB
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .verticalScroll(rememberScrollState()),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    DrawerItemButton(
                                        icon = Icons.Default.FileOpen,
                                        title = "Open Tab File (.gp, .gp5, .gpx, .ptb)",
                                        onClick = {
                                            showLeftDrawer = false
                                            filePickerLauncher.launch(arrayOf("*/*"))
                                        }
                                    )
                                    DrawerItemButton(
                                        icon = Icons.Default.Folder,
                                        title = "Saved Projects (${savedProjects.size})",
                                        onClick = {
                                            showLeftDrawer = false
                                            showProjectsDialog = true
                                        }
                                    )
                                    DrawerItemButton(
                                        icon = Icons.Default.Save,
                                        title = "Export / Save File",
                                        onClick = {
                                            showLeftDrawer = false
                                            val fileName = "${score?.title ?: "Tab"}.txt"
                                            saveFileLauncher.launch(fileName)
                                        }
                                    )
                                    DrawerItemButton(
                                        icon = Icons.Default.MusicNote,
                                        title = "Export to MIDI",
                                        onClick = {
                                            showLeftDrawer = false
                                            viewModel.exportToMidi(context)
                                        }
                                    )
                                    DrawerItemButton(
                                        icon = Icons.Default.Edit,
                                        title = "Paste ASCII / Text Tab",
                                        onClick = {
                                            showLeftDrawer = false
                                            showPasteDialog = true
                                        }
                                    )
                                    DrawerItemButton(
                                        icon = Icons.Default.Share,
                                        title = "Share Tab",
                                        onClick = {
                                            showLeftDrawer = false
                                            viewModel.shareScore(context)
                                        }
                                    )
                                }
                            } else {
                                // TRACKS TAB
                                Column(modifier = Modifier.fillMaxSize()) {
                                    LazyColumn(
                                        modifier = Modifier.weight(1f),
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        val tracks = score?.tracks ?: emptyList()
                                        itemsIndexed(tracks) { idx, track ->
                                            val isSelected = selectedTrackIndex == idx
                                            Card(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .clickable { viewModel.selectTrack(idx) },
                                                colors = CardDefaults.cardColors(
                                                    containerColor = if (isSelected) Color(0xFF2C323E) else Color(0xFF1E232B)
                                                ),
                                                border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B)) else null
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
                                                                onClick = { viewModel.toggleMuteTrack(idx) },
                                                                label = { Text("M", fontSize = 10.sp) },
                                                                modifier = Modifier.size(32.dp)
                                                            )
                                                            Spacer(Modifier.width(4.dp))
                                                            FilterChip(
                                                                selected = track.isSolo,
                                                                onClick = { viewModel.toggleSoloTrack(idx) },
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
                                        onClick = {
                                            showLeftDrawer = false
                                            showAddTrackDialog = true
                                        },
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

                        // Drawer Footer Panel (Mixer & Song Info buttons matching view_main_drawer.xml)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF14171D))
                                .padding(horizontal = 14.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {
                                    showLeftDrawer = false
                                    showMixerDialog = true
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Tune,
                                    contentDescription = "Mixer",
                                    tint = Color(0xFFF59E0B)
                                )
                            }
                            Text(
                                text = "TuxGuitar Engine",
                                color = Color(0xFF6B7280),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            IconButton(
                                onClick = {
                                    showLeftDrawer = false
                                    showSongInfoDialog = true
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Song Info",
                                    tint = Color(0xFFF59E0B)
                                )
                            }
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

    // ==========================================
    // DIALOGS
    // ==========================================
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
                TextButton(onClick = { showSongInfoDialog = false }) {
                    Text("Cancel")
                }
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

/**
 * The Iconic TGTabKeyboard (view_tab_keyboard.xml from TuxGuitar Android):
 * - Left: 3x4 keypad (7, 8, 9, Ins / 4, 5, 6, Del / 1, 2, 3, 0)
 * - Center: Duration controls (▲ Inc, note icon/value, ▼ Dec)
 * - Right: D-Pad navigation arrows (Up, Down, Left, Right, Select)
 */
@Composable
private fun TuxGuitarTabKeyboard(
    activeDuration: NoteDuration,
    onNumberClick: (Int) -> Unit,
    onInsertClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onIncrementDuration: () -> Unit,
    onDecrementDuration: () -> Unit,
    onUpClick: () -> Unit,
    onDownClick: () -> Unit,
    onLeftClick: () -> Unit,
    onRightClick: () -> Unit,
    onSelectClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF2E3440), RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)),
        shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E232B))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // LEFT: 4x3 Keypad
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                // Row 1: 7, 8, 9, Ins
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    KeypadButton("7") { onNumberClick(7) }
                    KeypadButton("8") { onNumberClick(8) }
                    KeypadButton("9") { onNumberClick(9) }
                    KeypadButton("Ins", isSpecial = true) { onInsertClick() }
                }
                // Row 2: 4, 5, 6, Del
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    KeypadButton("4") { onNumberClick(4) }
                    KeypadButton("5") { onNumberClick(5) }
                    KeypadButton("6") { onNumberClick(6) }
                    KeypadButton("Del", isSpecial = true) { onDeleteClick() }
                }
                // Row 3: 1, 2, 3, 0
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    KeypadButton("1") { onNumberClick(1) }
                    KeypadButton("2") { onNumberClick(2) }
                    KeypadButton("3") { onNumberClick(3) }
                    KeypadButton("0") { onNumberClick(0) }
                }
            }

            // CENTER: Duration Controls
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(3.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF14171D))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                IconButton(
                    onClick = onIncrementDuration,
                    modifier = Modifier.size(28.dp)
                ) {
                    Text("▲", color = Color(0xFFFBBF24), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = when (activeDuration) {
                            NoteDuration.WHOLE -> "𝅝 1"
                            NoteDuration.HALF -> "𝅗𝅥 1/2"
                            NoteDuration.QUARTER -> "♩ 1/4"
                            NoteDuration.EIGHTH -> "♪ 1/8"
                            NoteDuration.SIXTEENTH -> "𝅘𝅥𝅯 1/16"
                            NoteDuration.THIRTY_SECOND -> "𝅘𝅥𝅰 1/32"
                        },
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = onDecrementDuration,
                    modifier = Modifier.size(28.dp)
                ) {
                    Text("▼", color = Color(0xFFFBBF24), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            // RIGHT: D-Pad Navigation
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                // Up
                DpadButton("▲") { onUpClick() }

                // Left, Center/Select, Right
                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    DpadButton("◀") { onLeftClick() }
                    DpadButton("•", isCenter = true) { onSelectClick() }
                    DpadButton("▶") { onRightClick() }
                }

                // Down
                DpadButton("▼") { onDownClick() }
            }
        }
    }
}

@Composable
private fun KeypadButton(
    text: String,
    isSpecial: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(width = 32.dp, height = 30.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(if (isSpecial) Color(0xFF3B4252) else Color(0xFF2E3440))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isSpecial) Color(0xFFFBBF24) else Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = if (text.length > 1) 10.sp else 13.sp
        )
    }
}

@Composable
private fun DpadButton(
    text: String,
    isCenter: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(width = 32.dp, height = 28.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(if (isCenter) Color(0xFFD97706) else Color(0xFF2E3440))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = if (isCenter) Color.Black else Color.White,
            fontWeight = FontWeight.Black,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun DrawerItemButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF242933))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = Color(0xFFF59E0B), modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(10.dp))
        Text(text = title, color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
    }
}

/**
 * 24-Fret TuxGuitar Guitar Fretboard View
 */
@Composable
private fun TuxGuitarFretboardView(
    activeTrack: com.mmt.guitarlab.domain.model.TabTrack?,
    selectedMeasure: Int,
    selectedBeat: Int,
    selectedString: Int,
    onFretClicked: (Int) -> Unit
) {
    val stringLabels = activeTrack?.stringLabels ?: listOf("E", "B", "G", "D", "A", "E")
    val stringCount = stringLabels.size

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .padding(horizontal = 6.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1712)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp)
        ) {
            Canvas(modifier = Modifier.size(width = 850.dp, height = 120.dp)) {
                val fretCount = 24
                val fretWidth = size.width / (fretCount + 1)
                val stringSpacing = size.height / (stringCount + 1)

                // Fret markers (dots at 3, 5, 7, 9, 12, 15, 17, 19, 21, 24)
                val singleDots = listOf(3, 5, 7, 9, 15, 17, 19, 21)
                singleDots.forEach { fret ->
                    val cx = fret * fretWidth - fretWidth / 2
                    drawCircle(
                        color = Color(0xFF888888).copy(alpha = 0.5f),
                        radius = 4.dp.toPx(),
                        center = Offset(cx, size.height / 2)
                    )
                }
                // Double dots at 12 and 24
                listOf(12, 24).forEach { fret ->
                    val cx = fret * fretWidth - fretWidth / 2
                    drawCircle(
                        color = Color(0xFF888888).copy(alpha = 0.6f),
                        radius = 3.5.dp.toPx(),
                        center = Offset(cx, size.height * 0.3f)
                    )
                    drawCircle(
                        color = Color(0xFF888888).copy(alpha = 0.6f),
                        radius = 3.5.dp.toPx(),
                        center = Offset(cx, size.height * 0.7f)
                    )
                }

                // Vertical Fret wires
                for (f in 0..fretCount) {
                    val x = f * fretWidth
                    drawLine(
                        color = if (f == 0) Color(0xFFE5E9F0) else Color(0xFF888888),
                        start = Offset(x, 0f),
                        end = Offset(x, size.height),
                        strokeWidth = if (f == 0) 4.dp.toPx() else 1.5.dp.toPx()
                    )
                }

                // Horizontal Guitar strings
                for (s in 0 until stringCount) {
                    val y = (s + 1) * stringSpacing
                    drawLine(
                        color = if (s == selectedString) Color(0xFFF59E0B) else Color(0xFFD8DEE9),
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = (1f + s * 0.35f).dp.toPx()
                    )
                }
            }
        }
    }
}

/**
 * 2-Octave TuxGuitar Piano View
 */
@Composable
private fun TuxGuitarPianoView(onKeyClicked: (Int) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .padding(horizontal = 6.dp, vertical = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E232B)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val whiteKeys = listOf("C", "D", "E", "F", "G", "A", "B", "C2", "D2", "E2", "F2", "G2", "A2", "B2")
            whiteKeys.forEachIndexed { idx, name ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .padding(horizontal = 1.dp)
                        .clip(RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp))
                        .background(Color.White)
                        .clickable { onKeyClicked(idx) },
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Text(name, color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 2.dp))
                }
            }
        }
    }
}

/**
 * TuxGuitar Paginated Score View (TGSongView Canvas with authentic Caret Cursor):
 */
@Composable
private fun TuxGuitarScoreView(
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
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        itemsIndexed(measureChunkedLines) { lineIndex, measureChunk ->
            val startMeasureIndex = lineIndex * measuresPerLine

            TuxGuitarSystemRow(
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
private fun TuxGuitarSystemRow(
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

    val lineTrackColor = Color(0xFF4C566A)
    val staffTextColor = Color(0xFFD8DEE9)
    val primaryColor = Color(0xFFE5E9F0)
    val measureBarColor = Color(0xFF88C0D0)
    val caretColor = Color(0xFFEF4444) // TuxGuitar iconic Red Caret Cursor!

    val totalBeatsInLine = measures.sumOf { it.beats.size }
    val canvasWidth = ((totalBeatsInLine + measures.size * 2 + 4) * colWidth.value).coerceAtLeast(340f)
    val canvasHeight = ((stringCount + 1.5f) * stringSpacing.value).coerceAtLeast(140f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF1B2028))
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 8.dp),
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
                                if (offset.x >= currX - 8f && offset.x <= currX + colW + 8f) {
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

            // 6 horizontal string lines
            for (i in 0 until stringCount) {
                val y = startY + i * spacing
                drawLine(
                    color = lineTrackColor,
                    start = Offset(startX, y),
                    end = Offset(size.width - 10f, y),
                    strokeWidth = 1.5f * zoomScale,
                )
                val label = stringLabels.getOrElse(i) { "E" }
                drawText(
                    textMeasurer = textMeasurer,
                    text = label,
                    topLeft = Offset(8f * zoomScale, y - 8f * zoomScale),
                    style = TextStyle(color = staffTextColor, fontSize = (11 * zoomScale).sp, fontWeight = FontWeight.Bold),
                )
            }

            var xOffset = startX + 30f * zoomScale

            measures.forEachIndexed { relativeMIdx, measure ->
                val globalMIdx = startMeasureIndex + relativeMIdx

                // Vertical Measure Bar Line
                drawLine(
                    color = measureBarColor,
                    start = Offset(xOffset, startY),
                    end = Offset(xOffset, startY + (stringCount - 1) * spacing),
                    strokeWidth = 2.5f * zoomScale,
                )
                // Measure Number Header
                drawText(
                    textMeasurer = textMeasurer,
                    text = "M${measure.number}",
                    topLeft = Offset(xOffset + 2f, startY - 18f * zoomScale),
                    style = TextStyle(color = measureBarColor, fontSize = (10 * zoomScale).sp, fontWeight = FontWeight.Bold),
                )

                xOffset += 15f * zoomScale

                measure.beats.forEachIndexed { bIdx, beat ->
                    val isPlaybackActive = isPlaying && playbackMeasure == globalMIdx && playbackBeat == bIdx
                    val isSelectedCell = selectedMeasure == globalMIdx && selectedBeat == bIdx

                    // Playback Indicator Line (Amber/Green line)
                    if (isPlaybackActive) {
                        drawLine(
                            color = Color(0xFF10B981),
                            start = Offset(xOffset, startY - 8f),
                            end = Offset(xOffset, startY + (stringCount - 1) * spacing + 8f),
                            strokeWidth = 4f * zoomScale,
                        )
                    }

                    // TUXGUITAR CARET CURSOR (Red rectangle around active cell)
                    if (isSelectedCell) {
                        val selY = startY + selectedString.coerceIn(0, stringCount - 1) * spacing
                        drawRoundRect(
                            color = caretColor,
                            topLeft = Offset(xOffset - 8f * zoomScale, selY - 10f * zoomScale),
                            size = Size(16f * zoomScale, 20f * zoomScale),
                            cornerRadius = CornerRadius(3f * zoomScale, 3f * zoomScale),
                            style = Stroke(width = 2f * zoomScale)
                        )
                    }

                    // Notes on strings
                    beat.notes.forEach { note ->
                        val stringIdx = note.stringIndex.coerceIn(0, stringCount - 1)
                        val y = startY + stringIdx * spacing
                        val fretText = note.displayLabel

                        // Background pill so fret numbers are readable over string lines
                        drawCircle(
                            color = Color(0xFF14171D),
                            radius = 8f * zoomScale,
                            center = Offset(xOffset, y),
                        )

                        drawText(
                            textMeasurer = textMeasurer,
                            text = fretText,
                            topLeft = Offset(xOffset - 5f * zoomScale, y - 7f * zoomScale),
                            style = TextStyle(
                                color = if (isPlaybackActive) Color(0xFF10B981) else primaryColor,
                                fontSize = (12 * zoomScale).sp,
                                fontWeight = FontWeight.Black,
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
        title = { Text("TuxGuitar Audio Mixer", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
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
                    Text("No saved projects found.")
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
private fun AddTrackDialog(
    onAdd: (name: String, type: InstrumentType) -> Unit,
    onDismiss: () -> Unit,
) {
    var trackName by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(InstrumentType.GUITAR) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Track") },
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
                enabled = trackName.isNotBlank(),
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}

@Composable
private fun PasteTabDialog(
    onParse: (text: String, title: String) -> Unit,
    onDismiss: () -> Unit,
) {
    var text by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("Pasted Tab") }

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
                    value = text,
                    onValueChange = { text = it },
                    label = { Text("Tab Text / ASCII") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp),
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onParse(text, title) },
                enabled = text.isNotBlank(),
            ) {
                Text("Parse & Load")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}
