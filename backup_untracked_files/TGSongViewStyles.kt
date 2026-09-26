package app.tuxguitar.android.view.tablature

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import app.tuxguitar.graphics.control.TGLayoutStyles
import app.tuxguitar.ui.resource.UIColorModel
import app.tuxguitar.ui.resource.UIFontModel
import com.mmt.guitarlab.core.ui.theme.ElectricAmber
import com.mmt.guitarlab.core.ui.theme.ElectricRuby
import com.mmt.guitarlab.core.ui.theme.ElectricTeal
import com.mmt.guitarlab.core.ui.theme.StudioCardElevated
import com.mmt.guitarlab.core.ui.theme.StudioDarkBg
import com.mmt.guitarlab.core.ui.theme.StudioTextPrimary
import com.mmt.guitarlab.core.ui.theme.StudioTextSecondary

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
        this.setForegroundColor(StudioTextPrimary.toUIColorModel())
        this.setBackgroundColor(StudioDarkBg.toUIColorModel())
        this.setBackgroundColorPlaying(StudioCardElevated.toUIColorModel())
        this.setLineColor(StudioTextSecondary.toUIColorModel())
        this.setLineColorInvalid(ElectricRuby.toUIColorModel())
        this.setScoreNoteColor(StudioTextPrimary.toUIColorModel())
        this.setTabNoteColor(StudioTextPrimary.toUIColorModel())
        this.setPlayNoteColor(ElectricAmber.toUIColorModel())
        this.setLoopSMarkerColor(ElectricTeal.toUIColorModel())
        this.setLoopEMarkerColor(ElectricTeal.toUIColorModel())
        this.setMeasureNumberColor(ElectricAmber.toUIColorModel())
    }
}

private fun Color.toUIColorModel(): UIColorModel {
    val argb = toArgb()
    return UIColorModel((argb shr 16) and 0xff, (argb shr 8) and 0xff, argb and 0xff)
}
