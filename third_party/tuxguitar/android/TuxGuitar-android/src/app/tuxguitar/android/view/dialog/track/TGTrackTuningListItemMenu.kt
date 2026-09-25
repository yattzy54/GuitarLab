package app.tuxguitar.android.view.dialog.track

import android.view.Menu
import android.view.MenuInflater
import app.tuxguitar.android.R
import app.tuxguitar.android.menu.controller.TGMenuBase

class TGTrackTuningListItemMenu(
    private val dialog: TGTrackTuningDialog,
    private val model: TGTrackTuningModel
) : TGMenuBase(dialog.findActivity()) {
    override fun inflate(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_track_tuning_list_item, menu)
        initializeItems(menu)
    }

    fun initializeItems(menu: Menu) {
        initializeItem(
            menu,
            R.id.action_track_tuning_list_item_edit,
            dialog.actionHandler.createEditTuningModelAction(model),
            true
        )
        initializeItem(
            menu,
            R.id.action_track_tuning_list_item_remove,
            dialog.actionHandler.createRemoveTuningModelAction(model),
            true
        )
    }
}
