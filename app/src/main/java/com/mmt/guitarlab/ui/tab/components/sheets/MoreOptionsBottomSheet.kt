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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Transform
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoreOptionsBottomSheet(
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    onOpenTuner: () -> Unit,
    onOpenTransposition: () -> Unit,
    onOpenSongCatalog: () -> Unit,
    countInEnabled: Boolean,
    onToggleCountIn: () -> Unit,
    metronomeClickEnabled: Boolean,
    onToggleMetronomeClick: () -> Unit,
    onCopyTab: () -> Unit,
    onOpenEditor: () -> Unit = {},
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
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Дополнительные инструменты",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            // 0. TabLab Studio action button
            OptionItemRow(
                icon = Icons.Default.MusicNote,
                title = "TabLab (TuxGuitar Studio)",
                subtitle = "Редактор табулатур Guitar Pro (GP3/GP4/GP5), дорожки и синтез",
                onClick = onOpenEditor
            )

            // 1. Tuner action button
            OptionItemRow(
                icon = Icons.Default.GraphicEq,
                title = "Хроматический тюнер",
                subtitle = "Точная настройка инструмента под строй трека",
                onClick = onOpenTuner
            )

            // 2. Transposition action button
            OptionItemRow(
                icon = Icons.Default.Transform,
                title = "Смещение тона",
                subtitle = "Транспонирование нот и изменение строя табулатуры",
                onClick = onOpenTransposition
            )

            // 3. Song catalog
            OptionItemRow(
                icon = Icons.Default.LibraryMusic,
                title = "Каталог композиций",
                subtitle = "Выбор песен и ревизий табулатур",
                onClick = onOpenSongCatalog
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 4. Count-in toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF181B22))
                    .border(1.dp, Color(0xFF2E333D), RoundedCornerShape(14.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.Timer, contentDescription = null, tint = Color(0xFFFBBF24))
                    Column {
                        Text("Отсчёт перед стартом", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                        Text("4 клика перед началом воспроизведения", fontSize = 11.sp, color = Color(0xFF9CA3AF))
                    }
                }
                Switch(
                    checked = countInEnabled,
                    onCheckedChange = { onToggleCountIn() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF09090B),
                        checkedTrackColor = Color(0xFF10B981),
                    )
                )
            }

            // 5. Metronome Click toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF181B22))
                    .border(1.dp, Color(0xFF2E333D), RoundedCornerShape(14.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color(0xFFFBBF24))
                    Column {
                        Text("Метроном во время игры", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.White)
                        Text("Синхронный щелчок на доли такта", fontSize = 11.sp, color = Color(0xFF9CA3AF))
                    }
                }
                Switch(
                    checked = metronomeClickEnabled,
                    onCheckedChange = { onToggleMetronomeClick() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF09090B),
                        checkedTrackColor = Color(0xFF10B981),
                    )
                )
            }

            // 6. Copy Tab
            OptionItemRow(
                icon = Icons.Default.ContentCopy,
                title = "Скопировать табулатуру",
                subtitle = "Экспорт в буфер обмена в формате ASCII",
                onClick = onCopyTab
            )
        }
    }
}
