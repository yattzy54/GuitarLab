package com.mmt.guitarlab.ui.tab.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mmt.guitarlab.core.ui.theme.GuitarLabTheme
import com.mmt.guitarlab.core.ui.theme.StudioDarkBg

/**
 * Fixed Bottom Control Bar for Tab Player:
 * Fixed compact dock with no swipe expand/collapse behavior.
 * Displays all essential studio controls in a single polished row:
 * Track selector / Mixer, Loop A-B, Circular Play/Pause, Tempo/Speed, More options.
 */
@Composable
fun SongsterrBottomControlBar(
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    activeTrackName: String,
    speedRatio: Float,
    isLoopActive: Boolean,
    loopRange: Pair<Int, Int>?,
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
            .padding(horizontal = 12.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .shadow(elevation = 16.dp, shape = RoundedCornerShape(20.dp), spotColor = Color(0x66000000))
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xF014171E))
                .border(1.dp, Color(0xFF2C323E), RoundedCornerShape(20.dp))
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 1. Active Track Selector / Mixer Button
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF232832))
                    .border(1.dp, Color(0xFF374151), RoundedCornerShape(12.dp))
                    .clickable { onOpenMixer() }
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Партии",
                    tint = Color(0xFFFBBF24),
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = activeTrackName,
                    color = Color(0xFFF3F4F6),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = 80.dp)
                )
            }

            // 2. Loop A-B Button
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isLoopActive) Color(0x33F59E0B) else Color(0xFF232832))
                    .border(
                        width = 1.dp,
                        color = if (isLoopActive) Color(0xFFF59E0B) else Color(0xFF374151),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable { onToggleLoop() }
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Repeat,
                    contentDescription = "Loop A-B",
                    tint = if (isLoopActive) Color(0xFFFBBF24) else Color(0xFF9CA3AF),
                    modifier = Modifier.size(14.dp)
                )
                if (isLoopActive && loopRange != null) {
                    Text(
                        text = "${loopRange.first}-${loopRange.second}",
                        color = Color(0xFFFBBF24),
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 3. Central Play / Pause Button
            Box(
                modifier = Modifier
                    .size(42.dp)
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
                    modifier = Modifier.size(24.dp)
                )
            }

            // 4. Tempo / Speed Picker Button
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF232832))
                    .border(1.dp, Color(0xFF374151), RoundedCornerShape(12.dp))
                    .clickable { onOpenTempoPicker() }
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Speed,
                    contentDescription = "Скорость",
                    tint = Color(0xFFFBBF24),
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    text = "${(speedRatio * 100).toInt()}%",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }

            // 5. More Options Button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF232832))
                    .border(1.dp, Color(0xFF374151), RoundedCornerShape(12.dp))
                    .clickable { onOpenMoreMenu() }
                    .padding(horizontal = 6.dp, vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.MoreHoriz,
                    contentDescription = "Ещё",
                    tint = Color(0xFF9CA3AF),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun SongsterrBottomControlBarPreview() {
    GuitarLabTheme {
        Column(
            modifier = Modifier
                .background(StudioDarkBg)
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Состояние 1: Пауза, обычный режим без петли
            SongsterrBottomControlBar(
                isPlaying = false,
                onTogglePlay = {},
                activeTrackName = "Guitar 1 (Distortion)",
                speedRatio = 1.0f,
                isLoopActive = false,
                loopRange = null,
                onToggleLoop = {},
                onOpenMixer = {},
                onOpenTempoPicker = {},
                onOpenMoreMenu = {},
                trackCount = 3,
            )

            // Состояние 2: Воспроизведение, с активной петлей и измененной скоростью
            SongsterrBottomControlBar(
                isPlaying = true,
                onTogglePlay = {},
                activeTrackName = "Solo Lead",
                speedRatio = 0.75f,
                isLoopActive = true,
                loopRange = Pair(12, 16),
                onToggleLoop = {},
                onOpenMixer = {},
                onOpenTempoPicker = {},
                onOpenMoreMenu = {},
                trackCount = 3,
            )
        }
    }
}