package com.mmt.guitarlab.ui.tab

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mmt.guitarlab.domain.model.TuxGuitarSoundBank
import com.mmt.guitarlab.core.ui.theme.ElectricAmber
import com.mmt.guitarlab.core.ui.theme.GuitarLabTheme
import com.mmt.guitarlab.core.ui.theme.StudioCardBg
import com.mmt.guitarlab.core.ui.theme.StudioDarkBg
import com.mmt.guitarlab.core.ui.theme.StudioTextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
 fun SoundBankDialog(
    currentBank: TuxGuitarSoundBank,
    onSelectBank: (TuxGuitarSoundBank) -> Unit,
    onDismiss: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = StudioCardBg,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.GraphicEq, contentDescription = null, tint = Color(0xFFF59E0B))
                    Spacer(Modifier.width(8.dp))
                    Text("TuxGuitar SoundBank", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = StudioTextPrimary)
                }
                TextButton(onClick = onDismiss) {
                    Text("Close", color = ElectricAmber, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(Modifier.height(16.dp))

            Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                Text(
                    text = "Select synthesis soundbank & timbre profile for playback and live fretboard input:",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.LightGray,
                )
                Spacer(Modifier.height(12.dp))
                TuxGuitarSoundBank.entries.forEach { bank ->
                    val isSelected = bank == currentBank
                    Card(
                        onClick = { onSelectBank(bank) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) Color(0xFF2E3440) else Color(0xFF1E232B)
                        ),
                        border = if (isSelected) BorderStroke(1.5.dp, Color(0xFFF59E0B)) else null,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = bank.displayName,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) Color(0xFFFBBF24) else Color.White,
                                    fontSize = 14.sp
                                )
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(18.dp))
                                }
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = bank.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF9CA3AF),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
private fun SoundBankDialogPreview() {
    GuitarLabTheme {
        Box(
            modifier = Modifier
                .background(StudioDarkBg)
                .fillMaxWidth()
                .height(500.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center,
        ) {
            SoundBankDialog(
                currentBank = TuxGuitarSoundBank.entries.first(),
                onSelectBank = {},
                onDismiss = {}
            )
        }
    }
}
