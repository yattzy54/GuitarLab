package app.tuxguitar.android.menu.controller.impl.smart

import android.view.Menu
import android.view.MenuInflater
import app.tuxguitar.android.R
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.menu.controller.TGMenuBase
import app.tuxguitar.android.view.dialog.bend.TGBendDialogController
import app.tuxguitar.android.view.dialog.grace.TGGraceDialogController
import app.tuxguitar.android.view.dialog.harmonic.TGHarmonicDialogController
import app.tuxguitar.android.view.dialog.pickstroke.TGPickStrokeDialogController
import app.tuxguitar.android.view.dialog.stroke.TGStrokeDialogController
import app.tuxguitar.android.view.dialog.text.TGTextDialogController
import app.tuxguitar.android.view.dialog.tremoloBar.TGTremoloBarDialogController
import app.tuxguitar.android.view.dialog.tremoloPicking.TGTremoloPickingDialogController
import app.tuxguitar.android.view.dialog.trill.TGTrillDialogController
import app.tuxguitar.android.view.tablature.TGSongViewController
import app.tuxguitar.editor.action.effect.*
import app.tuxguitar.editor.action.note.*

class TGSelectedNoteMenu(activity: TGActivity) : TGMenuBase(activity) {
    override fun inflate(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_selected_note, menu)
        initializeItems(menu)
    }

    fun initializeItems(menu: Menu) {
        val note = TGSongViewController.getInstance(findContext()).caret.selectedNote
        initializeItem(menu, R.id.action_change_vibrato, createActionProcessor(TGChangeVibratoNoteAction.NAME), true)
        initializeItem(menu, R.id.action_change_dead_note, createActionProcessor(TGChangeDeadNoteAction.NAME), true)
        initializeItem(menu, R.id.action_change_slide, createActionProcessor(TGChangeSlideNoteAction.NAME), true)
        initializeItem(menu, R.id.action_change_hammer, createActionProcessor(TGChangeHammerNoteAction.NAME), true)
        initializeItem(menu, R.id.action_change_ghost_note, createActionProcessor(TGChangeGhostNoteAction.NAME), true)
        initializeItem(menu, R.id.action_change_accentuated_note, createActionProcessor(TGChangeAccentuatedNoteAction.NAME), true)
        initializeItem(menu, R.id.action_change_heavy_accentuated_note, createActionProcessor(TGChangeHeavyAccentuatedNoteAction.NAME), true)
        initializeItem(menu, R.id.action_change_palm_mute, createActionProcessor(TGChangePalmMuteAction.NAME), true)
        initializeItem(menu, R.id.action_change_let_ring, createActionProcessor(TGChangeLetRingAction.NAME), true)
        initializeItem(menu, R.id.action_change_staccato, createActionProcessor(TGChangeStaccatoAction.NAME), true)
        initializeItem(menu, R.id.action_change_tapping, createActionProcessor(TGChangeTappingAction.NAME), true)
        initializeItem(menu, R.id.action_change_slapping, createActionProcessor(TGChangeSlappingAction.NAME), true)
        initializeItem(menu, R.id.action_change_popping, createActionProcessor(TGChangePoppingAction.NAME), true)
        initializeItem(menu, R.id.action_change_fade_in, createActionProcessor(TGChangeFadeInAction.NAME), true)
        initializeItem(menu, R.id.action_change_bend, TGBendDialogController(), true)
        initializeItem(menu, R.id.action_change_tremolo_bar, TGTremoloBarDialogController(), true)
        initializeItem(menu, R.id.action_change_grace, TGGraceDialogController(), true)
        initializeItem(menu, R.id.action_change_harmonic, TGHarmonicDialogController(), true)
        initializeItem(menu, R.id.action_change_trill, TGTrillDialogController(), true)
        initializeItem(menu, R.id.action_change_tremolo_picking, TGTremoloPickingDialogController(), true)
        initializeItem(menu, R.id.action_change_tied_note, createActionProcessor(TGChangeTiedNoteAction.NAME), true, note?.isTiedNote == true)
        initializeItem(menu, R.id.action_change_text, TGTextDialogController(), true)
        initializeItem(menu, R.id.action_clean_beat, createActionProcessor(TGCleanBeatAction.NAME), true)
        initializeItem(menu, R.id.action_move_beats_left, createActionProcessor(TGMoveBeatsLeftAction.NAME), true)
        initializeItem(menu, R.id.action_move_beats_right, createActionProcessor(TGMoveBeatsRightAction.NAME), true)
        initializeItem(menu, R.id.action_set_voice_auto, createActionProcessor(TGSetVoiceAutoAction.NAME), true)
        initializeItem(menu, R.id.action_set_voice_down, createActionProcessor(TGSetVoiceDownAction.NAME), true)
        initializeItem(menu, R.id.action_set_voice_up, createActionProcessor(TGSetVoiceUpAction.NAME), true)
        initializeItem(menu, R.id.action_change_stroke, TGStrokeDialogController(), true)
        initializeItem(menu, R.id.action_change_pickstroke, TGPickStrokeDialogController(), true)
    }
}
