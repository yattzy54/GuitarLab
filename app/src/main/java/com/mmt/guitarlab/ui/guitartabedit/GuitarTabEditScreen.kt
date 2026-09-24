package com.mmt.guitarlab.ui.guitartabedit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Piano
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Redo
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.VolumeOff
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mmt.guitarlab.domain.model.InstrumentType
import com.mmt.guitarlab.domain.model.NoteDuration
import com.mmt.guitarlab.domain.model.NoteEffect
import com.mmt.guitarlab.domain.model.TabBeat
import com.mmt.guitarlab.domain.model.TabMeasure
import com.mmt.guitarlab.domain.model.TabNote
import com.mmt.guitarlab.domain.model.TabScore
import com.mmt.guitarlab.domain.model.TabTrack
import com.mmt.guitarlab.domain.model.TuxGuitarSoundBank

private val TGDarkBg = Color(0xFF0F141C)
private val TGCardBg = Color(0xFF181F2A)
private val TGCardBorder = Color(0xFF263244)
private val TGAmber = Color(0xFFF59E0B)
private val TGCyan = Color(0xFF06B6D4)
private val TGGrayText = Color(0xFF94A3B8)

@Composable
fun GuitarTabEditScreen(
    viewModel: GuitarTabEditViewModel = hiltViewModel()
) {
    val score by viewModel.score.collectAsState()
    val caret by viewModel.caret.collectAsState()
    val selectedDuration by viewModel.selectedDuration.collectAsState()
    val viewMode by viewModel.viewMode.collectAsState()
    val soundBank by viewModel.soundBank.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val isLooping by viewModel.isLooping.collectAsState()
    val isMetronomeEnabled by viewModel.isMetronomeEnabled.collectAsState()
    val zoomLevel by viewModel.zoomLevel.collectAsState()
    val canUndo by viewModel.canUndo.collectAsState()
    val canRedo by viewModel.canRedo.collectAsState()

    var showMenu by remember { mutableStateOf(false) }
    var showTrackMenu by remember { mutableStateOf(false) }
    var showTuningDialog by remember { mutableStateOf(false) }
    var showTimeSigDialog by remember { mutableStateOf(false) }
    var showTempoDialog by remember { mutableStateOf(false) }
    var showMixerDialog by remember { mutableStateOf(false) }
    var showSongInfoDialog by remember { mutableStateOf(false) }
    var showSoundBankDialog by remember { mutableStateOf(false) }
    var showDemoDialog by remember { mutableStateOf(false) }
    var showAddTrackDialog by remember { mutableStateOf(false) }

    var bottomToolTab by remember { mutableIntStateOf(0) } // 0: Keyboard, 1: Fretboard, 2: Piano

    val activeTrack = score.tracks.getOrNull(caret.trackIndex) ?: score.tracks.firstOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(TGDarkBg)
    ) {
        // TOP APP BAR (TuxGuitar Studio Header)
        Surface(
            color = Color(0xFF131923),
            tonalElevation = 4.dp,
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .wrapContentWidth(unbounded = true)
                        .padding(horizontal = 8.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left Brand & Track Selector
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF242F40))
                                .border(1.dp, TGAmber, RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.MusicNote,
                                contentDescription = null,
                                tint = TGAmber,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "GuitarTabEdit",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp,
                                    color = Color.White
                                )
                                Spacer(Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(TGAmber.copy(alpha = 0.2f))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text("TUX", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = TGAmber)
                                }
                            }

                            // Track Switcher Chip
                            Row(
                                modifier = Modifier
                                    .clickable { showTrackMenu = true }
                                    .padding(vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = activeTrack?.name ?: "Track",
                                    fontSize = 12.sp,
                                    color = TGCanvasTrackColor(caret.trackIndex),
                                    fontWeight = FontWeight.SemiBold
                                )
                                Icon(
                                    Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = Color.LightGray,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = showTrackMenu,
                                onDismissRequest = { showTrackMenu = false }
                            ) {
                                score.tracks.forEachIndexed { idx, track ->
                                    DropdownMenuItem(
                                        text = {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(8.dp)
                                                        .clip(CircleShape)
                                                        .background(TGCanvasTrackColor(idx))
                                                )
                                                Spacer(Modifier.width(8.dp))
                                                Text(track.name, fontWeight = if (idx == caret.trackIndex) FontWeight.Bold else FontWeight.Normal)
                                                Spacer(Modifier.width(8.dp))
                                                Text("(${track.tuningName})", fontSize = 11.sp, color = Color.Gray)
                                            }
                                        },
                                        onClick = {
                                            viewModel.selectTrack(idx)
                                            showTrackMenu = false
                                        }
                                    )
                                }
                                HorizontalDivider()
                                DropdownMenuItem(
                                    text = { Text("+ Add Track...", color = TGAmber) },
                                    onClick = {
                                        showTrackMenu = false
                                        showAddTrackDialog = true
                                    }
                                )
                            }
                        }
                    }

                    // Transport Controls
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { viewModel.undo() },
                            enabled = canUndo,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Undo, contentDescription = "Undo", tint = if (canUndo) Color.White else Color.DarkGray, modifier = Modifier.size(18.dp))
                        }
                        IconButton(
                            onClick = { viewModel.redo() },
                            enabled = canRedo,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Redo, contentDescription = "Redo", tint = if (canRedo) Color.White else Color.DarkGray, modifier = Modifier.size(18.dp))
                        }

                        Spacer(Modifier.width(4.dp))

                        // Play / Pause
                        TGActionCircleButton(
                            onClick = { viewModel.togglePlay() },
                            containerColor = if (isPlaying) Color(0xFFEF4444) else TGAmber,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = Color.Black,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        IconButton(
                            onClick = { viewModel.stop() },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Stop, contentDescription = "Stop", tint = Color.LightGray, modifier = Modifier.size(18.dp))
                        }

                        IconButton(
                            onClick = { viewModel.toggleLoop() },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Loop,
                                contentDescription = "Loop",
                                tint = if (isLooping) TGAmber else Color.DarkGray,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = { viewModel.toggleMetronome() },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Timer,
                                contentDescription = "Metronome",
                                tint = if (isMetronomeEnabled) TGCyan else Color.DarkGray,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // More Menu
                        Box {
                            IconButton(onClick = { showMenu = true }, modifier = Modifier.size(32.dp)) {
                                Icon(Icons.Default.MoreVert, contentDescription = "More", tint = Color.LightGray)
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Song Info...") },
                                    leadingIcon = { Icon(Icons.Default.Info, contentDescription = null) },
                                    onClick = { showMenu = false; showSongInfoDialog = true }
                                )
                                DropdownMenuItem(
                                    text = { Text("Track Tuning (${activeTrack?.tuningName})...") },
                                    leadingIcon = { Icon(Icons.Default.Tune, contentDescription = null) },
                                    onClick = { showMenu = false; showTuningDialog = true }
                                )
                                DropdownMenuItem(
                                    text = { Text("Multi-Track Mixer...") },
                                    leadingIcon = { Icon(Icons.Default.VolumeUp, contentDescription = null) },
                                    onClick = { showMenu = false; showMixerDialog = true }
                                )
                                DropdownMenuItem(
                                    text = { Text("Gervill SoundBank (${soundBank.displayName.take(15)}...)...") },
                                    leadingIcon = { Icon(Icons.Default.GraphicEq, contentDescription = null) },
                                    onClick = { showMenu = false; showSoundBankDialog = true }
                                )
                                DropdownMenuItem(
                                    text = { Text("Load Demo Tab...") },
                                    leadingIcon = { Icon(Icons.Default.LibraryMusic, contentDescription = null) },
                                    onClick = { showMenu = false; showDemoDialog = true }
                                )
                                DropdownMenuItem(
                                    text = { Text("New Blank Tab") },
                                    leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null) },
                                    onClick = { showMenu = false; viewModel.newBlankScore() }
                                )
                            }
                        }
                    }
                }

                // Sub Toolbar: Caret Pos, Time Sig, Tempo, View Mode, Measure Add/Remove
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF18202D))
                        .horizontalScroll(rememberScrollState())
                        .wrapContentWidth(unbounded = true)
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Caret Info & Signatures
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "M:${caret.measureIndex + 1}/${activeTrack?.measures?.size ?: 1} B:${caret.beatIndex + 1}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TGAmber,
                            modifier = Modifier.padding(end = 8.dp)
                        )

                        // Time Signature Chip
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF263244),
                            modifier = Modifier
                                .clickable { showTimeSigDialog = true }
                                .padding(end = 6.dp)
                        ) {
                            Text(
                                text = "${score.timeSignatureNumerator}/${score.timeSignatureDenominator}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        // Tempo Chip
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF263244),
                            modifier = Modifier
                                .clickable { showTempoDialog = true }
                                .padding(end = 6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Speed, contentDescription = null, tint = TGCyan, modifier = Modifier.size(12.dp))
                                Spacer(Modifier.width(3.dp))
                                Text(
                                    text = "${score.tempo} BPM",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    // View Mode & Measure Modifiers
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // View mode buttons (Tab, Score, Dual)
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF222B38))
                        ) {
                            TGViewMode.entries.forEach { mode ->
                                val isSel = viewMode == mode
                                Text(
                                    text = mode.title,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSel) FontWeight.Black else FontWeight.Normal,
                                    color = if (isSel) Color.Black else Color.LightGray,
                                    modifier = Modifier
                                        .background(if (isSel) TGAmber else Color.Transparent)
                                        .clickable { viewModel.setViewMode(mode) }
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(Modifier.width(8.dp))

                        // Measure Operations (+ / -)
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF263244),
                            modifier = Modifier.clickable { viewModel.addMeasure() }
                        ) {
                            Row(modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = TGAmber, modifier = Modifier.size(12.dp))
                                Text("Bar", fontSize = 10.sp, color = Color.White)
                            }
                        }

                        Spacer(Modifier.width(4.dp))

                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFF263244),
                            modifier = Modifier.clickable { viewModel.removeMeasure() }
                        ) {
                            Row(modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Remove, contentDescription = null, tint = Color.LightGray, modifier = Modifier.size(12.dp))
                                Text("Bar", fontSize = 10.sp, color = Color.LightGray)
                            }
                        }
                    }
                }
            }
        }

        // MAIN TABLATURE & SCORE VIEWPORT (TuxGuitar TGSongView)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Color(0xFF0E131A))
        ) {
            TGTablatureScoreCanvas(
                score = score,
                activeTrack = activeTrack,
                caret = caret,
                viewMode = viewMode,
                zoomLevel = zoomLevel,
                onSelectCell = { mIdx, bIdx, sIdx ->
                    viewModel.selectCell(mIdx, bIdx, sIdx)
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // TUXGUITAR ACTION TOOLBAR: DURATION & EFFECTS
        Surface(
            color = Color(0xFF141A24),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Duration buttons (1/1, 1/2, 1/4, 1/8, 1/16, 1/32)
                NoteDuration.entries.forEach { dur ->
                    val isSelected = selectedDuration == dur
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (isSelected) TGAmber else Color(0xFF222B38),
                        border = if (isSelected) BorderStroke(1.dp, Color.White) else null,
                        modifier = Modifier.clickable { viewModel.setDuration(dur) }
                    ) {
                        Text(
                            text = dur.label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.Black else Color.LightGray,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Box(modifier = Modifier.height(20.dp).width(1.dp).background(Color(0xFF334155)))

                // Effects: Bend, Slide, Hammer, Vibrato, Palm Mute, Let Ring, Dead
                val currentBeat = activeTrack?.measures?.getOrNull(caret.measureIndex)?.beats?.getOrNull(caret.beatIndex)
                val currentNote = currentBeat?.notes?.find { it.stringIndex == caret.stringIndex }
                val currentEffect = currentNote?.effect ?: NoteEffect.NONE

                listOf(
                    NoteEffect.BEND to "Bend",
                    NoteEffect.SLIDE to "Slide",
                    NoteEffect.HAMMER_ON to "H/P",
                    NoteEffect.VIBRATO to "Vib",
                    NoteEffect.PALM_MUTE to "PM",
                    NoteEffect.LET_RING to "LR",
                    NoteEffect.DEAD_NOTE to "Dead"
                ).forEach { (fx, name) ->
                    val isFxActive = currentEffect == fx
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = if (isFxActive) TGCyan else Color(0xFF1E2633),
                        modifier = Modifier.clickable { viewModel.toggleEffect(fx) }
                    ) {
                        Text(
                            text = name,
                            fontSize = 10.sp,
                            fontWeight = if (isFxActive) FontWeight.Bold else FontWeight.Normal,
                            color = if (isFxActive) Color.Black else Color(0xFFCBD5E1),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                        )
                    }
                }

                Box(modifier = Modifier.height(20.dp).width(1.dp).background(Color(0xFF334155)))

                // Semitone +/-
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFF222B38),
                    modifier = Modifier.clickable { viewModel.incrementSemitone() }
                ) {
                    Text("+1", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp))
                }
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = Color(0xFF222B38),
                    modifier = Modifier.clickable { viewModel.decrementSemitone() }
                ) {
                    Text("-1", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp))
                }
            }
        }

        // TOOL TABS SELECTOR (Keyboard / Fretboard / Piano)
        TabRow(
            selectedTabIndex = bottomToolTab,
            containerColor = Color(0xFF131923),
            contentColor = TGAmber,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[bottomToolTab]),
                    color = TGAmber
                )
            },
            modifier = Modifier.height(34.dp)
        ) {
            Tab(
                selected = bottomToolTab == 0,
                onClick = { bottomToolTab = 0 },
                text = { Text("Tab Keyboard", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
            )
            Tab(
                selected = bottomToolTab == 1,
                onClick = { bottomToolTab = 1 },
                text = { Text("Fretboard", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
            )
            Tab(
                selected = bottomToolTab == 2,
                onClick = { bottomToolTab = 2 },
                text = { Text("Piano", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
            )
        }

        // BOTTOM VIRTUAL INSTRUMENT / KEYPAD
        Surface(
            color = Color(0xFF161E29),
            modifier = Modifier.fillMaxWidth()
        ) {
            when (bottomToolTab) {
                0 -> {
                    // TuxGuitar Tab Keyboard (TGTabKeyboard)
                    TGTabKeyboard(
                        caret = caret,
                        onFretEntered = { fret -> viewModel.setFret(fret) },
                        onDelete = { viewModel.deleteNoteOrRest() },
                        onInsertRest = { viewModel.insertRest() },
                        onMoveLeft = { viewModel.moveLeft() },
                        onMoveRight = { viewModel.moveRight() },
                        onMoveUp = { viewModel.moveUp() },
                        onMoveDown = { viewModel.moveDown() }
                    )
                }
                1 -> {
                    // TuxGuitar 24-fret Fretboard with instant audio feedback
                    TGFretboardView(
                        activeTrack = activeTrack,
                        caret = caret,
                        onFretClicked = { fret, stringIdx ->
                            viewModel.selectString(stringIdx)
                            viewModel.setFret(fret)
                        }
                    )
                }
                2 -> {
                    // Piano View
                    TGPianoRollView(
                        onKeyClicked = { midiPitch ->
                            viewModel.playFretPreview(caret.stringIndex, (midiPitch % 24))
                        }
                    )
                }
            }
        }
    }

    // DIALOGS
    if (showTuningDialog && activeTrack != null) {
        TGTuningPresetDialog(
            activeTrack = activeTrack,
            onSelectPreset = { preset ->
                viewModel.setTrackTuning(preset)
                showTuningDialog = false
            },
            onDismiss = { showTuningDialog = false }
        )
    }

    if (showTimeSigDialog) {
        TGTimeSignatureDialog(
            numerator = score.timeSignatureNumerator,
            denominator = score.timeSignatureDenominator,
            onConfirm = { n, d ->
                viewModel.setTimeSignature(n, d)
                showTimeSigDialog = false
            },
            onDismiss = { showTimeSigDialog = false }
        )
    }

    if (showTempoDialog) {
        TGTempoDialog(
            initialTempo = score.tempo,
            onConfirm = { bpm ->
                viewModel.setTempo(bpm)
                showTempoDialog = false
            },
            onDismiss = { showTempoDialog = false }
        )
    }

    if (showMixerDialog) {
        TGMixerDialog(
            score = score,
            currentSoundBank = soundBank,
            onOpenSoundBank = {
                showMixerDialog = false
                showSoundBankDialog = true
            },
            onVolumeChange = viewModel::setTrackVolume,
            onPanChange = viewModel::setTrackPan,
            onMuteToggle = viewModel::toggleMuteTrack,
            onSoloToggle = viewModel::toggleSoloTrack,
            onDismiss = { showMixerDialog = false }
        )
    }

    if (showSoundBankDialog) {
        TGSoundBankDialog(
            currentBank = soundBank,
            onSelectBank = {
                viewModel.setSoundBank(it)
                showSoundBankDialog = false
            },
            onDismiss = { showSoundBankDialog = false }
        )
    }

    if (showSongInfoDialog) {
        TGSongInfoDialog(
            title = score.title,
            artist = score.artist,
            onSave = { t, a ->
                viewModel.updateScoreInfo(t, a)
                showSongInfoDialog = false
            },
            onDismiss = { showSongInfoDialog = false }
        )
    }

    if (showDemoDialog) {
        TGDemoDialog(
            onSelectTuxGuitar = {
                viewModel.loadDemoTuxGuitar()
                showDemoDialog = false
            },
            onSelectRock = {
                viewModel.loadDemoRock()
                showDemoDialog = false
            },
            onSelectBlank = {
                viewModel.newBlankScore()
                showDemoDialog = false
            },
            onDismiss = { showDemoDialog = false }
        )
    }

    if (showAddTrackDialog) {
        TGAddTrackDialog(
            onAdd = { name, instrument ->
                viewModel.addTrack(name, instrument)
                showAddTrackDialog = false
            },
            onDismiss = { showAddTrackDialog = false }
        )
    }
}

/**
 * Tablature and Score Canvas Renderer matching TuxGuitar's layout painter.
 */
@Composable
private fun TGTablatureScoreCanvas(
    score: TabScore,
    activeTrack: TabTrack?,
    caret: TGCaretState,
    viewMode: TGViewMode,
    zoomLevel: Float,
    onSelectCell: (measureIdx: Int, beatIdx: Int, stringIdx: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val stringLabels = activeTrack?.stringLabels ?: listOf("e", "B", "G", "D", "A", "E")
    val stringCount = stringLabels.size
    val measures = activeTrack?.measures ?: emptyList()

    val measureWidthDp = (180 * zoomLevel).dp
    val measureHeightDp = when (viewMode) {
        TGViewMode.TAB_ONLY -> (28 + stringCount * 18).dp
        TGViewMode.SCORE_ONLY -> 130.dp
        TGViewMode.DUAL -> (110 + stringCount * 18).dp
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .horizontalScroll(scrollState)
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.height(measureHeightDp)) {
            measures.forEachIndexed { mIdx, measure ->
                val isSelectedMeasure = mIdx == caret.measureIndex
                val beats = measure.beats

                Box(
                    modifier = Modifier
                        .width(measureWidthDp)
                        .fillMaxSize()
                        .padding(horizontal = 2.dp)
                ) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(mIdx, beats.size, stringCount) {
                                detectTapGestures { offset ->
                                    val bWidth = size.width / (beats.size.coerceAtLeast(1))
                                    val bIdx = (offset.x / bWidth).toInt().coerceIn(0, (beats.size - 1).coerceAtLeast(0))
                                    val sSpacing = 16.dp.toPx()
                                    val tabTop = if (viewMode == TGViewMode.DUAL) 60.dp.toPx() else 24.dp.toPx()
                                    val sIdx = ((offset.y - tabTop) / sSpacing).toInt().coerceIn(0, stringCount - 1)
                                    onSelectCell(mIdx, bIdx, sIdx)
                                }
                            }
                    ) {
                        val canvasW = size.width
                        val canvasH = size.height

                        // Measure background highlight if selected
                        if (isSelectedMeasure) {
                            drawRect(
                                color = Color(0xFF1E2838),
                                size = Size(canvasW, canvasH)
                            )
                        }

                        // Measure border lines (barlines)
                        drawLine(
                            color = Color(0xFF475569),
                            start = Offset(0f, 10f),
                            end = Offset(0f, canvasH - 10f),
                            strokeWidth = 2.dp.toPx()
                        )
                        drawLine(
                            color = Color(0xFF475569),
                            start = Offset(canvasW, 10f),
                            end = Offset(canvasW, canvasH - 10f),
                            strokeWidth = 2.dp.toPx()
                        )

                        var currentY = 24.dp.toPx()

                        // 1. STANDARD NOTATION STAFF (Score view)
                        if (viewMode == TGViewMode.SCORE_ONLY || viewMode == TGViewMode.DUAL) {
                            val staffSpacing = 8.dp.toPx()
                            val staffTop = currentY
                            for (l in 0..4) {
                                val ly = staffTop + l * staffSpacing
                                drawLine(
                                    color = Color(0xFF64748B),
                                    start = Offset(0f, ly),
                                    end = Offset(canvasW, ly),
                                    strokeWidth = 1.dp.toPx()
                                )
                            }
                            currentY = staffTop + 5 * staffSpacing + 20.dp.toPx()
                        }

                        // 2. TABLATURE STAFF (TAB view)
                        if (viewMode == TGViewMode.TAB_ONLY || viewMode == TGViewMode.DUAL) {
                            val stringSpacing = 16.dp.toPx()
                            val tabTop = currentY

                            for (s in 0 until stringCount) {
                                val sy = tabTop + s * stringSpacing
                                drawLine(
                                    color = if (isSelectedMeasure && s == caret.stringIndex) TGAmber.copy(alpha = 0.5f) else Color(0xFF334155),
                                    start = Offset(0f, sy),
                                    end = Offset(canvasW, sy),
                                    strokeWidth = (1f + s * 0.25f).dp.toPx()
                                )
                            }

                            // Render Notes in beats
                            val beatWidth = canvasW / beats.size.coerceAtLeast(1)

                            beats.forEachIndexed { bIdx, beat ->
                                val bx = bIdx * beatWidth + beatWidth / 2f
                                val isSelectedBeat = isSelectedMeasure && bIdx == caret.beatIndex

                                // Caret Highlight Bounding Box
                                if (isSelectedBeat) {
                                    val cy = tabTop + caret.stringIndex * stringSpacing
                                    drawRoundRect(
                                        color = TGAmber,
                                        topLeft = Offset(bx - 12.dp.toPx(), cy - 10.dp.toPx()),
                                        size = Size(24.dp.toPx(), 20.dp.toPx()),
                                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx()),
                                        style = Stroke(width = 2.dp.toPx())
                                    )
                                }

                                // Draw Tab Notes
                                beat.notes.forEach { note ->
                                    if (note.stringIndex in 0 until stringCount) {
                                        val ny = tabTop + note.stringIndex * stringSpacing

                                        // Background clear behind fret number
                                        drawRect(
                                            color = if (isSelectedMeasure && isSelectedBeat && note.stringIndex == caret.stringIndex) Color(0xFF263244) else Color(0xFF0E131A),
                                            topLeft = Offset(bx - 9.dp.toPx(), ny - 8.dp.toPx()),
                                            size = Size(18.dp.toPx(), 16.dp.toPx())
                                        )

                                        // Draw note dot / indicator
                                        drawCircle(
                                            color = if (isSelectedBeat) TGAmber else Color(0xFFF1F5F9),
                                            radius = 4.dp.toPx(),
                                            center = Offset(bx, ny)
                                        )

                                        // Note effect symbol marker if present
                                        if (note.effect != NoteEffect.NONE) {
                                            drawCircle(
                                                color = TGCyan,
                                                radius = 2.dp.toPx(),
                                                center = Offset(bx + 7.dp.toPx(), ny - 6.dp.toPx())
                                            )
                                        }
                                    }
                                }

                                // Beat duration stem line
                                val stemBottom = tabTop + stringCount * stringSpacing + 10.dp.toPx()
                                drawLine(
                                    color = if (isSelectedBeat) TGAmber else Color(0xFF64748B),
                                    start = Offset(bx, tabTop + (stringCount - 1) * stringSpacing + 2.dp.toPx()),
                                    end = Offset(bx, stemBottom),
                                    strokeWidth = 1.5.dp.toPx()
                                )
                            }
                        }
                    }

                    // Measure Number & Time Signature Labels
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp, vertical = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "${measure.number}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelectedMeasure) TGAmber else Color(0xFF64748B)
                        )
                        if (measure.tempoBpm != null) {
                            Text("♩=${measure.tempoBpm}", fontSize = 9.sp, color = TGCyan)
                        }
                    }
                }
            }
        }
    }
}

/**
 * TuxGuitar Tab Keypad (TGTabKeyboard).
 */
@Composable
private fun TGTabKeyboard(
    caret: TGCaretState,
    onFretEntered: (Int) -> Unit,
    onDelete: () -> Unit,
    onInsertRest: () -> Unit,
    onMoveLeft: () -> Unit,
    onMoveRight: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        // Direct fret quick access (0, 1, 2, 3, 5, 7, 9, 12, 15, 17, 19, 21, 24)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 17, 19, 21, 24).forEach { fret ->
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (fret == 0) Color(0xFF334155) else Color(0xFF1E2838),
                    border = BorderStroke(1.dp, Color(0xFF334155)),
                    modifier = Modifier.clickable { onFretEntered(fret) }
                ) {
                    Text(
                        text = "$fret",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = if (fret in listOf(0, 3, 5, 7, 9, 12, 15, 17, 19, 21, 24)) TGAmber else Color.White,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Action and Navigation Pad
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Function Buttons (Rest, Delete)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onInsertRest,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF263244)),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text("Rest", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.LightGray)
                }

                Button(
                    onClick = onDelete,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF451A1A)),
                    shape = RoundedCornerShape(6.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, tint = Color(0xFFEF4444), modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Delete", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFEF4444))
                }
            }

            // Directional Caret Controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(onClick = onMoveLeft, modifier = Modifier.size(36.dp).background(Color(0xFF222B38), CircleShape)) {
                    Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Left", tint = Color.White)
                }
                IconButton(onClick = onMoveUp, modifier = Modifier.size(36.dp).background(Color(0xFF222B38), CircleShape)) {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Up", tint = Color.White)
                }
                IconButton(onClick = onMoveDown, modifier = Modifier.size(36.dp).background(Color(0xFF222B38), CircleShape)) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Down", tint = Color.White)
                }
                IconButton(onClick = onMoveRight, modifier = Modifier.size(36.dp).background(Color(0xFF222B38), CircleShape)) {
                    Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Right", tint = Color.White)
                }
            }
        }
    }
}

/**
 * 24-fret Interactive Guitar Fretboard.
 */
@Composable
private fun TGFretboardView(
    activeTrack: TabTrack?,
    caret: TGCaretState,
    onFretClicked: (fret: Int, stringIdx: Int) -> Unit
) {
    val stringLabels = activeTrack?.stringLabels ?: listOf("e", "B", "G", "D", "A", "E")
    val stringCount = stringLabels.size
    val activeNotes = activeTrack?.measures?.getOrNull(caret.measureIndex)?.beats?.getOrNull(caret.beatIndex)?.notes ?: emptyList()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 6.dp, horizontal = 8.dp)
    ) {
        Canvas(
            modifier = Modifier
                .size(width = 900.dp, height = (24 + stringCount * 18).dp)
                .pointerInput(stringCount) {
                    detectTapGestures { offset ->
                        val fretCount = 24
                        val fretWidth = size.width / (fretCount + 1)
                        val stringSpacing = size.height / (stringCount + 1)
                        val fret = (offset.x / fretWidth).toInt().coerceIn(0, 24)
                        val sIdx = ((offset.y / stringSpacing) - 0.5f).toInt().coerceIn(0, stringCount - 1)
                        onFretClicked(fret, sIdx)
                    }
                }
        ) {
            val fretCount = 24
            val fretWidth = size.width / (fretCount + 1)
            val stringSpacing = size.height / (stringCount + 1)

            // Fretboard wood background
            drawRect(
                color = Color(0xFF1F1B18),
                size = size
            )

            // Nut line
            drawLine(
                color = Color(0xFFD4D4D8),
                start = Offset(fretWidth * 0.6f, 0f),
                end = Offset(fretWidth * 0.6f, size.height),
                strokeWidth = 6.dp.toPx()
            )

            // Frets & Inlays
            for (f in 1..fretCount) {
                val fx = f * fretWidth
                drawLine(
                    color = Color(0xFF71717A),
                    start = Offset(fx, 0f),
                    end = Offset(fx, size.height),
                    strokeWidth = 2.dp.toPx()
                )

                // Fret markers (3, 5, 7, 9, 12, 15, 17, 19, 21, 24)
                if (f in listOf(3, 5, 7, 9, 15, 17, 19, 21)) {
                    drawCircle(
                        color = Color(0xFFE4E4E7).copy(alpha = 0.4f),
                        radius = 4.dp.toPx(),
                        center = Offset(fx - fretWidth / 2f, size.height / 2f)
                    )
                } else if (f in listOf(12, 24)) {
                    drawCircle(
                        color = Color(0xFFE4E4E7).copy(alpha = 0.5f),
                        radius = 4.dp.toPx(),
                        center = Offset(fx - fretWidth / 2f, size.height * 0.3f)
                    )
                    drawCircle(
                        color = Color(0xFFE4E4E7).copy(alpha = 0.5f),
                        radius = 4.dp.toPx(),
                        center = Offset(fx - fretWidth / 2f, size.height * 0.7f)
                    )
                }
            }

            // Guitar Strings
            for (s in 0 until stringCount) {
                val sy = (s + 1) * stringSpacing
                val isSelectedString = s == caret.stringIndex
                drawLine(
                    color = if (isSelectedString) TGAmber else Color(0xFFCBD5E1),
                    start = Offset(0f, sy),
                    end = Offset(size.width, sy),
                    strokeWidth = (1.2f + s * 0.4f).dp.toPx()
                )
            }

            // Active beat notes on fretboard
            activeNotes.forEach { note ->
                if (note.stringIndex in 0 until stringCount) {
                    val cy = (note.stringIndex + 1) * stringSpacing
                    val cx = if (note.fret == 0) fretWidth * 0.3f else note.fret * fretWidth - fretWidth / 2f
                    drawCircle(
                        color = Color(0xFFEF4444),
                        radius = 8.dp.toPx(),
                        center = Offset(cx, cy)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 3.dp.toPx(),
                        center = Offset(cx, cy)
                    )
                }
            }
        }
    }
}

/**
 * Interactive 88-Key Piano View.
 */
@Composable
private fun TGPianoRollView(
    onKeyClicked: (midiPitch: Int) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(vertical = 8.dp, horizontal = 4.dp)
    ) {
        Row(modifier = Modifier.height(80.dp)) {
            val whiteKeys = listOf(0, 2, 4, 5, 7, 9, 11)
            (36..84).forEach { pitch ->
                val isWhite = (pitch % 12) in whiteKeys
                if (isWhite) {
                    Surface(
                        modifier = Modifier
                            .width(22.dp)
                            .height(80.dp)
                            .border(0.5.dp, Color.Black)
                            .clickable { onKeyClicked(pitch) },
                        color = Color(0xFFF8FAFC)
                    ) {}
                } else {
                    Surface(
                        modifier = Modifier
                            .width(14.dp)
                            .height(50.dp)
                            .clickable { onKeyClicked(pitch) },
                        color = Color(0xFF0F172A)
                    ) {}
                }
            }
        }
    }
}

// ----------------------------------------------------
// DIALOGS
// ----------------------------------------------------

@Composable
private fun TGTuningPresetDialog(
    activeTrack: TabTrack,
    onSelectPreset: (TGTuningPreset) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Tune, contentDescription = null, tint = TGAmber)
                Spacer(Modifier.width(8.dp))
                Text("Track Tuning (TuxGuitar)")
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                Text(
                    text = "Select preset from TuxGuitar tunings library:",
                    fontSize = 12.sp,
                    color = Color.LightGray
                )
                Spacer(Modifier.height(8.dp))

                TGTuningPresets.presets.forEach { preset ->
                    val isCurrent = preset.name == activeTrack.tuningName
                    Card(
                        onClick = { onSelectPreset(preset) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCurrent) Color(0xFF2E3440) else Color(0xFF1E232B)
                        ),
                        border = if (isCurrent) BorderStroke(1.5.dp, TGAmber) else null,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(preset.name, fontWeight = FontWeight.Bold, color = if (isCurrent) TGAmber else Color.White, fontSize = 13.sp)
                                Text("${preset.group} • ${preset.labels.joinToString(" ")}", fontSize = 11.sp, color = Color.Gray)
                            }
                            if (isCurrent) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = TGAmber, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
private fun TGTimeSignatureDialog(
    numerator: Int,
    denominator: Int,
    onConfirm: (Int, Int) -> Unit,
    onDismiss: () -> Unit
) {
    var num by remember { mutableIntStateOf(numerator) }
    var den by remember { mutableIntStateOf(denominator) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Time Signature") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Numerator (beats per measure): $num", fontSize = 13.sp, color = Color.LightGray)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(2, 3, 4, 5, 6, 7, 8, 12).forEach { n ->
                        FilterChip(
                            selected = num == n,
                            onClick = { num = n },
                            label = { Text("$n") }
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                Text("Denominator (beat value): $den", fontSize = 13.sp, color = Color.LightGray)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(2, 4, 8, 16).forEach { d ->
                        FilterChip(
                            selected = den == d,
                            onClick = { den = d },
                            label = { Text("$d") }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(num, den) }) { Text("Apply") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun TGTempoDialog(
    initialTempo: Int,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var tempo by remember { mutableIntStateOf(initialTempo) }
    var lastTapTime by remember { mutableLongStateOf(0L) }
    val tapIntervals = remember { mutableListOf<Long>() }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Tempo (BPM)") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "$tempo BPM",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = TGAmber,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Slider(
                    value = tempo.toFloat(),
                    onValueChange = { tempo = it.toInt() },
                    valueRange = 40f..280f,
                    colors = SliderDefaults.colors(thumbColor = TGAmber, activeTrackColor = TGAmber)
                )

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = {
                        val now = System.currentTimeMillis()
                        if (lastTapTime > 0) {
                            val interval = now - lastTapTime
                            if (interval in 200..2500) {
                                tapIntervals.add(interval)
                                if (tapIntervals.size > 4) tapIntervals.removeAt(0)
                                val avgInterval = tapIntervals.average()
                                tempo = (60000.0 / avgInterval).toInt().coerceIn(40, 280)
                            } else {
                                tapIntervals.clear()
                            }
                        }
                        lastTapTime = now
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF263244)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Speed, contentDescription = null, tint = TGCyan)
                    Spacer(Modifier.width(8.dp))
                    Text("TAP TEMPO")
                }
            }
        },
        confirmButton = {
            Button(onClick = { onConfirm(tempo) }) { Text("Apply") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun TGMixerDialog(
    score: TabScore,
    currentSoundBank: TuxGuitarSoundBank,
    onOpenSoundBank: () -> Unit,
    onVolumeChange: (Int, Float) -> Unit,
    onPanChange: (Int, Float) -> Unit,
    onMuteToggle: (Int) -> Unit,
    onSoloToggle: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("TuxGuitar Multi-Track Mixer") },
        text = {
            Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .clickable(onClick = onOpenSoundBank),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF222B38)),
                    border = BorderStroke(1.dp, TGAmber)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("Gervill SoundBank / Timbre", fontSize = 11.sp, color = Color.Gray)
                            Text(currentSoundBank.displayName, fontWeight = FontWeight.Bold, color = TGAmber, fontSize = 13.sp)
                        }
                        Icon(Icons.Default.GraphicEq, contentDescription = null, tint = TGAmber)
                    }
                }

                score.tracks.forEachIndexed { idx, track ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E232B))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(TGCanvasTrackColor(idx))
                                    )
                                    Spacer(Modifier.width(8.dp))
                                    Text(track.name, fontWeight = FontWeight.Bold, color = Color.White)
                                }

                                Row {
                                    TGActionCircleButton(
                                        onClick = { onMuteToggle(idx) },
                                        containerColor = if (track.isMuted) Color(0xFFEF4444) else Color(0xFF334155),
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Text("M", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                    }
                                    Spacer(Modifier.width(4.dp))
                                    TGActionCircleButton(
                                        onClick = { onSoloToggle(idx) },
                                        containerColor = if (track.isSolo) TGAmber else Color(0xFF334155),
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Text("S", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (track.isSolo) Color.Black else Color.White)
                                    }
                                }
                            }

                            Spacer(Modifier.height(4.dp))
                            Text("Volume: ${(track.volume * 100).toInt()}%", fontSize = 11.sp, color = Color.LightGray)
                            Slider(
                                value = track.volume,
                                onValueChange = { onVolumeChange(idx, it) },
                                valueRange = 0f..1f,
                                colors = SliderDefaults.colors(thumbColor = TGAmber, activeTrackColor = TGAmber)
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
private fun TGSoundBankDialog(
    currentBank: TuxGuitarSoundBank,
    onSelectBank: (TuxGuitarSoundBank) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("TuxGuitar Gervill SoundBank") },
        text = {
            Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                TuxGuitarSoundBank.entries.forEach { bank ->
                    val isSel = bank == currentBank
                    Card(
                        onClick = { onSelectBank(bank) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSel) Color(0xFF2E3440) else Color(0xFF1E232B)
                        ),
                        border = if (isSel) BorderStroke(1.5.dp, TGAmber) else null,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(bank.displayName, fontWeight = FontWeight.Bold, color = if (isSel) TGAmber else Color.White)
                            Text(bank.description, fontSize = 11.sp, color = Color.Gray)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
private fun TGSongInfoDialog(
    title: String,
    artist: String,
    onSave: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    var t by remember { mutableStateOf(title) }
    var a by remember { mutableStateOf(artist) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Song Information") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = t,
                    onValueChange = { t = it },
                    label = { Text("Song Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = a,
                    onValueChange = { a = it },
                    label = { Text("Artist / Band") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSave(t, a) }) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun TGDemoDialog(
    onSelectTuxGuitar: () -> Unit,
    onSelectRock: () -> Unit,
    onSelectBlank: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Load Tab Project") },
        text = {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Card(
                    onClick = onSelectTuxGuitar,
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF222B38)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("TuxGuitar Anthem (Multi-Track)", fontWeight = FontWeight.Bold, color = TGAmber)
                        Text("Lead Guitar + Electric Bass demo score from TuxGuitar repository", fontSize = 11.sp, color = Color.LightGray)
                    }
                }
                Card(
                    onClick = onSelectRock,
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF222B38)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("Rock Riff (Smoke on the Water)", fontWeight = FontWeight.Bold, color = Color.White)
                        Text("Classic rock riff with hammer-ons and bends", fontSize = 11.sp, color = Color.LightGray)
                    }
                }
                Card(
                    onClick = onSelectBlank,
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF222B38)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("New Blank Score", fontWeight = FontWeight.Bold, color = TGCyan)
                        Text("Clean 4-measure tab template ready for editing", fontSize = 11.sp, color = Color.LightGray)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        }
    )
}

@Composable
private fun TGAddTrackDialog(
    onAdd: (String, InstrumentType) -> Unit,
    onDismiss: () -> Unit
) {
    var name by remember { mutableStateOf("New Track") }
    var selectedType by remember { mutableStateOf(InstrumentType.GUITAR) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Track") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Track Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                Text("Instrument:", fontSize = 12.sp, color = Color.Gray)
                InstrumentType.entries.forEach { inst ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedType = inst
                                if (name == "New Track") name = inst.displayName
                            }
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(if (selectedType == inst) TGAmber else Color(0xFF334155))
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(inst.displayName, color = if (selectedType == inst) Color.White else Color.Gray)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { onAdd(name, selectedType) }) { Text("Create Track") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun TGActionCircleButton(
    onClick: () -> Unit,
    containerColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(containerColor)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

private fun TGCanvasTrackColor(index: Int): Color {
    return when (index % 5) {
        0 -> Color(0xFFF59E0B) // Amber
        1 -> Color(0xFF06B6D4) // Cyan
        2 -> Color(0xFF10B981) // Emerald
        3 -> Color(0xFFA855F7) // Purple
        else -> Color(0xFFEC4899) // Pink
    }
}
