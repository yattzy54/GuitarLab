package app.tuxguitar.android.menu.controller.impl.contextual

import android.view.Menu
import android.view.MenuInflater
import app.tuxguitar.android.R
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.menu.controller.TGMenuBase
import app.tuxguitar.android.view.dialog.bend.TGBendDialogController
import app.tuxguitar.android.view.dialog.grace.TGGraceDialogController
import app.tuxguitar.android.view.dialog.harmonic.TGHarmonicDialogController
import app.tuxguitar.android.view.dialog.tremoloBar.TGTremoloBarDialogController
import app.tuxguitar.android.view.dialog.tremoloPicking.TGTremoloPickingDialogController
import app.tuxguitar.android.view.dialog.trill.TGTrillDialogController
import app.tuxguitar.android.view.tablature.TGSongViewController
import app.tuxguitar.editor.action.effect.*
import app.tuxguitar.player.base.MidiPlayer

class TGEffectMenu(activity: TGActivity) : TGMenuBase(activity) {
    override fun inflate(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_effect, menu)
        initializeItems(menu)
    }

    fun initializeItems(menu: Menu) {
        val context = findContext()
        val note = TGSongViewController.getInstance(context).caret.selectedNote
        val enabled = !MidiPlayer.getInstance(context).isRunning && note != null
        initializeItem(menu, R.id.action_change_vibrato, createActionProcessor(TGChangeVibratoNoteAction.NAME), enabled)
        initializeItem(menu, R.id.action_change_dead_note, createActionProcessor(TGChangeDeadNoteAction.NAME), enabled)
        initializeItem(menu, R.id.action_change_slide, createActionProcessor(TGChangeSlideNoteAction.NAME), enabled)
        initializeItem(menu, R.id.action_change_hammer, createActionProcessor(TGChangeHammerNoteAction.NAME), enabled)
        initializeItem(menu, R.id.action_change_ghost_note, createActionProcessor(TGChangeGhostNoteAction.NAME), enabled)
        initializeItem(menu, R.id.action_change_accentuated_note, createActionProcessor(TGChangeAccentuatedNoteAction.NAME), enabled)
        initializeItem(menu, R.id.action_change_heavy_accentuated_note, createActionProcessor(TGChangeHeavyAccentuatedNoteAction.NAME), enabled)
        initializeItem(menu, R.id.action_change_palm_mute, createActionProcessor(TGChangePalmMuteAction.NAME), enabled)
        initializeItem(menu, R.id.action_change_let_ring, createActionProcessor(TGChangeLetRingAction.NAME), enabled)
        initializeItem(menu, R.id.action_change_staccato, createActionProcessor(TGChangeStaccatoAction.NAME), enabled)
        initializeItem(menu, R.id.action_change_tapping, createActionProcessor(TGChangeTappingAction.NAME), enabled)
        initializeItem(menu, R.id.action_change_slapping, createActionProcessor(TGChangeSlappingAction.NAME), enabled)
        initializeItem(menu, R.id.action_change_popping, createActionProcessor(TGChangePoppingAction.NAME), enabled)
        initializeItem(menu, R.id.action_change_fade_in, createActionProcessor(TGChangeFadeInAction.NAME), enabled)
        initializeItem(menu, R.id.action_change_bend, TGBendDialogController(), enabled)
        initializeItem(menu, R.id.action_change_tremolo_bar, TGTremoloBarDialogController(), enabled)
        initializeItem(menu, R.id.action_change_grace, TGGraceDialogController(), enabled)
        initializeItem(menu, R.id.action_change_harmonic, TGHarmonicDialogController(), enabled)
        initializeItem(menu, R.id.action_change_trill, TGTrillDialogController(), enabled)
        initializeItem(menu, R.id.action_change_tremolo_picking, TGTremoloPickingDialogController(), enabled)
    }
}
