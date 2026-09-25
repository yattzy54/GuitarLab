package app.tuxguitar.android.menu.controller.impl.smart

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

class TGSelectedBeatMenu(activity: TGActivity) : TGMenuBase(activity) {
    override fun inflate(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_selected_beat, menu)
        initializeItems(menu)
    }

    fun initializeItems(menu: Menu) {
        val note = TGSongViewController.getInstance(findContext()).caret.selectedNote
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
