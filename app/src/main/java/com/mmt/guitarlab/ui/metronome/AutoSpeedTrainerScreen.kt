package com.mmt.guitarlab.ui.metronome

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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mmt.guitarlab.domain.model.MetronomeBeat
import com.mmt.guitarlab.domain.model.MetronomeConfig
import com.mmt.guitarlab.domain.model.MetronomeSound
import com.mmt.guitarlab.domain.model.TrainerIntervalKind
import com.mmt.guitarlab.ui.metronome.components.LiveSpeedProgressCard
import com.mmt.guitarlab.ui.metronome.components.SoundSelectionCard
import com.mmt.guitarlab.ui.metronome.components.TrainerSettingsCard
import com.mmt.guitarlab.ui.theme.GuitarLabTheme
import com.mmt.guitarlab.ui.theme.StudioDarkBg

@Composable
fun AutoSpeedTrainerScreen(viewModel: MetronomeViewModel = hiltViewModel()) {
    val config by viewModel.config.collectAsStateWithLifecycle()
    val beat by viewModel.beat.collectAsStateWithLifecycle()
    val running by viewModel.running.collectAsStateWithLifecycle()

    AutoSpeedTrainerContent(
        config = config,
        beat = beat,
        running = running,
        onTogglePlay = {
            if (!config.trainer.enabled) viewModel.setTrainerEnabled(true)
            viewModel.toggle()
        },
        onSetSound = viewModel::setSound,
        onSetTrainerStart = viewModel::setTrainerStart,
        onSetTrainerTarget = viewModel::setTrainerTarget,
        onSetTrainerIncrement = viewModel::setTrainerIncrement,
        onSetTrainerIntervalKind = viewModel::setTrainerIntervalKind,
        onSetTrainerIntervalValue = viewModel::setTrainerIntervalValue,
    )
}

@Composable
fun AutoSpeedTrainerContent(
    config: MetronomeConfig,
    beat: MetronomeBeat? = null,
    running: Boolean = false,
    onTogglePlay: () -> Unit = {},
    onSetSound: (MetronomeSound) -> Unit = {},
    onSetTrainerStart: (Int) -> Unit = {},
    onSetTrainerTarget: (Int) -> Unit = {},
    onSetTrainerIncrement: (Int) -> Unit = {},
    onSetTrainerIntervalKind: (TrainerIntervalKind) -> Unit = {},
    onSetTrainerIntervalValue: (Int) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Status & Live Speed Progress Card
        LiveSpeedProgressCard(
            config = config,
            beat = beat,
            running = running,
            onTogglePlay = onTogglePlay,
        )

        Spacer(Modifier.height(16.dp))

        // Metronome Sound Selection Card
        SoundSelectionCard(
            selectedSound = config.sound,
            onSetSound = onSetSound,
        )

        Spacer(Modifier.height(16.dp))

        // Trainer Settings Card
        TrainerSettingsCard(
            trainer = config.trainer,
            onSetTrainerStart = onSetTrainerStart,
            onSetTrainerTarget = onSetTrainerTarget,
            onSetTrainerIncrement = onSetTrainerIncrement,
            onSetTrainerIntervalKind = onSetTrainerIntervalKind,
            onSetTrainerIntervalValue = onSetTrainerIntervalValue,
        )

        Spacer(Modifier.height(24.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun AutoSpeedTrainerScreenPreview() {
    GuitarLabTheme {
        AutoSpeedTrainerContent(
            config = MetronomeConfig(
                bpm = 100,
            ),
            running = false,
        )
    }
}