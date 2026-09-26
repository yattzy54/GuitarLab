package app.tuxguitar.android.menu.controller.impl.contextual

import android.view.Menu
import android.view.MenuInflater
import app.tuxguitar.android.R
import app.tuxguitar.android.action.impl.layout.TGSetChordDiagramEnabledAction
import app.tuxguitar.android.action.impl.layout.TGSetChordNameEnabledAction
import app.tuxguitar.android.action.impl.layout.TGSetScoreEnabledAction
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.menu.controller.TGMenuBase
import app.tuxguitar.android.view.tablature.TGSongViewController
import app.tuxguitar.graphics.control.TGLayout

class TGViewMenu(activity: TGActivity) : TGMenuBase(activity) {
    override fun inflate(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_view, menu)
        initializeItems(menu)
    }

    fun initializeItems(menu: Menu) {
        val style = TGSongViewController.getInstance(findContext()).layout.style
        initializeItem(menu, R.id.action_view_layout_show_score, createActionProcessor(TGSetScoreEnabledAction.NAME), true, style and TGLayout.DISPLAY_SCORE != 0)
        initializeItem(menu, R.id.action_view_layout_show_chord_name, createActionProcessor(TGSetChordNameEnabledAction.NAME), true, style and TGLayout.DISPLAY_CHORD_NAME != 0)
        initializeItem(menu, R.id.action_view_layout_show_chord_diagram, createActionProcessor(TGSetChordDiagramEnabledAction.NAME), true, style and TGLayout.DISPLAY_CHORD_DIAGRAM != 0)
    }
}
