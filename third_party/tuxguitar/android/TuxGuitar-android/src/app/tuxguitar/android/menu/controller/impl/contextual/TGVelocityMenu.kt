package app.tuxguitar.android.menu.controller.impl.contextual

import android.view.Menu
import android.view.MenuInflater
import app.tuxguitar.android.R
import app.tuxguitar.android.action.TGActionProcessorListener
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.menu.controller.TGMenuBase
import app.tuxguitar.android.view.tablature.TGSongViewController
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.editor.action.note.TGChangeVelocityAction
import app.tuxguitar.player.base.MidiPlayer
import app.tuxguitar.song.models.TGVelocities

class TGVelocityMenu(activity: TGActivity) : TGMenuBase(activity) {
    override fun inflate(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_velocity, menu)
        initializeItems(menu)
    }

    fun initializeItems(menu: Menu) {
        val context = findContext()
        val caret = TGSongViewController.getInstance(context).caret
        val note = caret.selectedNote
        val selection = note?.velocity ?: caret.velocity
        val running = MidiPlayer.getInstance(context).isRunning
        initializeVelocityItem(menu, R.id.action_set_velocity_ppp, TGVelocities.PIANO_PIANISSIMO, selection, running)
        initializeVelocityItem(menu, R.id.action_set_velocity_pp, TGVelocities.PIANISSIMO, selection, running)
        initializeVelocityItem(menu, R.id.action_set_velocity_p, TGVelocities.PIANO, selection, running)
        initializeVelocityItem(menu, R.id.action_set_velocity_mp, TGVelocities.MEZZO_PIANO, selection, running)
        initializeVelocityItem(menu, R.id.action_set_velocity_mf, TGVelocities.MEZZO_FORTE, selection, running)
        initializeVelocityItem(menu, R.id.action_set_velocity_f, TGVelocities.FORTE, selection, running)
        initializeVelocityItem(menu, R.id.action_set_velocity_ff, TGVelocities.FORTISSIMO, selection, running)
        initializeVelocityItem(menu, R.id.action_set_velocity_fff, TGVelocities.FORTE_FORTISSIMO, selection, running)
    }

    fun initializeVelocityItem(menu: Menu, id: Int, value: Int, selection: Int, playerRunning: Boolean) {
        initializeItem(menu, id, createVelocityActionProcessor(value), !playerRunning, value == selection)
    }

    fun createVelocityActionProcessor(velocity: Int): TGActionProcessorListener =
        createActionProcessor(TGChangeVelocityAction.NAME).apply {
            setAttribute(TGDocumentContextAttributes.ATTRIBUTE_VELOCITY, velocity)
        }
}
