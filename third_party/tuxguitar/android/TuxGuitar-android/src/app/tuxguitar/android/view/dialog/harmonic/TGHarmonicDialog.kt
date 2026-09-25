package app.tuxguitar.android.view.dialog.harmonic

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.tuxguitar.android.R
import app.tuxguitar.android.view.dialog.compose.TGComposeBottomSheetDialogFragment
import app.tuxguitar.android.view.dialog.compose.TGDialogDropdownField
import app.tuxguitar.android.view.dialog.compose.TGDropdownOption
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.editor.action.effect.TGChangeHarmonicNoteAction
import app.tuxguitar.song.managers.TGSongManager
import app.tuxguitar.song.models.TGBeat
import app.tuxguitar.song.models.TGMeasure
import app.tuxguitar.song.models.TGNote
import app.tuxguitar.song.models.TGString
import app.tuxguitar.song.models.effects.TGEffectHarmonic

data class TGHarmonicFields(
    val type: Int,
    val data: Int,
    val naturalAvailable: Boolean,
)

private data class TGHarmonicTypeOption(
    val value: Int,
    val labelRes: Int,
    val enabled: Boolean = true,
)

class TGHarmonicDialog : TGComposeBottomSheetDialogFragment() {
    @Composable
    override fun SheetContent(onDismiss: () -> Unit) {
        TGHarmonicDialogContent(
            initial = createInitialFields(),
            onSave = {
                updateEffect(createHarmonic(it))
                onDismiss()
            },
            onClean = {
                updateEffect(null)
                onDismiss()
            },
            onCancel = onDismiss,
        )
    }

    private fun createInitialFields(): TGHarmonicFields {
        val note = getNote()
        val type = if (note != null && note.effect.isHarmonic) {
            note.effect.harmonic.type
        } else if (isNaturalHarmonicAvailable()) {
            TGEffectHarmonic.TYPE_NATURAL
        } else {
            TGEffectHarmonic.TYPE_ARTIFICIAL
        }
        val data = if (note != null && note.effect.isHarmonic) note.effect.harmonic.data else 0
        return TGHarmonicFields(type = type, data = data, naturalAvailable = isNaturalHarmonicAvailable())
    }

    private fun isNaturalHarmonicAvailable(): Boolean {
        val note = getNote() ?: return false
        return TGEffectHarmonic.NATURAL_FREQUENCIES.any { note.value % 12 == it[0] % 12 }
    }

    private fun createHarmonic(fields: TGHarmonicFields): TGEffectHarmonic =
        getSongManager().factory.newEffectHarmonic().also {
            it.setType(fields.type)
            it.setData(fields.data)
        }

    private fun updateEffect(effect: TGEffectHarmonic?) {
        val processor = TGActionProcessor(findContext(), TGChangeHarmonicNoteAction.NAME)
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, getMeasure())
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, getBeat())
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, getString())
        processor.setAttribute(TGChangeHarmonicNoteAction.ATTRIBUTE_EFFECT, effect)
        processor.process()
    }

    fun getSongManager(): TGSongManager =
        requireNotNull(getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER))
    fun getMeasure(): TGMeasure? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE)
    fun getBeat(): TGBeat? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT)
    fun getNote(): TGNote? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE)
    fun getString(): TGString? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING)
}

@Composable
fun TGHarmonicDialogContent(
    initial: TGHarmonicFields,
    onSave: (TGHarmonicFields) -> Unit,
    onClean: () -> Unit,
    onCancel: () -> Unit,
) {
    val typeOptions = listOf(
        TGHarmonicTypeOption(TGEffectHarmonic.TYPE_NATURAL, R.string.harmonic_dlg_type_nh, initial.naturalAvailable),
        TGHarmonicTypeOption(TGEffectHarmonic.TYPE_ARTIFICIAL, R.string.harmonic_dlg_type_ah),
        TGHarmonicTypeOption(TGEffectHarmonic.TYPE_TAPPED, R.string.harmonic_dlg_type_th),
        TGHarmonicTypeOption(TGEffectHarmonic.TYPE_PINCH, R.string.harmonic_dlg_type_ph),
        TGHarmonicTypeOption(TGEffectHarmonic.TYPE_SEMI, R.string.harmonic_dlg_type_sh),
    )

    var type by remember { mutableIntStateOf(initial.type) }
    var data by remember { mutableIntStateOf(initial.data) }

    val dataOptions = remember(type) {
        if (type == TGEffectHarmonic.TYPE_NATURAL) {
            emptyList()
        } else {
            val prefix = when (type) {
                TGEffectHarmonic.TYPE_ARTIFICIAL -> TGEffectHarmonic.KEY_ARTIFICIAL
                TGEffectHarmonic.TYPE_TAPPED -> TGEffectHarmonic.KEY_TAPPED
                TGEffectHarmonic.TYPE_PINCH -> TGEffectHarmonic.KEY_PINCH
                else -> TGEffectHarmonic.KEY_SEMI
            }
            TGEffectHarmonic.NATURAL_FREQUENCIES.mapIndexed { index, frequency ->
                TGDropdownOption(index, "$prefix(${frequency[0]})")
            }
        }
    }
    if (dataOptions.isNotEmpty() && dataOptions.none { it.value == data }) {
        data = 0
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .heightIn(max = 480.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = stringResource(R.string.harmonic_dlg_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        typeOptions.forEach { option ->
            HarmonicRadioOption(
                label = stringResource(option.labelRes),
                selected = type == option.value,
                enabled = option.enabled,
                onClick = {
                    if (option.enabled) {
                        type = option.value
                        data = 0
                    }
                },
            )
        }

        if (dataOptions.isNotEmpty()) {
            TGDialogDropdownField(
                label = stringResource(
                    when (type) {
                        TGEffectHarmonic.TYPE_ARTIFICIAL -> R.string.harmonic_dlg_type_ah
                        TGEffectHarmonic.TYPE_TAPPED -> R.string.harmonic_dlg_type_th
                        TGEffectHarmonic.TYPE_PINCH -> R.string.harmonic_dlg_type_ph
                        else -> R.string.harmonic_dlg_type_sh
                    }
                ),
                selectedOption = dataOptions.first { it.value == data },
                options = dataOptions,
                onSelected = { data = it.value },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.End,
        ) {
            TextButton(onClick = onCancel) {
                Text(stringResource(R.string.global_button_cancel))
            }
            TextButton(onClick = onClean) {
                Text(stringResource(R.string.global_button_clean))
            }
            TextButton(
                onClick = {
                    onSave(
                        TGHarmonicFields(
                            type = type,
                            data = data,
                            naturalAvailable = initial.naturalAvailable,
                        )
                    )
                },
            ) {
                Text(stringResource(R.string.global_button_ok))
            }
        }
    }
}

@Composable
private fun HarmonicRadioOption(
    label: String,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (enabled) 1f else 0.38f)
            .selectable(
                selected = selected,
                enabled = enabled,
                onClick = onClick,
                role = Role.RadioButton,
            )
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = null, enabled = enabled)
        Text(text = label, modifier = Modifier.padding(start = 8.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun TGHarmonicDialogNaturalPreview() {
    MaterialTheme {
        TGHarmonicDialogContent(
            initial = TGHarmonicFields(
                type = TGEffectHarmonic.TYPE_NATURAL,
                data = 0,
                naturalAvailable = true,
            ),
            onSave = {},
            onClean = {},
            onCancel = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TGHarmonicDialogArtificialPreview() {
    MaterialTheme {
        TGHarmonicDialogContent(
            initial = TGHarmonicFields(
                type = TGEffectHarmonic.TYPE_ARTIFICIAL,
                data = 2,
                naturalAvailable = false,
            ),
            onSave = {},
            onClean = {},
            onCancel = {},
        )
    }
}
