package app.tuxguitar.android.view.tablature

import app.tuxguitar.android.action.impl.caret.TGMoveToAction
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.graphics.control.TGBeatImpl
import app.tuxguitar.graphics.control.TGLayout
import app.tuxguitar.graphics.control.TGMeasureImpl
import app.tuxguitar.graphics.control.TGTrackImpl
import app.tuxguitar.graphics.control.TGTrackSpacing
import app.tuxguitar.song.models.TGBeat
import app.tuxguitar.song.models.TGString
import java.util.HashMap

class TGSongViewAxisSelector(private val controller: TGSongViewController) {

    fun select(x: Float, y: Float, requestSmartMenu: Boolean): Boolean {
        if (x >= 0 && y >= 0) {
            val track = findSelectedTrack(y)
            if (track != null) {
                val measure = findSelectedMeasure(track, x, y)
                if (measure != null) {
                    val beat = findSelectedBeat(measure, x)
                    if (beat != null) {
                        var string = findSelectedString(measure, y)
                        if (string == null) {
                            string = this.controller.caret.selectedString
                        }
                        if (string == null) {
                            return false
                        }

                        var smartMenuProperties: Map<String, Any>? = null
                        if (requestSmartMenu) {
                            smartMenuProperties = this.findSmartMenuProperties(track, measure, beat, x)
                        }

                        this.callMoveTo(track, measure, beat, string, smartMenuProperties)
                        return true
                    }
                }
            }
        }
        return false
    }

    private fun findSelectedTrack(y: Float): TGTrackImpl? {
        val layout: TGLayout = this.controller.layout
        val number = layout.getTrackNumberAt(y)
        if (number >= 0) {
            return layout.songManager.getTrack(this.controller.song, number) as TGTrackImpl
        }
        return null
    }

    private fun findSelectedMeasure(track: TGTrackImpl, x: Float, y: Float): TGMeasureImpl? {
        var measure: TGMeasureImpl? = null
        var minorDistance = 0f

        val iterator = track.measures
        while (iterator.hasNext()) {
            val m = iterator.next() as TGMeasureImpl
            if (!m.isOutOfBounds && m.ts != null) {
                val isAtX = x >= m.posX && x <= m.posX + m.getWidth(this.controller.layout) + m.spacing
                if (isAtX) {
                    val measureHeight = m.ts.size
                    val distanceY = Math.min(Math.abs(y - m.posY), Math.abs(y - (m.posY + measureHeight - 10)))
                    if (measure == null || distanceY < minorDistance) {
                        measure = m
                        minorDistance = distanceY
                    }
                }
            }
        }
        return measure
    }

    private fun findSelectedBeat(measure: TGMeasureImpl, x: Float): TGBeatImpl? {
        val layout = this.controller.layout
        val voice = this.controller.caret.getVoice()
        val posX = measure.headerImpl.getLeftSpacing(layout) + measure.posX
        var bestDiff = -1f
        var bestBeat: TGBeatImpl? = null
        val iterator = measure.beats.iterator()
        while (iterator.hasNext()) {
            val beat = iterator.next() as TGBeatImpl
            if (!beat.getVoice(voice).isEmpty) {
                val diff = Math.abs(x - (posX + (beat.posX + beat.getSpacing(layout))))
                if (bestDiff == -1f || diff < bestDiff) {
                    bestBeat = beat
                    bestDiff = diff
                }
            }
        }
        if (bestBeat == null) {
            bestBeat = layout.songManager.measureManager.getFirstBeat(measure.beats) as TGBeatImpl?
        }
        return bestBeat
    }

    private fun findSelectedString(measure: TGMeasureImpl, y: Float): TGString? {
        var string: TGString? = null
        val stringSpacing = this.controller.layout.stringSpacing
        var minorDistance = 0f
        val firstStringY = measure.posY + measure.ts.getPosition(TGTrackSpacing.POSITION_TABLATURE)

        val iterator = measure.track.strings.iterator()
        while (iterator.hasNext()) {
            val currString = iterator.next() as TGString
            val distanceX = Math.abs(y - (firstStringY + ((currString.number * stringSpacing) - stringSpacing)))
            if (string == null || distanceX < minorDistance) {
                string = currString
                minorDistance = distanceX
            }
        }
        return string
    }

    private fun findSmartMenuProperties(track: TGTrackImpl, measure: TGMeasureImpl, beat: TGBeat, x: Float): Map<String, Any> {
        val map = HashMap<String, Any>()
        map[TGSongViewSmartMenu.REQUEST_SMART_MENU] = true

        val layout = this.controller.layout
        val measureX1 = measure.posX
        val measureX2 = measureX1 + measure.getWidth(layout) + measure.spacing
        val noteWidth = (((layout.stringSpacing - (2.0f * layout.scale)) * 2) / 2f)

        val leftX1 = measureX1
        val leftX2 = leftX1 + measure.headerImpl.getLeftSpacing(layout) + measure.getFirstNoteSpacing(layout) - noteWidth
        val rightX1 = measureX2 - measure.headerImpl.getRightSpacing(layout) + noteWidth
        val rightX2 = measureX2
        if (x >= leftX1 && x <= leftX2 || x >= rightX1 && x <= rightX2) {
            map[TGSongViewSmartMenu.MEASURE_AREA_SELECTED] = true
        }
        return map
    }

    private fun callMoveTo(track: TGTrackImpl, measure: TGMeasureImpl, beat: TGBeat, string: TGString, smartMenuProperties: Map<String, Any>?) {
        val tgActionProcessor = TGActionProcessor(this.controller.context, TGMoveToAction.NAME)
        tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track)
        tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, measure)
        tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, beat)
        tgActionProcessor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, string)

        if (smartMenuProperties != null) {
            for ((key, value) in smartMenuProperties) {
                tgActionProcessor.setAttribute(key, value)
            }
        }
        tgActionProcessor.processOnNewThread()
    }
}
