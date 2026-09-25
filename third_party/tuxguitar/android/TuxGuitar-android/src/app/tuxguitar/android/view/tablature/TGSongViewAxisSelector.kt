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
import kotlin.math.abs
import kotlin.math.min

class TGSongViewAxisSelector(private val controller: TGSongViewController) {
    fun select(x: Float, y: Float, requestSmartMenu: Boolean): Boolean {
        if (x < 0 || y < 0) return false
        val track = findSelectedTrack(y) ?: return false
        val measure = findSelectedMeasure(track, x, y) ?: return false
        val beat = findSelectedBeat(measure, x) ?: return false
        val string = findSelectedString(measure, y) ?: controller.caret.selectedString
        val smartMenuProperties =
            if (requestSmartMenu) findSmartMenuProperties(track, measure, beat, x) else null
        callMoveTo(track, measure, beat, string, smartMenuProperties)
        return true
    }

    private fun findSelectedTrack(y: Float): TGTrackImpl? {
        val layout = controller.layout
        val number = layout.getTrackNumberAt(y)
        return if (number >= 0) {
            layout.songManager.getTrack(controller.song, number) as? TGTrackImpl
        } else {
            null
        }
    }

    private fun findSelectedMeasure(track: TGTrackImpl, x: Float, y: Float): TGMeasureImpl? {
        var selectedMeasure: TGMeasureImpl? = null
        var minorDistance = 0f
        val measures = track.measures
        while (measures.hasNext()) {
            val measure = measures.next() as TGMeasureImpl
            if (!measure.isOutOfBounds && measure.ts != null) {
                val isAtX = x >= measure.posX &&
                    x <= measure.posX + measure.getWidth(controller.layout) + measure.spacing
                if (isAtX) {
                    val height = measure.ts.size
                    val distanceY = min(
                        abs(y - measure.posY),
                        abs(y - (measure.posY + height - 10))
                    )
                    if (selectedMeasure == null || distanceY < minorDistance) {
                        selectedMeasure = measure
                        minorDistance = distanceY
                    }
                }
            }
        }
        return selectedMeasure
    }

    private fun findSelectedBeat(measure: TGMeasureImpl, x: Float): TGBeatImpl? {
        val layout = controller.layout
        val voice = controller.caret.voice
        val positionX = measure.headerImpl.getLeftSpacing(layout) + measure.posX
        var bestDifference = -1f
        var bestBeat: TGBeatImpl? = null
        val beats = measure.beats.iterator()
        while (beats.hasNext()) {
            val beat = beats.next() as TGBeatImpl
            if (!beat.getVoice(voice).isEmpty) {
                val difference = abs(x - (positionX + beat.posX + beat.getSpacing(layout)))
                if (bestDifference == -1f || difference < bestDifference) {
                    bestBeat = beat
                    bestDifference = difference
                }
            }
        }
        return bestBeat ?: (layout.songManager.measureManager.getFirstBeat(measure.beats) as? TGBeatImpl)
    }

    private fun findSelectedString(measure: TGMeasureImpl, y: Float): TGString? {
        var selectedString: TGString? = null
        var minorDistance = 0f
        val stringSpacing = controller.layout.stringSpacing
        val firstStringY = measure.posY + measure.ts!!.getPosition(TGTrackSpacing.POSITION_TABLATURE)
        val strings = measure.track.strings.iterator()
        while (strings.hasNext()) {
            val currentString = strings.next()
            val distance = abs(
                y - (firstStringY + (currentString.number * stringSpacing) - stringSpacing)
            )
            if (selectedString == null || distance < minorDistance) {
                selectedString = currentString
                minorDistance = distance
            }
        }
        return selectedString
    }

    private fun findSmartMenuProperties(
        track: TGTrackImpl,
        measure: TGMeasureImpl,
        beat: TGBeat,
        x: Float
    ): Map<String, Any> {
        val properties = mutableMapOf<String, Any>()
        properties[TGSongViewSmartMenu.REQUEST_SMART_MENU] = true
        val layout = controller.layout
        val measureX1 = measure.posX
        val measureX2 = measureX1 + measure.getWidth(layout) + measure.spacing
        val noteWidth = ((layout.stringSpacing - (2f * layout.scale)) * 2) / 2f
        val leftX1 = measureX1
        val leftX2 = leftX1 + measure.headerImpl.getLeftSpacing(layout) +
            measure.getFirstNoteSpacing(layout) - noteWidth
        val rightX1 = measureX2 - measure.headerImpl.getRightSpacing(layout) + noteWidth
        val rightX2 = measureX2
        if ((x >= leftX1 && x <= leftX2) || (x >= rightX1 && x <= rightX2)) {
            properties[TGSongViewSmartMenu.MEASURE_AREA_SELECTED] = true
        }
        return properties
    }

    private fun callMoveTo(
        track: TGTrackImpl,
        measure: TGMeasureImpl,
        beat: TGBeatImpl,
        string: TGString?,
        smartMenuProperties: Map<String, Any>?
    ) {
        val processor = TGActionProcessor(controller.context, TGMoveToAction.NAME)
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, track)
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, measure)
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_BEAT, beat)
        processor.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_STRING, string)
        smartMenuProperties?.forEach { (key, value) -> processor.setAttribute(key, value) }
        processor.processOnNewThread()
    }
}
