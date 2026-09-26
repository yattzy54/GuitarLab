package app.tuxguitar.android.menu.controller.impl.contextual

import android.view.Menu
import android.view.MenuInflater
import app.tuxguitar.android.R
import app.tuxguitar.android.action.impl.edit.TGSetVoice1Action
import app.tuxguitar.android.action.impl.edit.TGSetVoice2Action
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.menu.controller.TGMenuBase
import app.tuxguitar.android.view.dialog.measure.TGMeasureCopyDialogController
import app.tuxguitar.android.view.dialog.measure.TGMeasurePasteDialogController
import app.tuxguitar.editor.action.edit.TGRedoAction
import app.tuxguitar.editor.action.edit.TGUndoAction
import app.tuxguitar.editor.clipboard.TGClipboard
import app.tuxguitar.player.base.MidiPlayer

class TGEditMenu(activity: TGActivity) : TGMenuBase(activity) {
    override fun inflate(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_edit, menu)
        initializeItems(menu)
    }

    fun initializeItems(menu: Menu) {
        val context = findContext()
        val enabled = !MidiPlayer.getInstance(context).isRunning
        initializeItem(menu, R.id.action_undo, createActionProcessor(TGUndoAction.NAME), enabled)
        initializeItem(menu, R.id.action_redo, createActionProcessor(TGRedoAction.NAME), enabled)
        initializeItem(menu, R.id.action_voice_1, createActionProcessor(TGSetVoice1Action.NAME), true)
        initializeItem(menu, R.id.action_voice_2, createActionProcessor(TGSetVoice2Action.NAME), true)
        initializeItem(menu, R.id.action_copy, TGMeasureCopyDialogController(), enabled)
        initializeItem(menu, R.id.action_paste, TGMeasurePasteDialogController(), enabled && TGClipboard.getInstance(context).segment != null)
    }
}
