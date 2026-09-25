package com.mmt.guitarlab.ui.tab

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mmt.guitarlab.domain.model.InstrumentType
import com.mmt.guitarlab.domain.model.TabMeasure
import com.mmt.guitarlab.domain.model.TabTrack
import com.mmt.guitarlab.core.ui.theme.GuitarLabTheme
import com.mmt.guitarlab.core.ui.theme.StudioDarkBg

/**
 * 24-Fret TuxGuitar Guitar Fretboard View
 */
@Composable
 fun TuxGuitarFretboardView(
    activeTrack: com.mmt.guitarlab.domain.model.TabTrack?,
    selectedMeasure: Int,
    selectedBeat: Int,
    selectedString: Int,
    onFretClicked: (Int, Int) -> Unit
) {
    val stringLabels = activeTrack?.stringLabels ?: listOf("E", "B", "G", "D", "A", "E")
    val stringCount = stringLabels.size
    val activeNotes = activeTrack?.measures?.getOrNull(selectedMeasure)?.beats?.getOrNull(selectedBeat)?.notes ?: emptyList()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .padding(horizontal = 6.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E1712)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp)
        ) {
            Canvas(
                modifier = Modifier
                    .size(width = 850.dp, height = 120.dp)
                    .pointerInput(stringCount) {
                        detectTapGestures { offset ->
                            val fretCount = 24
                            val fretWidth = size.width / (fretCount + 1)
                            val stringSpacing = size.height / (stringCount + 1)
                            val fret = (offset.x / fretWidth).toInt().coerceIn(0, 24)
                            val sIdx = ((offset.y / stringSpacing) - 0.5f).toInt().coerceIn(0, stringCount - 1)
                            onFretClicked(fret, sIdx)
                        }
                    }
            ) {
                val fretCount = 24
                val fretWidth = size.width / (fretCount + 1)
                val stringSpacing = size.height / (stringCount + 1)

                // Fret markers (dots at 3, 5, 7, 9, 12, 15, 17, 19, 21, 24)
                val singleDots = listOf(3, 5, 7, 9, 15, 17, 19, 21)
                singleDots.forEach { fret ->
                    val cx = fret * fretWidth - fretWidth / 2
                    drawCircle(
                        color = Color(0xFF888888).copy(alpha = 0.5f),
                        radius = 4.dp.toPx(),
                        center = Offset(cx, size.height / 2)
                    )
                }
                // Double dots at 12 and 24
                listOf(12, 24).forEach { fret ->
                    val cx = fret * fretWidth - fretWidth / 2
                    drawCircle(
                        color = Color(0xFF888888).copy(alpha = 0.6f),
                        radius = 3.5.dp.toPx(),
                        center = Offset(cx, size.height * 0.3f)
                    )
                    drawCircle(
                        color = Color(0xFF888888).copy(alpha = 0.6f),
                        radius = 3.5.dp.toPx(),
                        center = Offset(cx, size.height * 0.7f)
                    )
                }

                // Vertical Fret wires
                for (f in 0..fretCount) {
                    val x = f * fretWidth
                    drawLine(
                        color = if (f == 0) Color(0xFFE5E9F0) else Color(0xFF888888),
                        start = Offset(x, 0f),
                        end = Offset(x, size.height),
                        strokeWidth = if (f == 0) 4.dp.toPx() else 1.5.dp.toPx()
                    )
                }

                // Horizontal Guitar strings
                for (s in 0 until stringCount) {
                    val y = (s + 1) * stringSpacing
                    drawLine(
                        color = if (s == selectedString) Color(0xFFF59E0B) else Color(0xFFD8DEE9),
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = (1f + s * 0.35f).dp.toPx()
                    )
                }

                // Draw note markers for active notes on fretboard
                activeNotes.forEach { note ->
                    if (note.stringIndex in 0 until stringCount) {
                        val cy = (note.stringIndex + 1) * stringSpacing
                        val cx = if (note.fret == 0) fretWidth * 0.25f else note.fret * fretWidth - fretWidth / 2
                        drawCircle(
                            color = Color(0xFFEF4444),
                            radius = 7.dp.toPx(),
                            center = Offset(cx, cy)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun TuxGuitarFretboardViewPreview() {
    GuitarLabTheme {
        Box(
            modifier = Modifier
                .background(StudioDarkBg)
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            TuxGuitarFretboardView(
                activeTrack = TabTrack(
                    name = "Lead Guitar",
                    instrumentType = InstrumentType.GUITAR,
                    tuningName = "Standard E",
                    stringLabels = listOf("E", "B", "G", "D", "A", "E"),
                    measures = listOf(TabMeasure(1))
                ),
                selectedMeasure = 0,
                selectedBeat = 0,
                selectedString = 2,
                onFretClicked = { _, _ -> }
            )
        }
    }
}