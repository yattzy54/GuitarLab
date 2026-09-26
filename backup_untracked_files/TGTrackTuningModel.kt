package app.tuxguitar.android.view.dialog.track

class TGTrackTuningModel {
    var value: Int? = null

    fun getName(): String = TGTrackTuningLabel.valueOf(value)
}
