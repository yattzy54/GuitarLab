package com.mmt.guitarlab.ui.tab.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

enum class AudioSourceMode {
    ORIG, SYNTH
}

@Composable
fun SongsterrBottomControlBar(
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    activeTrackName: String,
    speedRatio: Float,
    audioSource: AudioSourceMode,
    onToggleAudioSource: (AudioSourceMode) -> Unit,
    isLoopActive: Boolean,
    onToggleLoop: () -> Unit,
    onOpenMixer: () -> Unit,
    onOpenTempoPicker: () -> Unit,
    onOpenMoreMenu: () -> Unit,
    trackCount: Int,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        // Floating dock capsule
        Row(
            modifier = Modifier
                .shadow(elevation = 16.dp, shape = RoundedCornerShape(28.dp), spotColor = Color(0x66000000))
                .clip(RoundedCornerShape(28.dp))
                .background(Color(0xE6181B20))
                .border(1.dp, Color(0xFF2E333D), RoundedCornerShape(28.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            // 1. Active Track Indicator & Mixer / Tracks Button
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF232832))
                    .border(1.dp, Color(0xFF374151), RoundedCornerShape(14.dp))
                    .clickable { onOpenMixer() }
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Партии",
                    tint = Color(0xFFFBBF24),
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = activeTrackName,
                    color = Color(0xFFF3F4F6),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = 85.dp)
                )
                if (trackCount > 1) {
                    Text(
                        text = "($trackCount)",
                        color = Color(0xFF9CA3AF),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Normal,
                    )
                }
            }

            // 2. Loop A-B button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isLoopActive) Color(0x33F59E0B) else Color.Transparent)
                    .border(
                        width = 1.dp,
                        color = if (isLoopActive) Color(0xFFF59E0B) else Color.Transparent,
                        shape = RoundedCornerShape(14.dp)
                    )
                    .clickable { onToggleLoop() }
                    .padding(horizontal = 6.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Repeat,
                    contentDescription = "Loop A-B",
                    tint = if (isLoopActive) Color(0xFFFBBF24) else Color(0xFF9CA3AF),
                    modifier = Modifier.size(18.dp)
                )
            }

            // 3. Tempo Indicator & Regulator (e.g. 100%)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF272A30))
                    .border(1.dp, Color(0xFF3F3F46), RoundedCornerShape(14.dp))
                    .clickable { onOpenTempoPicker() }
                    .padding(horizontal = 6.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Speed,
                    contentDescription = "Tempo",
                    tint = Color(0xFFFBBF24),
                    modifier = Modifier.size(15.dp)
                )
                Text(
                    text = "${(speedRatio * 100).toInt()}%",
                    color = Color.White,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }

            // 4. Prominent Circular Play / Pause Button
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .shadow(8.dp, CircleShape, spotColor = Color(0xFF10B981))
                    .clip(CircleShape)
                    .background(Color(0xFF10B981))
                    .clickable { onTogglePlay() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color(0xFF09090B),
                    modifier = Modifier.size(26.dp)
                )
            }

            // 5. Audio Source Toggle: [ORIG.] / [SYNTH]
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF0D0F12))
                    .border(1.dp, Color(0xFF272A30), RoundedCornerShape(12.dp))
                    .padding(2.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (audioSource == AudioSourceMode.ORIG) Color(0xFFF59E0B) else Color.Transparent)
                        .clickable { onToggleAudioSource(AudioSourceMode.ORIG) }
                        .padding(horizontal = 5.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "ORIG.",
                        color = if (audioSource == AudioSourceMode.ORIG) Color(0xFF09090B) else Color(0xFF9CA3AF),
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (audioSource == AudioSourceMode.SYNTH) Color(0xFFF59E0B) else Color.Transparent)
                        .clickable { onToggleAudioSource(AudioSourceMode.SYNTH) }
                        .padding(horizontal = 5.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "SYNTH",
                        color = if (audioSource == AudioSourceMode.SYNTH) Color(0xFF09090B) else Color(0xFF9CA3AF),
                        fontSize = 9.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 6. More Options (...)
            IconButton(
                onClick = onOpenMoreMenu,
                modifier = Modifier.size(32.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.MoreHoriz,
                    contentDescription = "More Options",
                    tint = Color(0xFF9CA3AF),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}
