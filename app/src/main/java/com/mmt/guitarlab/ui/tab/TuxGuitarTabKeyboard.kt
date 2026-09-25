package com.mmt.guitarlab.ui.tab

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mmt.guitarlab.domain.model.NoteDuration
import com.mmt.guitarlab.ui.theme.GuitarLabTheme
import com.mmt.guitarlab.ui.theme.StudioDarkBg

/**
 * The Iconic TGTabKeyboard (view_tab_keyboard.xml from TuxGuitar Android):
 * - Left: 3x4 keypad (7, 8, 9, Ins / 4, 5, 6, Del / 1, 2, 3, 0)
 * - Center: Duration controls (▲ Inc, note icon/value, ▼ Dec)
 * - Right: D-Pad navigation arrows (Up, Down, Left, Right, Select)
 */
@Composable
 fun TuxGuitarTabKeyboard(
    activeDuration: NoteDuration,
    onNumberClick: (Int) -> Unit,
    onInsertClick: () -> Unit,
    onDeleteClick: () -> Unit,
    onIncrementDuration: () -> Unit,
    onDecrementDuration: () -> Unit,
    onUpClick: () -> Unit,
    onDownClick: () -> Unit,
    onLeftClick: () -> Unit,
    onRightClick: () -> Unit,
    onSelectClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color(0xFF2E3440), RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp)),
        shape = RoundedCornerShape(topStart = 14.dp, topEnd = 14.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E232B))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // LEFT: 4x3 Keypad
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                // Row 1: 7, 8, 9, Ins
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    KeypadButton("7") { onNumberClick(7) }
                    KeypadButton("8") { onNumberClick(8) }
                    KeypadButton("9") { onNumberClick(9) }
                    KeypadButton("Ins", isSpecial = true) { onInsertClick() }
                }
                // Row 2: 4, 5, 6, Del
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    KeypadButton("4") { onNumberClick(4) }
                    KeypadButton("5") { onNumberClick(5) }
                    KeypadButton("6") { onNumberClick(6) }
                    KeypadButton("Del", isSpecial = true) { onDeleteClick() }
                }
                // Row 3: 1, 2, 3, 0
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    KeypadButton("1") { onNumberClick(1) }
                    KeypadButton("2") { onNumberClick(2) }
                    KeypadButton("3") { onNumberClick(3) }
                    KeypadButton("0") { onNumberClick(0) }
                }
            }

            // CENTER: Duration Controls
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(3.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF14171D))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                IconButton(
                    onClick = onIncrementDuration,
                    modifier = Modifier.size(28.dp)
                ) {
                    Text("▲", color = Color(0xFFFBBF24), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = when (activeDuration) {
                            NoteDuration.WHOLE -> "𝅝 1"
                            NoteDuration.HALF -> "𝅗𝅥 1/2"
                            NoteDuration.QUARTER -> "♩ 1/4"
                            NoteDuration.EIGHTH -> "♪ 1/8"
                            NoteDuration.SIXTEENTH -> "𝅘𝅥𝅯 1/16"
                            NoteDuration.THIRTY_SECOND -> "𝅘𝅥𝅰 1/32"
                        },
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = onDecrementDuration,
                    modifier = Modifier.size(28.dp)
                ) {
                    Text("▼", color = Color(0xFFFBBF24), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            // RIGHT: D-Pad Navigation
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                // Up
                DpadButton("▲") { onUpClick() }

                // Left, Center/Select, Right
                Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                    DpadButton("◀") { onLeftClick() }
                    DpadButton("•", isCenter = true) { onSelectClick() }
                    DpadButton("▶") { onRightClick() }
                }

                // Down
                DpadButton("▼") { onDownClick() }
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun TuxGuitarTabKeyboardPreview() {
    GuitarLabTheme {
        Box(
            modifier = Modifier
                .background(StudioDarkBg)
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            TuxGuitarTabKeyboard(
                activeDuration = NoteDuration.QUARTER,
                onNumberClick = {},
                onInsertClick = {},
                onDeleteClick = {},
                onIncrementDuration = {},
                onDecrementDuration = {},
                onUpClick = {},
                onDownClick = {},
                onLeftClick = {},
                onRightClick = {},
                onSelectClick = {},
            )
        }
    }
}