package app.tuxguitar.android.menu.controller.impl.smart

import android.view.Menu
import android.view.MenuInflater
import app.tuxguitar.android.R
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.menu.controller.TGMenuBase
import app.tuxguitar.android.view.dialog.clef.TGClefDialogController
import app.tuxguitar.android.view.dialog.keySignature.TGKeySignatureDialogController
import app.tuxguitar.android.view.dialog.repeat.TGRepeatAlternativeDialogController
import app.tuxguitar.android.view.dialog.repeat.TGRepeatCloseDialogController
import app.tuxguitar.android.view.dialog.tempo.TGTempoDialogController
import app.tuxguitar.android.view.dialog.timeSignature.TGTimeSignatureDialogController
import app.tuxguitar.android.view.dialog.tripletFeel.TGTripletFeelDialogController
import app.tuxguitar.editor.action.composition.TGRepeatOpenAction

class TGSelectedMeasureMenu(activity: TGActivity) : TGMenuBase(activity) {
    override fun inflate(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_selected_measure, menu)
        initializeItems(menu)
    }

    fun initializeItems(menu: Menu) {
        initializeItem(menu, R.id.action_change_tempo, TGTempoDialogController(), true)
        initializeItem(menu, R.id.action_change_clef, TGClefDialogController(), true)
        initializeItem(menu, R.id.action_change_key_signature, TGKeySignatureDialogController(), true)
        initializeItem(menu, R.id.action_change_time_signature, TGTimeSignatureDialogController(), true)
        initializeItem(menu, R.id.action_change_triplet_feel, TGTripletFeelDialogController(), true)
        initializeItem(menu, R.id.action_change_repeat_alternative, TGRepeatAlternativeDialogController(), true)
        initializeItem(menu, R.id.action_change_repeat_close, TGRepeatCloseDialogController(), true)
        initializeItem(menu, R.id.action_change_repeat_open, createActionProcessor(TGRepeatOpenAction.NAME), true)
    }
}
