package com.mmt.guitarlab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.mmt.guitarlab.ui.navigation.GuitarLabApp
import com.mmt.guitarlab.ui.theme.GuitarLabTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GuitarLabTheme {
                GuitarLabApp()
            }
        }
    }
}
