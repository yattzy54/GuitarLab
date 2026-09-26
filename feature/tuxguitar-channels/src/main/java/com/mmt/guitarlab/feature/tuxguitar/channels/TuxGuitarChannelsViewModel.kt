package com.mmt.guitarlab.feature.tuxguitar.channels

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class ChannelUiModel(
    val id: Int,
    val name: String,
    val volume: Int,
    val instrument: String
)

data class TuxGuitarChannelsUiState(
    val channels: List<ChannelUiModel> = emptyList(),
    val isLoading: Boolean = false
)

@HiltViewModel
class TuxGuitarChannelsViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(TuxGuitarChannelsUiState())
    val uiState: StateFlow<TuxGuitarChannelsUiState> = _uiState.asStateFlow()

    init {
        loadChannels()
    }

    private fun loadChannels() {
        _uiState.update {
            it.copy(
                channels = listOf(
                    ChannelUiModel(1, "Acoustic Guitar", 100, "Acoustic Grand Piano"),
                    ChannelUiModel(2, "Electric Guitar", 90, "Distortion Guitar"),
                    ChannelUiModel(3, "Bass Guitar", 95, "Electric Bass (finger)")
                )
            )
        }
    }

    fun updateVolume(channelId: Int, newVolume: Int) {
        _uiState.update { state ->
            state.copy(
                channels = state.channels.map {
                    if (it.id == channelId) it.copy(volume = newVolume) else it
                }
            )
        }
    }
}
