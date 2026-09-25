package com.mmt.guitarlab.ui.metronome

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.mmt.guitarlab.domain.model.MetronomeConfig
import com.mmt.guitarlab.domain.model.TimeSignature
import com.mmt.guitarlab.ui.theme.GuitarLabTheme

@Composable
fun MetronomeScreen(viewModel: MetronomeViewModel = hiltViewModel()) {
    val config by viewModel.config.collectAsStateWithLifecycle()
    val beat by viewModel.beat.collectAsStateWithLifecycle()
    val running by viewModel.running.collectAsStateWithLifecycle()

    MetronomeContent(
        config = config,
        beat = beat,
        running = running,
        onSetBpm = viewModel::setBpm,
        onTapTempo = viewModel::onTapTempo,
        onToggle = viewModel::toggle,
        onSetTimeSignature = viewModel::setTimeSignature,
        onSetVibrateOnly = viewModel::setVibrateOnly,
        onSetSound = viewModel::setSound,
        onSetVolume = viewModel::setVolume,
    )
}

@Preview(showBackground = true)
@Composable
private fun MetronomeScreenPreview() {
    GuitarLabTheme {
        MetronomeContent(
            config = MetronomeConfig(
                bpm = 120,
                timeSignature = TimeSignature.FOUR_FOUR,
                volume = 0.8f,
            ),
            running = false,
        )
    }
}