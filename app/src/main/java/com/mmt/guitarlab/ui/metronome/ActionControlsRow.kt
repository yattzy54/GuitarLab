package com.mmt.guitarlab.ui.metronome

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.TouchApp
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mmt.guitarlab.ui.components.Studio3DAccent
import com.mmt.guitarlab.ui.components.Studio3DIconBadge
import com.mmt.guitarlab.core.ui.theme.ElectricAmber
import com.mmt.guitarlab.core.ui.theme.ElectricRuby
import com.mmt.guitarlab.core.ui.theme.GuitarLabTheme
import com.mmt.guitarlab.core.ui.theme.StudioCardBorder
import com.mmt.guitarlab.core.ui.theme.StudioDarkBg
import com.mmt.guitarlab.core.ui.theme.StudioTextPrimary
import com.mmt.guitarlab.core.ui.theme.StudioTextSecondary

@Composable
fun ActionControlsRow(
    running: Boolean,
    onTapTempo: () -> Unit,
    onToggle: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Tap Tempo 3D Button
        Row(
            modifier = Modifier
                .weight(1f)
                .height(60.dp)
                .clip(RoundedCornerShape(18.dp))
                .background(
                    brush = Brush.verticalGradient(
                        listOf(Color(0xFF262F42), Color(0xFF181E2B)),
                    ),
                )
                .border(1.dp, StudioCardBorder, RoundedCornerShape(18.dp))
                .clickable { onTapTempo() }
                .padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            Studio3DIconBadge(
                icon = Icons.Default.TouchApp,
                contentDescription = null,
                size = 36.dp,
                accent = Studio3DAccent.TEAL,
            )
            Spacer(Modifier.width(10.dp))
            Column {
                Text(
                    text = "TAP TEMPO",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = StudioTextPrimary,
                )
                Text(
                    text = "Tap to rhythm",
                    style = MaterialTheme.typography.labelSmall,
                    color = StudioTextSecondary,
                )
            }
        }

        // Play / Pause Master 3D Button
        Box(
            modifier = Modifier
                .size(68.dp)
                .shadow(
                    elevation = 10.dp,
                    shape = CircleShape,
                    ambientColor = if (running) ElectricRuby else ElectricAmber,
                    spotColor = if (running) ElectricRuby else ElectricAmber,
                )
                .clip(CircleShape)
                .background(
                    brush = Brush.verticalGradient(
                        if (running) {
                            listOf(Color(0xFFFF5277), Color(0xFFFF2A55), Color(0xFFB80028))
                        } else {
                            listOf(Color(0xFFFFD166), ElectricAmber, Color(0xFFC47D00))
                        },
                    ),
                )
                .border(
                    1.5.dp,
                    Color.White.copy(alpha = 0.4f),
                    CircleShape,
                )
                .clickable { onToggle() },
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = if (running) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = if (running) "Stop" else "Start",
                tint = if (running) Color.White else Color(0xFF1F1400),
                modifier = Modifier.size(34.dp),
            )
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun ActionControlsRowPreview() {
    GuitarLabTheme {
        Column(
            modifier = Modifier
                .background(StudioDarkBg)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            ActionControlsRow(running = false, onTapTempo = {}, onToggle = {})
            ActionControlsRow(running = true, onTapTempo = {}, onToggle = {})
        }
    }
}