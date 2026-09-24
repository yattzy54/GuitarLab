package com.mmt.guitarlab.ui.tab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.hilt.navigation.compose.hiltViewModel
import com.mmt.guitarlab.ui.theme.GuitarLabTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SongsterrImportActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GuitarLabTheme {
                SongsterrTabPlayerScreen(
                    viewModel = hiltViewModel(),
                    onOpenDrawer = {},
                    onNavigateToEditor = { finish() },
                )
            }
        }
    }
}
