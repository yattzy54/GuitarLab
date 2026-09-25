package app.tuxguitar.android.menu.controller.impl.contextual

import android.view.Menu
import android.view.MenuInflater
import app.tuxguitar.android.R
import app.tuxguitar.android.action.impl.layout.TGToggleHighlightPlayedBeatAction
import app.tuxguitar.android.action.impl.transport.*
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.menu.controller.TGMenuBase
import app.tuxguitar.android.view.dialog.transport.TGTransportModeDialogController
import app.tuxguitar.android.view.tablature.TGSongViewController
import app.tuxguitar.editor.action.transport.TGTransportCountDownAction
import app.tuxguitar.editor.action.transport.TGTransportMetronomeAction
import app.tuxguitar.graphics.control.TGLayout
import app.tuxguitar.player.base.MidiPlayer

class TGTransportMenu(activity: TGActivity) : TGMenuBase(activity) {
    override fun inflate(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_transport, menu)
        initializeItems(menu)
    }

    fun initializeItems(menu: Menu) {
        val context = findContext()
        val measureNumber = TGSongViewController.getInstance(context).caret.measure.number
        val midiPlayer = MidiPlayer.getInstance(context)
        val playerMode = midiPlayer.mode
        val layoutStyle = TGSongViewController.getInstance(context).layout.style
        initializeItem(menu, R.id.action_transport_play, createActionProcessor(TGTransportPlayAction.NAME), true)
        initializeItem(menu, R.id.action_transport_stop, createActionProcessor(TGTransportStopAction.NAME), midiPlayer.isRunning)
        initializeItem(menu, R.id.action_transport_metronome, createActionProcessor(TGTransportMetronomeAction.NAME), true, midiPlayer.isMetronomeEnabled)
        initializeItem(menu, R.id.action_transport_count_down, createActionProcessor(TGTransportCountDownAction.NAME), true, midiPlayer.countDown.isEnabled)
        initializeItem(menu, R.id.action_transport_mode, TGTransportModeDialogController(), true)
        initializeItem(menu, R.id.action_transport_set_loop_start, createActionProcessor(TGTransportSetLoopSHeaderAction.NAME), playerMode.isLoop, measureNumber == playerMode.loopSHeader)
        initializeItem(menu, R.id.action_transport_set_loop_end, createActionProcessor(TGTransportSetLoopEHeaderAction.NAME), playerMode.isLoop, measureNumber == playerMode.loopEHeader)
        initializeItem(menu, R.id.action_transport_highlight_played_beat, createActionProcessor(TGToggleHighlightPlayedBeatAction.NAME), true, layoutStyle and TGLayout.HIGHLIGHT_PLAYED_BEAT != 0)
    }
}
