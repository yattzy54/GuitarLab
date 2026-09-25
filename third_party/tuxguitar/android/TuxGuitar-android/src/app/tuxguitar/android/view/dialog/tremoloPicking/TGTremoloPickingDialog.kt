package app.tuxguitar.android.view.dialog.tremoloPicking

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.widget.RadioButton
import android.widget.RadioGroup
import app.tuxguitar.android.R
import app.tuxguitar.android.view.dialog.fragment.TGModalFragment
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.editor.action.effect.TGChangeTremoloPickingAction
import app.tuxguitar.song.managers.TGSongManager
import app.tuxguitar.song.models.TGBeat
import app.tuxguitar.song.models.TGDuration
import app.tuxguitar.song.models.TGMeasure
import app.tuxguitar.song.models.TGNote
import app.tuxguitar.song.models.TGString
import app.tuxguitar.song.models.effects.TGEffectTremoloPicking

class TGTremoloPickingDialog : TGModalFragment(R.layout.view_tremolo_picking_dialog) {
    override fun onPostCreate(savedInstanceState: Bundle?) {
        createActionBar(true, false, R.string.tremolo_picking_dlg_title)
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
        fillDurations()
    }

    fun fillDurations() {
        val note = getNote()
        val duration = if (note != null && note.effect.isTremoloPicking) {
            note.effect.tremoloPicking.duration.value
        } else {
            TGDuration.EIGHTH
        }
        fillDuration(R.id.tremolo_picking_dlg_duration_8, TGDuration.EIGHTH, duration)
        fillDuration(R.id.tremolo_picking_dlg_duration_16, TGDuration.SIXTEENTH, duration)
        fillDuration(R.id.tremolo_picking_dlg_duration_32, TGDuration.THIRTY_SECOND, duration)
    }

    fun fillDuration(id: Int, value: Int, selection: Int) {
        requireView().findViewById<RadioButton>(id).apply {
            tag = value
            isChecked = value == selection
        }
    }

    fun findSelectedDuration(): Int {
        val group = requireView().findViewById<RadioGroup>(R.id.tremolo_picking_dlg_duration_group)
        val id = group.checkedRadioButtonId
        return if (id != -1) {
            group.findViewById<RadioButton>(id)?.tag as? Int ?: TGDuration.EIGHTH
        } else {
            TGDuration.EIGHTH
        }
    }

    fun createTremoloPicking(): TGEffectTremoloPicking {
        return getSongManager().factory.newEffectTremoloPicking().also {
            it.duration.setValue(findSelectedDuration())
        }
    }

    fun cleanEffect() {
        updateEffect(null)
    }

    fun updateEffect() {
        updateEffect(createTremoloPicking())
    }

    fun updateEffect(effect: TGEffectTremoloPicking?) {
        val processor = TGActionProcessor(findContext(), TGChangeTremoloPickingAction.NAME)
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, getMeasure())
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, getBeat())
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, getString())
        processor.setAttribute(TGChangeTremoloPickingAction.ATTRIBUTE_EFFECT, effect)
        processor.process()
    }

    fun getSongManager(): TGSongManager =
        requireNotNull(getAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER))

    fun getMeasure(): TGMeasure? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE)
    fun getBeat(): TGBeat? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT)
    fun getNote(): TGNote? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE)
    fun getString(): TGString? = getAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING)
}
