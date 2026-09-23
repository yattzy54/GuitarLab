package com.mmt.guitarlab.ui.tab.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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
    var isExpanded by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(Unit) {
                detectVerticalDragGestures { _, dragAmount ->
                    if (dragAmount < -15f) {
                        isExpanded = true
                    } else if (dragAmount > 15f) {
                        isExpanded = false
                    }
                }
            }
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .shadow(elevation = 16.dp, shape = RoundedCornerShape(24.dp), spotColor = Color(0x66000000))
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xF014171E))
                .border(1.dp, Color(0xFF2C323E), RoundedCornerShape(24.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            // Sleek Wide Bar Handle (without "Управление" text)
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { isExpanded = !isExpanded }
                    .padding(horizontal = 24.dp, vertical = 5.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .height(4.dp)
                        .background(Color(0xFF4B5563), RoundedCornerShape(2.dp))
                )
            }

            // COLLAPSED COMPACT DOCK (minimalist floating bar)
            if (!isExpanded) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 2.dp, bottom = 2.dp)
                ) {
                    // Active Track Pill
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF232832))
                            .border(1.dp, Color(0xFF374151), RoundedCornerShape(12.dp))
                            .clickable { onOpenMixer() }
                            .padding(horizontal = 8.dp, vertical = 5.dp),
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
                            modifier = Modifier.widthIn(max = 95.dp)
                        )
                    }

                    // Compact Circular Play/Pause
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .shadow(6.dp, CircleShape, spotColor = Color(0xFF10B981))
                            .clip(CircleShape)
                            .background(Color(0xFF10B981))
                            .clickable { onTogglePlay() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = Color(0xFF09090B),
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    // Loop Indicator Pill
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
                            .padding(horizontal = 8.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
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

                    // Speed Pill
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF232832))
                            .border(1.dp, Color(0xFF374151), RoundedCornerShape(12.dp))
                            .clickable { onOpenTempoPicker() }
                            .padding(horizontal = 8.dp, vertical = 5.dp),
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
                }
            }

            // EXPANDED CONTROLS DOCK (revealed when swiped up or tapped)
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.padding(top = 6.dp, bottom = 4.dp)
                ) {
                    // 1. Active Track & Mixer Button
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF232832))
                            .border(1.dp, Color(0xFF374151), RoundedCornerShape(14.dp))
                            .clickable { onOpenMixer() }
                            .padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
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
                                fontSize = 10.sp
                            )
                        }
                    }

                    // 2. Loop A-B Button
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isLoopActive) Color(0x33F59E0B) else Color(0xFF232832))
                            .border(
                                width = 1.dp,
                                color = if (isLoopActive) Color(0xFFF59E0B) else Color(0xFF374151),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { onToggleLoop() }
                            .padding(horizontal = 10.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Repeat,
                            contentDescription = "Loop A-B",
                            tint = if (isLoopActive) Color(0xFFFBBF24) else Color(0xFF9CA3AF),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (isLoopActive && loopRange != null) "Такты ${loopRange.first}-${loopRange.second}" else "Зациклить",
                            color = if (isLoopActive) Color(0xFFFBBF24) else Color(0xFFE5E7EB),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // 3. Prominent Circular Play / Pause
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
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // 4. Tempo / Speed Picker
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF272A30))
                            .border(1.dp, Color(0xFF3F3F46), RoundedCornerShape(14.dp))
                            .clickable { onOpenTempoPicker() }
                            .padding(horizontal = 8.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = "Темп",
                            tint = Color(0xFFFBBF24),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "${(speedRatio * 100).toInt()}%",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    // 5. More Options Button
                    IconButton(
                        onClick = onOpenMoreMenu,
                        modifier = Modifier.size(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreHoriz,
                            contentDescription = "Ещё",
                            tint = Color(0xFF9CA3AF),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
