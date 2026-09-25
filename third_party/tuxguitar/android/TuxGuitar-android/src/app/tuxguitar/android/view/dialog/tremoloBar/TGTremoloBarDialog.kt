package app.tuxguitar.android.view.dialog.tremoloBar

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
import app.tuxguitar.android.view.dialog.compose.TGComposeBottomSheetDialogFragment
import app.tuxguitar.android.view.dialog.compose.TGDialogDropdownField
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.editor.action.effect.TGChangeTremoloBarAction
import app.tuxguitar.song.factory.TGFactory
import app.tuxguitar.song.managers.TGSongManager
import app.tuxguitar.song.models.TGBeat
import app.tuxguitar.song.models.TGMeasure
import app.tuxguitar.song.models.TGNote
import app.tuxguitar.song.models.TGString
import app.tuxguitar.song.models.effects.TGEffectTremoloBar

class TGTremoloBarDialog : TGComposeBottomSheetDialogFragment() {
    @Composable
    override fun SheetContent(onDismiss: () -> Unit) {
        val presets = createPresets()
        val defaultPreset = findDefaultPreset(presets)
        TGTremoloBarDialogContent(
            presets = presets,
            initialPreset = defaultPreset,
            initialEffect = findDefaultTremoloBar(defaultPreset),
            onConfirm = { editorView ->
                updateEffect(editorView.createTremoloBar(getSongManager().factory))
                onDismiss()
            },
            onClean = {
                cleanEffect()
                onDismiss()
            },
        )
    }

    fun findDefaultTremoloBar(defaultPreset: TGTremoloBarPreset?): TGEffectTremoloBar? {
        val note = getNote()
        if (note != null && note.effect.isTremoloBar) {
            return note.effect.tremoloBar
        }
        return defaultPreset?.tremoloBar
    }

    fun findDefaultPreset(presets: List<TGTremoloBarPreset>): TGTremoloBarPreset? {
        val note = getNote()
        if (note != null && note.effect.isTremoloBar) {
            return null
        }
        return presets.firstOrNull()
    }

    fun createPresets(): List<TGTremoloBarPreset> {
        val factory = getSongManager().factory
        return listOf(
            createPreset(factory, R.string.tremolo_bar_dlg_preset_dip, listOf(0 to 0, 6 to -2, 12 to 0)),
            createPreset(factory, R.string.tremolo_bar_dlg_preset_dive, listOf(0 to 0, 9 to -2, 12 to -2)),
            createPreset(factory, R.string.tremolo_bar_dlg_preset_release_up, listOf(0 to -2, 9 to -2, 12 to 0)),
            createPreset(factory, R.string.tremolo_bar_dlg_preset_inverted_dip, listOf(0 to 0, 6 to 2, 12 to 0)),
            createPreset(factory, R.string.tremolo_bar_dlg_preset_return, listOf(0 to 0, 9 to 2, 12 to 2)),
            createPreset(factory, R.string.tremolo_bar_dlg_preset_release_down, listOf(0 to 2, 9 to 2, 12 to 0)),
        )
    }

    private fun createPreset(
        factory: TGFactory,
        nameResource: Int,
        points: List<Pair<Int, Int>>,
    ): TGTremoloBarPreset {
        val effect = factory.newEffectTremoloBar()
        points.forEach { (position, value) -> effect.addPoint(position, value) }
        return TGTremoloBarPreset(getString(nameResource), effect)
    }

    fun cleanEffect() {
        updateEffect(null)
    }

    fun updateEffect(effect: TGEffectTremoloBar?) {
        TGActionProcessor(findContext(), TGChangeTremoloBarAction.NAME).apply {
            setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, getMeasure())
            setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, getBeat())
            setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, getString())
            setAttribute(TGChangeTremoloBarAction.ATTRIBUTE_EFFECT, effect)
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
private fun TGTremoloBarDialogContent(
    presets: List<TGTremoloBarPreset>,
    initialPreset: TGTremoloBarPreset?,
    initialEffect: TGEffectTremoloBar?,
    onConfirm: (TGTremoloBarEditor) -> Unit,
    onClean: () -> Unit,
    createEditorView: (Context) -> TGTremoloBarEditor = { context -> TGTremoloBarEditor(context, null) },
) {
    val context = LocalContext.current
    val selectOptionLabel = stringResource(R.string.global_spinner_select_option)
    var selectedPreset by remember(initialPreset, presets) { mutableStateOf(initialPreset) }
    var effectToLoad by remember(initialEffect) { mutableStateOf(initialEffect) }
    var loadRequest by remember(initialEffect) { mutableIntStateOf(if (initialEffect != null) 1 else 0) }
    val editorView = remember(context) { createEditorView(context) }

    DisposableEffect(editorView) {
        val listener = object : TGTremoloBarEditorListener {
            override fun onChange() {
                selectedPreset = null
            }
        }
        editorView.setListener(listener)
        onDispose { editorView.setListener(null) }
    }

    LaunchedEffect(editorView, loadRequest) {
        if (loadRequest > 0) {
            effectToLoad?.let(editorView::loadTremoloBar)
        }
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .heightIn(max = 560.dp)
            .verticalScroll(rememberScrollState()),
    ) {
        Text(
            text = stringResource(R.string.tremolo_bar_dlg_title),
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        TGDialogDropdownField(
            label = stringResource(R.string.tremolo_bar_dlg_preset_label),
            selectedText = selectedPreset?.name ?: selectOptionLabel,
            options = buildList {
                add(selectOptionLabel)
                addAll(presets.map { it.name })
            },
            onOptionSelected = { index ->
                val preset = presets.getOrNull(index - 1)
                selectedPreset = preset
                if (preset != null) {
                    effectToLoad = preset.tremoloBar
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
private fun TGTremoloBarDialogContentPreview() {
    MaterialTheme {
        TGTremoloBarDialogContent(
            presets = listOf(
                TGTremoloBarPreset("Dip", previewTremoloBarEffect(listOf(0 to 0, 6 to -2, 12 to 0))),
                TGTremoloBarPreset("Dive", previewTremoloBarEffect(listOf(0 to 0, 9 to -2, 12 to -2))),
            ),
            initialPreset = null,
            initialEffect = previewTremoloBarEffect(listOf(0 to 0, 4 to 2, 8 to -2, 12 to 0)),
            onConfirm = {},
            onClean = {},
        )
    }
}

private fun previewTremoloBarEffect(points: List<Pair<Int, Int>>): TGEffectTremoloBar =
    TGFactory().newEffectTremoloBar().also { effect ->
        points.forEach { (position, value) -> effect.addPoint(position, value) }
    }
