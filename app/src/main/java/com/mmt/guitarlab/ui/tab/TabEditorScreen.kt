package com.mmt.guitarlab.ui.tab

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.Slider
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mmt.guitarlab.data.db.TabProjectEntity
import com.mmt.guitarlab.domain.model.InstrumentType
import com.mmt.guitarlab.domain.model.NoteDuration
import com.mmt.guitarlab.domain.model.NoteEffect
import com.mmt.guitarlab.domain.model.TabScore
import com.mmt.guitarlab.ui.tab.components.TabCanvasRenderer

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
            IconButton(onClick = { showMixerDialog = true }) {
                Icon(Icons.Default.Tune, contentDescription = "Mixer")
            }
        }

        Spacer(Modifier.height(8.dp))

        // Tracks Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            score?.tracks?.forEachIndexed { index, track ->
                FilterChip(
                    selected = selectedTrackIndex == index,
                    onClick = { viewModel.selectTrack(index) },
                    label = { Text("${track.name} (${track.instrumentType.displayName})") },
                )
            }
            IconButton(onClick = { showAddTrackDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Track")
            }
        }

        Spacer(Modifier.height(8.dp))

        // Canvas Area
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
            TabCanvasRenderer(
                score = score,
                zoomScale = zoomScale,
                currentMeasureIndex = currentMeasureIndex,
                currentBeatIndex = currentBeatIndex,
                selectedMeasureIndex = selectedMeasureIndex,
                selectedBeatIndex = selectedBeatIndex,
                selectedStringIndex = selectedStringIndex,
                onNoteTap = { measureIdx, beatIdx, stringIdx ->
                    viewModel.selectCell(measureIdx, beatIdx, stringIdx)
                },
                modifier = Modifier.fillMaxSize(),
            )
        }

        Spacer(Modifier.height(8.dp))

        // Bottom Controls: Durations, Effects, Fret Pad, Playback
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            // Note Durations
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                NoteDuration.entries.forEach { duration ->
                    FilterChip(
                        selected = activeDuration == duration,
                        onClick = { viewModel.setActiveDuration(duration) },
                        label = { Text(duration.label) },
                    )
                }
            }

            // Note Effects
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                NoteEffect.entries.forEach { effect ->
                    FilterChip(
                        selected = activeEffect == effect,
                        onClick = { viewModel.setActiveEffect(effect) },
                        label = { Text(effect.symbol.ifEmpty { "None" }) },
                    )
                }
            }

            // Fret Pad Input
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                (0..24).forEach { fret ->
                    FilledIconButton(
                        onClick = { viewModel.inputFret(fret) },
                    ) {
                        Text(fret.toString(), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                FilledIconButton(
                    onClick = { viewModel.deleteNote() },
                ) {
                    Icon(Icons.AutoMirrored.Filled.Backspace, contentDescription = "Delete")
                }
            }

            // Playback Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = { viewModel.togglePlay() }) {
                        Icon(
                            if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                    IconButton(onClick = { viewModel.stopPlay() }) {
                        Icon(Icons.Default.Stop, contentDescription = "Stop")
                    }
                }

                Row(
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Speed:", fontSize = 12.sp)
                    Slider(
                        value = speedMultiplier,
                        onValueChange = { viewModel.setSpeedMultiplier(it) },
                        valueRange = 0.25f..2.0f,
                        steps = 6,
                        modifier = Modifier.weight(1f),
                    )
                    Text("${(speedMultiplier * 100).toInt()}%", fontSize = 12.sp)
                }

                Text(
                    text = "${score?.tempo ?: 120} BPM",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                )
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }

    if (showPasteDialog) {
        PasteAsciiDialog(
            onDismiss = { showPasteDialog = false },
            onConfirm = { text ->
                viewModel.parseAscii(text)
                showPasteDialog = false
            },
        )
    }

    if (showProjectsDialog) {
        ProjectsDialog(
            projects = savedProjects,
            onLoad = { project ->
                viewModel.loadProject(project)
                showProjectsDialog = false
            },
            onSave = { name ->
                viewModel.saveCurrentProject(name)
            },
            onDelete = { project ->
                viewModel.deleteProject(project)
            },
            onDismiss = { showProjectsDialog = false },
        )
    }

    if (showMixerDialog && score != null) {
        MixerDialog(
            score = score!!,
            onVolumeChange = { idx, vol -> viewModel.setTrackVolume(idx, vol) },
            onPanChange = { idx, pan -> viewModel.setTrackPan(idx, pan) },
            onMuteToggle = { idx -> viewModel.toggleTrackMute(idx) },
            onSoloToggle = { idx -> viewModel.toggleTrackSolo(idx) },
            onDismiss = { showMixerDialog = false },
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
                                    fontWeight = FontWeight.Bold,
                                )
                                Row {
                                    IconButton(onClick = { onMuteToggle(idx) }) {
                                        Icon(
                                            if (track.isMuted) Icons.AutoMirrored.Filled.VolumeOff else Icons.AutoMirrored.Filled.VolumeUp,
                                            contentDescription = "Mute",
                                            tint = if (track.isMuted) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                                        )
                                    }
                                    IconButton(onClick = { onSoloToggle(idx) }) {
                                        Text(
                                            "S",
                                            fontWeight = FontWeight.Bold,
                                            color = if (track.isSolo) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                        )
                                    }
                                }
                            }
                            Text("Volume: ${(track.volume * 100).toInt()}%", fontSize = 11.sp)
                            Slider(
                                value = track.volume,
                                onValueChange = { onVolumeChange(idx, it) },
                                valueRange = 0f..1f,
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
private fun AddTrackDialog(
    onAdd: (name: String, type: InstrumentType) -> Unit,
    onDismiss: () -> Unit,
) {
    var name by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(InstrumentType.ELECTRIC_CLEAN) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add Track") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Track Name") },
                    singleLine = true,
                )
                Text("Instrument Type:", fontWeight = FontWeight.Bold)
                Column {
                    InstrumentType.entries.forEach { type ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth(),
                        ) {
                            FilterChip(
                                selected = selectedType == type,
                                onClick = { selectedType = type },
                                label = { Text(type.displayName) },
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (name.isNotBlank()) {
                        onAdd(name, selectedType)
                    }
                },
            ) {
                Text("Add")
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
private fun ProjectsDialog(
    projects: List<TabProjectEntity>,
    onLoad: (TabProjectEntity) -> Unit,
    onSave: (String) -> Unit,
    onDelete: (TabProjectEntity) -> Unit,
    onDismiss: () -> Unit,
) {
    var newProjectName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Saved Tab Projects") },
        text = {
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedTextField(
                        value = newProjectName,
                        onValueChange = { newProjectName = it },
                        placeholder = { Text("New Project Name") },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                    )
                    Button(
                        onClick = {
                            if (newProjectName.isNotBlank()) {
                                onSave(newProjectName)
                                newProjectName = ""
                            }
                        },
                    ) {
                        Text("Save")
                    }
                }

                LazyColumn(modifier = Modifier.height(200.dp)) {
                    items(projects) { project ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(project.name, fontWeight = FontWeight.Bold)
                                    Text("${project.artist} • ${project.tempo} BPM", fontSize = 11.sp)
                                }
                                TextButton(onClick = { onLoad(project) }) {
                                    Text("Load")
                                }
                                IconButton(onClick = { onDelete(project) }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                }
                            }
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
private fun PasteAsciiDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var text by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Paste ASCII Tablature") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                label = { Text("Paste plain text tabs here...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp),
                maxLines = 15,
            )
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(text) },
                enabled = text.isNotBlank(),
            ) {
                Text("Import")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}
