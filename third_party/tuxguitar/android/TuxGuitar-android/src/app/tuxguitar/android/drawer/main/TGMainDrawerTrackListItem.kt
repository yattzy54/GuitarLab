package app.tuxguitar.android.drawer.main

import app.tuxguitar.song.models.TGTrack

class TGMainDrawerTrackListItem {
    private var track: TGTrack? = null
    private var label: String? = null
    private var selected: Boolean? = null

    fun getTrack(): TGTrack? = track
    fun setTrack(track: TGTrack?) {
        this.track = track
    }

    fun getLabel(): String? = label
    fun setLabel(label: String?) {
        this.label = label
    }

    fun getSelected(): Boolean? = selected
    fun setSelected(selected: Boolean?) {
        this.selected = selected
    }
}
