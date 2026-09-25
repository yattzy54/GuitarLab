package app.tuxguitar.android.view.dialog.bend

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
import app.tuxguitar.editor.action.effect.TGChangeBendNoteAction
import app.tuxguitar.song.factory.TGFactory
import app.tuxguitar.song.managers.TGSongManager
import app.tuxguitar.song.models.TGBeat
import app.tuxguitar.song.models.TGMeasure
import app.tuxguitar.song.models.TGNote
import app.tuxguitar.song.models.TGString
import app.tuxguitar.song.models.effects.TGEffectBend

class TGBendDialog : TGModalFragment(R.layout.view_bend_dialog) {
    override fun onPostCreate(savedInstanceState: Bundle?) {
        createActionBar(true, false, R.string.bend_dlg_title)
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
        val defaultBend = findDefaultBend(defaultPreset)
        fillSelectablePresets(presets, defaultPreset)
        defaultBend?.let { bend -> postWhenReady { loadBend(bend) } }
    }

    fun findDefaultBend(defaultPreset: TGBendPreset?): TGEffectBend? {
        val note = getNote()
        if (note != null && note.getEffect().isBend()) return note.getEffect().getBend()
        return defaultPreset?.bend
    }

    fun findDefaultPreset(presets: List<TGBendPreset>): TGBendPreset? {
        val note = getNote()
        if (note != null && note.getEffect().isBend()) return null
        return presets.firstOrNull()
    }

    fun createPresets(): List<TGBendPreset> {
        val factory = getSongManager().getFactory()
        val presets = mutableListOf<TGBendPreset>()
        val bendLength = TGEffectBend.SEMITONE_LENGTH * 4

        presets.add(createPreset(factory, R.string.bend_dlg_preset_bend, listOf(0 to 0, 6 to bendLength, 12 to bendLength)))
        presets.add(
            createPreset(
                factory,
                R.string.bend_dlg_preset_bend_release,
                listOf(0 to 0, 3 to bendLength, 6 to bendLength, 9 to 0, 12 to 0)
            )
        )
        presets.add(
            createPreset(
                factory,
                R.string.bend_dlg_preset_bend_release_bend,
                listOf(0 to 0, 2 to bendLength, 4 to bendLength, 6 to 0, 8 to 0, 10 to bendLength, 12 to bendLength)
            )
        )
        presets.add(createPreset(factory, R.string.bend_dlg_preset_prebend, listOf(0 to bendLength, 12 to bendLength)))
        presets.add(
            createPreset(
                factory,
                R.string.bend_dlg_preset_prebend_release,
                listOf(0 to bendLength, 4 to bendLength, 8 to 0, 12 to 0)
            )
        )
        return presets
    }

    private fun createPreset(
        factory: TGFactory,
        nameResource: Int,
        points: List<Pair<Int, Int>>
    ): TGBendPreset {
        val bend = factory.newEffectBend()
        points.forEach { (position, value) -> bend.addPoint(position, value) }
        return TGBendPreset(getString(nameResource), bend)
    }

    fun createSelectablePresets(presets: List<TGBendPreset>): Array<TGSelectableItem> =
        (listOf(TGSelectableItem(null, getString(R.string.global_spinner_select_option))) +
            presets.map { TGSelectableItem(it, it.name) }).toTypedArray()

    fun fillSelectablePresets(presets: List<TGBendPreset>, selection: TGBendPreset?) {
        val adapter = ArrayAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item,
            createSelectablePresets(presets)
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        requireView().findViewById<Spinner>(R.id.bend_dlg_preset_value).adapter = adapter
        updateSelectedPreset(selection)
        appendListeners()
    }

    fun updateSelectedPreset(selection: TGBendPreset?) {
        val spinner = requireView().findViewById<Spinner>(R.id.bend_dlg_preset_value)
        @Suppress("UNCHECKED_CAST")
        val adapter = spinner.adapter as ArrayAdapter<TGSelectableItem>
        spinner.setSelection(adapter.getPosition(TGSelectableItem(selection, null)), false)
    }

    fun findSelectedPreset(): TGBendPreset? =
        (requireView().findViewById<Spinner>(R.id.bend_dlg_preset_value)
            .selectedItem as? TGSelectableItem)?.getItem() as? TGBendPreset

    fun loadSelectedPreset() {
        findSelectedPreset()?.let { loadBend(it.bend) }
    }

    fun loadBend(bend: TGEffectBend) {
        requireView().findViewById<TGBendEditor>(R.id.bend_dlg_bend_editor).loadBend(bend)
    }

    fun createBend(): TGEffectBend? =
        requireView().findViewById<TGBendEditor>(R.id.bend_dlg_bend_editor)
            .createBend(getSongManager().getFactory())

    fun appendListeners() {
        requireView().findViewById<Spinner>(R.id.bend_dlg_preset_value)
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
        requireView().findViewById<TGBendEditor>(R.id.bend_dlg_bend_editor)
            .setListener(object : TGBendEditorListener {
                override fun onChange() {
                    updateSelectedPreset(null)
                }
            })
    }

    fun cleanEffect() {
        updateEffect(null)
    }

    fun updateEffect() {
        updateEffect(createBend())
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
