package app.tuxguitar.android.view.dialog.harmonic

import android.annotation.SuppressLint
import android.os.Bundle
import android.R as AndroidR
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import app.tuxguitar.android.R
import app.tuxguitar.android.view.dialog.fragment.TGModalFragment
import app.tuxguitar.android.view.util.TGSelectableItem
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.editor.action.effect.TGChangeHarmonicNoteAction
import app.tuxguitar.song.managers.TGSongManager
import app.tuxguitar.song.models.TGBeat
import app.tuxguitar.song.models.TGMeasure
import app.tuxguitar.song.models.TGNote
import app.tuxguitar.song.models.TGString
import app.tuxguitar.song.models.effects.TGEffectHarmonic

class TGHarmonicDialog : TGModalFragment(R.layout.view_harmonic_dialog) {
    override fun onPostCreate(savedInstanceState: Bundle?) {
        createActionBar(true, false, R.string.harmonic_dlg_title)
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
        fillHarmonics()
        fillData()
    }

    fun isNaturalHarmonicAvailable(): Boolean {
        val note = getNote() ?: return false
        return TGEffectHarmonic.NATURAL_FREQUENCIES.any {
            note.value % 12 == it[0] % 12
        }
    }

    fun getCurrentType(): Int {
        val note = getNote()
        if (note != null && note.effect.isHarmonic) return note.effect.harmonic.type
        return if (isNaturalHarmonicAvailable()) {
            TGEffectHarmonic.TYPE_NATURAL
        } else {
            TGEffectHarmonic.TYPE_ARTIFICIAL
        }
    }

    fun getTypeLabel(type: Int): String = when (type) {
        TGEffectHarmonic.TYPE_NATURAL -> TGEffectHarmonic.KEY_NATURAL
        TGEffectHarmonic.TYPE_ARTIFICIAL -> TGEffectHarmonic.KEY_ARTIFICIAL
        TGEffectHarmonic.TYPE_TAPPED -> TGEffectHarmonic.KEY_TAPPED
        TGEffectHarmonic.TYPE_PINCH -> TGEffectHarmonic.KEY_PINCH
        TGEffectHarmonic.TYPE_SEMI -> TGEffectHarmonic.KEY_SEMI
        else -> ""
    }

    fun createDataValues(type: Int): Array<TGSelectableItem> {
        if (type == TGEffectHarmonic.TYPE_NATURAL) return emptyArray()
        val label = getTypeLabel(type)
        return Array(TGEffectHarmonic.NATURAL_FREQUENCIES.size) { index ->
            TGSelectableItem(
                index,
                "$label(${TGEffectHarmonic.NATURAL_FREQUENCIES[index][0]})"
            )
        }
    }

    fun fillData() {
        val note = getNote()
        val selection = if (note != null && note.effect.isHarmonic) {
            note.effect.harmonic.data
        } else {
            -1
        }
        fillData(getCurrentType(), selection)
    }

    fun fillData(type: Int, selection: Int) {
        val values = createDataValues(type)
        val adapter = ArrayAdapter(
            requireActivity(),
            AndroidR.layout.simple_spinner_item,
            values
        )
        adapter.setDropDownViewResource(AndroidR.layout.simple_spinner_dropdown_item)
        val spinner = requireView().findViewById<Spinner>(R.id.harmonic_dlg_data_value)
        spinner.adapter = adapter
        spinner.isEnabled = values.isNotEmpty()
        spinner.visibility = if (values.isNotEmpty()) View.VISIBLE else View.GONE
        if (values.isNotEmpty()) {
            spinner.setSelection(adapter.getPosition(TGSelectableItem(selection, null)))
        }
    }

    fun findSelectedData(): Int {
        val item = requireView().findViewById<Spinner>(R.id.harmonic_dlg_data_value)
            .selectedItem as? TGSelectableItem
        return item?.item as? Int ?: 0
    }

    fun fillHarmonics() {
        val selected = getCurrentType()
        val naturalAvailable = isNaturalHarmonicAvailable()
        fillHarmonic(R.id.harmonic_dlg_type_nh, TGEffectHarmonic.TYPE_NATURAL, selected, naturalAvailable)
        fillHarmonic(R.id.harmonic_dlg_type_ah, TGEffectHarmonic.TYPE_ARTIFICIAL, selected, true)
        fillHarmonic(R.id.harmonic_dlg_type_th, TGEffectHarmonic.TYPE_TAPPED, selected, true)
        fillHarmonic(R.id.harmonic_dlg_type_ph, TGEffectHarmonic.TYPE_PINCH, selected, true)
        fillHarmonic(R.id.harmonic_dlg_type_sh, TGEffectHarmonic.TYPE_SEMI, selected, true)
    }

    fun fillHarmonic(id: Int, value: Int, selection: Int, enabled: Boolean) {
        requireView().findViewById<RadioButton>(id).apply {
            tag = value
            isChecked = value == selection
            isEnabled = enabled
            setOnClickListener { fillData(value, 0) }
        }
    }

    fun findSelectedHarmonic(): Int {
        val group = requireView().findViewById<RadioGroup>(R.id.harmonic_dlg_type_group)
        val id = group.checkedRadioButtonId
        return if (id != -1) {
            group.findViewById<RadioButton>(id)?.tag as? Int ?: TGEffectHarmonic.TYPE_NATURAL
        } else {
            TGEffectHarmonic.TYPE_NATURAL
        }
    }

    fun createHarmonic(): TGEffectHarmonic =
        getSongManager().factory.newEffectHarmonic().also {
            it.setType(findSelectedHarmonic())
            it.setData(findSelectedData())
        }

    fun cleanEffect() {
        updateEffect(null)
    }

    fun updateEffect() {
        updateEffect(createHarmonic())
    }

    fun updateEffect(effect: TGEffectHarmonic?) {
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
