package app.tuxguitar.android.compose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

/**
 * Standalone Compose entry point used while legacy activities are migrated.
 *
 * It is intentionally opt-in: the existing TGActivity remains the integration
 * point for the Java action engine until each editor surface has a Kotlin
 * equivalent.
 */
class TGComposeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isPlaying by remember { mutableStateOf(false) }
            MaterialTheme {
                TGComposeEditor(
                    state = TGComposeEditorState(isPlaying = isPlaying),
                    onPlayPause = { isPlaying = !isPlaying },
                    onSave = { },
                )
            }
        }
    }
}
