package app.tuxguitar.android.view.tablature

import app.tuxguitar.android.action.impl.gui.TGOpenCabMenuAction
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.activity.TGActivityController
import app.tuxguitar.android.menu.controller.TGMenuController
import app.tuxguitar.android.menu.controller.impl.smart.TGSelectedBeatMenu
import app.tuxguitar.android.menu.controller.impl.smart.TGSelectedMeasureMenu
import app.tuxguitar.android.menu.controller.impl.smart.TGSelectedNoteMenu
import app.tuxguitar.android.menu.controller.impl.smart.TGSelectedTrackMenu
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.player.base.MidiPlayer
import app.tuxguitar.song.models.TGNote
import app.tuxguitar.util.TGAbstractContext

class TGSongViewSmartMenu(private val controller: TGSongViewController) {

    fun openCabMenuAction(activity: TGActivity, menuController: TGMenuController) {
        val tgActionProcessor = TGActionProcessor(this.controller.context, TGOpenCabMenuAction.NAME)
        tgActionProcessor.setAttribute(TGOpenCabMenuAction.ATTRIBUTE_MENU_ACTIVITY, activity)
        tgActionProcessor.setAttribute(TGOpenCabMenuAction.ATTRIBUTE_MENU_CONTROLLER, menuController)
        tgActionProcessor.setAttribute(TGOpenCabMenuAction.ATTRIBUTE_MENU_SELECTABLE_VIEW, null)
        tgActionProcessor.process()
    }

    fun openSmartMenu(context: TGAbstractContext) {
        if (!MidiPlayer.getInstance(this.controller.context).isRunning) {
            val activity = TGActivityController.getInstance(this.controller.context).activity
            if (activity != null) {
                if (java.lang.Boolean.TRUE == context.getAttribute(TRACK_AREA_SELECTED)) {
                    this.openCabMenuAction(activity, TGSelectedTrackMenu(activity))
                } else if (java.lang.Boolean.TRUE == context.getAttribute(MEASURE_AREA_SELECTED)) {
                    this.openCabMenuAction(activity, TGSelectedMeasureMenu(activity))
                } else {
                    val note = this.controller.caret.selectedNote
                    if (note != null) {
                        this.openCabMenuAction(activity, TGSelectedNoteMenu(activity))
                    } else {
                        this.openCabMenuAction(activity, TGSelectedBeatMenu(activity))
                    }
                }
            }
        }
    }

    companion object {
        const val REQUEST_SMART_MENU = "requestSmartMenu"
        const val TRACK_AREA_SELECTED = "trackAreaSelected"
        const val MEASURE_AREA_SELECTED = "measureAreaSelected"
    }
}
