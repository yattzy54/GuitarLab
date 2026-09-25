package app.tuxguitar.android.view.dialog.tempo

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.tuxguitar.android.R
import app.tuxguitar.android.view.dialog.compose.TGComposeBottomSheetDialogFragment
import app.tuxguitar.android.view.dialog.compose.TGDialogActionButtons
import app.tuxguitar.android.view.dialog.compose.TGDialogDropdownField
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.editor.action.composition.TGChangeTempoRangeAction
import app.tuxguitar.song.models.TGMeasureHeader
import app.tuxguitar.song.models.TGSong
import app.tuxguitar.song.models.TGTempoBase

private data class TGTempoBaseOption(
    val base: Int,
    val dotted: Boolean,
    val label: String,
    val iconResId: Int,
)

private data class TGTempoApplyOption(
    val value: Int,
    val label: String,
)

class TGTempoDialog : TGComposeBottomSheetDialogFragment() {
    private val tempoBase = TGTempoBase.getTempoBases().toList()

    @Composable
    override fun SheetContent(onDismiss: () -> Unit) {
        val header = requireNotNull(getHeader())
        val currentTempo = header.tempo
        TGTempoDialogContent(
            tempoBaseOptions = createTempoBaseOptions(),
            tempoValues = createTempoValues().toList(),
            selectedTempoValue = currentTempo.rawValue,
            selectedTempoBase = currentTempo.base,
            selectedTempoBaseDotted = currentTempo.isDotted,
            applyOptions = createApplyOptions(),
            selectedApplyTo = TGChangeTempoRangeAction.APPLY_TO_NEXT,
            onSave = { tempoValue, base, dotted, applyTo ->
                changeTempo(tempoValue, base, dotted, applyTo)
                onDismiss()
            },
            onCancel = onDismiss,
        )
    }

    @Composable
    private fun createTempoBaseOptions(): List<TGTempoBaseOption> {
        val context = LocalContext.current
        return tempoBase.map { base ->
            var iconName = "duration_${base.base}"
            if (base.isDotted) {
                iconName += "dotted"
            }
            TGTempoBaseOption(
                base = base.base,
                dotted = base.isDotted,
                label = "1/${base.base}" + if (base.isDotted) "•" else "",
                iconResId = context.resources.getIdentifier(iconName, "drawable", context.packageName),
            )
        }
    }

    fun createTempoValues(): Array<Int> =
        Array(TGChangeTempoRangeAction.MAX_TEMPO - TGChangeTempoRangeAction.MIN_TEMPO + 1) {
            it + TGChangeTempoRangeAction.MIN_TEMPO
        }

    private fun createApplyOptions(): List<TGTempoApplyOption> = listOf(
        TGTempoApplyOption(
            TGChangeTempoRangeAction.APPLY_TO_ALL,
            getString(R.string.tempo_dlg_options_apply_to_song)
        ),
        TGTempoApplyOption(
            TGChangeTempoRangeAction.APPLY_TO_END,
            getString(R.string.tempo_dlg_options_apply_to_end)
        ),
        TGTempoApplyOption(
            TGChangeTempoRangeAction.APPLY_TO_NEXT,
            getString(R.string.tempo_dlg_options_apply_to_next_marker)
        ),
    )

    fun changeTempo(tempoValue: Int, base: Int, dotted: Boolean, applyTo: Int) {
        val processor = TGActionProcessor(findContext(), TGChangeTempoRangeAction.NAME)
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, getSong())
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER, getHeader())
        processor.setAttribute(TGChangeTempoRangeAction.ATTRIBUTE_TEMPO, tempoValue)
        processor.setAttribute(TGChangeTempoRangeAction.ATTRIBUTE_TEMPO_BASE, base)
        processor.setAttribute(TGChangeTempoRangeAction.ATTRIBUTE_TEMPO_BASE_DOTTED, dotted)
        processor.setAttribute(TGChangeTempoRangeAction.ATTRIBUTE_APPLY_TO, applyTo)
        processor.processOnNewThread()
    }

    fun getSong(): TGSong? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG)
    fun getHeader(): TGMeasureHeader? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_HEADER)
}

@Composable
private fun TGTempoDialogContent(
    tempoBaseOptions: List<TGTempoBaseOption>,
    tempoValues: List<Int>,
    selectedTempoValue: Int,
    selectedTempoBase: Int,
    selectedTempoBaseDotted: Boolean,
    applyOptions: List<TGTempoApplyOption>,
    selectedApplyTo: Int,
    onSave: (Int, Int, Boolean, Int) -> Unit,
    onCancel: () -> Unit,
) {
    val initialTempoIndex = tempoValues.indexOf(selectedTempoValue).takeIf { it >= 0 } ?: 0
    val initialBaseIndex = tempoBaseOptions.indexOfFirst {
        it.base == selectedTempoBase && it.dotted == selectedTempoBaseDotted
    }.takeIf { it >= 0 } ?: 0
    val initialApplyIndex = applyOptions.indexOfFirst { it.value == selectedApplyTo }.takeIf { it >= 0 } ?: 0

    var tempoIndex by remember(selectedTempoValue, tempoValues) { mutableIntStateOf(initialTempoIndex) }
    var baseIndex by remember(selectedTempoBase, selectedTempoBaseDotted, tempoBaseOptions) {
        mutableIntStateOf(initialBaseIndex)
    }
    var applyIndex by remember(selectedApplyTo, applyOptions) { mutableIntStateOf(initialApplyIndex) }

    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .heightIn(max = 560.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = stringResource(R.string.tempo_dlg_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        tempoBaseOptions.forEachIndexed { index, option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(selected = baseIndex == index, onClick = { baseIndex = index })
                    .padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(selected = baseIndex == index, onClick = { baseIndex = index })
                if (option.iconResId != 0) {
                    Image(
                        painter = painterResource(option.iconResId),
                        contentDescription = null,
                        modifier = Modifier.padding(horizontal = 8.dp),
                    )
                }
                Text(option.label)
            }
        }

        TGDialogDropdownField(
            label = stringResource(R.string.tempo_dlg_tempo_label),
            selectedText = tempoValues[tempoIndex].toString(),
            options = tempoValues.map(Int::toString),
            onOptionSelected = { tempoIndex = it },
            modifier = Modifier.padding(top = 8.dp),
        )

        applyOptions.forEachIndexed { index, option ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .selectable(selected = applyIndex == index, onClick = { applyIndex = index })
                    .padding(top = if (index == 0) 16.dp else 6.dp, bottom = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(selected = applyIndex == index, onClick = { applyIndex = index })
                Text(option.label)
            }
        }

        TGDialogActionButtons(
            onConfirm = {
                val selectedBase = tempoBaseOptions[baseIndex]
                onSave(
                    tempoValues[tempoIndex],
                    selectedBase.base,
                    selectedBase.dotted,
                    applyOptions[applyIndex].value,
                )
            },
            onCancel = onCancel,
            modifier = Modifier.padding(top = 16.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TGTempoDialogContentPreview() {
    MaterialTheme {
        TGTempoDialogContent(
            tempoBaseOptions = listOf(
                TGTempoBaseOption(4, false, "1/4", R.drawable.duration_4),
                TGTempoBaseOption(8, true, "1/8•", R.drawable.duration_8dotted),
                TGTempoBaseOption(8, false, "1/8", R.drawable.duration_8),
            ),
            tempoValues = listOf(60, 80, 100, 120),
            selectedTempoValue = 120,
            selectedTempoBase = 4,
            selectedTempoBaseDotted = false,
            applyOptions = listOf(
                TGTempoApplyOption(TGChangeTempoRangeAction.APPLY_TO_ALL, "Apply this Tempo in the whole Song"),
                TGTempoApplyOption(TGChangeTempoRangeAction.APPLY_TO_END, "Apply this Tempo to the end"),
                TGTempoApplyOption(TGChangeTempoRangeAction.APPLY_TO_NEXT, "Apply this Tempo to the next Tempo Marker"),
            ),
            selectedApplyTo = TGChangeTempoRangeAction.APPLY_TO_NEXT,
            onSave = { _, _, _, _ -> },
            onCancel = {},
        )
    }
}
