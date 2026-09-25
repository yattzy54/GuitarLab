package app.tuxguitar.android.menu.controller.impl.smart

import android.view.Menu
import android.view.MenuInflater
import app.tuxguitar.android.R
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.menu.controller.TGMenuBase
import app.tuxguitar.android.view.dialog.track.*
import app.tuxguitar.android.view.tablature.TGSongViewController
import app.tuxguitar.editor.action.track.*

class TGSelectedTrackMenu(activity: TGActivity) : TGMenuBase(activity) {
    override fun inflate(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_selected_track, menu)
        initializeItems(menu)
    }

    fun initializeItems(menu: Menu) {
        val caret = TGSongViewController.getInstance(findContext()).caret
        val song = caret.song
        val track = caret.track
        initializeItem(menu, R.id.action_track_clone, createActionProcessor(TGCloneTrackAction.NAME), true)
        initializeItem(menu, R.id.action_track_change_solo, createActionProcessor(TGChangeTrackSoloAction.NAME), true, track.isSolo)
        initializeItem(menu, R.id.action_track_change_mute, createActionProcessor(TGChangeTrackMuteAction.NAME), true, track.isMute)
        initializeItem(menu, R.id.action_track_set_name, TGTrackNameDialogController(), true)
        initializeItem(menu, R.id.action_track_set_channel, TGTrackChannelDialogController(), true)
        if (song.countTracks() > 1) {
            initializeItem(menu, R.id.action_track_remove, createActionProcessor(TGRemoveTrackAction.NAME), true)
            initializeItem(menu, R.id.action_track_move_up, createActionProcessor(TGMoveTrackUpAction.NAME), true)
            initializeItem(menu, R.id.action_track_move_down, createActionProcessor(TGMoveTrackDownAction.NAME), true)
        }
        if (caret.songManager.isPercussionChannel(song, track.channelId)) {
            initializeItem(menu, R.id.action_track_change_string_count, TGTrackStringCountDialogController(), true)
        } else {
            initializeItem(menu, R.id.action_track_change_tuning, TGTrackTuningDialogController(), true)
        }
    }
}
