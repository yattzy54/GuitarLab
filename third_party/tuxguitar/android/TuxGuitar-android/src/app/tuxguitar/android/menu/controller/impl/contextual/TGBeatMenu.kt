package app.tuxguitar.android.menu.controller.impl.contextual

import android.view.Menu
import android.view.MenuInflater
import app.tuxguitar.android.R
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.menu.controller.TGMenuBase
import app.tuxguitar.android.view.dialog.pickstroke.TGPickStrokeDialogController
import app.tuxguitar.android.view.dialog.stroke.TGStrokeDialogController
import app.tuxguitar.android.view.dialog.text.TGTextDialogController
import app.tuxguitar.android.view.tablature.TGSongViewController
import app.tuxguitar.editor.action.note.*
import app.tuxguitar.player.base.MidiPlayer

class TGBeatMenu(activity: TGActivity) : TGMenuBase(activity) {
    override fun inflate(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_beat, menu)
        initializeItems(menu)
    }

    fun initializeItems(menu: Menu) {
        val context = findContext()
        val caret = TGSongViewController.getInstance(context).caret
        val note = caret.selectedNote
        val restBeat = caret.isRestBeatSelected()
        val running = MidiPlayer.getInstance(context).isRunning
        initializeItem(menu, R.id.action_change_tied_note, createActionProcessor(TGChangeTiedNoteAction.NAME), !running, note?.isTiedNote == true)
        initializeItem(menu, R.id.action_clean_beat, createActionProcessor(TGCleanBeatAction.NAME), !running)
        initializeItem(menu, R.id.action_decrement_note_semitone, createActionProcessor(TGDecrementNoteSemitoneAction.NAME), !running && note != null)
        initializeItem(menu, R.id.action_delete_note_or_rest, createActionProcessor(TGDeleteNoteOrRestAction.NAME), !running)
        initializeItem(menu, R.id.action_increment_note_semitone, createActionProcessor(TGIncrementNoteSemitoneAction.NAME), !running && note != null)
        initializeItem(menu, R.id.action_insert_rest_beat, createActionProcessor(TGInsertRestBeatAction.NAME), !running)
        initializeItem(menu, R.id.action_move_beats_left, createActionProcessor(TGMoveBeatsLeftAction.NAME), !running)
        initializeItem(menu, R.id.action_move_beats_right, createActionProcessor(TGMoveBeatsRightAction.NAME), !running)
        initializeItem(menu, R.id.action_set_voice_auto, createActionProcessor(TGSetVoiceAutoAction.NAME), !running && !restBeat)
        initializeItem(menu, R.id.action_set_voice_down, createActionProcessor(TGSetVoiceDownAction.NAME), !running && !restBeat)
        initializeItem(menu, R.id.action_set_voice_up, createActionProcessor(TGSetVoiceUpAction.NAME), !running && !restBeat)
        initializeItem(menu, R.id.action_shift_note_down, createActionProcessor(TGShiftNoteDownAction.NAME), !running && note != null)
        initializeItem(menu, R.id.action_shift_note_up, createActionProcessor(TGShiftNoteUpAction.NAME), !running && note != null)
        initializeItem(menu, R.id.action_change_stroke, TGStrokeDialogController(), !running)
        initializeItem(menu, R.id.action_change_pickstroke, TGPickStrokeDialogController(), !running)
        initializeItem(menu, R.id.action_change_text, TGTextDialogController(), !running)
    }
}
