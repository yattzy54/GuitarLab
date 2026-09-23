package com.mmt.guitarlab.ui.tab

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun TabViewerScreen(viewModel: TabViewModel = hiltViewModel()) {
    Box(modifier = Modifier.fillMaxSize()) {
        SongsterrTabPlayerScreen(viewModel = viewModel)
    }
}
