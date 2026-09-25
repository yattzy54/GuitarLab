package com.mmt.guitarlab.ui.tab.components.sheets

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mmt.guitarlab.core.ui.theme.GuitarLabTheme
import com.mmt.guitarlab.core.ui.theme.StudioDarkBg

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChromaticTunerBottomSheet(
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    targetTuningName: String,
    targetTuningNotes: List<String>,
    centsDiff: Float = 0f,
    detectedNote: String = "C",
    isListening: Boolean = true,
    onPluckString: (stringIndex: Int) -> Unit = {},
    modifier: Modifier = Modifier,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        containerColor = Color(0xFF12151A),
        contentColor = Color.White,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ChromaticTunerHeader(targetTuningName = targetTuningName)

            Spacer(modifier = Modifier.height(24.dp))

            ChromaticTunerGauge(
                detectedNote = detectedNote,
                centsDiff = centsDiff
            )

            Spacer(modifier = Modifier.height(24.dp))

            ChromaticTunerNotesRow(
                targetTuningNotes = targetTuningNotes,
                onPluckString = onPluckString
            )

            Spacer(modifier = Modifier.height(20.dp))

            ChromaticTunerMicStatus(isListening = isListening)
        }
    }
}

@Composable
private fun ChromaticTunerHeader(targetTuningName: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Хроматический тюнер",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Text(
            text = "Строй: $targetTuningName",
            fontSize = 13.sp,
            color = Color(0xFFFBBF24),
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun ChromaticTunerGauge(
    detectedNote: String,
    centsDiff: Float,
) {
    val isInTune = kotlin.math.abs(centsDiff) < 5

    Box(
        modifier = Modifier
            .size(140.dp)
            .clip(CircleShape)
            .background(Color(0xFF1E222A))
            .border(
                width = 3.dp,
                color = if (isInTune) Color(0xFF10B981) else Color(0xFFF59E0B),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = detectedNote,
                fontSize = 38.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace,
                color = if (isInTune) Color(0xFF34D399) else Color.White
            )
            Text(
                text = "${if (centsDiff > 0) "+" else ""}${centsDiff.toInt()} cents",
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                color = Color(0xFF9CA3AF)
            )
        }
    }
}

@Composable
private fun ChromaticTunerNotesRow(
    targetTuningNotes: List<String>,
    onPluckString: (Int) -> Unit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Опорные ноты строя (нажмите для прослушивания):",
            fontSize = 12.sp,
            color = Color(0xFF9CA3AF)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            targetTuningNotes.forEachIndexed { sIdx, noteStr ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF1E222A))
                        .border(1.dp, Color(0xFF2E333D), RoundedCornerShape(12.dp))
                        .clickable { onPluckString(sIdx) }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = (sIdx + 1).toString(),
                            fontSize = 10.sp,
                            color = Color(0xFF6B7280)
                        )
                        Text(
                            text = noteStr,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFFFBBF24)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChromaticTunerMicStatus(isListening: Boolean) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Mic,
            contentDescription = "Mic",
            tint = if (isListening) Color(0xFF10B981) else Color(0xFF6B7280),
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = if (isListening) "Микрофон активен (автоматический захват)" else "Микрофон отключен",
            fontSize = 12.sp,
            color = if (isListening) Color(0xFF10B981) else Color(0xFF6B7280)
        )
    }
}

// ==================== PREVIEWS ====================

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun ChromaticTunerGaugePreview() {
    GuitarLabTheme {
        Column(
            modifier = Modifier
                .background(StudioDarkBg)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            ChromaticTunerGauge(detectedNote = "E", centsDiff = 1f)
            ChromaticTunerGauge(detectedNote = "A", centsDiff = 25f)
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun ChromaticTunerNotesRowPreview() {
    GuitarLabTheme {
        Box(
            modifier = Modifier
                .background(StudioDarkBg)
                .padding(16.dp)
        ) {
            ChromaticTunerNotesRow(
                targetTuningNotes = listOf("E", "A", "D", "G", "B", "E"),
                onPluckString = {}
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun ChromaticTunerBottomSheetPreview() {
    GuitarLabTheme {
        Box(
            modifier = Modifier
                .background(StudioDarkBg)
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Рендерим содержимое шторки напрямую для превью без вызова ModalBottomSheet
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ChromaticTunerHeader(targetTuningName = "Standard E")

                Spacer(modifier = Modifier.height(24.dp))

                ChromaticTunerGauge(
                    detectedNote = "E",
                    centsDiff = -2f
                )

                Spacer(modifier = Modifier.height(24.dp))

                ChromaticTunerNotesRow(
                    targetTuningNotes = listOf("E", "A", "D", "G", "B", "E"),
                    onPluckString = {}
                )

                Spacer(modifier = Modifier.height(20.dp))

                ChromaticTunerMicStatus(isListening = true)
            }
        }
    }
}