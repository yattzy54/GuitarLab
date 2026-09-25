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
        val processor = TGActionProcessor(controller.context, TGOpenCabMenuAction.NAME)
        processor.setAttribute(TGOpenCabMenuAction.ATTRIBUTE_MENU_ACTIVITY, activity)
        processor.setAttribute(TGOpenCabMenuAction.ATTRIBUTE_MENU_CONTROLLER, menuController)
        processor.setAttribute(TGOpenCabMenuAction.ATTRIBUTE_MENU_SELECTABLE_VIEW, null)
        processor.process()
    }

    fun openSmartMenu(context: TGAbstractContext) {
        if (MidiPlayer.getInstance(controller.context).isRunning) return
        val activity = TGActivityController.getInstance(controller.context).activity ?: return
        when {
            context.getAttribute<Boolean>(TRACK_AREA_SELECTED) == true ->
                openCabMenuAction(activity, TGSelectedTrackMenu(activity))
            context.getAttribute<Boolean>(MEASURE_AREA_SELECTED) == true ->
                openCabMenuAction(activity, TGSelectedMeasureMenu(activity))
            controller.caret.getSelectedNote() != null ->
                openCabMenuAction(activity, TGSelectedNoteMenu(activity))
            else -> openCabMenuAction(activity, TGSelectedBeatMenu(activity))
        }
    }

    companion object {
        const val REQUEST_SMART_MENU = "requestSmartMenu"
        const val TRACK_AREA_SELECTED = "trackAreaSelected"
        const val MEASURE_AREA_SELECTED = "measureAreaSelected"
    }
}
