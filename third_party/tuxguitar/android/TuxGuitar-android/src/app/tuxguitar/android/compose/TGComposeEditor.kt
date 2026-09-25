package app.tuxguitar.android.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * Compose replacement for the Android editor chrome.
 *
 * The TuxGuitar model and action system remain independent from the UI. This
 * keeps the existing Java engine callable while screens are migrated one at a
 * time to Kotlin.
 */
@Immutable
data class TGComposeEditorState(
    val title: String = "Untitled song",
    val artist: String = "",
    val tempo: Int = 120,
    val isPlaying: Boolean = false,
)

@Composable
fun TGComposeEditor(
    state: TGComposeEditorState,
    onPlayPause: () -> Unit,
    onSave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(state.title, style = MaterialTheme.typography.titleLarge)
                    if (state.artist.isNotBlank()) {
                        Text(
                            text = state.artist,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                Text("${state.tempo} BPM", style = MaterialTheme.typography.labelLarge)
                IconButton(onClick = onPlayPause) {
                    Icon(
                        imageVector = if (state.isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                        contentDescription = if (state.isPlaying) "Pause" else "Play",
                    )
                }
                IconButton(onClick = onSave) {
                    Icon(Icons.Filled.Save, contentDescription = "Save")
                }
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(Color(0xFF17191E)),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "Tablature",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall,
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(onClick = onPlayPause, modifier = Modifier.weight(1f)) {
                    Text(if (state.isPlaying) "Pause" else "Play")
                }
                Button(onClick = onSave, modifier = Modifier.weight(1f)) {
                    Text("Save")
                }
            }
        }
    }
}

@Composable
fun rememberTGComposeEditorState(
    title: String = "Untitled song",
    artist: String = "",
    tempo: Int = 120,
): Pair<TGComposeEditorState, (Boolean) -> Unit> {
    var isPlaying by remember { mutableStateOf(false) }
    return TGComposeEditorState(title, artist, tempo, isPlaying) to { isPlaying = it }
}
