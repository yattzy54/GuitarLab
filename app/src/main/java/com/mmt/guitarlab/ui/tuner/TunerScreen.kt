package com.mmt.guitarlab.ui.tuner

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.mmt.guitarlab.domain.model.TuningNote
import com.mmt.guitarlab.ui.components.Studio3DAccent
import com.mmt.guitarlab.ui.components.Studio3DIconBadge
import com.mmt.guitarlab.ui.components.StudioCard
import com.mmt.guitarlab.ui.components.StudioPill
import com.mmt.guitarlab.ui.theme.ElectricAmber
import com.mmt.guitarlab.ui.theme.ElectricGreen
import com.mmt.guitarlab.ui.theme.ElectricRuby
import com.mmt.guitarlab.ui.theme.ElectricTeal
import com.mmt.guitarlab.ui.theme.StudioCardBg
import com.mmt.guitarlab.ui.theme.StudioCardBorder
import com.mmt.guitarlab.ui.theme.StudioCardElevated
import com.mmt.guitarlab.ui.theme.StudioDarkBg
import com.mmt.guitarlab.ui.theme.StudioTextMuted
import com.mmt.guitarlab.ui.theme.StudioTextPrimary
import com.mmt.guitarlab.ui.theme.StudioTextSecondary
import java.util.Locale
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
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
    var lockedNoteIndex by remember { mutableStateOf<Int?>(null) } // null = Auto-detect mode

    val context = LocalContext.current
    var granted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
                PackageManager.PERMISSION_GRANTED,
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            granted = isGranted
            if (isGranted) viewModel.start()
        },
    )

    DisposableEffect(granted) {
        if (granted) viewModel.start()
        onDispose { viewModel.stop() }
    }

    // Determine active note: either user locked peg or auto-detected targetNote
    val activeTuningNote: TuningNote? = if (lockedNoteIndex != null && selectedTuning != null) {
        selectedTuning!!.notes.getOrNull(lockedNoteIndex!!)
    } else {
        targetNote ?: selectedTuning?.notes?.firstOrNull()
    }

    val currentCents = pitch?.cents ?: 0f
    val isInTune = pitch != null && abs(currentCents) <= 3f && pitch!!.clarity > 0.6f

    val centsAnimated by animateFloatAsState(
        targetValue = if (pitch != null) currentCents.coerceIn(-50f, 50f) else 0f,
        animationSpec = spring(stiffness = 600f, dampingRatio = 0.85f),
        label = "tuner_cents",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Top Bar: Preset Selector & Mic Toggle
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Preset Button
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(StudioCardElevated, StudioCardBg),
                        ),
                    )
                    .border(1.dp, StudioCardBorder, RoundedCornerShape(16.dp))
                    .clickable { showTuningDialog = true }
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Studio3DIconBadge(
                    icon = Icons.Default.Tune,
                    contentDescription = null,
                    size = 32.dp,
                    accent = Studio3DAccent.AMBER,
                )
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        text = selectedTuning?.name ?: "Standard E",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = StudioTextPrimary,
                    )
                    Text(
                        text = selectedTuning?.notes?.joinToString(" ") { it.noteName } ?: "E A D G B E",
                        style = MaterialTheme.typography.labelSmall,
                        color = StudioTextSecondary,
                    )
                }
            }

            // Mic On/Off 3D Badge
            Studio3DIconBadge(
                icon = if (running) Icons.Default.Mic else Icons.Default.MicOff,
                contentDescription = if (running) "Mute Tuner" else "Start Tuner",
                size = 46.dp,
                accent = if (running) Studio3DAccent.GREEN else Studio3DAccent.SLATE,
                onClick = {
                    if (!granted) {
                        launcher.launch(Manifest.permission.RECORD_AUDIO)
                    } else {
                        if (running) viewModel.stop() else viewModel.start()
                    }
                },
            )
        }

        Spacer(Modifier.height(18.dp))

        // Big Precision Tuner Gauge Card
        StudioCard(
            modifier = Modifier.fillMaxWidth(),
            accentBorder = if (isInTune) ElectricGreen else null,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 20.dp, horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Status Pill
                val statusText = when {
                    !running -> "Microphone Paused"
                    pitch == null || pitch!!.clarity < 0.4f -> "Pluck a string..."
                    isInTune -> "PERFECT IN TUNE"
                    currentCents < -3f -> String.format(Locale.US, "%.1f cents FLAT (Tune UP ↑)", abs(currentCents))
                    else -> String.format(Locale.US, "%.1f cents SHARP (Tune DOWN ↓)", currentCents)
                }

                val statusColor = when {
                    !running -> StudioTextMuted
                    pitch == null -> StudioTextSecondary
                    isInTune -> ElectricGreen
                    else -> ElectricAmber
                }

                Text(
                    text = statusText,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = statusColor,
                    letterSpacing = 1.sp,
                )

                Spacer(Modifier.height(14.dp))

                // High-End Radial Strobe Meter
                StrobeNeedleGauge(
                    cents = centsAnimated,
                    isInTune = isInTune,
                    hasSignal = pitch != null && pitch!!.clarity >= 0.4f,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                )

                Spacer(Modifier.height(10.dp))

                // Central Note Display
                val displayNote = activeTuningNote?.noteName ?: (pitch?.noteName ?: "--")
                val displayOctave = activeTuningNote?.octave?.toString() ?: (pitch?.octave?.toString() ?: "")
                val detectedHz = pitch?.frequencyHz ?: 0f
                val targetHz = activeTuningNote?.targetFrequencyHz ?: 0f

                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = displayNote,
                        style = MaterialTheme.typography.displayLarge.copy(
                            fontSize = 68.sp,
                            fontWeight = FontWeight.Black,
                        ),
                        color = if (isInTune) ElectricGreen else StudioTextPrimary,
                    )
                    if (displayOctave.isNotEmpty()) {
                        Text(
                            text = displayOctave,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                            ),
                            color = if (isInTune) ElectricGreen else ElectricTeal,
                            modifier = Modifier.padding(bottom = 12.dp, start = 4.dp),
                        )
                    }
                }

                // Frequency Readout & Delta
                Row(
                    modifier = Modifier.padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        text = if (detectedHz > 0) String.format(Locale.US, "%.1f Hz", detectedHz) else "--.- Hz",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = StudioTextSecondary,
                    )
                    if (targetHz > 0 && detectedHz > 0) {
                        val delta = detectedHz - targetHz
                        val sign = if (delta > 0) "+" else ""
                        Text(
                            text = String.format(Locale.US, "%s%.1f Hz", sign, delta),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isInTune) ElectricGreen else ElectricAmber,
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        // Headstock 6-Peg Visualizer (GuitarTuna Style)
        Text(
            text = "GUITAR HEADSTOCK",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = StudioTextMuted,
            letterSpacing = 1.2.sp,
            modifier = Modifier.align(Alignment.Start),
        )

        Spacer(Modifier.height(8.dp))

        StudioCard(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                // Auto / Lock mode toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = if (lockedNoteIndex == null) "Auto Detection Active" else "Peg Lock Mode",
                        style = MaterialTheme.typography.labelMedium,
                        color = ElectricTeal,
                        fontWeight = FontWeight.SemiBold,
                    )
                    if (lockedNoteIndex != null) {
                        Text(
                            text = "Switch to Auto",
                            style = MaterialTheme.typography.labelMedium,
                            color = ElectricAmber,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { lockedNoteIndex = null },
                        )
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Interactive 6 Pegs (3 Left: 6E, 5A, 4D ; 3 Right: 3G, 2B, 1E)
                val notes = selectedTuning?.notes ?: emptyList()
                val leftNotes = notes.take(3)
                val rightNotes = notes.drop(3).take(3)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    // Left 3 pegs
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        leftNotes.forEachIndexed { index, note ->
                            val isSelected = (lockedNoteIndex == index) ||
                                (lockedNoteIndex == null && activeTuningNote?.stringNumber == note.stringNumber)
                            PegItem(
                                note = note,
                                isSelected = isSelected,
                                isInTune = isSelected && isInTune,
                                onClick = {
                                    lockedNoteIndex = if (lockedNoteIndex == index) null else index
                                },
                            )
                        }
                    }

                    // Headstock Graphic in the middle
                    HeadstockCenterGraphic(
                        modifier = Modifier
                            .width(80.dp)
                            .height(150.dp),
                    )

                    // Right 3 pegs
                    Column(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        rightNotes.forEachIndexed { rIndex, note ->
                            val actualIndex = rIndex + 3
                            val isSelected = (lockedNoteIndex == actualIndex) ||
                                (lockedNoteIndex == null && activeTuningNote?.stringNumber == note.stringNumber)
                            PegItem(
                                note = note,
                                isSelected = isSelected,
                                isInTune = isSelected && isInTune,
                                onClick = {
                                    lockedNoteIndex = if (lockedNoteIndex == actualIndex) null else actualIndex
                                },
                            )
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(18.dp))

        // Reference Pitch (A4 Calibration) Pills
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "CALIBRATION (A4)",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = StudioTextMuted,
                letterSpacing = 1.sp,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                listOf(432f, 440f, 442f).forEach { hz ->
                    val isCur = abs(a4 - hz) < 0.5f
                    StudioPill(
                        text = "${hz.toInt()} Hz",
                        selected = isCur,
                        onClick = { viewModel.setA4(hz) },
                        accentColor = ElectricAmber,
                    )
                }
            }
        }

        Spacer(Modifier.height(24.dp))
    }

    // Tuning Selection Modal
    if (showTuningDialog) {
        TuningSelectionDialog(
            tunings = tunings,
            selectedId = selectedTuning?.id ?: "",
            onSelect = {
                viewModel.selectTuning(it.id)
                lockedNoteIndex = null
                showTuningDialog = false
            },
            onToggleFavorite = viewModel::toggleFavorite,
            onDismiss = { showTuningDialog = false },
        )
    }
}

@Composable
private fun PegItem(
    note: TuningNote,
    isSelected: Boolean,
    isInTune: Boolean,
    onClick: () -> Unit,
) {
    val (bgColors, borderColor, textColor) = when {
        isInTune -> Triple(
            listOf(Color(0xFF00E676), Color(0xFF00A850)),
            Color(0xFF69F0AE),
            Color(0xFF0D1117),
        )
        isSelected -> Triple(
            listOf(ElectricAmber, Color(0xFFC47D00)),
            Color(0xFFFFD166),
            Color(0xFF1A1200),
        )
        else -> Triple(
            listOf(Color(0xFF1E2433), Color(0xFF141824)),
            StudioCardBorder,
            StudioTextPrimary,
        )
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(brush = Brush.verticalGradient(bgColors))
            .border(1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = "${note.stringNumber}: ${note.noteName}${note.octave}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = textColor,
        )
    }
}

@Composable
private fun HeadstockCenterGraphic(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        // Draw Headstock silhouette
        drawRoundRect(
            color = Color(0xFF1A1F2C),
            topLeft = Offset(w * 0.15f, 0f),
            size = androidx.compose.ui.geometry.Size(w * 0.7f, h),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(16f, 16f),
        )
        // Center guitar logo line
        drawLine(
            color = Color(0xFFFFB703).copy(alpha = 0.6f),
            start = Offset(w * 0.5f, 20f),
            end = Offset(w * 0.5f, h - 20f),
            strokeWidth = 2f,
        )
    }
}

@Composable
private fun StrobeNeedleGauge(
    cents: Float,
    isInTune: Boolean,
    hasSignal: Boolean,
    modifier: Modifier = Modifier,
) {
    val trackColor = Color(0xFF1E2536)
    val tickColor = Color(0xFF424F6C)
    val safeZoneColor = ElectricGreen.copy(alpha = 0.25f)
    val needleColor = when {
        !hasSignal -> Color(0xFF64748B)
        isInTune -> ElectricGreen
        else -> ElectricAmber
    }

    Canvas(modifier = modifier) {
        val sweep = 160f
        val start = 190f
        val stroke = Stroke(width = 14.dp.toPx(), cap = StrokeCap.Round)

        // Arc track
        drawArc(
            color = trackColor,
            startAngle = start,
            sweepAngle = sweep,
            useCenter = false,
            style = stroke,
        )

        // Green safe zone around center (0 cents)
        val safeSweep = 16f
        drawArc(
            color = safeZoneColor,
            startAngle = 270f - safeSweep / 2f,
            sweepAngle = safeSweep,
            useCenter = false,
            style = stroke,
        )

        val radius = size.minDimension / 1.15f
        val center = Offset(size.width / 2f, size.height * 0.98f)

        // Tick marks (-50 to +50 cents in steps of 10)
        for (c in -50..50 step 10) {
            val t = (c + 50) / 100f
            val angle = Math.toRadians((start + sweep * t).toDouble())
            val inner = radius - (if (c % 25 == 0) 18.dp.toPx() else 10.dp.toPx())
            val outer = radius - 4.dp.toPx()

            val curTickColor = if (c == 0) ElectricGreen else tickColor
            drawLine(
                color = curTickColor,
                start = Offset(
                    center.x + inner * cos(angle).toFloat(),
                    center.y + inner * sin(angle).toFloat(),
                ),
                end = Offset(
                    center.x + outer * cos(angle).toFloat(),
                    center.y + outer * sin(angle).toFloat(),
                ),
                strokeWidth = if (c == 0) 3.5.dp.toPx() else 1.8.dp.toPx(),
                cap = StrokeCap.Round,
            )
        }

        // Animated Needle
        val needleNorm = (cents.coerceIn(-50f, 50f) + 50f) / 100f
        val needleAngle = Math.toRadians((start + sweep * needleNorm).toDouble())

        drawLine(
            color = needleColor,
            start = center,
            end = Offset(
                center.x + (radius - 6.dp.toPx()) * cos(needleAngle).toFloat(),
                center.y + (radius - 6.dp.toPx()) * sin(needleAngle).toFloat(),
            ),
            strokeWidth = 4.5.dp.toPx(),
            cap = StrokeCap.Round,
        )

        // Central pivot cap
        drawCircle(
            color = needleColor,
            radius = 9.dp.toPx(),
            center = center,
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
        containerColor = StudioCardBg,
        title = {
            Text(
                "Guitar Tuning Presets",
                fontWeight = FontWeight.Bold,
                color = StudioTextPrimary,
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                ScrollableTabRow(
                    selectedTabIndex = categories.indexOf(selectedCategory).coerceAtLeast(0),
                    edgePadding = 0.dp,
                    containerColor = StudioDarkBg,
                    contentColor = ElectricAmber,
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
                            "No presets found in this category.",
                            color = StudioTextSecondary,
                        )
                    }
                } else {
                    LazyColumn(modifier = Modifier.height(300.dp)) {
                        items(filteredTunings) { tuning ->
                            val isSelected = tuning.id == selectedId
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) Color(0xFF382600) else StudioCardElevated,
                                    )
                                    .border(
                                        1.dp,
                                        if (isSelected) ElectricAmber else StudioCardBorder,
                                        RoundedCornerShape(12.dp),
                                    )
                                    .clickable { onSelect(tuning) }
                                    .padding(horizontal = 14.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = tuning.name,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) ElectricAmber else StudioTextPrimary,
                                    )
                                    Text(
                                        text = tuning.notes.joinToString("  ") { it.noteName },
                                        style = MaterialTheme.typography.bodySmall,
                                        color = StudioTextSecondary,
                                    )
                                }
                                IconButton(
                                    onClick = { onToggleFavorite(tuning.id) },
                                ) {
                                    Icon(
                                        imageVector = if (tuning.isFavorite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                                        contentDescription = "Favorite",
                                        tint = if (tuning.isFavorite) ElectricAmber else StudioTextMuted,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = ElectricAmber, fontWeight = FontWeight.Bold)
            }
        },
    )
}
