package com.mmt.guitarlab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.mmt.guitarlab.data.LanguageManager
import com.mmt.guitarlab.ui.navigation.GuitarLabApp
import com.mmt.guitarlab.ui.theme.GuitarLabTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val languageCode by LanguageManager.getLanguageStream(this)
                .collectAsState(initial = LanguageManager.getInitialLanguageCode(this))

            val localizedContext = remember(languageCode) {
                LanguageManager.applyLocale(this, languageCode)
                LanguageManager.createLocalizedContext(this, languageCode)
            }

            CompositionLocalProvider(LocalContext provides localizedContext) {
                GuitarLabTheme {
                    GuitarLabApp()
                }
            }
        }
    }
}
