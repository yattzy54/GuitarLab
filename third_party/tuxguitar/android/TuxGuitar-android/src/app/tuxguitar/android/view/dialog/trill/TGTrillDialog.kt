package app.tuxguitar.android.view.dialog.trill

import android.annotation.SuppressLint
import android.os.Bundle
import android.R as AndroidR
import android.view.Menu
import android.view.MenuInflater
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import app.tuxguitar.android.R
import app.tuxguitar.android.view.dialog.fragment.TGModalFragment
import app.tuxguitar.android.view.util.TGSelectableItem
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.editor.action.effect.TGChangeTrillNoteAction
import app.tuxguitar.song.managers.TGSongManager
import app.tuxguitar.song.models.TGBeat
import app.tuxguitar.song.models.TGDuration
import app.tuxguitar.song.models.TGMeasure
import app.tuxguitar.song.models.TGNote
import app.tuxguitar.song.models.TGString
import app.tuxguitar.song.models.effects.TGEffectTrill

class TGTrillDialog : TGModalFragment(R.layout.view_trill_dialog) {
    override fun onPostCreate(savedInstanceState: Bundle?) {
        createActionBar(true, false, R.string.trill_dlg_title)
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
        fillFret()
        fillDurations()
    }

    fun createFretValues(): Array<TGSelectableItem> =
        Array(101) { TGSelectableItem(it, it.toString()) }

    fun fillFret() {
        val note = getNote()
        val selection = if (note != null) {
            if (note.effect.isTrill) note.effect.trill.fret else note.value
        } else {
            0
        }
        val adapter = ArrayAdapter(
            requireActivity(),
            AndroidR.layout.simple_spinner_item,
            createFretValues()
        )
        adapter.setDropDownViewResource(AndroidR.layout.simple_spinner_dropdown_item)
        val spinner = requireView().findViewById<Spinner>(R.id.trill_dlg_fret_value)
        spinner.adapter = adapter
        spinner.setSelection(adapter.getPosition(TGSelectableItem(selection, null)))
    }

    fun findSelectedFret(): Int =
        (requireView().findViewById<Spinner>(R.id.trill_dlg_fret_value)
            .selectedItem as TGSelectableItem).item as Int

    fun fillDurations() {
        val note = getNote()
        val duration = if (note != null && note.effect.isTrill) {
            note.effect.trill.duration.value
        } else {
            TGDuration.EIGHTH
        }
        fillDuration(R.id.trill_dlg_duration_16, TGDuration.SIXTEENTH, duration)
        fillDuration(R.id.trill_dlg_duration_32, TGDuration.THIRTY_SECOND, duration)
        fillDuration(R.id.trill_dlg_duration_64, TGDuration.SIXTY_FOURTH, duration)
    }

    fun fillDuration(id: Int, value: Int, selection: Int) {
        requireView().findViewById<RadioButton>(id).apply {
            tag = value
            isChecked = value == selection
        }
    }

    fun findSelectedDuration(): Int {
        val group = requireView().findViewById<RadioGroup>(R.id.trill_dlg_duration_group)
        val id = group.checkedRadioButtonId
        return if (id != -1) {
            group.findViewById<RadioButton>(id)?.tag as? Int ?: TGDuration.EIGHTH
        } else {
            TGDuration.EIGHTH
        }
    }

    fun createTrill(): TGEffectTrill {
        return getSongManager().factory.newEffectTrill().also {
            it.setFret(findSelectedFret())
            it.duration.setValue(findSelectedDuration())
        }
    }

    fun cleanEffect() {
        updateEffect(null)
    }

    fun updateEffect() {
        updateEffect(createTrill())
    }

    fun updateEffect(effect: TGEffectTrill?) {
        val processor = TGActionProcessor(findContext(), TGChangeTrillNoteAction.NAME)
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, getMeasure())
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, getBeat())
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, getString())
        processor.setAttribute(TGChangeTrillNoteAction.ATTRIBUTE_EFFECT, effect)
        processor.process()
    }

    fun getSongManager(): TGSongManager =
        requireNotNull(getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER))

    fun getMeasure(): TGMeasure? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE)
    fun getBeat(): TGBeat? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT)
    fun getNote(): TGNote? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE)
    fun getString(): TGString? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING)
}
