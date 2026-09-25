package app.tuxguitar.android.view.dialog.transport

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
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
import app.tuxguitar.android.action.impl.caret.TGMoveToAction
import app.tuxguitar.android.view.dialog.compose.TGComposeDialog
import app.tuxguitar.android.view.dialog.compose.TGDialogDropdownField
import app.tuxguitar.android.view.dialog.compose.TGDialogSectionTitle
import app.tuxguitar.android.view.dialog.compose.TGDropdownOption
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.player.base.MidiPlayer
import app.tuxguitar.player.base.MidiPlayerMode
import app.tuxguitar.song.models.TGMeasureHeader

data class TGTransportModeFields(
    val customMode: Boolean,
    val simplePercent: Int,
    val simpleLoop: Boolean,
    val customFrom: Int,
    val customTo: Int,
    val customIncrement: Int,
    val loopFromMeasure: Int?,
    val loopToMeasure: Int?,
    val measures: List<TGDropdownOption<Int?>>,
)

private enum class TGTransportTempoField {
    FROM,
    TO,
    INCREMENT,
}

private data class TGTransportValidation(
    val valid: Boolean,
    val fromError: Boolean = false,
    val toError: Boolean = false,
    val incrementError: Boolean = false,
)

class TGTransportModeDialog : TGComposeDialog() {
    @Composable
    override fun SheetContent(onDismiss: () -> Unit) {
        TGTransportModeDialogContent(
            initial = createInitialFields(),
            onSave = { fields ->
                updateMode(fields)
                onDismiss()
            },
            onCancel = onDismiss,
        )
    }

    private fun createInitialFields(): TGTransportModeFields {
        val mode = MidiPlayer.getInstance(findContext()).mode
        return TGTransportModeFields(
            customMode = mode.type == MidiPlayerMode.TYPE_CUSTOM,
            simplePercent = mode.simplePercent,
            simpleLoop = mode.isLoop(),
            customFrom = mode.customPercentFrom,
            customTo = mode.customPercentTo,
            customIncrement = mode.customPercentIncrement,
            loopFromMeasure = mode.loopSHeader.takeIf { it > 0 },
            loopToMeasure = mode.loopEHeader.takeIf { it > 0 },
            measures = createLoopMeasureOptions(),
        )
    }

    private fun createLoopMeasureOptions(): List<TGDropdownOption<Int?>> = buildList {
        for (measure in 1..MidiPlayer.getInstance(findContext()).song.countMeasureHeaders()) {
            add(TGDropdownOption(measure, getItemText(measure)))
        }
    }

    private fun getItemText(measure: Int): String {
        val header: TGMeasureHeader = MidiPlayer.getInstance(findContext()).song.getMeasureHeader(measure - 1)
        return "#$measure" + if (header.hasMarker()) " (${header.getMarker().getTitle()})" else ""
    }

    private fun updateMode(fields: TGTransportModeFields) {
        val type = if (fields.customMode) MidiPlayerMode.TYPE_CUSTOM else MidiPlayerMode.TYPE_SIMPLE
        val loop = type == MidiPlayerMode.TYPE_CUSTOM || (type == MidiPlayerMode.TYPE_SIMPLE && fields.simpleLoop)
        val loopStart = fields.loopFromMeasure ?: -1
        val loopEnd = fields.loopToMeasure ?: -1

        if (loop) {
            val track = MidiPlayer.getInstance(findContext()).song.getTrack(0)
            val beat = track.getMeasure(if (loopStart > 0) loopStart - 1 else 0).getBeat(0)
            TGActionProcessor(findContext(), TGMoveToAction.NAME).apply {
                setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, beat)
                process()
            }
        }

        MidiPlayer.getInstance(findContext()).mode.apply {
            setType(type)
            setLoop(loop)
            setSimplePercent(fields.simplePercent)
            setCustomPercentFrom(fields.customFrom)
            setCustomPercentTo(fields.customTo)
            setCustomPercentIncrement(fields.customIncrement)
            setLoopSHeader(loopStart)
            setLoopEHeader(loopEnd)
        }
    }
}

@Composable
fun TGTransportModeDialogContent(
    initial: TGTransportModeFields,
    onSave: (TGTransportModeFields) -> Unit,
    onCancel: () -> Unit,
) {
    val percentOptions = remember {
        (1..500).map { TGDropdownOption(it, it.toString()) }
    }
    val loopFromDefault = stringResource(R.string.transport_mode_dlg_loop_range_from_default)
    val loopToDefault = stringResource(R.string.transport_mode_dlg_loop_range_to_default)
    val loopFromOptions = remember(initial.measures, loopFromDefault) {
        buildList {
            add(TGDropdownOption<Int?>(null, loopFromDefault))
            addAll(initial.measures)
        }
    }

    var customMode by remember { mutableStateOf(initial.customMode) }
    var simplePercent by remember { mutableIntStateOf(initial.simplePercent) }
    var simpleLoop by remember { mutableStateOf(initial.simpleLoop) }
    var customFrom by remember { mutableIntStateOf(initial.customFrom) }
    var customTo by remember { mutableIntStateOf(initial.customTo) }
    var customIncrement by remember { mutableIntStateOf(initial.customIncrement) }
    var loopFromMeasure by remember { mutableStateOf(initial.loopFromMeasure) }
    var loopToMeasure by remember { mutableStateOf(initial.loopToMeasure) }
    var lastChangedField by remember { mutableStateOf(TGTransportTempoField.FROM) }

    val loopEnabled = customMode || simpleLoop
    val loopToOptions = remember(initial.measures, loopFromMeasure, loopToDefault) {
        buildList {
            add(TGDropdownOption<Int?>(null, loopToDefault))
            addAll(initial.measures.filter { (it.value ?: 0) >= (loopFromMeasure ?: 1) })
        }
    }

    LaunchedEffect(loopFromMeasure, loopToOptions) {
        val validValues = loopToOptions.map { it.value }.toSet()
        if (loopToMeasure !in validValues) {
            loopToMeasure = if (loopToMeasure == null) null else loopFromMeasure
        }
    }

    val validation = remember(customFrom, customTo, customIncrement, lastChangedField) {
        validateTransport(customFrom, customTo, customIncrement, lastChangedField)
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .heightIn(max = 560.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = stringResource(R.string.transport_mode_dlg_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        TransportRadioOption(
            label = stringResource(R.string.transport_mode_dlg_simple),
            selected = !customMode,
            onClick = { customMode = false },
        )
        TransportRadioOption(
            label = stringResource(R.string.transport_mode_dlg_trainer),
            selected = customMode,
            onClick = { customMode = true },
        )

        TGDialogSectionTitle(
            text = stringResource(R.string.transport_mode_dlg_simple),
            enabled = !customMode,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
        )
        TGDialogDropdownField(
            label = stringResource(R.string.transport_mode_dlg_simple_tempo_percent_label),
            selectedOption = percentOptions.first { it.value == simplePercent },
            options = percentOptions,
            onSelected = { simplePercent = it.value },
            enabled = !customMode,
            modifier = Modifier.fillMaxWidth(),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(if (!customMode) 1f else 0.38f)
                .padding(top = 8.dp)
                .selectable(
                    selected = simpleLoop,
                    enabled = !customMode,
                    onClick = { if (!customMode) simpleLoop = !simpleLoop },
                    role = Role.Checkbox,
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(
                checked = simpleLoop,
                enabled = !customMode,
                onCheckedChange = { simpleLoop = it },
            )
            Text(text = stringResource(R.string.transport_mode_dlg_simple_loop))
        }

        TGDialogSectionTitle(
            text = stringResource(R.string.transport_mode_dlg_trainer),
            enabled = customMode,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
        )
        TGDialogDropdownField(
            label = stringResource(R.string.transport_mode_dlg_trainer_tempo_percent_from_label),
            selectedOption = percentOptions.first { it.value == customFrom },
            options = percentOptions,
            onSelected = {
                customFrom = it.value
                lastChangedField = TGTransportTempoField.FROM
            },
            enabled = customMode,
            isError = validation.fromError,
            modifier = Modifier.fillMaxWidth(),
        )
        TGDialogDropdownField(
            label = stringResource(R.string.transport_mode_dlg_trainer_tempo_percent_to_label),
            selectedOption = percentOptions.first { it.value == customTo },
            options = percentOptions,
            onSelected = {
                customTo = it.value
                lastChangedField = TGTransportTempoField.TO
            },
            enabled = customMode,
            isError = validation.toError,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        )
        TGDialogDropdownField(
            label = stringResource(R.string.transport_mode_dlg_trainer_tempo_increment_label),
            selectedOption = percentOptions.first { it.value == customIncrement },
            options = percentOptions,
            onSelected = {
                customIncrement = it.value
                lastChangedField = TGTransportTempoField.INCREMENT
            },
            enabled = customMode,
            isError = validation.incrementError,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        )

        TGDialogSectionTitle(
            text = stringResource(R.string.transport_mode_dlg_loop_range_label),
            enabled = loopEnabled,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
        )
        TGDialogDropdownField(
            label = stringResource(R.string.transport_mode_dlg_loop_range_from_label),
            selectedOption = loopFromOptions.first { it.value == loopFromMeasure },
            options = loopFromOptions,
            onSelected = { loopFromMeasure = it.value },
            enabled = loopEnabled,
            modifier = Modifier.fillMaxWidth(),
        )
        TGDialogDropdownField(
            label = stringResource(R.string.transport_mode_dlg_loop_range_to_label),
            selectedOption = loopToOptions.first { it.value == loopToMeasure },
            options = loopToOptions,
            onSelected = { loopToMeasure = it.value },
            enabled = loopEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.End,
        ) {
            TextButton(onClick = onCancel) {
                Text(stringResource(R.string.global_button_cancel))
            }
            TextButton(
                onClick = {
                    onSave(
                        TGTransportModeFields(
                            customMode = customMode,
                            simplePercent = simplePercent,
                            simpleLoop = simpleLoop,
                            customFrom = customFrom,
                            customTo = customTo,
                            customIncrement = customIncrement,
                            loopFromMeasure = loopFromMeasure,
                            loopToMeasure = loopToMeasure,
                            measures = initial.measures,
                        )
                    )
                },
                enabled = validation.valid,
            ) {
                Text(stringResource(R.string.global_button_ok))
            }
        }
    }
}

private fun validateTransport(
    customFrom: Int,
    customTo: Int,
    customIncrement: Int,
    lastChangedField: TGTransportTempoField,
): TGTransportValidation {
    var valid = true
    var fromError = false
    var toError = false
    var incrementError = false

    if (lastChangedField == TGTransportTempoField.FROM) {
        if (customFrom >= customTo) {
            valid = false
            fromError = true
        }
    } else if (lastChangedField == TGTransportTempoField.TO) {
        if (customTo <= customFrom) {
            valid = false
            toError = true
        }
    }

    if (valid && customIncrement > (customTo - customFrom)) {
        valid = false
        incrementError = true
    }

    return TGTransportValidation(
        valid = valid,
        fromError = fromError,
        toError = toError,
        incrementError = incrementError,
    )
}

@Composable
private fun TransportRadioOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(selected = selected, onClick = onClick, role = Role.RadioButton)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = null)
        Text(text = label, modifier = Modifier.padding(start = 8.dp))
    }
}

@Preview(showBackground = true)
@Composable
private fun TGTransportModeDialogSimplePreview() {
    MaterialTheme {
        TGTransportModeDialogContent(
            initial = TGTransportModeFields(
                customMode = false,
                simplePercent = 100,
                simpleLoop = true,
                customFrom = 80,
                customTo = 100,
                customIncrement = 5,
                loopFromMeasure = null,
                loopToMeasure = 3,
                measures = listOf(
                    TGDropdownOption(1, "#1"),
                    TGDropdownOption(2, "#2 (Verse)"),
                    TGDropdownOption(3, "#3"),
                ),
            ),
            onSave = {},
            onCancel = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun TGTransportModeDialogCustomPreview() {
    MaterialTheme {
        TGTransportModeDialogContent(
            initial = TGTransportModeFields(
                customMode = true,
                simplePercent = 100,
                simpleLoop = false,
                customFrom = 70,
                customTo = 100,
                customIncrement = 10,
                loopFromMeasure = 2,
                loopToMeasure = 4,
                measures = listOf(
                    TGDropdownOption(1, "#1"),
                    TGDropdownOption(2, "#2 (Verse)"),
                    TGDropdownOption(3, "#3"),
                    TGDropdownOption(4, "#4 (Chorus)"),
                ),
            ),
            onSave = {},
            onCancel = {},
        )
    }
}
