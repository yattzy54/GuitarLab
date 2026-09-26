package com.mmt.guitarlab.feature.tuxguitar.browser

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

data class TuxGuitarBrowserUiState(
    val currentPath: String = "/",
    val items: List<BrowserItemUiModel> = emptyList(),
    val isLoading: Boolean = false,
    val searchQuery: String = ""
)

data class BrowserItemUiModel(
    val id: String,
    val name: String,
    val isDirectory: Boolean,
    val path: String
)

@HiltViewModel
class TuxGuitarBrowserViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(TuxGuitarBrowserUiState())
    val uiState: StateFlow<TuxGuitarBrowserUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        _uiState.update {
            it.copy(
                items = listOf(
                    BrowserItemUiModel("1", "Demo Songs", true, "/demo-songs"),
                    BrowserItemUiModel("2", "My Tabs", true, "/my-tabs"),
                    BrowserItemUiModel("3", "song_example.gp5", false, "/song_example.gp5")
                )
            )
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onItemClicked(item: BrowserItemUiModel) {
        if (item.isDirectory) {
            _uiState.update { 
                it.copy(
                    currentPath = item.path,
                    items = listOf(
                        BrowserItemUiModel("sub1", "track_1.gp4", false, item.path + "/track_1.gp4")
                    )
                )
            }
        }
    }
}
