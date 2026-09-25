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
import com.mmt.guitarlab.domain.model.DrumPattern
import com.mmt.guitarlab.ui.drums.GroovePresetItem
import com.mmt.guitarlab.ui.theme.GuitarLabTheme
import com.mmt.guitarlab.ui.theme.StudioCardBorder
import com.mmt.guitarlab.ui.theme.StudioDarkBg
import com.mmt.guitarlab.ui.theme.StudioTextMuted
import com.mmt.guitarlab.ui.theme.StudioTextPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroovePresetBottomSheet(
    currentPattern: DrumPattern,
    patterns: List<DrumPattern>,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onSelectPattern: (DrumPattern) -> Unit,
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
                text = "Groove Presets",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = StudioTextPrimary,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "Select a pre-made rhythm pattern",
                style = MaterialTheme.typography.bodySmall,
                color = StudioTextMuted,
            )

            Spacer(Modifier.height(16.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                items(patterns) { pat ->
                    GroovePresetItem(
                        pattern = pat,
                        isSelected = currentPattern.name == pat.name,
                        onSelect = {
                            onSelectPattern(pat)
                            onDismiss()
                        }
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
private fun GroovePresetBottomSheetPreview() {
    GuitarLabTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 100.dp)
        ) {
            GroovePresetBottomSheet(
                currentPattern = DrumPattern.DEFAULT_PATTERNS.first(),
                patterns = DrumPattern.DEFAULT_PATTERNS,
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                onDismiss = {},
                onSelectPattern = {}
            )
        }
    }
}