package com.mmt.guitarlab.ui.practice

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mmt.guitarlab.domain.model.FretboardMode
import com.mmt.guitarlab.domain.model.MusicTheory
import com.mmt.guitarlab.core.ui.components.Studio3DAccent
import com.mmt.guitarlab.core.ui.components.Studio3DIconBadge
import com.mmt.guitarlab.core.ui.theme.ElectricAmber
import com.mmt.guitarlab.core.ui.theme.ElectricTeal
import com.mmt.guitarlab.core.ui.theme.GuitarLabTheme

@Composable
public fun RealisticFretboardCanvas(
    stringCount: Int,
    tuningNotes: List<com.mmt.guitarlab.domain.model.TuningNote>,
    mode: FretboardMode,
    rootPitchIndex: Int,
    targetIntervals: List<Int>,
    pressedFrets: Set<FretPosition>,
    onFretTapped: (stringIndex: Int, fret: Int, midiNote: Int) -> Unit,
) {
    val totalFrets = 15
    val fretWidth = 54.dp
    val stringSpacing = 28.dp
    val textMeasurer = rememberTextMeasurer()
    val totalWidth = fretWidth * (totalFrets + 1) + 60.dp
    val totalHeight = stringSpacing * (stringCount + 1)

    // Layout constants in DP
    val startXdp = 44.dp
    val startYdp = 18.dp

    Canvas(
        modifier = Modifier
            .size(width = totalWidth, height = totalHeight)
            .pointerInput(mode, stringCount, tuningNotes) {
                detectTapGestures { offset ->
                    val fWidthPx = fretWidth.toPx()
                    val sHeightPx = stringSpacing.toPx()
                    val startXPx = startXdp.toPx()
                    val startYPx = startYdp.toPx()

                    val relX = offset.x - startXPx
                    val relY = offset.y - startYPx

                    // Vertical hit-testing for string index
                    if (relY >= -sHeightPx / 2f && relY <= (stringCount - 0.5f) * sHeightPx) {
                        val stringIdx = ((relY + sHeightPx / 2f) / sHeightPx).toInt().coerceIn(0, stringCount - 1)

                        // Fret hit-testing:
                        // relX < 0 is open string (fret 0)
                        // relX in [0, fWidthPx) is fret 1 (between nut and fret wire 1)
                        // relX in [fWidthPx * (f-1), fWidthPx * f) is fret f
                        val fret = if (relX < 0) {
                            0
                        } else {
                            val calculatedFret = (relX / fWidthPx).toInt() + 1
                            calculatedFret.coerceIn(0, totalFrets)
                        }

                        val baseMidi = tuningNotes.getOrNull(stringIdx)?.midiNote ?: (64 - stringIdx * 5)
                        onFretTapped(stringIdx, fret, baseMidi + fret)
                    }
                }
            },
    ) {
        val fWidth = fretWidth.toPx()
        val sHeight = stringSpacing.toPx()
        val startX = startXdp.toPx()
        val startY = startYdp.toPx()
        val neckHeight = (stringCount - 1) * sHeight

        // Dark Studio Ebony Wood Neck Background
        drawRoundRect(
            brush = Brush.verticalGradient(
                listOf(Color(0xFF1E232F), Color(0xFF131720)),
            ),
            topLeft = Offset(startX, startY - 8.dp.toPx()),
            size = Size(totalFrets * fWidth, neckHeight + 16.dp.toPx()),
            cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx()),
        )

        // Mother of Pearl Fret Markers: Single dots at 3, 5, 7, 9, 15; Double dots at 12
        val singleDotFrets = listOf(3, 5, 7, 9, 15)
        singleDotFrets.forEach { f ->
            val cx = startX + (f - 0.5f) * fWidth
            val cy = startY + neckHeight / 2f
            drawCircle(
                color = Color(0xFF8899B0).copy(alpha = 0.45f),
                radius = 5.dp.toPx(),
                center = Offset(cx, cy),
            )
        }

        // Double dot at fret 12
        val cx12 = startX + (12 - 0.5f) * fWidth
        drawCircle(
            color = Color(0xFF8899B0).copy(alpha = 0.45f),
            radius = 4.5.dp.toPx(),
            center = Offset(cx12, startY + neckHeight * 0.28f),
        )
        drawCircle(
            color = Color(0xFF8899B0).copy(alpha = 0.45f),
            radius = 4.5.dp.toPx(),
            center = Offset(cx12, startY + neckHeight * 0.72f),
        )

        // Draw Nut (Fret 0 wire)
        drawLine(
            color = Color(0xFFE2E8F0),
            start = Offset(startX, startY - 8.dp.toPx()),
            end = Offset(startX, startY + neckHeight + 8.dp.toPx()),
            strokeWidth = 6.dp.toPx(),
        )

        // Draw Fret 0 indicator label
        drawText(
            textMeasurer = textMeasurer,
            text = "0",
            topLeft = Offset(startX - 22.dp.toPx(), startY + neckHeight + 12.dp.toPx()),
            style = TextStyle(
                color = Color(0xFFFBBF24),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
            ),
        )

        // Draw Frets 1..15
        for (fret in 1..totalFrets) {
            val x = startX + fret * fWidth
            drawLine(
                color = Color(0xFF3E4B66),
                start = Offset(x, startY - 8.dp.toPx()),
                end = Offset(x, startY + neckHeight + 8.dp.toPx()),
                strokeWidth = 2.5.dp.toPx(),
            )

            // Fret Numbers below neck centered between fret-1 and fret
            drawText(
                textMeasurer = textMeasurer,
                text = "$fret",
                topLeft = Offset(x - fWidth / 2f - 4.dp.toPx(), startY + neckHeight + 12.dp.toPx()),
                style = TextStyle(
                    color = if (fret in listOf(3, 5, 7, 9, 12, 15)) Color(0xFFE2E8F0) else Color(0xFF64748B),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                ),
            )
        }

        // Draw Strings & Note Inlays
        for (stringIdx in 0 until stringCount) {
            val y = startY + stringIdx * sHeight
            val noteObj = tuningNotes.getOrNull(stringIdx)
            val baseMidi = noteObj?.midiNote ?: (64 - stringIdx * 5)
            val stringName = noteObj?.noteName ?: "E"

            // Steel/Nickel guitar string with thickness variation (thickest at bottom)
            val stringThickness = (stringIdx) * 0.5f + 1.2f
            drawLine(
                color = Color(0xFFB0BCCC),
                start = Offset(startX - 30.dp.toPx(), y),
                end = Offset(startX + totalFrets * fWidth, y),
                strokeWidth = stringThickness.dp.toPx(),
            )

            // String Name Label to the far left
            drawText(
                textMeasurer = textMeasurer,
                text = stringName,
                topLeft = Offset(startX - 38.dp.toPx(), y - 7.dp.toPx()),
                style = TextStyle(
                    color = Color(0xFFFDE68A),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                ),
            )

            // Notes on frets (0 to totalFrets)
            for (fret in 0..totalFrets) {
                val noteMidi = baseMidi + fret
                val pitchClass = (noteMidi % 12 + 12) % 12
                val noteName = MusicTheory.noteNames[pitchClass]
                val intervalFromRoot = (pitchClass - rootPitchIndex + 12) % 12
                val isTargetNote = targetIntervals.contains(intervalFromRoot)
                val isRoot = intervalFromRoot == 0 && isTargetNote
                val isPressed = mode == FretboardMode.REVERSE_LOOKUP &&
                        pressedFrets.any { it.stringIndex == stringIdx && it.fret == fret }

                // Correct note center:
                // Fret 0 is open note to the left of the nut (startX - 16.dp)
                // Fret f (1..15) is centered between fret wire (f-1) and f: startX + (f - 0.5f) * fWidth
                val nx = if (fret == 0) (startX - 16.dp.toPx()) else (startX + (fret - 0.5f) * fWidth)

                if ((mode == FretboardMode.CHORD_SCALE_FINDER && isTargetNote) || isPressed) {
                    val dotColor = when {
                        isPressed -> ElectricTeal
                        isRoot -> ElectricAmber
                        else -> Color(0xFF00B4D8)
                    }

                    // Outer halo for root notes or pressed notes
                    if (isRoot || isPressed) {
                        drawCircle(
                            color = (if (isPressed) ElectricTeal else ElectricAmber).copy(alpha = 0.35f),
                            radius = 16.dp.toPx(),
                            center = Offset(nx, y),
                        )
                    }

                    // Note Circle
                    drawCircle(
                        color = dotColor,
                        radius = 12.dp.toPx(),
                        center = Offset(nx, y),
                    )

                    // Text Note Name inside circle
                    drawText(
                        textMeasurer = textMeasurer,
                        text = if (isPressed) "$noteName" else noteName,
                        topLeft = Offset(nx - 5.dp.toPx(), y - 7.dp.toPx()),
                        style = TextStyle(
                            color = Color(0xFF0C1017),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                        ),
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RealisticFretboardCanvasPreview() {
    GuitarLabTheme {
        RealisticFretboardCanvas(
            mode = FretboardMode.CHORD_SCALE_FINDER,
            rootPitchIndex = 0,
            targetIntervals = listOf(),
            stringCount = 6,
            tuningNotes = listOf(),
            pressedFrets = emptySet(),
            onFretTapped = {_, _, _ ->},
        )
    }
}
