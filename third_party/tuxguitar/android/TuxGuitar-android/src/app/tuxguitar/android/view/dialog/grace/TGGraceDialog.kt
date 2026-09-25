package app.tuxguitar.android.view.dialog.grace

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import app.tuxguitar.android.R
import app.tuxguitar.android.view.dialog.compose.TGComposeBottomSheetDialogFragment
import app.tuxguitar.android.view.dialog.compose.TGDialogDropdownField
import app.tuxguitar.android.view.dialog.compose.TGDialogSectionTitle
import app.tuxguitar.android.view.dialog.compose.TGDropdownOption
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.editor.action.effect.TGChangeGraceNoteAction
import app.tuxguitar.song.managers.TGSongManager
import app.tuxguitar.song.models.TGBeat
import app.tuxguitar.song.models.TGMeasure
import app.tuxguitar.song.models.TGNote
import app.tuxguitar.song.models.TGString
import app.tuxguitar.song.models.TGVelocities
import app.tuxguitar.song.models.effects.TGEffectGrace

data class TGGraceFields(
    val fret: Int,
    val deadNote: Boolean,
    val onBeat: Boolean,
    val duration: Int,
    val dynamic: Int,
    val transition: Int,
)

private data class TGGraceOption(
    val value: Int,
    val labelRes: Int,
    val iconRes: Int? = null,
)

class TGGraceDialog : TGComposeBottomSheetDialogFragment() {
    @Composable
    override fun SheetContent(onDismiss: () -> Unit) {
        TGGraceDialogContent(
            initial = createInitialFields(),
            onSave = {
                updateEffect(createGrace(it))
                onDismiss()
            },
            onClean = {
                updateEffect(null)
                onDismiss()
            },
            onCancel = onDismiss,
        )
    }

    private fun createInitialFields(): TGGraceFields {
        val note = getNote()
        val effect = note?.effect?.takeIf { it.isGrace }?.grace
        return TGGraceFields(
            fret = effect?.fret ?: note?.value ?: 0,
            deadNote = effect?.isDead ?: false,
            onBeat = effect?.isOnBeat ?: false,
            duration = effect?.duration ?: TGEffectGrace.DURATION_SIXTY_FOURTH,
            dynamic = effect?.dynamic ?: TGVelocities.DEFAULT,
            transition = effect?.transition ?: TGEffectGrace.TRANSITION_NONE,
        )
    }

    private fun createGrace(fields: TGGraceFields): TGEffectGrace =
        getSongManager().factory.newEffectGrace().also {
            it.setDead(fields.deadNote)
            it.setOnBeat(fields.onBeat)
            it.setFret(fields.fret)
            it.setDuration(fields.duration)
            it.setDynamic(fields.dynamic)
            it.setTransition(fields.transition)
        }

    private fun updateEffect(effect: TGEffectGrace?) {
        val processor = TGActionProcessor(findContext(), TGChangeGraceNoteAction.NAME)
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, getMeasure())
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, getBeat())
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, getString())
        processor.setAttribute(TGChangeGraceNoteAction.ATTRIBUTE_EFFECT, effect)
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
fun TGGraceDialogContent(
    initial: TGGraceFields,
    onSave: (TGGraceFields) -> Unit,
    onClean: () -> Unit,
    onCancel: () -> Unit,
) {
    val fretOptions = remember {
        (0..100).map { TGDropdownOption(it, it.toString()) }
    }
    val durationOptions = listOf(
        TGGraceOption(TGEffectGrace.DURATION_SIXTEENTH, R.string.grace_dlg_duration_16, R.drawable.duration_16),
        TGGraceOption(TGEffectGrace.DURATION_THIRTY_SECOND, R.string.grace_dlg_duration_32, R.drawable.duration_32),
        TGGraceOption(TGEffectGrace.DURATION_SIXTY_FOURTH, R.string.grace_dlg_duration_64, R.drawable.duration_64),
    )
    val dynamicOptions = listOf(
        TGGraceOption(TGVelocities.PIANO_PIANISSIMO, R.string.grace_dlg_dynamic_ppp, R.drawable.dynamic_ppp),
        TGGraceOption(TGVelocities.PIANISSIMO, R.string.grace_dlg_dynamic_pp, R.drawable.dynamic_pp),
        TGGraceOption(TGVelocities.PIANO, R.string.grace_dlg_dynamic_p, R.drawable.dynamic_p),
        TGGraceOption(TGVelocities.MEZZO_PIANO, R.string.grace_dlg_dynamic_mp, R.drawable.dynamic_mp),
        TGGraceOption(TGVelocities.MEZZO_FORTE, R.string.grace_dlg_dynamic_mf, R.drawable.dynamic_mf),
        TGGraceOption(TGVelocities.FORTE, R.string.grace_dlg_dynamic_f, R.drawable.dynamic_f),
        TGGraceOption(TGVelocities.FORTISSIMO, R.string.grace_dlg_dynamic_ff, R.drawable.dynamic_ff),
        TGGraceOption(TGVelocities.FORTE_FORTISSIMO, R.string.grace_dlg_dynamic_fff, R.drawable.dynamic_fff),
    )
    val transitionOptions = listOf(
        TGGraceOption(TGEffectGrace.TRANSITION_NONE, R.string.grace_dlg_transition_none),
        TGGraceOption(TGEffectGrace.TRANSITION_BEND, R.string.grace_dlg_transition_bend),
        TGGraceOption(TGEffectGrace.TRANSITION_SLIDE, R.string.grace_dlg_transition_slide),
        TGGraceOption(TGEffectGrace.TRANSITION_HAMMER, R.string.grace_dlg_transition_hammer),
    )

    var fret by remember { mutableIntStateOf(initial.fret) }
    var deadNote by remember { mutableStateOf(initial.deadNote) }
    var onBeat by remember { mutableStateOf(initial.onBeat) }
    var duration by remember { mutableIntStateOf(initial.duration) }
    var dynamic by remember { mutableIntStateOf(initial.dynamic) }
    var transition by remember { mutableIntStateOf(initial.transition) }

    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .heightIn(max = 560.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = stringResource(R.string.grace_dlg_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp),
        )
        TGDialogDropdownField(
            label = stringResource(R.string.grace_dlg_fret_label),
            selectedOption = fretOptions.first { it.value == fret },
            options = fretOptions,
            onSelected = { fret = it.value },
            modifier = Modifier.fillMaxWidth(),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp)
                .clickable { deadNote = !deadNote },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Checkbox(checked = deadNote, onCheckedChange = { deadNote = it })
            Text(text = stringResource(R.string.grace_dlg_dead_note_label))
        }

        TGDialogSectionTitle(
            text = stringResource(R.string.grace_dlg_position_label),
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
        )
        GraceRadioOption(
            label = stringResource(R.string.grace_dlg_position_before_beat),
            selected = !onBeat,
            onClick = { onBeat = false },
        )
        GraceRadioOption(
            label = stringResource(R.string.grace_dlg_position_on_beat),
            selected = onBeat,
            onClick = { onBeat = true },
        )

        TGDialogSectionTitle(
            text = stringResource(R.string.grace_dlg_duration_label),
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
        )
        durationOptions.forEach { option ->
            GraceRadioOption(
                label = stringResource(option.labelRes),
                selected = duration == option.value,
                onClick = { duration = option.value },
                iconRes = option.iconRes,
            )
        }

        TGDialogSectionTitle(
            text = stringResource(R.string.grace_dlg_dynamic_label),
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
        )
        dynamicOptions.forEach { option ->
            GraceRadioOption(
                label = stringResource(option.labelRes),
                selected = dynamic == option.value,
                onClick = { dynamic = option.value },
                iconRes = option.iconRes,
            )
        }

        TGDialogSectionTitle(
            text = stringResource(R.string.grace_dlg_transition_label),
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
        )
        transitionOptions.forEach { option ->
            GraceRadioOption(
                label = stringResource(option.labelRes),
                selected = transition == option.value,
                onClick = { transition = option.value },
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
                        TGGraceFields(
                            fret = fret,
                            deadNote = deadNote,
                            onBeat = onBeat,
                            duration = duration,
                            dynamic = dynamic,
                            transition = transition,
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
private fun GraceRadioOption(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    iconRes: Int? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton,
            )
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = null)
        Text(
            text = label,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
        )
        if (iconRes != null) {
            Image(
                painter = painterResource(iconRes),
                contentDescription = null,
                modifier = Modifier.padding(start = 8.dp),
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TGGraceDialogContentPreview() {
    MaterialTheme {
        TGGraceDialogContent(
            initial = TGGraceFields(
                fret = 5,
                deadNote = false,
                onBeat = true,
                duration = TGEffectGrace.DURATION_THIRTY_SECOND,
                dynamic = TGVelocities.MEZZO_FORTE,
                transition = TGEffectGrace.TRANSITION_SLIDE,
            ),
            onSave = {},
            onClean = {},
            onCancel = {},
        )
    }
}
