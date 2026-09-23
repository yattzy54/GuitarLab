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
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mmt.guitarlab.domain.model.NoteDuration
import com.mmt.guitarlab.domain.model.NoteEffect
import com.mmt.guitarlab.domain.model.TabTrack

/**
 * High-performance Jetpack Compose Canvas renderer for tablatures.
 * Renders strings with 26.dp spacing to prevent fret badge overlap,
 * measure headers, fixed palm-mute brackets, interactive loop badges,
 * rhythm stems, and green animated playhead.
 */
@Composable
fun TabCanvasRenderer(
    track: TabTrack,
    currentMeasureIndex: Int,
    currentBeatIndex: Int,
    isPlaying: Boolean,
    loopRange: Pair<Int, Int>?,
    onSelectPosition: (measureIdx: Int, beatIdx: Int) -> Unit,
    onMeasureTapped: ((measureIndex: Int) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val textMeasurer = rememberTextMeasurer()
    val stringCount = track.stringCount.coerceAtLeast(4)
    val stringLabels = track.stringLabels.ifEmpty { listOf("e", "B", "G", "D", "A", "E") }

    val density = LocalDensity.current
    // Generous string spacing ensuring fret badges never collide
    val stringSpacing = with(density) { 24.dp.toPx() }
    val topPadding = with(density) { 40.dp.toPx() }
    val stemHeight = with(density) { 28.dp.toPx() }
    val measureBottomPadding = with(density) { 20.dp.toPx() }
    val measureTotalHeight = topPadding + (stringCount - 1) * stringSpacing + stemHeight + measureBottomPadding

    val totalHeightDp = with(density) {
        ((track.measures.size.coerceAtLeast(1) * measureTotalHeight) + 120.dp.toPx()).toDp()
    }

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
                        val mIdx = (offset.y / measureTotalHeight).toInt()
                            .coerceIn(0, track.measures.lastIndex.coerceAtLeast(0))
                        
                        onMeasureTapped?.invoke(mIdx)

                        val measure = track.measures.getOrNull(mIdx)
                        if (measure != null && measure.beats.isNotEmpty()) {
                            val beatsCount = measure.beats.size
                            val leftPadding = 42f
                            val contentWidth = size.width - leftPadding - 12f
                            val beatWidth = contentWidth / beatsCount.coerceAtLeast(1)
                            val relativeX = offset.x - leftPadding
                            val bIdx = (relativeX / beatWidth).toInt().coerceIn(0, beatsCount - 1)
                            onSelectPosition(mIdx, bIdx)
                        }
                    }
                }
        ) {
            val width = size.width
            val leftMargin = 42f
            val rightMargin = 12f
            val tabWidth = (width - leftMargin - rightMargin).coerceAtLeast(10f)

            var currentY = 16f

            track.measures.forEachIndexed { mIdx, measure ->
                val isMeasureActive = isPlaying && currentMeasureIndex == mIdx
                val barNumber = mIdx + 1
                val isLoopStart = loopRange != null && barNumber == loopRange.first
                val isLoopEnd = loopRange != null && barNumber == loopRange.second
                val isInLoop = loopRange != null && barNumber >= loopRange.first && barNumber <= loopRange.second

                // Measure card background
                val cardRect = Size(width - 24f, measureTotalHeight - 12f)
                val cardColor = when {
                    isMeasureActive -> Color(0xFF1B241D)
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
                    style = Stroke(width = if (isMeasureActive) 2.5f else if (isInLoop) 2.0f else 1f),
                )

                // Measure Header (Bar number & Time signature)
                val barLabel = "BAR ${measure.number} [${measure.timeSignatureNumerator}/${measure.timeSignatureDenominator}]"
                val barLayout = textMeasurer.measure(
                    text = barLabel,
                    style = TextStyle(
                        color = if (isMeasureActive) Color(0xFF34D399) else Color(0xFF9CA3AF),
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold,
                    )
                )

                drawText(
                    textLayoutResult = barLayout,
                    topLeft = Offset(14f.coerceAtMost((width - barLayout.size.width - 4f).coerceAtLeast(0f)), currentY + 12f),
                )

                // PALM MUTE: Clean Header Badge and Bracket (no broken layout)
                if (measure.palmMute) {
                    val pmBadgeX = 14f + barLayout.size.width + 10f
                    val pmBadgeY = currentY + 11f
                    drawRoundRect(
                        color = Color(0x33F59E0B),
                        topLeft = Offset(pmBadgeX, pmBadgeY),
                        size = Size(44f, 16f),
                        cornerRadius = CornerRadius(4f, 4f),
                    )
                    drawRoundRect(
                        color = Color(0xFFF59E0B),
                        topLeft = Offset(pmBadgeX, pmBadgeY),
                        size = Size(44f, 16f),
                        cornerRadius = CornerRadius(4f, 4f),
                        style = Stroke(width = 1f),
                    )
                    if (pmBadgeX + 44f < width) {
                        val pmLayout = textMeasurer.measure(
                            text = "P.M.",
                            style = TextStyle(
                                color = Color(0xFFF59E0B),
                                fontSize = 9.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                            )
                        )
                        drawText(
                            textLayoutResult = pmLayout,
                            topLeft = Offset(pmBadgeX + 8f, pmBadgeY + 1f),
                        )
                    }
                }

                // LOOP MARKER BADGES on Measure Top-Right
                val loopBadgeText = when {
                    isLoopStart && isLoopEnd -> "🔁 ЦИКЛ (A=B: Такт $barNumber)"
                    isLoopStart -> "🔁 СТАРТ ЦИКЛА (A: Такт $barNumber)"
                    isLoopEnd -> "🔁 КОНЕЦ ЦИКЛА (B: Такт $barNumber)"
                    else -> null
                }
                if (loopBadgeText != null) {
                    val loopTextLayout = textMeasurer.measure(
                        text = loopBadgeText,
                        style = TextStyle(
                            fontSize = 9.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                        )
                    )
                    val badgeW = (loopTextLayout.size.width + 14).toFloat()
                    val badgeH = 18f
                    val badgeX = (width - rightMargin - badgeW - 8f).coerceIn(leftMargin, width - badgeW)
                    val badgeY = currentY + 10f

                    drawRoundRect(
                        color = Color(0xFFF59E0B),
                        topLeft = Offset(badgeX, badgeY),
                        size = Size(badgeW, badgeH),
                        cornerRadius = CornerRadius(5f, 5f),
                    )
                    if (badgeX >= 0f && badgeX + badgeW <= width) {
                        drawText(
                            textLayoutResult = loopTextLayout,
                            topLeft = Offset(badgeX + 7f, badgeY + 1f),
                        )
                    }
                }

                val stringsStartY = currentY + topPadding

                // PALM MUTE BRACKET above strings (dashed line across muted measure)
                if (measure.palmMute) {
                    val pmLineY = stringsStartY - 10f
                    drawLine(
                        color = Color(0xFFF59E0B),
                        start = Offset(leftMargin, pmLineY),
                        end = Offset(width - rightMargin - 4f, pmLineY),
                        strokeWidth = 1.5f,
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 6f), 0f),
                    )
                    drawLine(
                        color = Color(0xFFF59E0B),
                        start = Offset(width - rightMargin - 4f, pmLineY),
                        end = Offset(width - rightMargin - 4f, pmLineY + 6f),
                        strokeWidth = 1.5f,
                    )
                }

                // Draw string names (e, B, G, D, A, E) on the left margin, aligned with string lines
                for (sIdx in 0 until stringCount) {
                    val label = stringLabels.getOrElse(sIdx) { "" }
                    val stringY = stringsStartY + sIdx * stringSpacing
                    val labelLayout = textMeasurer.measure(
                        text = label,
                        style = TextStyle(
                            color = Color(0xFFFBBF24),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                        )
                    )
                    drawText(
                        textLayoutResult = labelLayout,
                        topLeft = Offset(14f, stringY - labelLayout.size.height / 2f),
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
                            val activeHeight = (stringCount - 1) * stringSpacing + stemHeight + 16f
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
                                        fontSize = if (isBeatActive) 13.sp else 12.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Black,
                                    )
                                )

                                val fretBoxWidth = (textLayout.size.width + 12).toFloat()
                                val fretBoxHeight = (textLayout.size.height + 4).toFloat().coerceAtMost(stringSpacing - 4f)

                                // Fret background badge: masks horizontal string line cleanly behind fret digit
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
                                    style = Stroke(width = 1.2f),
                                )

                                // Text
                                val fretTextX = (beatCenterX - textLayout.size.width / 2f).coerceIn(0f, (width - textLayout.size.width).coerceAtLeast(0f))
                                val fretTextLayout = textMeasurer.measure(
                                    text = fretText,
                                    style = TextStyle(
                                        color = if (isBeatActive) Color(0xFF09090B) else Color(0xFFF4F4F5),
                                        fontSize = if (isBeatActive) 13.sp else 12.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.Black,
                                    )
                                )
                                drawText(
                                    textLayoutResult = fretTextLayout,
                                    topLeft = Offset(fretTextX, noteY - fretTextLayout.size.height / 2f)
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
