package com.mmt.guitarlab

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import app.tuxguitar.android.activity.TGActivity
import com.mmt.guitarlab.data.LanguageManager
import com.mmt.guitarlab.ui.navigation.GuitarLabApp
import com.mmt.guitarlab.ui.theme.GuitarLabTheme
import dagger.hilt.android.AndroidEntryPoint

/**
 * Sole Activity in the app (single-Activity architecture). The legacy
 * TuxGuitar editor (previously its own `TGActivity`/`TGReaderActivity`) now
 * runs as a [TGActivity] Fragment hosted from a Compose destination; hardware
 * key events and `ACTION_VIEW` intents that it used to receive directly as an
 * Activity are forwarded to whichever [TGActivity] instance is currently
 * attached, if any.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
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

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        TGActivity.currentInstance?.onActivityResult(requestCode, resultCode, data)
    }

    @Suppress("DEPRECATION")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        @Suppress("UNCHECKED_CAST")
        val permissionNames = permissions as Array<String>
        TGActivity.currentInstance?.onRequestPermissionsResult(requestCode, permissionNames, grantResults)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        TGActivity.currentInstance?.let {
            it.setIntent(intent)
            it.callProcessIntent()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        val current = TGActivity.currentInstance
        if (current != null && current.onKeyDown(keyCode, event)) {
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        val current = TGActivity.currentInstance
        if (current != null && current.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    @Suppress("DEPRECATION")
    override fun onBackPressed() {
        val current = TGActivity.currentInstance
        if (current != null) {
            current.callBackAction()
        } else {
            super.onBackPressed()
        }
    }
}

