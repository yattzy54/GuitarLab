package app.tuxguitar.android.view.dialog.bend

import com.mmt.guitarlab.core.ui.theme.GuitarLabTheme

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import app.tuxguitar.android.R
import app.tuxguitar.android.view.dialog.compose.TGComposeDialog
import app.tuxguitar.android.view.dialog.compose.TGDialogDropdownField
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.editor.action.effect.TGChangeBendNoteAction
import app.tuxguitar.song.factory.TGFactory
import app.tuxguitar.song.managers.TGSongManager
import app.tuxguitar.song.models.TGBeat
import app.tuxguitar.song.models.TGMeasure
import app.tuxguitar.song.models.TGNote
import app.tuxguitar.song.models.TGString
import app.tuxguitar.song.models.effects.TGEffectBend

class TGBendDialog : TGComposeDialog() {
    @Composable
    override fun SheetContent(onDismiss: () -> Unit) {
        val presets = createPresets()
        val defaultPreset = findDefaultPreset(presets)
        TGBendDialogContent(
            presets = presets,
            initialPreset = defaultPreset,
            initialEffect = findDefaultBend(defaultPreset),
            onConfirm = { editorView ->
                updateEffect(editorView.createBend(getSongManager().factory))
                onDismiss()
            },
            onClean = {
                cleanEffect()
                onDismiss()
            },
        )
    }

    fun findDefaultBend(defaultPreset: TGBendPreset?): TGEffectBend? {
        val note = getNote()
        if (note != null && note.effect.isBend) {
            return note.effect.bend
        }
        return defaultPreset?.bend
    }

    fun findDefaultPreset(presets: List<TGBendPreset>): TGBendPreset? {
        val note = getNote()
        if (note != null && note.effect.isBend) {
            return null
        }
        return presets.firstOrNull()
    }

    fun createPresets(): List<TGBendPreset> {
        val factory = getSongManager().factory
        val bendLength = TGEffectBend.SEMITONE_LENGTH * 4
        return listOf(
            createPreset(factory, R.string.bend_dlg_preset_bend, listOf(0 to 0, 6 to bendLength, 12 to bendLength)),
            createPreset(
                factory,
                R.string.bend_dlg_preset_bend_release,
                listOf(0 to 0, 3 to bendLength, 6 to bendLength, 9 to 0, 12 to 0),
            ),
            createPreset(
                factory,
                R.string.bend_dlg_preset_bend_release_bend,
                listOf(0 to 0, 2 to bendLength, 4 to bendLength, 6 to 0, 8 to 0, 10 to bendLength, 12 to bendLength),
            ),
            createPreset(factory, R.string.bend_dlg_preset_prebend, listOf(0 to bendLength, 12 to bendLength)),
            createPreset(
                factory,
                R.string.bend_dlg_preset_prebend_release,
                listOf(0 to bendLength, 4 to bendLength, 8 to 0, 12 to 0),
            ),
        )
    }

    private fun createPreset(
        factory: TGFactory,
        nameResource: Int,
        points: List<Pair<Int, Int>>,
    ): TGBendPreset {
        val bend = factory.newEffectBend()
        points.forEach { (position, value) -> bend.addPoint(position, value) }
        return TGBendPreset(getString(nameResource), bend)
    }

    fun cleanEffect() {
        updateEffect(null)
    }

    fun updateEffect(effect: TGEffectBend?) {
        TGActionProcessor(findContext(), TGChangeBendNoteAction.NAME).apply {
            setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, getMeasure())
            setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, getBeat())
            setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, getString())
            setAttribute(TGChangeBendNoteAction.ATTRIBUTE_EFFECT, effect)
            process()
        }
    }

    fun getSongManager(): TGSongManager =
        requireNotNull(getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER))

    fun getMeasure(): TGMeasure? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE)

    fun getBeat(): TGBeat? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT)

    fun getNote(): TGNote? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE)

    fun getString(): TGString? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING)
}

@Composable
private fun TGBendDialogContent(
    presets: List<TGBendPreset>,
    initialPreset: TGBendPreset?,
    initialEffect: TGEffectBend?,
    onConfirm: (TGBendEditor) -> Unit,
    onClean: () -> Unit,
    createEditorView: (Context) -> TGBendEditor = { context -> TGBendEditor(context, null) },
) {
    val context = LocalContext.current
    val selectOptionLabel = stringResource(R.string.global_spinner_select_option)
    var selectedPreset by remember(initialPreset, presets) { mutableStateOf(initialPreset) }
    var effectToLoad by remember(initialEffect) { mutableStateOf(initialEffect) }
    var loadRequest by remember(initialEffect) { mutableIntStateOf(if (initialEffect != null) 1 else 0) }
    val editorView = remember(context) { createEditorView(context) }

    DisposableEffect(editorView) {
        val listener = object : TGBendEditorListener {
            override fun onChange() {
                selectedPreset = null
            }
        }
        editorView.setListener(listener)
        onDispose { editorView.setListener(null) }
    }

    LaunchedEffect(editorView, loadRequest) {
        if (loadRequest > 0) {
            effectToLoad?.let(editorView::loadBend)
        }
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .heightIn(max = 560.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = stringResource(R.string.bend_dlg_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        TGDialogDropdownField(
            label = stringResource(R.string.bend_dlg_preset_label),
            selectedText = selectedPreset?.name ?: selectOptionLabel,
            options = buildList {
                add(selectOptionLabel)
                addAll(presets.map { it.name })
            },
            onOptionSelected = { index ->
                val preset = presets.getOrNull(index - 1)
                selectedPreset = preset
                if (preset != null) {
                    effectToLoad = preset.bend
                    loadRequest += 1
                }
            },
        )

        AndroidView(
            factory = { editorView },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.End,
        ) {
            TextButton(onClick = onClean) {
                Text(stringResource(R.string.global_button_clean))
            }
            TextButton(onClick = { onConfirm(editorView) }) {
                Text(stringResource(R.string.global_button_ok))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun TGBendDialogContentPreview() {
    GuitarLabTheme {
        TGBendDialogContent(
            presets = listOf(
                TGBendPreset("Bend", previewBendEffect(listOf(0 to 0, 6 to 4, 12 to 4))),
                TGBendPreset("Bend/Release", previewBendEffect(listOf(0 to 0, 6 to 4, 12 to 0))),
            ),
            initialPreset = null,
            initialEffect = previewBendEffect(listOf(0 to 0, 4 to 3, 8 to 6, 12 to 4)),
            onConfirm = {},
            onClean = {},
        )
    }
}

private fun previewBendEffect(points: List<Pair<Int, Int>>): TGEffectBend =
    TGFactory().newEffectBend().also { bend ->
        points.forEach { (position, value) -> bend.addPoint(position, value) }
    }
