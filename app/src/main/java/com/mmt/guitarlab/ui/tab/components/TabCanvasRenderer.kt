package com.mmt.guitarlab.ui.tab.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mmt.guitarlab.domain.model.NoteDuration
import com.mmt.guitarlab.domain.model.NoteEffect
import com.mmt.guitarlab.domain.model.TabMeasure
import com.mmt.guitarlab.domain.model.TabTrack

/**
 * High-performance 60 FPS Jetpack Compose Canvas renderer for tablature.
 * Renders strings, measures, fret digits, stems, palm-mutes and animated green playhead frame.
 */
@Composable
fun TabCanvasRenderer(
    track: TabTrack,
    currentMeasureIndex: Int,
    currentBeatIndex: Int,
    isPlaying: Boolean,
    loopRange: Pair<Int, Int>?,
    onSelectPosition: (measureIdx: Int, beatIdx: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val textMeasurer = rememberTextMeasurer()
    val stringCount = track.stringCount.coerceAtLeast(4)
    val stringLabels = track.stringLabels.ifEmpty { listOf("e", "B", "G", "D", "A", "E") }

    val stringSpacing = 28f
    val topPadding = 48f
    val stemHeight = 36f
    val measureHeaderHeight = 32f
    val measureTotalHeight = topPadding + (stringCount - 1) * stringSpacing + stemHeight + 24f

    val totalHeightDp = (track.measures.size * measureTotalHeight / 2.7f).coerceAtLeast(300f).dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF0D0F12))
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(totalHeightDp)
                .pointerInput(track.measures) {
                    detectTapGestures { offset ->
                        val mIdx = (offset.y / measureTotalHeight).toInt().coerceIn(0, track.measures.lastIndex.coerceAtLeast(0))
                        val measure = track.measures.getOrNull(mIdx)
                        if (measure != null && measure.beats.isNotEmpty()) {
                            val beatsCount = measure.beats.size
                            val leftPadding = 56f
                            val contentWidth = size.width - leftPadding - 24f
                            val beatWidth = contentWidth / beatsCount.coerceAtLeast(1)
                            val relativeX = offset.x - leftPadding
                            val bIdx = (relativeX / beatWidth).toInt().coerceIn(0, beatsCount - 1)
                            onSelectPosition(mIdx, bIdx)
                        }
                    }
                }
        ) {
            val width = size.width
            val leftMargin = 56f
            val rightMargin = 16f
            val tabWidth = width - leftMargin - rightMargin

            var currentY = 16f

            track.measures.forEachIndexed { mIdx, measure ->
                val isMeasureActive = isPlaying && currentMeasureIndex == mIdx
                val isInLoop = loopRange != null && (mIdx + 1) >= loopRange.first && (mIdx + 1) <= loopRange.second

                // Measure card background
                val cardRect = Size(width - 24f, measureTotalHeight - 12f)
                val cardColor = when {
                    isMeasureActive -> Color(0xFF1E2620)
                    isInLoop -> Color(0xFF262116)
                    else -> Color(0xFF13161C)
                }
                val borderColor = when {
                    isMeasureActive -> Color(0xFF10B981)
                    isInLoop -> Color(0xFFF59E0B)
                    else -> Color(0xFF272A30)
                }

                drawRoundRect(
                    color = cardColor,
                    topLeft = Offset(12f, currentY),
                    size = cardRect,
                    cornerRadius = CornerRadius(16f, 16f),
                )
                drawRoundRect(
                    color = borderColor,
                    topLeft = Offset(12f, currentY),
                    size = cardRect,
                    cornerRadius = CornerRadius(16f, 16f),
                    style = Stroke(width = if (isMeasureActive) 2.5f else 1f),
                )

                // Measure Header (Bar number & Time signature)
                val barLabel = "BAR ${measure.number} [${measure.timeSignatureNumerator}/${measure.timeSignatureDenominator}]"
                drawText(
                    textMeasurer = textMeasurer,
                    text = barLabel,
                    topLeft = Offset(24f, currentY + 10f),
                    style = TextStyle(
                        color = if (isMeasureActive) Color(0xFF34D399) else Color(0xFF9CA3AF),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                    )
                )

                // Palm Mute annotation line: P.M. ----------------|
                if (measure.palmMute) {
                    val pmLabel = measure.palmMuteLabel ?: "P.M. ----------------------------|"
                    drawText(
                        textMeasurer = textMeasurer,
                        text = pmLabel,
                        topLeft = Offset(160f, currentY + 10f),
                        style = TextStyle(
                            color = Color(0xFFF59E0B),
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                        )
                    )
                }

                val stringsStartY = currentY + topPadding

                // String labels on the left (e.g. D, A, F, C, G, C)
                for (sIdx in 0 until stringCount) {
                    val label = stringLabels.getOrNull(sIdx) ?: " "
                    val stringY = stringsStartY + sIdx * stringSpacing
                    drawText(
                        textMeasurer = textMeasurer,
                        text = label,
                        topLeft = Offset(24f, stringY - 10f),
                        style = TextStyle(
                            color = Color(0xFFFBBF24),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                        )
                    )
                }

                // Horizontal wire lines
                for (sIdx in 0 until stringCount) {
                    val stringY = stringsStartY + sIdx * stringSpacing
                    val thickness = ((stringCount - sIdx) * 0.45f).coerceAtLeast(1f)
                    drawLine(
                        color = if (isMeasureActive) Color(0xFF52525B) else Color(0xFF3F3F46),
                        start = Offset(leftMargin, stringY),
                        end = Offset(width - rightMargin, stringY),
                        strokeWidth = thickness,
                    )
                }

                // Vertical Bar divider lines
                drawLine(
                    color = Color(0xFF52525B),
                    start = Offset(leftMargin, stringsStartY),
                    end = Offset(leftMargin, stringsStartY + (stringCount - 1) * stringSpacing),
                    strokeWidth = 2f,
                )
                drawLine(
                    color = Color(0xFF52525B),
                    start = Offset(width - rightMargin, stringsStartY),
                    end = Offset(width - rightMargin, stringsStartY + (stringCount - 1) * stringSpacing),
                    strokeWidth = 2f,
                )

                // Draw Beats and Notes
                val beats = measure.beats
                if (beats.isNotEmpty()) {
                    val beatWidth = tabWidth / beats.size.coerceAtLeast(1)

                    beats.forEachIndexed { bIdx, beat ->
                        val isBeatActive = isMeasureActive && currentBeatIndex == bIdx
                        val beatCenterX = leftMargin + bIdx * beatWidth + beatWidth / 2f
                        val beatBoxLeft = leftMargin + bIdx * beatWidth + 2f
                        val beatBoxWidth = beatWidth - 4f

                        // Playhead green rounded frame around active beat
                        if (isBeatActive) {
                            val activeHeight = (stringCount - 1) * stringSpacing + stemHeight + 20f
                            drawRoundRect(
                                color = Color(0x3310B981),
                                topLeft = Offset(beatBoxLeft, stringsStartY - 10f),
                                size = Size(beatBoxWidth, activeHeight),
                                cornerRadius = CornerRadius(10f, 10f),
                            )
                            drawRoundRect(
                                color = Color(0xFF10B981),
                                topLeft = Offset(beatBoxLeft, stringsStartY - 10f),
                                size = Size(beatBoxWidth, activeHeight),
                                cornerRadius = CornerRadius(10f, 10f),
                                style = Stroke(width = 2.5f),
                            )
                        }

                        // Draw notes on strings
                        beat.notes.forEach { note ->
                            if (note.stringIndex in 0 until stringCount) {
                                val noteY = stringsStartY + note.stringIndex * stringSpacing
                                val fretText = when (note.effect) {
                                    NoteEffect.DEAD_NOTE -> "X"
                                    NoteEffect.SLIDE -> "${note.fret}/"
                                    NoteEffect.BEND -> "${note.fret}b"
                                    NoteEffect.VIBRATO -> "${note.fret}~"
                                    else -> note.fret.toString()
                                }

                                val textLayout = textMeasurer.measure(
                                    text = fretText,
                                    style = TextStyle(
                                        fontSize = if (isBeatActive) 14.sp else 12.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Black,
                                    )
                                )

                                val fretBoxWidth = (textLayout.size.width + 12).toFloat()
                                val fretBoxHeight = (textLayout.size.height + 4).toFloat()

                                // Fret background badge
                                drawRoundRect(
                                    color = if (isBeatActive) Color(0xFF10B981) else Color(0xFF18181B),
                                    topLeft = Offset(beatCenterX - fretBoxWidth / 2f, noteY - fretBoxHeight / 2f),
                                    size = Size(fretBoxWidth, fretBoxHeight),
                                    cornerRadius = CornerRadius(6f, 6f),
                                )
                                drawRoundRect(
                                    color = if (isBeatActive) Color(0xFF34D399) else Color(0xFF3F3F46),
                                    topLeft = Offset(beatCenterX - fretBoxWidth / 2f, noteY - fretBoxHeight / 2f),
                                    size = Size(fretBoxWidth, fretBoxHeight),
                                    cornerRadius = CornerRadius(6f, 6f),
                                    style = Stroke(width = 1f),
                                )

                                // Text
                                drawText(
                                    textMeasurer = textMeasurer,
                                    text = fretText,
                                    topLeft = Offset(
                                        beatCenterX - textLayout.size.width / 2f,
                                        noteY - textLayout.size.height / 2f
                                    ),
                                    style = TextStyle(
                                        color = if (isBeatActive) Color(0xFF09090B) else Color(0xFFF4F4F5),
                                        fontSize = if (isBeatActive) 14.sp else 12.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Black,
                                    )
                                )
                            }
                        }

                        // Rhythm Stem & Beam below strings
                        val stemStartY = stringsStartY + (stringCount - 1) * stringSpacing + 8f
                        val stemEndY = stemStartY + 18f
                        drawLine(
                            color = if (isBeatActive) Color(0xFF10B981) else Color(0xFF71717A),
                            start = Offset(beatCenterX, stemStartY),
                            end = Offset(beatCenterX, stemEndY),
                            strokeWidth = 2f,
                        )

                        // Flag for eighth or sixteenth note
                        if (beat.durationType == NoteDuration.EIGHTH || beat.durationType == NoteDuration.SIXTEENTH) {
                            drawLine(
                                color = if (isBeatActive) Color(0xFF10B981) else Color(0xFF71717A),
                                start = Offset(beatCenterX, stemEndY),
                                end = Offset(beatCenterX + 8f, stemEndY - 4f),
                                strokeWidth = 2.5f,
                            )
                        }
                        if (beat.durationType == NoteDuration.SIXTEENTH) {
                            drawLine(
                                color = if (isBeatActive) Color(0xFF10B981) else Color(0xFF71717A),
                                start = Offset(beatCenterX, stemEndY - 6f),
                                end = Offset(beatCenterX + 8f, stemEndY - 10f),
                                strokeWidth = 2.5f,
                            )
                        }
                    }
                }

                currentY += measureTotalHeight
            }
        }
    }
}
