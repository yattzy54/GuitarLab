package com.mmt.guitarlab.ui.tuner

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mmt.guitarlab.domain.model.Tuning
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun TunerScreen(viewModel: TunerViewModel = hiltViewModel()) {
    val pitch by viewModel.pitch.collectAsStateWithLifecycle()
    val running by viewModel.running.collectAsStateWithLifecycle()
    val a4 by viewModel.a4Hz.collectAsStateWithLifecycle()
    val tunings by viewModel.tunings.collectAsStateWithLifecycle()
    val selectedTuning by viewModel.selectedTuning.collectAsStateWithLifecycle()
    val targetNote by viewModel.targetNote.collectAsStateWithLifecycle()

    var showTuningDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    var granted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_GRANTED,
        )
    }
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { ok ->
        granted = ok
        if (ok) viewModel.start()
    }

    DisposableEffect(granted) {
        if (granted) viewModel.start()
        onDispose { viewModel.stop() }
    }

    val cents = pitch?.cents ?: 0f
    val needle by animateFloatAsState(
        targetValue = cents.coerceIn(-50f, 50f) / 50f,
        animationSpec = spring(stiffness = 180f, dampingRatio = 0.72f),
        label = "needle",
    )
    val inTune = pitch?.inTune == true
    val accent by animateColorAsState(
        if (inTune) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary,
        label = "accent",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Selected Tuning Card Button
        Card(
            onClick = { showTuningDialog = true },
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            ),
            shape = RoundedCornerShape(20.dp),
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = selectedTuning?.name ?: "Standard E",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.width(6.dp))
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = "Select Tuning",
                )
            }
        }

        // Pitch Readout
        val current = pitch
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = if (current == null) "--" else "${current.noteName}${current.octave}",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontSize = 60.sp,
                    fontWeight = FontWeight.ExtraBold,
                ),
                color = accent,
            )
            Text(
                text = if (current == null) {
                    "Play a string to tune"
                } else {
                    "%+.1f ¢   ·   %.1f Hz".format(current.cents, current.frequencyHz)
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        // Target Strings Single Row
        selectedTuning?.let { tuning ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.Center,
            ) {
                tuning.notes.forEach { note ->
                    val isMatched = targetNote?.midiNote == note.midiNote
                    val stringInTune = isMatched && inTune

                    val chipBg = when {
                        stringInTune -> MaterialTheme.colorScheme.secondary
                        isMatched -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.surfaceContainerHigh
                    }
                    val textColor = when {
                        isMatched -> MaterialTheme.colorScheme.onPrimary
                        else -> MaterialTheme.colorScheme.onSurface
                    }

                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(chipBg)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = note.displayLabel,
                                style = MaterialTheme.typography.titleMedium,
                                color = textColor,
                                fontWeight = if (isMatched) FontWeight.ExtraBold else FontWeight.SemiBold,
                            )
                            Text(
                                text = "${note.targetFrequencyHz.toInt()}Hz",
                                style = MaterialTheme.typography.labelSmall,
                                color = textColor.copy(alpha = 0.8f),
                            )
                        }
                    }
                }
            }
        }

        TunerGauge(
            needle = needle,
            inTune = inTune,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
        )

        // Mic Toggle Button
        FilledIconButton(
            onClick = {
                if (running) {
                    viewModel.stop()
                } else {
                    if (granted) viewModel.start() else launcher.launch(Manifest.permission.RECORD_AUDIO)
                }
            },
            modifier = Modifier.size(64.dp),
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = if (running) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceContainerHigh,
            ),
        ) {
            Icon(
                imageVector = if (running) Icons.Default.Mic else Icons.Default.MicOff,
                contentDescription = if (running) "Stop tuner" else "Start tuner",
                modifier = Modifier.size(28.dp),
            )
        }

        if (!granted) {
            Text(
                "Microphone permission is required.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
            )
        }

        // A4 Calibration Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("A4 Calibration", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                    Text("${a4.toInt()} Hz", style = MaterialTheme.typography.labelLarge, color = accent, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = a4,
                    onValueChange = viewModel::setA4,
                    valueRange = 415f..466f,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedButton(onClick = { viewModel.setA4(432f) }) { Text("432 Hz") }
                    OutlinedButton(onClick = { viewModel.setA4(440f) }) { Text("440 Hz (Std)") }
                    OutlinedButton(onClick = { viewModel.setA4(442f) }) { Text("442 Hz") }
                }
            }
        }
    }

    if (showTuningDialog) {
        TuningSelectionDialog(
            tunings = tunings,
            selectedId = selectedTuning?.id ?: "",
            onSelect = {
                viewModel.selectTuning(it.id)
                showTuningDialog = false
            },
            onToggleFavorite = viewModel::toggleFavorite,
            onDismiss = { showTuningDialog = false },
        )
    }
}

@Composable
private fun TuningSelectionDialog(
    tunings: List<Tuning>,
    selectedId: String,
    onSelect: (Tuning) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val categories = remember(tunings) {
        listOf("Favorites", "All") + tunings.map { it.category }.distinct()
    }
    var selectedCategory by remember { mutableStateOf("All") }

    val filteredTunings = remember(tunings, selectedCategory) {
        when (selectedCategory) {
            "Favorites" -> tunings.filter { it.isFavorite }
            "All" -> tunings
            else -> tunings.filter { it.category == selectedCategory }
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select Guitar Tuning", fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                ScrollableTabRow(
                    selectedTabIndex = categories.indexOf(selectedCategory).coerceAtLeast(0),
                    edgePadding = 0.dp,
                ) {
                    categories.forEach { cat ->
                        Tab(
                            selected = cat == selectedCategory,
                            onClick = { selectedCategory = cat },
                            text = { Text(cat, fontWeight = FontWeight.SemiBold) },
                        )
                    }
                }

                Spacer(Modifier.height(12.dp))

                if (filteredTunings.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            "No tunings found in this category.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                } else {
                    LazyColumn(modifier = Modifier.height(280.dp)) {
                        items(filteredTunings) { tuning ->
                            val isSelected = tuning.id == selectedId
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable { onSelect(tuning) },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) {
                                        MaterialTheme.colorScheme.primaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.surfaceContainer
                                    },
                                ),
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 14.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = tuning.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                        )
                                        Text(
                                            text = tuning.notes.joinToString("  ") { it.noteName },
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
                                    IconButton(
                                        onClick = { onToggleFavorite(tuning.id) },
                                    ) {
                                        Icon(
                                            imageVector = if (tuning.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                            contentDescription = "Favorite",
                                            tint = if (tuning.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        )
                                    }
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
private fun TunerGauge(needle: Float, inTune: Boolean, modifier: Modifier = Modifier) {
    val track = MaterialTheme.colorScheme.surfaceVariant
    val tick = MaterialTheme.colorScheme.onSurfaceVariant
    val needleColor = if (inTune) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary

    Canvas(modifier) {
        val sweep = 180f
        val start = 180f
        val stroke = Stroke(width = 16.dp.toPx(), cap = StrokeCap.Round)
        drawArc(
            color = track,
            startAngle = start,
            sweepAngle = sweep,
            useCenter = false,
            style = stroke,
        )
        val greenSweep = 18f
        drawArc(
            color = needleColor.copy(alpha = 0.35f),
            startAngle = 270f - greenSweep / 2f,
            sweepAngle = greenSweep,
            useCenter = false,
            style = stroke,
        )
        val radius = size.minDimension / 2f - 24.dp.toPx()
        val center = Offset(size.width / 2f, size.height * 0.92f)
        for (cents in -50..50 step 10) {
            val t = (cents + 50) / 100f
            val angle = Math.toRadians((180.0 + sweep * t))
            val inner = radius - 8.dp.toPx()
            val outer = radius + 6.dp.toPx()
            drawLine(
                color = tick,
                start = Offset(
                    center.x + inner * cos(angle).toFloat(),
                    center.y + inner * sin(angle).toFloat(),
                ),
                end = Offset(
                    center.x + outer * cos(angle).toFloat(),
                    center.y + outer * sin(angle).toFloat(),
                ),
                strokeWidth = 2.5f.dp.toPx(),
                cap = StrokeCap.Round,
            )
        }
        val angle = Math.toRadians((180.0 + sweep * ((needle + 1f) / 2f).toDouble()))
        drawLine(
            color = needleColor,
            start = center,
            end = Offset(
                center.x + radius * cos(angle).toFloat(),
                center.y + radius * sin(angle).toFloat(),
            ),
            strokeWidth = 5.dp.toPx(),
            cap = StrokeCap.Round,
        )
        drawCircle(color = needleColor, radius = 8.dp.toPx(), center = center)
    }
}
