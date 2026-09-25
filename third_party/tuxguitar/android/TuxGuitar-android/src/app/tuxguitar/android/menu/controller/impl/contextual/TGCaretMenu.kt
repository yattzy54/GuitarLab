package app.tuxguitar.android.menu.controller.impl.contextual

import android.view.Menu
import android.view.MenuInflater
import app.tuxguitar.android.R
import app.tuxguitar.android.action.impl.caret.TGGoDownAction
import app.tuxguitar.android.action.impl.caret.TGGoLeftAction
import app.tuxguitar.android.action.impl.caret.TGGoRightAction
import app.tuxguitar.android.action.impl.caret.TGGoUpAction
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.menu.controller.TGMenuBase
import app.tuxguitar.player.base.MidiPlayer

class TGCaretMenu(activity: TGActivity) : TGMenuBase(activity) {
    override fun inflate(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_caret, menu)
        initializeItems(menu)
    }

    fun initializeItems(menu: Menu) {
        val running = MidiPlayer.getInstance(findContext()).isRunning
        initializeItem(menu, R.id.action_go_left, createActionProcessor(TGGoLeftAction.NAME), !running)
        initializeItem(menu, R.id.action_go_right, createActionProcessor(TGGoRightAction.NAME), !running)
        initializeItem(menu, R.id.action_go_up, createActionProcessor(TGGoUpAction.NAME), !running)
        initializeItem(menu, R.id.action_go_down, createActionProcessor(TGGoDownAction.NAME), !running)
    }
}
