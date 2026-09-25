package com.mmt.guitarlab.ui.tuner

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mmt.guitarlab.domain.model.DetectedPitch
import com.mmt.guitarlab.domain.model.Tuning
import com.mmt.guitarlab.domain.model.TuningNote
import com.mmt.guitarlab.ui.components.Studio3DAccent
import com.mmt.guitarlab.ui.components.Studio3DIconBadge
import com.mmt.guitarlab.ui.components.StudioCard
import com.mmt.guitarlab.ui.theme.ElectricAmber
import com.mmt.guitarlab.ui.theme.ElectricGreen
import com.mmt.guitarlab.ui.theme.ElectricTeal
import com.mmt.guitarlab.ui.theme.GuitarLabTheme
import com.mmt.guitarlab.ui.theme.StudioCardBg
import com.mmt.guitarlab.ui.theme.StudioCardBorder
import com.mmt.guitarlab.ui.theme.StudioCardElevated
import com.mmt.guitarlab.ui.theme.StudioDarkBg
import com.mmt.guitarlab.ui.theme.StudioTextMuted
import com.mmt.guitarlab.ui.theme.StudioTextPrimary
import com.mmt.guitarlab.ui.theme.StudioTextSecondary
import java.util.Locale
import kotlin.math.abs

@Composable
fun TunerScreen(viewModel: TunerViewModel = hiltViewModel()) {
    val pitch by viewModel.pitch.collectAsStateWithLifecycle()
    val running by viewModel.running.collectAsStateWithLifecycle()
    val a4 by viewModel.a4Hz.collectAsStateWithLifecycle()
    val tunings by viewModel.tunings.collectAsStateWithLifecycle()
    val selectedTuning by viewModel.selectedTuning.collectAsStateWithLifecycle()
    val targetNote by viewModel.targetNote.collectAsStateWithLifecycle()

    var showTuningDialog by remember { mutableStateOf(false) }
    var calibrationDropdownExpanded by remember { mutableStateOf(false) }
    var lockedNoteIndex by remember { mutableStateOf<Int?>(null) }
    var isChromaticMode by remember { mutableStateOf(false) }

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

    val activeTuningNote: TuningNote? = if (isChromaticMode) {
        null
    } else if (lockedNoteIndex != null && selectedTuning != null) {
        selectedTuning!!.notes.getOrNull(lockedNoteIndex!!)
    } else {
        targetNote ?: selectedTuning?.notes?.firstOrNull()
    }

    val currentCents: Float = when {
        pitch == null -> 0f
        isChromaticMode || activeTuningNote == null -> pitch!!.cents
        else -> {
            val f = pitch!!.frequencyHz
            val targetF = activeTuningNote.targetFrequencyHz
            if (f > 0f && targetF > 0f) {
                (1200.0 * (kotlin.math.ln((f / targetF).toDouble()) / kotlin.math.ln(2.0))).toFloat()
            } else {
                pitch!!.cents
            }
        }
    }

    val isInTune = pitch != null && abs(currentCents) <= 3f
    val centsAnimated by animateFloatAsState(
        targetValue = if (pitch != null) currentCents.coerceIn(-50f, 50f) else 0f,
        animationSpec = spring(stiffness = 500f, dampingRatio = 0.85f),
        label = "tuner_cents",
    )

    TunerContent(
        pitch = pitch,
        running = running,
        a4 = a4,
        selectedTuning = selectedTuning,
        activeTuningNote = activeTuningNote,
        isChromaticMode = isChromaticMode,
        lockedNoteIndex = lockedNoteIndex,
        isInTune = isInTune,
        currentCents = currentCents,
        centsAnimated = centsAnimated,
        calibrationDropdownExpanded = calibrationDropdownExpanded,
        onCalibrationDropdownExpandedChange = { calibrationDropdownExpanded = it },
        onSetA4 = viewModel::setA4,
        onToggleMic = {
            if (!granted) {
                launcher.launch(Manifest.permission.RECORD_AUDIO)
            } else {
                if (running) viewModel.stop() else viewModel.start()
            }
        },
        onOpenTuningDialog = { showTuningDialog = true },
        onSelectNoteLock = { index ->
            lockedNoteIndex = if (lockedNoteIndex == index) null else index
        },
        onResetLock = { lockedNoteIndex = null },
    )

    if (showTuningDialog) {
        TuningSelectionDialog(
            tunings = tunings,
            selectedId = selectedTuning?.id ?: "",
            isChromatic = isChromaticMode,
            onToggleChromatic = {
                isChromaticMode = !isChromaticMode
                if (isChromaticMode) lockedNoteIndex = null
                showTuningDialog = false
            },
            onSelect = {
                isChromaticMode = false
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
fun TunerContent(
    pitch: DetectedPitch?,
    running: Boolean,
    a4: Float,
    selectedTuning: Tuning?,
    activeTuningNote: TuningNote?,
    isChromaticMode: Boolean,
    lockedNoteIndex: Int?,
    isInTune: Boolean,
    currentCents: Float,
    centsAnimated: Float,
    calibrationDropdownExpanded: Boolean,
    onCalibrationDropdownExpandedChange: (Boolean) -> Unit,
    onSetA4: (Float) -> Unit,
    onToggleMic: () -> Unit,
    onOpenTuningDialog: () -> Unit,
    onSelectNoteLock: (Int) -> Unit,
    onResetLock: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TunerTopBar(
            selectedTuningName = selectedTuning?.name,
            selectedTuningNotes = selectedTuning?.notes,
            isChromaticMode = isChromaticMode,
            a4 = a4,
            running = running,
            calibrationDropdownExpanded = calibrationDropdownExpanded,
            onCalibrationDropdownExpandedChange = onCalibrationDropdownExpandedChange,
            onSetA4 = onSetA4,
            onToggleMic = onToggleMic,
            onOpenTuningDialog = onOpenTuningDialog,
        )

        Spacer(Modifier.height(18.dp))

        PrecisionTunerGaugeCard(
            pitch = pitch,
            running = running,
            isInTune = isInTune,
            currentCents = currentCents,
            centsAnimated = centsAnimated,
            isChromaticMode = isChromaticMode,
            activeTuningNote = activeTuningNote,
        )

        if (!isChromaticMode) {
            Spacer(Modifier.height(18.dp))
            StringPegsCard(
                selectedTuning = selectedTuning,
                activeTuningNote = activeTuningNote,
                lockedNoteIndex = lockedNoteIndex,
                isInTune = isInTune,
                onSelectNoteLock = onSelectNoteLock,
                onResetLock = onResetLock,
            )
        }

        Spacer(Modifier.height(20.dp))
    }
}





@Preview(showBackground = true)
@Composable
private fun TunerScreenPreview() {
    GuitarLabTheme {
        TunerContent(
            pitch = null,
            running = true,
            a4 = 440f,
            selectedTuning = null,
            activeTuningNote = null,
            isChromaticMode = false,
            lockedNoteIndex = null,
            isInTune = false,
            currentCents = 0f,
            centsAnimated = 0f,
            calibrationDropdownExpanded = false,
            onCalibrationDropdownExpandedChange = {},
            onSetA4 = {},
            onToggleMic = {},
            onOpenTuningDialog = {},
            onSelectNoteLock = {},
            onResetLock = {},
        )
    }
}