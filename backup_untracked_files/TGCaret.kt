package app.tuxguitar.android.view.tablature

import app.tuxguitar.android.util.MidiTickUtil
import app.tuxguitar.graphics.control.TGBeatImpl
import app.tuxguitar.graphics.control.TGLayout
import app.tuxguitar.graphics.control.TGMeasureImpl
import app.tuxguitar.graphics.control.TGTrackImpl
import app.tuxguitar.graphics.control.TGTrackSpacing
import app.tuxguitar.player.base.MidiPlayer
import app.tuxguitar.song.managers.TGMeasureManager
import app.tuxguitar.song.managers.TGSongManager
import app.tuxguitar.song.models.TGBeat
import app.tuxguitar.song.models.TGDuration
import app.tuxguitar.song.models.TGNote
import app.tuxguitar.song.models.TGSong
import app.tuxguitar.song.models.TGString
import app.tuxguitar.song.models.TGVelocities
import app.tuxguitar.song.models.TGVoice
import app.tuxguitar.ui.resource.UIColor
import app.tuxguitar.ui.resource.UIColorModel
import app.tuxguitar.ui.resource.UIPainter
import app.tuxguitar.ui.resource.UIResource
import app.tuxguitar.util.TGContext
import java.util.Iterator

class TGCaret(private val tablature: TGSongViewController) {
    private var selectedTrack: TGTrackImpl? = null
    private var selectedMeasure: TGMeasureImpl? = null
    var selectedBeat: TGBeat? = null
    var selectedVoice: TGVoice? = null
    var selectedNote: TGNote? = null
    private var selectedDuration: TGDuration = getSongManager().getFactory().newDuration()
    var selectedString: TGString? = null
    var position: Long = 0
    private var string = 1
    private var voice = 0
    var velocity: Int = TGVelocities.DEFAULT
    private var restBeat = false
    private var changes = false
    private var color1: UIColor? = null
    private var color2: UIColor? = null

    val track: TGTrackImpl?
        get() = selectedTrack

    val measure: TGMeasureImpl?
        get() = selectedMeasure

    var duration: TGDuration
        get() = selectedDuration
        set(value) {
            selectedDuration = value
        }

    var stringNumber: Int
        get() = string
        set(value) {
            string = value
            updateNote()
        }

    @Synchronized
    fun update() {
        val trackNumber = if (selectedTrack != null) selectedTrack!!.number else 1
        update(trackNumber, position, string)
    }

    @Synchronized
    fun update(trackNumber: Int) {
        update(trackNumber, position, string)
    }

    @Synchronized
    fun update(trackNumber: Int, position: Long, string: Int) {
        update(trackNumber, position, string, velocity)
    }

    @Synchronized
    fun update(trackNumber: Int, position: Long, string: Int, velocity: Int) {
        val context: TGContext = tablature.context
        val midiPlayer = MidiPlayer.getInstance(context)
        val realPosition = if (midiPlayer.isRunning) MidiTickUtil.getStart(context, midiPlayer.tickPosition) else position
        val track = findTrack(trackNumber)
        var measure: TGMeasureImpl? = null
        val selectedMeasureLocal = selectedMeasure
        if (selectedMeasureLocal != null && getSongManager().isFreeEditionMode(selectedMeasureLocal) && selectedMeasureLocal.track.number == selectedTrack?.number) {
            measure = selectedMeasureLocal
        } else {
            measure = findMeasure(realPosition, track)
        }
        val beat = findBeat(realPosition, measure)
        if (track != null && measure != null && beat != null) {
            moveTo(track, measure, beat, string)
        }
        this.velocity = velocity
    }

    fun moveTo(selectedTrack: TGTrackImpl, selectedMeasure: TGMeasureImpl, selectedBeat: TGBeat, string: Int) {
        this.selectedTrack = selectedTrack
        this.selectedMeasure = selectedMeasure
        this.selectedBeat = selectedBeat
        this.string = string
        updatePosition()
        updateDuration()
        updateString()
        updateNote()
        updateVoice()
        updateBeat()
        setChanges(true)
    }

    private fun findTrack(number: Int): TGTrackImpl? {
        var track = getSongManager().getTrack(getSong(), number) as TGTrackImpl?
        if (track == null) {
            track = getSongManager().getFirstTrack(getSong()) as TGTrackImpl?
        }
        return track
    }

    private fun findMeasure(position: Long, track: TGTrackImpl?): TGMeasureImpl? {
        var measure: TGMeasureImpl? = null
        if (track != null) {
            measure = getSongManager().trackManager.getMeasureAt(track, position) as TGMeasureImpl?
            if (measure == null) {
                measure = getSongManager().trackManager.getFirstMeasure(track) as TGMeasureImpl?
            }
        }
        return measure
    }

    private fun findBeat(position: Long, measure: TGMeasureImpl?): TGBeat? {
        var beat: TGBeat? = null
        if (measure != null) {
            val manager = getSongManager().measureManager
            val voice = manager.getVoiceIn(measure, position, getVoice())
            if (voice != null) {
                beat = voice.beat
            }
            if (beat == null) {
                beat = manager.getFirstBeat(measure.beats)
            }
        }
        return beat
    }

    @Synchronized
    fun goToTickPosition() {
        val context: TGContext = tablature.context
        val midiPlayer = MidiPlayer.getInstance(context)
        val start = MidiTickUtil.getStart(context, midiPlayer.tickPosition)
        update(selectedTrack?.number ?: 1, start, string)
        setChanges(true)
    }

    fun paintCaret(layout: TGLayout, painter: UIPainter) {
        if (!MidiPlayer.getInstance(tablature.context).isRunning) {
            val measure = selectedMeasure
            if (measure != null && !measure.isOutOfBounds && selectedBeat is TGBeatImpl) {
                val beat = selectedBeat as TGBeatImpl
                if (layout.style and TGLayout.DISPLAY_TABLATURE != 0) {
                    val expectedVoice = (selectedNote == null || selectedNote!!.voice.index == getVoice())
                    val stringSpacing = tablature.layout.stringSpacing
                    val leftSpacing = beat.measureImpl.headerImpl.getLeftSpacing(layout)
                    val width = (stringSpacing - 3.0f * layout.scale) * 2
                    val height = (stringSpacing - 3.0f * layout.scale) * 2
                    val xMargin = width / 2.0f - 2.0f * layout.scale
                    val yMargin = height / 2.0f
                    val x = measure.posX + beat.posX + beat.getSpacing(layout) + leftSpacing - xMargin
                    val y = measure.posY + measure.ts.getPosition(TGTrackSpacing.POSITION_TABLATURE) + (string * stringSpacing - stringSpacing) - yMargin
                    setPaintStyle(painter, expectedVoice)
                    painter.initPath()
                    painter.setAntialias(false)
                    painter.addRectangle(x, y, width, height)
                    painter.closePath()
                } else if (layout.style and TGLayout.DISPLAY_SCORE != 0) {
                    val line = tablature.layout.scoreLineSpacing
                    val leftSpacing = beat.measureImpl.headerImpl.getLeftSpacing(layout)
                    val xMargin = 2.0f * layout.scale
                    val x1 = measure.posX + beat.posX + beat.getSpacing(layout) + leftSpacing - xMargin
                    val x2 = x1 + layout.scoreNoteWidth + xMargin
                    val y1 = measure.posY + measure.ts.getPosition(TGTrackSpacing.POSITION_TOP) - line
                    val y2 = measure.posY + measure.ts.getPosition(TGTrackSpacing.POSITION_BOTTOM)
                    setPaintStyle(painter, true)
                    painter.initPath()
                    painter.moveTo(x1, y1)
                    painter.lineTo(x1 + (x2 - x1) / 2f, y1 + line / 2f)
                    painter.lineTo(x2, y1)
                    painter.moveTo(x1, y2 + line)
                    painter.lineTo(x1 + (x2 - x1) / 2f, y2 + line / 2f)
                    painter.lineTo(x2, y2 + line)
                    painter.closePath()
                }
            }
        }
    }

    fun setPaintStyle(painter: UIPainter, expectedVoice: Boolean) {
        val foreground = if (expectedVoice) color1 else color2
        if (foreground != null) {
            painter.setForeground(foreground)
        }
    }

    fun moveRight(): Boolean {
        val selectedBeat = selectedBeat as? TGBeatImpl ?: return true
        val measure = measure ?: return true
        var voice = getSongManager().measureManager.getNextVoice(measure.beats, selectedBeat, getVoice())
        var beat: TGBeat? = if (voice != null) voice.beat else null
        if (beat == null) {
            var nextMeasure = getSongManager().trackManager.getNextMeasure(measure) as TGMeasureImpl?
            if (nextMeasure == null) return false
            voice = getSongManager().measureManager.getFirstVoice(nextMeasure.beats, getVoice())
            beat = if (voice != null) voice.beat else null
            if (beat == null) {
                beat = getSongManager().measureManager.getFirstBeat(nextMeasure.beats)
            }
            if (beat != null) {
                moveTo(track ?: return false, nextMeasure, beat, stringNumber)
            }
            return true
        }
        moveTo(track ?: return false, measure, beat, stringNumber)
        return true
    }

    fun moveLeft(): Boolean {
        val selectedBeat = selectedBeat as? TGBeatImpl ?: return true
        val measure = measure ?: return true
        var voice = getSongManager().measureManager.getPreviousVoice(measure.beats, selectedBeat, getVoice())
        var beat: TGBeat? = if (voice != null) voice.beat else null
        if (beat == null) {
            val prevMeasure = getSongManager().trackManager.getPrevMeasure(measure) as TGMeasureImpl?
            if (prevMeasure == null) return false
            voice = getSongManager().measureManager.getLastVoice(prevMeasure.beats, getVoice())
            beat = if (voice != null) voice.beat else null
            if (beat == null) {
                beat = getSongManager().measureManager.getFirstBeat(prevMeasure.beats)
            }
            if (beat != null) {
                moveTo(track ?: return false, prevMeasure, beat, stringNumber)
            }
            return true
        }
        moveTo(track ?: return false, measure, beat, stringNumber)
        return true
    }

    private fun updateDuration() {
        val beat = selectedBeat ?: return
        if (!beat.getVoice(getVoice()).isRestVoice) {
            selectedDuration.copyFrom(beat.getVoice(getVoice()).duration)
        }
    }

    fun moveUp() {
        val stringCount = selectedTrack?.stringCount() ?: 0
        if (stringCount <= 0) return
        val nextString = (((string - 2 + stringCount) % stringCount) + 1)
        stringNumber = nextString
    }

    fun moveDown() {
        val stringCount = selectedTrack?.stringCount() ?: 0
        if (stringCount <= 0) return
        val nextString = ((string % stringCount) + 1)
        stringNumber = nextString
    }

    fun setSelectedDuration(selectedDuration: TGDuration) {
        this.selectedDuration = selectedDuration
    }

    private fun updatePosition() {
        position = (selectedBeat as? TGBeatImpl)?.start ?: 0
    }

    private fun updateString() {
        if (string < 1 || selectedTrack == null || string > selectedTrack!!.stringCount()) {
            string = 1
        }
        for (instrumentString in selectedTrack?.strings ?: emptyList<TGString>()) {
            if (instrumentString.number == string) {
                selectedString = instrumentString
            }
        }
    }

    fun hasChanges(): Boolean = changes

    fun setChanges(changes: Boolean) {
        this.changes = changes
    }

    private fun updateNote() {
        selectedNote = null
        val string = selectedString
        if (string != null) {
            selectedNote = getSongManager().measureManager.getNote(measure, position, string.number)
        }
    }

    private fun updateBeat() {
        restBeat = selectedBeat?.isRestBeat == true
    }

    fun getSongManager(): TGSongManager = tablature.songManager

    fun getSong(): TGSong = tablature.song

    private fun updateVoice() {
        selectedVoice = (selectedBeat as? TGBeatImpl)?.getVoice(getVoice())
    }

    fun getVoice(): Int = voice

    fun setVoice(voice: Int) {
        this.voice = voice
        update()
    }

    fun isRestBeatSelected(): Boolean = restBeat

    fun setColor1(cm: UIColorModel) {
        disposeResource(color1)
        color1 = tablature.resourceFactory.createColor(cm)
    }

    fun setColor2(cm: UIColorModel) {
        disposeResource(color2)
        color2 = tablature.resourceFactory.createColor(cm)
    }

    fun disposeResource(resource: UIResource?) {
        if (resource != null && !resource.isDisposed) {
            resource.dispose()
        }
    }

    fun dispose() {
        disposeResource(color1)
        disposeResource(color2)
    }
}
