package app.tuxguitar.android.menu.controller.impl.contextual

import android.view.Menu
import android.view.MenuInflater
import app.tuxguitar.android.R
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.menu.controller.TGMenuBase
import app.tuxguitar.android.view.dialog.clef.TGClefDialogController
import app.tuxguitar.android.view.dialog.info.TGSongInfoDialogController
import app.tuxguitar.android.view.dialog.keySignature.TGKeySignatureDialogController
import app.tuxguitar.android.view.dialog.repeat.TGRepeatAlternativeDialogController
import app.tuxguitar.android.view.dialog.repeat.TGRepeatCloseDialogController
import app.tuxguitar.android.view.dialog.tempo.TGTempoDialogController
import app.tuxguitar.android.view.dialog.timeSignature.TGTimeSignatureDialogController
import app.tuxguitar.android.view.dialog.tripletFeel.TGTripletFeelDialogController
import app.tuxguitar.editor.action.composition.TGRepeatOpenAction
import app.tuxguitar.player.base.MidiPlayer

class TGCompositionMenu(activity: TGActivity) : TGMenuBase(activity) {
    override fun inflate(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_composition, menu)
        initializeItems(menu)
    }

    fun initializeItems(menu: Menu) {
        val enabled = !MidiPlayer.getInstance(findContext()).isRunning
        initializeItem(menu, R.id.action_change_tempo, TGTempoDialogController(), enabled)
        initializeItem(menu, R.id.action_change_clef, TGClefDialogController(), enabled)
        initializeItem(menu, R.id.action_change_key_signature, TGKeySignatureDialogController(), enabled)
        initializeItem(menu, R.id.action_change_time_signature, TGTimeSignatureDialogController(), enabled)
        initializeItem(menu, R.id.action_change_triplet_feel, TGTripletFeelDialogController(), enabled)
        initializeItem(menu, R.id.action_change_properties, TGSongInfoDialogController(), enabled)
        initializeItem(menu, R.id.action_change_repeat_alternative, TGRepeatAlternativeDialogController(), enabled)
        initializeItem(menu, R.id.action_change_repeat_close, TGRepeatCloseDialogController(), enabled)
        initializeItem(menu, R.id.action_change_repeat_open, createActionProcessor(TGRepeatOpenAction.NAME), enabled)
    }
}
