package com.mmt.guitarlab.ui.drums

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.mmt.guitarlab.domain.model.DrumKit
import com.mmt.guitarlab.domain.model.DrumSound
import com.mmt.guitarlab.ui.theme.GuitarLabTheme
import com.mmt.guitarlab.ui.theme.StudioCardBorder
import com.mmt.guitarlab.ui.theme.StudioDarkBg
import com.mmt.guitarlab.ui.theme.StudioTextMuted
import com.mmt.guitarlab.ui.theme.StudioTextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrumKitBottomSheet(
    currentKit: DrumKit,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onSelectKit: (DrumKit) -> Unit,
    onPreviewSound: (DrumSound) -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = StudioDarkBg,
        contentColor = StudioTextPrimary,
        dragHandle = { BottomSheetDefaults.DragHandle(color = StudioCardBorder) },
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .navigationBarsPadding(),
        ) {
            Text(
                text = "Select Drum Kit",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = StudioTextPrimary,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Choose the acoustic character of your drum machine",
                style = MaterialTheme.typography.bodySmall,
                color = StudioTextMuted,
            )

            Spacer(Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(DrumKit.entries) { kit ->
                    DrumKitItem(
                        kit = kit,
                        isSelected = currentKit == kit,
                        onSelect = {
                            onSelectKit(kit)
                            onDismiss()
                        },
                        onPreview = { onPreviewSound(DrumSound.SNARE) }
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, backgroundColor = 0xFF12151C)
@Composable
private fun DrumKitBottomSheetPreview() {
    GuitarLabTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp)
        ) {
            DrumKitBottomSheet(
                currentKit = DrumKit.ROCK,
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                onDismiss = {},
                onSelectKit = {},
                onPreviewSound = {}
            )
        }
    }
}