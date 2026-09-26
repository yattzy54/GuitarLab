package com.mmt.guitarlab.ui.metronome.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mmt.guitarlab.core.ui.theme.ElectricRuby
import com.mmt.guitarlab.core.ui.theme.ElectricTeal
import com.mmt.guitarlab.core.ui.theme.GuitarLabTheme
import com.mmt.guitarlab.core.ui.theme.StudioDarkBg
import com.mmt.guitarlab.core.ui.theme.StudioTextSecondary

@Composable
 fun PlayPauseButton(
    running: Boolean,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(56.dp)
            .shadow(
                elevation = 8.dp,
                shape = CircleShape,
                ambientColor = if (running) ElectricRuby else ElectricTeal,
                spotColor = if (running) ElectricRuby else ElectricTeal,
            )
            .clip(CircleShape)
            .background(
                brush = Brush.verticalGradient(
                    if (running) {
                        listOf(Color(0xFFFF5277), Color(0xFFFF2A55), Color(0xFFB80028))
                    } else {
                        listOf(Color(0xFF80F5FF), ElectricTeal, Color(0xFF008394))
                    },
                ),
            )
            .border(1.dp, Color.White.copy(alpha = 0.4f), CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = if (running) Icons.Default.Pause else Icons.Default.PlayArrow,
            contentDescription = if (running) "Stop" else "Start",
            tint = if (running) Color.White else Color(0xFF002227),
            modifier = Modifier.size(32.dp),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun PlayPauseButtonPreview() {
    GuitarLabTheme {
        Row(
            modifier = Modifier
                .background(StudioDarkBg)
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Состояние: Неактивен (Показывает иконку Play)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PlayPauseButton(
                    running = false,
                    onClick = {}
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Ready / Play",
                    style = MaterialTheme.typography.labelSmall,
                    color = StudioTextSecondary
                )
            }

            // Состояние: Активен (Показывает иконку Pause и другой градиент)
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                PlayPauseButton(
                    running = true,
                    onClick = {}
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "Running / Pause",
                    style = MaterialTheme.typography.labelSmall,
                    color = StudioTextSecondary
                )
            }
        }
    }
}
