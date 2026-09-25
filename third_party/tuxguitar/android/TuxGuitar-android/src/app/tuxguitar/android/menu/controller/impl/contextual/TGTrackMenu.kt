package app.tuxguitar.android.menu.controller.impl.contextual

import android.view.Menu
import android.view.MenuInflater
import app.tuxguitar.android.R
import app.tuxguitar.android.action.impl.track.*
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.menu.controller.TGMenuBase
import app.tuxguitar.android.view.dialog.track.*
import app.tuxguitar.android.view.tablature.TGSongViewController
import app.tuxguitar.editor.action.track.*
import app.tuxguitar.player.base.MidiPlayer

class TGTrackMenu(activity: TGActivity) : TGMenuBase(activity) {
    override fun inflate(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_track, menu)
        initializeItems(menu)
    }

    fun initializeItems(menu: Menu) {
        val context = findContext()
        val caret = TGSongViewController.getInstance(context).caret
        val track = caret.track
        val trackCount = track.song.countTracks()
        val isFirst = track.number == 1
        val isLast = track.number == trackCount
        val running = MidiPlayer.getInstance(context).isRunning
        val percussion = caret.songManager.isPercussionChannel(caret.song, track.channelId)
        initializeItem(menu, R.id.action_track_first, createActionProcessor(TGGoFirstTrackAction.NAME), !isFirst)
        initializeItem(menu, R.id.action_track_previous, createActionProcessor(TGGoPreviousTrackAction.NAME), !isFirst)
        initializeItem(menu, R.id.action_track_next, createActionProcessor(TGGoNextTrackAction.NAME), !isLast)
        initializeItem(menu, R.id.action_track_last, createActionProcessor(TGGoLastTrackAction.NAME), !isLast)
        initializeItem(menu, R.id.action_track_add, createActionProcessor(TGAddNewTrackAction.NAME), !running)
        initializeItem(menu, R.id.action_track_clone, createActionProcessor(TGCloneTrackAction.NAME), !running)
        initializeItem(menu, R.id.action_track_remove, createActionProcessor(TGRemoveTrackAction.NAME), !running)
        initializeItem(menu, R.id.action_track_move_up, createActionProcessor(TGMoveTrackUpAction.NAME), !running)
        initializeItem(menu, R.id.action_track_move_down, createActionProcessor(TGMoveTrackDownAction.NAME), !running)
        initializeItem(menu, R.id.action_track_change_solo, createActionProcessor(TGChangeTrackSoloAction.NAME), !running, track.isSolo)
        initializeItem(menu, R.id.action_track_change_mute, createActionProcessor(TGChangeTrackMuteAction.NAME), !running, track.isMute)
        initializeItem(menu, R.id.action_track_set_name, TGTrackNameDialogController(), !running)
        initializeItem(menu, R.id.action_track_set_channel, TGTrackChannelDialogController(), !running)
        if (percussion) {
            initializeItem(menu, R.id.action_track_change_string_count, TGTrackStringCountDialogController(), !running)
        } else {
            initializeItem(menu, R.id.action_track_change_tuning, TGTrackTuningDialogController(), !running)
        }
    }
}
