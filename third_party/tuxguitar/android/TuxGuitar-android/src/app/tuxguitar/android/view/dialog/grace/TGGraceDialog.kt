package app.tuxguitar.android.view.dialog.grace

import android.annotation.SuppressLint
import android.os.Bundle
import android.R as AndroidR
import android.view.Menu
import android.view.MenuInflater
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import app.tuxguitar.android.R
import app.tuxguitar.android.view.dialog.fragment.TGModalFragment
import app.tuxguitar.android.view.util.TGSelectableItem
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

class TGGraceDialog : TGModalFragment(R.layout.view_grace_dialog) {
    override fun onPostCreate(savedInstanceState: Bundle?) {
        createActionBar(true, false, R.string.grace_dlg_title)
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
        fillDeadNoteOption()
        fillOnBeatOptions()
        fillDurations()
        fillDynamics()
        fillTransitions()
    }

    fun createFretValues(): Array<TGSelectableItem> =
        Array(101) { TGSelectableItem(it, it.toString()) }

    fun fillFret() {
        val note = getNote()
        val selection = if (note != null) {
            if (note.effect.isGrace) note.effect.grace.fret else note.value
        } else {
            0
        }
        val adapter = ArrayAdapter(
            requireActivity(),
            AndroidR.layout.simple_spinner_item,
            createFretValues()
        )
        adapter.setDropDownViewResource(AndroidR.layout.simple_spinner_dropdown_item)
        val spinner = requireView().findViewById<Spinner>(R.id.grace_dlg_fret_value)
        spinner.adapter = adapter
        spinner.setSelection(adapter.getPosition(TGSelectableItem(selection, null)))
    }

    fun findSelectedFret(): Int =
        (requireView().findViewById<Spinner>(R.id.grace_dlg_fret_value)
            .selectedItem as TGSelectableItem).item as Int

    fun fillDeadNoteOption() {
        val note = getNote()
        val checked = note != null && note.effect.isGrace && note.effect.grace.isDead
        requireView().findViewById<CheckBox>(R.id.grace_dlg_dead_note_option).isChecked = checked
    }

    fun findDeadNoteValue(): Boolean =
        requireView().findViewById<CheckBox>(R.id.grace_dlg_dead_note_option).isChecked

    fun fillOnBeatOptions() {
        val note = getNote()
        val selection = note != null && note.effect.isGrace && note.effect.grace.isOnBeat
        fillOnBeatOption(R.id.grace_dlg_position_before_beat, false, selection)
        fillOnBeatOption(R.id.grace_dlg_position_on_beat, true, selection)
    }

    fun fillOnBeatOption(id: Int, value: Boolean, selection: Boolean) {
        requireView().findViewById<RadioButton>(id).apply {
            tag = value
            isChecked = value == selection
        }
    }

    fun findSelectedOnBeat(): Boolean {
        val group = requireView().findViewById<RadioGroup>(R.id.grace_dlg_position_group)
        val id = group.checkedRadioButtonId
        return if (id != -1) group.findViewById<RadioButton>(id)?.tag as? Boolean ?: false else false
    }

    fun fillDurations() {
        val note = getNote()
        val selection = if (note != null && note.effect.isGrace) {
            note.effect.grace.duration
        } else {
            1
        }
        fillDuration(R.id.grace_dlg_duration_16, TGEffectGrace.DURATION_SIXTEENTH, selection)
        fillDuration(R.id.grace_dlg_duration_32, TGEffectGrace.DURATION_THIRTY_SECOND, selection)
        fillDuration(R.id.grace_dlg_duration_64, TGEffectGrace.DURATION_SIXTY_FOURTH, selection)
    }

    fun fillDuration(id: Int, value: Int, selection: Int) {
        requireView().findViewById<RadioButton>(id).apply {
            tag = value
            isChecked = value == selection
        }
    }

    fun findSelectedDuration(): Int = findSelectedOption(
        requireView().findViewById(R.id.grace_dlg_duration_group),
        TGEffectGrace.DURATION_SIXTEENTH
    )

    fun fillDynamics() {
        val note = getNote()
        val selection = if (note != null && note.effect.isGrace) {
            note.effect.grace.dynamic
        } else {
            TGVelocities.DEFAULT
        }
        fillDynamic(R.id.grace_dlg_dynamic_ppp, TGVelocities.PIANO_PIANISSIMO, selection)
        fillDynamic(R.id.grace_dlg_dynamic_pp, TGVelocities.PIANISSIMO, selection)
        fillDynamic(R.id.grace_dlg_dynamic_p, TGVelocities.PIANO, selection)
        fillDynamic(R.id.grace_dlg_dynamic_mp, TGVelocities.MEZZO_PIANO, selection)
        fillDynamic(R.id.grace_dlg_dynamic_mf, TGVelocities.MEZZO_FORTE, selection)
        fillDynamic(R.id.grace_dlg_dynamic_f, TGVelocities.FORTE, selection)
        fillDynamic(R.id.grace_dlg_dynamic_ff, TGVelocities.FORTISSIMO, selection)
        fillDynamic(R.id.grace_dlg_dynamic_fff, TGVelocities.FORTE_FORTISSIMO, selection)
    }

    fun fillDynamic(id: Int, value: Int, selection: Int) = fillDuration(id, value, selection)

    fun findSelectedDynamic(): Int = findSelectedOption(
        requireView().findViewById(R.id.grace_dlg_dynamic_group),
        TGVelocities.DEFAULT
    )

    fun fillTransitions() {
        val note = getNote()
        val selection = if (note != null && note.effect.isGrace) {
            note.effect.grace.transition
        } else {
            TGEffectGrace.TRANSITION_NONE
        }
        fillTransition(R.id.grace_dlg_transition_none, TGEffectGrace.TRANSITION_NONE, selection)
        fillTransition(R.id.grace_dlg_transition_bend, TGEffectGrace.TRANSITION_BEND, selection)
        fillTransition(R.id.grace_dlg_transition_slide, TGEffectGrace.TRANSITION_SLIDE, selection)
        fillTransition(R.id.grace_dlg_transition_hammer, TGEffectGrace.TRANSITION_HAMMER, selection)
    }

    fun fillTransition(id: Int, value: Int, selection: Int) = fillDuration(id, value, selection)

    fun findSelectedTransition(): Int = findSelectedOption(
        requireView().findViewById(R.id.grace_dlg_transition_group),
        TGEffectGrace.TRANSITION_NONE
    )

    fun findSelectedOption(group: RadioGroup, defaultValue: Int): Int {
        val id = group.checkedRadioButtonId
        return if (id != -1) group.findViewById<RadioButton>(id)?.tag as? Int ?: defaultValue else defaultValue
    }

    fun createGrace(): TGEffectGrace =
        getSongManager().factory.newEffectGrace().also {
            it.setDead(findDeadNoteValue())
            it.setOnBeat(findSelectedOnBeat())
            it.setFret(findSelectedFret())
            it.setDuration(findSelectedDuration())
            it.setDynamic(findSelectedDynamic())
            it.setTransition(findSelectedTransition())
        }

    fun cleanEffect() {
        updateEffect(null)
    }

    fun updateEffect() {
        updateEffect(createGrace())
    }

    fun updateEffect(effect: TGEffectGrace?) {
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
