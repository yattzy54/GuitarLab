package com.mmt.guitarlab.ui.metronome

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.mmt.guitarlab.domain.model.MetronomeBeat
import com.mmt.guitarlab.domain.model.MetronomeConfig
import com.mmt.guitarlab.domain.model.MetronomeSound
import com.mmt.guitarlab.domain.model.TimeSignature
import com.mmt.guitarlab.core.ui.theme.StudioDarkBg

@Composable
fun MetronomeContent(
    config: MetronomeConfig,
    beat: MetronomeBeat? = null,
    running: Boolean = false,
    onSetBpm: (Int) -> Unit = {},
    onTapTempo: () -> Unit = {},
    onToggle: () -> Unit = {},
    onSetTimeSignature: (TimeSignature) -> Unit = {},
    onSetVibrateOnly: (Boolean) -> Unit = {},
    onSetSound: (MetronomeSound) -> Unit = {},
    onSetVolume: (Float) -> Unit = {},
) {
    val pulse by animateFloatAsState(
        targetValue = if (beat?.accent == true) 1.28f else if (beat != null) 1.12f else 1f,
        animationSpec = tween(80),
        label = "beatPulse",
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Hero Pulse Dial Card (Soundbrenner Style)
        HeroPulseDialCard(
            config = config,
            beat = beat,
            running = running,
            pulse = pulse,
            onSetBpm = onSetBpm,
        )

        Spacer(Modifier.height(16.dp))

        // Main Play / Stop Hero Action Bar & Tap Tempo
        ActionControlsRow(
            running = running,
            onTapTempo = onTapTempo,
            onToggle = onToggle,
        )

        Spacer(Modifier.height(16.dp))

        // Time Signature Selector Card
        TimeSignatureCard(
            selectedTimeSignature = config.timeSignature,
            onSetTimeSignature = onSetTimeSignature,
        )

        Spacer(Modifier.height(14.dp))

        // Sound Selection & Silent Vibration Mode Card
        SoundAndVibrationCard(
            config = config,
            onSetVibrateOnly = onSetVibrateOnly,
            onSetSound = onSetSound,
        )

        Spacer(Modifier.height(14.dp))

        // Click Volume Card
        VolumeCard(
            volume = config.volume,
            onSetVolume = onSetVolume,
        )

        Spacer(Modifier.height(24.dp))
    }
}