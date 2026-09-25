package com.mmt.guitarlab

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.SystemBarStyle
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import app.tuxguitar.android.ui.editor.EditorHost
import com.mmt.guitarlab.data.LanguageManager
import com.mmt.guitarlab.ui.navigation.GuitarLabApp
import com.mmt.guitarlab.core.ui.theme.GuitarLabTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Sole Activity. Android callbacks are forwarded through the editor's host
 * contract; engine ownership and navigation belong to the editor module.
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject lateinit var editorHost: EditorHost

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(android.graphics.Color.TRANSPARENT),
        )
        setContent {
            val languageCode by LanguageManager.getLanguageStream(this)
                .collectAsState(initial = LanguageManager.getInitialLanguageCode(this))

            val localizedContext = remember(languageCode) {
                LanguageManager.applyLocale(this, languageCode)
                LanguageManager.createLocalizedContext(this, languageCode)
            }

            CompositionLocalProvider(LocalContext provides localizedContext) {
                GuitarLabTheme {
                    GuitarLabApp(editorHost)
                }
            }
        }
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        editorHost.onActivityResult(requestCode, resultCode, data)
    }

    @Suppress("DEPRECATION")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        editorHost.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        editorHost.onNewIntent(intent)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (editorHost.onKeyDown(keyCode, event)) {
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        if (editorHost.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}
