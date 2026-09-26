package app.tuxguitar.android.drawer.main

import app.tuxguitar.song.models.TGTrack

data class TGMainDrawerTrackListItem(
    val track: TGTrack,
    val label: String,
    val selected: Boolean,
)
