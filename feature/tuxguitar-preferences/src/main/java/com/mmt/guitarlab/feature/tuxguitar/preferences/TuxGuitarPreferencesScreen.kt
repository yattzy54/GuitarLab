package com.mmt.guitarlab.feature.tuxguitar.preferences

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TuxGuitarPreferencesScreen(
    modifier: Modifier = Modifier,
    viewModel: TuxGuitarPreferencesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TuxGuitar Preferences") }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Playback & Audio", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Enable Sound")
                            Switch(
                                checked = uiState.soundEnabled,
                                onCheckedChange = viewModel::updateSoundEnabled
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("Metronome Volume: ${uiState.metronomeVolume}")
                        Slider(
                            value = uiState.metronomeVolume.toFloat(),
                            onValueChange = { viewModel.updateMetronomeVolume(it.toInt()) },
                            valueRange = 0f..100f
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = "Display & Layout", style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Show Score (Stave)")
                            Switch(
                                checked = uiState.showScore,
                                onCheckedChange = viewModel::updateShowScore
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Show Tablature")
                            Switch(
                                checked = uiState.showTablature,
                                onCheckedChange = viewModel::updateShowTablature
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Highlight Played Beat")
                            Switch(
                                checked = uiState.highlightPlayedBeat,
                                onCheckedChange = viewModel::updateHighlightPlayedBeat
                            )
                        }
                    }
                }
            }
        }
    }
}
