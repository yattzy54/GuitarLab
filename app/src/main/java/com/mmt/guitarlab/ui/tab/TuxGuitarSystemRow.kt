package com.mmt.guitarlab.ui.tab

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mmt.guitarlab.domain.model.TabMeasure
import com.mmt.guitarlab.ui.theme.GuitarLabTheme
import com.mmt.guitarlab.ui.theme.StudioDarkBg

@Composable
 fun TuxGuitarSystemRow(
    measures: List<TabMeasure>,
    startMeasureIndex: Int,
    stringCount: Int,
    stringLabels: List<String>,
    zoomScale: Float,
    isPlaying: Boolean,
    playbackMeasure: Int,
    playbackBeat: Int,
    selectedMeasure: Int,
    selectedBeat: Int,
    selectedString: Int,
    onCellSelected: (measureIdx: Int, beatIdx: Int, stringIdx: Int) -> Unit,
) {
    val textMeasurer = rememberTextMeasurer()
    val stringSpacing = (18 * zoomScale).dp
    val colWidth = (28 * zoomScale).dp

    val lineTrackColor = Color(0xFF4C566A)
    val staffTextColor = Color(0xFFD8DEE9)
    val primaryColor = Color(0xFFE5E9F0)
    val measureBarColor = Color(0xFF88C0D0)
    val caretColor = Color(0xFFEF4444) // TuxGuitar iconic Red Caret Cursor!

    val totalBeatsInLine = measures.sumOf { it.beats.size }
    val canvasWidth = ((totalBeatsInLine + measures.size * 2 + 4) * colWidth.value).coerceAtLeast(340f)
    val canvasHeight = ((stringCount + 1.5f) * stringSpacing.value).coerceAtLeast(140f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xFF1B2028))
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 8.dp),
    ) {
        Canvas(
            modifier = Modifier
                .width(canvasWidth.dp)
                .height(canvasHeight.dp)
                .padding(vertical = 8.dp)
                .pointerInput(measures, zoomScale) {
                    detectTapGestures { offset ->
                        val startX = 40f * zoomScale
                        val startY = 24f * zoomScale
                        val spacing = stringSpacing.toPx()
                        val colW = colWidth.toPx()

                        val tappedString = ((offset.y - startY + spacing / 2f) / spacing).toInt().coerceIn(0, stringCount - 1)

                        var currX = startX + 30f * zoomScale
                        measures.forEachIndexed { relativeMIdx, measure ->
                            val globalMIdx = startMeasureIndex + relativeMIdx
                            currX += 15f * zoomScale
                            measure.beats.forEachIndexed { bIdx, _ ->
                                if (offset.x >= currX - 8f && offset.x <= currX + colW + 8f) {
                                    onCellSelected(globalMIdx, bIdx, tappedString)
                                    return@detectTapGestures
                                }
                                currX += colW
                            }
                            currX += 15f * zoomScale
                        }
                    }
                },
        ) {
            val startX = 40f * zoomScale
            val startY = 24f * zoomScale
            val spacing = stringSpacing.toPx()

            // 6 horizontal string lines
            for (i in 0 until stringCount) {
                val y = startY + i * spacing
                drawLine(
                    color = lineTrackColor,
                    start = Offset(startX, y),
                    end = Offset(size.width - 10f, y),
                    strokeWidth = 1.5f * zoomScale,
                )
                val label = stringLabels.getOrElse(i) { "E" }
                drawText(
                    textMeasurer = textMeasurer,
                    text = label,
                    topLeft = Offset(8f * zoomScale, y - 8f * zoomScale),
                    style = TextStyle(color = staffTextColor, fontSize = (11 * zoomScale).sp, fontWeight = FontWeight.Bold),
                )
            }

            var xOffset = startX + 30f * zoomScale

            measures.forEachIndexed { relativeMIdx, measure ->
                val globalMIdx = startMeasureIndex + relativeMIdx

                // Vertical Measure Bar Line
                drawLine(
                    color = measureBarColor,
                    start = Offset(xOffset, startY),
                    end = Offset(xOffset, startY + (stringCount - 1) * spacing),
                    strokeWidth = 2.5f * zoomScale,
                )
                // Measure Number Header
                drawText(
                    textMeasurer = textMeasurer,
                    text = "M${measure.number}",
                    topLeft = Offset(xOffset + 2f, startY - 18f * zoomScale),
                    style = TextStyle(color = measureBarColor, fontSize = (10 * zoomScale).sp, fontWeight = FontWeight.Bold),
                )

                xOffset += 15f * zoomScale

                measure.beats.forEachIndexed { bIdx, beat ->
                    val isPlaybackActive = isPlaying && playbackMeasure == globalMIdx && playbackBeat == bIdx
                    val isSelectedCell = selectedMeasure == globalMIdx && selectedBeat == bIdx

                    // Playback Indicator Line (Amber/Green line)
                    if (isPlaybackActive) {
                        drawLine(
                            color = Color(0xFF10B981),
                            start = Offset(xOffset, startY - 8f),
                            end = Offset(xOffset, startY + (stringCount - 1) * spacing + 8f),
                            strokeWidth = 4f * zoomScale,
                        )
                    }

                    // TUXGUITAR CARET CURSOR (Red rectangle around active cell)
                    if (isSelectedCell) {
                        val selY = startY + selectedString.coerceIn(0, stringCount - 1) * spacing
                        drawRoundRect(
                            color = caretColor,
                            topLeft = Offset(xOffset - 8f * zoomScale, selY - 10f * zoomScale),
                            size = Size(16f * zoomScale, 20f * zoomScale),
                            cornerRadius = CornerRadius(3f * zoomScale, 3f * zoomScale),
                            style = Stroke(width = 2f * zoomScale)
                        )
                    }

                    // Notes on strings
                    beat.notes.forEach { note ->
                        val stringIdx = note.stringIndex.coerceIn(0, stringCount - 1)
                        val y = startY + stringIdx * spacing
                        val fretText = note.displayLabel

                        // Background pill so fret numbers are readable over string lines
                        drawCircle(
                            color = Color(0xFF14171D),
                            radius = 8f * zoomScale,
                            center = Offset(xOffset, y),
                        )

                        drawText(
                            textMeasurer = textMeasurer,
                            text = fretText,
                            topLeft = Offset(xOffset - 5f * zoomScale, y - 7f * zoomScale),
                            style = TextStyle(
                                color = if (isPlaybackActive) Color(0xFF10B981) else primaryColor,
                                fontSize = (12 * zoomScale).sp,
                                fontWeight = FontWeight.Black,
                            ),
                        )
                    }
                    xOffset += colWidth.toPx()
                }
                xOffset += 15f * zoomScale
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun TuxGuitarSystemRowPreview() {
    GuitarLabTheme {
        Box(
            modifier = Modifier
                .background(StudioDarkBg)
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            TuxGuitarSystemRow(
                measures = listOf(
                    TabMeasure(1),
                    TabMeasure(2)
                ),
                startMeasureIndex = 0,
                stringCount = 6,
                stringLabels = listOf("E", "B", "G", "D", "A", "E"),
                zoomScale = 1.0f,
                isPlaying = false,
                playbackMeasure = 0,
                playbackBeat = 0,
                selectedMeasure = 0,
                selectedBeat = 0,
                selectedString = 2,
                onCellSelected = { _, _, _ -> }
            )
        }
    }
}