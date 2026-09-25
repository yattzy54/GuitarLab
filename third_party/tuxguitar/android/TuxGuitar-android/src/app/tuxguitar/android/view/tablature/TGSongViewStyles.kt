package app.tuxguitar.android.view.tablature

import app.tuxguitar.graphics.control.TGLayoutStyles
import app.tuxguitar.ui.resource.UIColorModel
import app.tuxguitar.ui.resource.UIFontModel

class TGSongViewStyles : TGLayoutStyles() {

    init {
        this.setBufferEnabled(false)

        this.setTrackSpacing(5f)
        this.setFirstTrackSpacing(15f)
        this.setFirstMeasureSpacing(5f)

        this.setStringSpacing(10f)
        this.setScoreLineSpacing(8f)

        this.setMinBufferSeparator(20f)
        this.setMinTopSpacing(10f)
        this.setMinScoreTabSpacing(5f)

        this.setFirstNoteSpacing(10f)
        this.setMeasureLeftSpacing(15f)
        this.setMeasureRightSpacing(15f)
        this.setClefSpacing(30f)
        this.setKeySignatureSpacing(15f)
        this.setTimeSignatureSpacing(15f)

        this.setChordFretIndexSpacing(8f)
        this.setChordStringSpacing(5f)
        this.setChordFretSpacing(6f)
        this.setChordNoteSize(4f)
        this.setChordLineWidth(0f)
        this.setRepeatEndingSpacing(20f)
        this.setTextSpacing(15f)
        this.setMarkerSpacing(15f)
        this.setLoopMarkerSpacing(5f)
        this.setDivisionTypeSpacing(10f)
        this.setPickStrokeSpacing(8f)
        this.setBendSpacing(8f)
        this.setEffectSpacing(8f)

        this.setLineWidths(floatArrayOf(0f, 1f, 2f, 3f, 4f, 5f))
        this.setDurationWidths(floatArrayOf(30f, 25f, 21f, 20f, 19f, 18f))

        this.setDefaultFont(UIFontModel("sans-serif", 8f, false, false))
        this.setNoteFont(UIFontModel("sans-serif", 9f, true, false))
        this.setLyricFont(UIFontModel("sans-serif", 8f, false, false))
        this.setTextFont(UIFontModel("sans-serif", 8f, false, false))
        this.setMarkerFont(UIFontModel("sans-serif", 8f, false, false))
        this.setGraceFont(UIFontModel("sans-serif", 6f, false, false))
        this.setChordFont(UIFontModel("sans-serif", 8f, false, false))
        this.setChordFretFont(UIFontModel("sans-serif", 8f, false, false))
        this.setForegroundColor(UIColorModel(0, 0, 0))
        this.setBackgroundColor(UIColorModel(255, 255, 255))
        this.setLineColor(UIColorModel(200, 200, 200))
        this.setLineColorInvalid(UIColorModel(205, 0, 0))
        this.setScoreNoteColor(UIColorModel(105, 105, 105))
        this.setTabNoteColor(UIColorModel(105, 105, 105))
        this.setPlayNoteColor(UIColorModel(255, 0, 0))
        this.setLoopSMarkerColor(UIColorModel(0, 0, 0))
        this.setLoopEMarkerColor(UIColorModel(0, 0, 0))
        this.setMeasureNumberColor(UIColorModel(255, 0, 0))
    }
}
