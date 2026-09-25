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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
fun TranspositionBottomSheet(
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    semitones: Int,
    onSemitonesChange: (Int) -> Unit,
    currentTuningName: String,
    onApplyPreset: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val chromaticNotes = listOf("C", "C♯", "D", "D♯", "E", "F", "F♯", "G", "G♯", "A", "A♯", "B")
    val presets = listOf("Standard", "Drop D", "Drop C", "Half Step Down", "DADGAD", "Open G")

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
            Text(
                text = "Смещение тона (Транспозиция)",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = "Текущий строй: $currentTuningName",
                fontSize = 13.sp,
                color = Color(0xFFFBBF24),
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Semitone Stepper
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Color(0xFF1E222A))
                    .border(1.dp, Color(0xFF2E333D), RoundedCornerShape(20.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                IconButton(
                    onClick = { onSemitonesChange(semitones - 1) },
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF2B303B), CircleShape)
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Down", tint = Color.White)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (semitones > 0) "+$semitones" else "$semitones",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Black,
                        fontFamily = FontFamily.Monospace,
                        color = if (semitones == 0) Color.White else Color(0xFF10B981)
                    )
                    Text(
                        text = "полутонов",
                        fontSize = 11.sp,
                        color = Color(0xFF9CA3AF)
                    )
                }

                IconButton(
                    onClick = { onSemitonesChange(semitones + 1) },
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFF2B303B), CircleShape)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Up", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Chromatic Note Matrix
            Text(
                text = "Корневая тональность",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF9CA3AF),
                modifier = Modifier.align(Alignment.Start)
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(6),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp)
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(chromaticNotes) { note ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF1E222A))
                            .border(1.dp, Color(0xFF2E333D), RoundedCornerShape(8.dp))
                            .clickable { /* Select pitch */ }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = note,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFFF3F4F6)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Presets
            Text(
                text = "Популярные строи (Пресеты)",
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF9CA3AF),
                modifier = Modifier.align(Alignment.Start)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                presets.take(3).forEach { preset ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF1A1E26))
                            .border(1.dp, Color(0xFF374151), RoundedCornerShape(10.dp))
                            .clickable { onApplyPreset(preset) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = preset,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFE5E7EB)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun TranspositionBottomSheetClosedPreview() {
    GuitarLabTheme {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

        Box(
            modifier = Modifier
                .background(StudioDarkBg)
                .fillMaxWidth()
                .height(450.dp)
        ) {
            TranspositionBottomSheet(
                sheetState = sheetState,
                onDismissRequest = {},
                semitones = 2,
                onSemitonesChange = {},
                currentTuningName = "Standard E",
                onApplyPreset = {},
            )
        }
    }
}
