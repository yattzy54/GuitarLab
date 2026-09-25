package app.tuxguitar.android.view.dialog.tremoloBar

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import app.tuxguitar.android.R
import app.tuxguitar.android.view.dialog.fragment.TGModalFragment
import app.tuxguitar.android.view.util.TGSelectableItem
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

class TGTremoloBarDialog : TGModalFragment(R.layout.view_tremolo_bar_dialog) {
    override fun onPostCreate(savedInstanceState: Bundle?) {
        createActionBar(true, false, R.string.tremolo_bar_dlg_title)
    }

    override fun onCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu_modal_fragment_ok_clean, menu)
        menu.findItem(R.id.action_ok).setOnMenuItemClickListener {
            updateEffect()
            close()
            true
        }
        menu.findItem(R.id.action_clean).setOnMenuItemClickListener {
            cleanEffect()
            close()
            true
        }
    }

    @SuppressLint("InflateParams")
    override fun onPostInflateView() {
        val presets = createPresets()
        val defaultPreset = findDefaultPreset(presets)
        val defaultEffect = findDefaultTremoloBar(defaultPreset)
        fillSelectablePresets(presets, defaultPreset)
        defaultEffect?.let { effect -> postWhenReady { loadTremoloBar(effect) } }
    }

    fun findDefaultTremoloBar(defaultPreset: TGTremoloBarPreset?): TGEffectTremoloBar? {
        val note = getNote()
        if (note != null && note.getEffect().isTremoloBar()) {
            return note.getEffect().getTremoloBar()
        }
        return defaultPreset?.tremoloBar
    }

    fun findDefaultPreset(presets: List<TGTremoloBarPreset>): TGTremoloBarPreset? {
        val note = getNote()
        if (note != null && note.getEffect().isTremoloBar()) return null
        return presets.firstOrNull()
    }

    fun createPresets(): List<TGTremoloBarPreset> {
        val factory = getSongManager().getFactory()
        return listOf(
            createPreset(factory, R.string.tremolo_bar_dlg_preset_dip, listOf(0 to 0, 6 to -2, 12 to 0)),
            createPreset(factory, R.string.tremolo_bar_dlg_preset_dive, listOf(0 to 0, 9 to -2, 12 to -2)),
            createPreset(factory, R.string.tremolo_bar_dlg_preset_release_up, listOf(0 to -2, 9 to -2, 12 to 0)),
            createPreset(factory, R.string.tremolo_bar_dlg_preset_inverted_dip, listOf(0 to 0, 6 to 2, 12 to 0)),
            createPreset(factory, R.string.tremolo_bar_dlg_preset_return, listOf(0 to 0, 9 to 2, 12 to 2)),
            createPreset(factory, R.string.tremolo_bar_dlg_preset_release_down, listOf(0 to 2, 9 to 2, 12 to 0))
        )
    }

    private fun createPreset(
        factory: TGFactory,
        nameResource: Int,
        points: List<Pair<Int, Int>>
    ): TGTremoloBarPreset {
        val effect = factory.newEffectTremoloBar()
        points.forEach { (position, value) -> effect.addPoint(position, value) }
        return TGTremoloBarPreset(getString(nameResource), effect)
    }

    fun createSelectablePresets(presets: List<TGTremoloBarPreset>): Array<TGSelectableItem> =
        (listOf(TGSelectableItem(null, getString(R.string.global_spinner_select_option))) +
            presets.map { TGSelectableItem(it, it.name) }).toTypedArray()

    fun fillSelectablePresets(presets: List<TGTremoloBarPreset>, selection: TGTremoloBarPreset?) {
        val adapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item,
            createSelectablePresets(presets)
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        requireView().findViewById<Spinner>(R.id.tremolo_bar_dlg_preset_value).adapter = adapter
        updateSelectedPreset(selection)
        appendListeners()
    }

    fun updateSelectedPreset(selection: TGTremoloBarPreset?) {
        val spinner = requireView().findViewById<Spinner>(R.id.tremolo_bar_dlg_preset_value)
        @Suppress("UNCHECKED_CAST")
        val adapter = spinner.adapter as ArrayAdapter<TGSelectableItem>
        spinner.setSelection(adapter.getPosition(TGSelectableItem(selection, null)), false)
    }

    fun findSelectedPreset(): TGTremoloBarPreset? =
        (requireView().findViewById<Spinner>(R.id.tremolo_bar_dlg_preset_value)
            .selectedItem as? TGSelectableItem)?.getItem() as? TGTremoloBarPreset

    fun loadSelectedPreset() {
        findSelectedPreset()?.let { loadTremoloBar(it.tremoloBar) }
    }

    fun loadTremoloBar(effect: TGEffectTremoloBar) {
        requireView()
            .findViewById<TGTremoloBarEditor>(R.id.tremolo_bar_dlg_tremolo_bar_editor)
            .loadTremoloBar(effect)
    }

    fun createTremoloBar(): TGEffectTremoloBar? =
        requireView()
            .findViewById<TGTremoloBarEditor>(R.id.tremolo_bar_dlg_tremolo_bar_editor)
            .createTremoloBar(getSongManager().getFactory())

    fun appendListeners() {
        requireView().findViewById<Spinner>(R.id.tremolo_bar_dlg_preset_value)
            .onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    loadSelectedPreset()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    loadSelectedPreset()
                }
            }
        requireView()
            .findViewById<TGTremoloBarEditor>(R.id.tremolo_bar_dlg_tremolo_bar_editor)
            .setListener(object : TGTremoloBarEditorListener {
                override fun onChange() {
                    updateSelectedPreset(null)
                }
            })
    }

    fun cleanEffect() {
        updateEffect(null)
    }

    fun updateEffect() {
        updateEffect(createTremoloBar())
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
