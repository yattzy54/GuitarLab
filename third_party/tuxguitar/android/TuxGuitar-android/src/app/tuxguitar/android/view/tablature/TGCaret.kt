package app.tuxguitar.android.view.tablature

import app.tuxguitar.android.util.MidiTickUtil
import app.tuxguitar.graphics.control.TGBeatImpl
import app.tuxguitar.graphics.control.TGLayout
import app.tuxguitar.graphics.control.TGMeasureImpl
import app.tuxguitar.graphics.control.TGTrackImpl
import app.tuxguitar.graphics.control.TGTrackSpacing
import app.tuxguitar.player.base.MidiPlayer
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
class TGCaret(private val tablature: TGSongViewController) {
    lateinit var track: TGTrackImpl
    lateinit var measure: TGMeasureImpl
    @JvmField var selectedBeat: TGBeat? = null
    @JvmField var selectedVoice: TGVoice? = null
    @JvmField var selectedNote: TGNote? = null
    val songManager: TGSongManager = tablature.songManager
    val song: TGSong = tablature.song
    var duration: TGDuration = songManager.factory.newDuration()
    lateinit var selectedString: TGString
    @JvmField var position = 0L
    @JvmField var stringNumber = 1
    @JvmField var voice = 0
    @JvmField var velocity = TGVelocities.DEFAULT
    private var restBeat = false
    private var changes = false
    private var hasSelection = false
    private var color1: UIColor? = null
    private var color2: UIColor? = null

    @Synchronized
    fun update() {
        val trackNumber = track?.number ?: 1
        update(trackNumber, position, stringNumber)
    }

    @Synchronized
    fun update(trackNumber: Int) {
        update(trackNumber, position, stringNumber)
    }

    @Synchronized
    fun update(trackNumber: Int, position: Long, string: Int) {
        update(trackNumber, position, string, getVelocity())
    }

    @Synchronized
    fun update(trackNumber: Int, position: Long, string: Int, velocity: Int) {
        val context = tablature.context
        val midiPlayer = MidiPlayer.getInstance(context)
        val realPosition =
            if (midiPlayer.isRunning) MidiTickUtil.getStart(context, midiPlayer.tickPosition) else position
        val track = findTrack(trackNumber)
        val measure =
            if (hasSelection &&
                songManager.isFreeEditionMode(this.measure) &&
                this.measure.track.number == this.track.number
            ) {
                this.measure
            } else {
                findMeasure(realPosition, track)
            }
        val beat = findBeat(realPosition, measure)
        if (track != null && measure != null && beat != null) {
            moveTo(track, measure, beat, string)
        }
        setVelocity(velocity)
    }

    fun moveTo(
        selectedTrack: TGTrackImpl,
        selectedMeasure: TGMeasureImpl,
        selectedBeat: TGBeat,
        string: Int
    ) {
        this.track = selectedTrack
        this.measure = selectedMeasure
        this.selectedBeat = selectedBeat
        hasSelection = true
        stringNumber = string
        updatePosition()
        updateDuration()
        updateString()
        updateNote()
        updateVoice()
        updateBeat()
        setChanges(true)
    }

    private fun findTrack(number: Int): TGTrackImpl? {
        return (songManager.getTrack(song, number) as? TGTrackImpl)
            ?: songManager.getFirstTrack(song) as? TGTrackImpl
    }

    private fun findMeasure(position: Long, track: TGTrackImpl?): TGMeasureImpl? {
        if (track == null) return null
        return (songManager.trackManager.getMeasureAt(track, position) as? TGMeasureImpl)
            ?: songManager.trackManager.getFirstMeasure(track) as? TGMeasureImpl
    }

    private fun findBeat(position: Long, measure: TGMeasureImpl?): TGBeat? {
        if (measure == null) return null
        val manager = songManager.measureManager
        val voiceAtPosition = manager.getVoiceIn(measure, position, getVoice())
        return voiceAtPosition?.beat ?: manager.getFirstBeat(measure.beats)
    }

    @Synchronized
    fun goToTickPosition() {
        val context = tablature.context
        val midiPlayer = MidiPlayer.getInstance(context)
        val start = MidiTickUtil.getStart(context, midiPlayer.tickPosition)
        update(track!!.number, start, stringNumber)
        setChanges(true)
    }

    fun paintCaret(layout: TGLayout, painter: UIPainter) {
        if (MidiPlayer.getInstance(tablature.context).isRunning) return
        val measure = measure ?: return
        val selectedBeat = selectedBeat as? TGBeatImpl ?: return
        if (measure.isOutOfBounds) return
        if ((layout.style and TGLayout.DISPLAY_TABLATURE) != 0) {
            val expectedVoice = selectedNote == null || selectedNote!!.voice.index == getVoice()
            val stringSpacing = tablature.layout.stringSpacing
            val leftSpacing = selectedBeat.measureImpl.headerImpl.getLeftSpacing(layout)
            val width = (stringSpacing - (3f * layout.scale)) * 2
            val height = (stringSpacing - (3f * layout.scale)) * 2
            val xMargin = width / 2f - (2f * layout.scale)
            val yMargin = height / 2f
            val x = measure.posX + selectedBeat.posX + selectedBeat.getSpacing(layout) +
                leftSpacing - xMargin
            val y = measure.posY + measure.ts!!.getPosition(TGTrackSpacing.POSITION_TABLATURE) +
                (stringNumber * stringSpacing) - stringSpacing - yMargin
            setPaintStyle(painter, expectedVoice)
            painter.initPath()
            painter.setAntialias(false)
            painter.addRectangle(x, y, width, height)
            painter.closePath()
        } else if ((layout.style and TGLayout.DISPLAY_SCORE) != 0) {
            val line = tablature.layout.scoreLineSpacing
            val leftSpacing = selectedBeat.measureImpl.headerImpl.getLeftSpacing(layout)
            val xMargin = 2f * layout.scale
            val x1 = measure.posX + selectedBeat.posX + selectedBeat.getSpacing(layout) +
                leftSpacing - xMargin
            val x2 = x1 + layout.scoreNoteWidth + xMargin
            val y1 = measure.posY + measure.ts!!.getPosition(TGTrackSpacing.POSITION_TOP) - line
            val y2 = measure.posY + measure.ts!!.getPosition(TGTrackSpacing.POSITION_BOTTOM)
            setPaintStyle(painter, true)
            painter.initPath()
            painter.moveTo(x1, y1)
            painter.lineTo(x1 + ((x2 - x1) / 2f), y1 + line / 2f)
            painter.lineTo(x2, y1)
            painter.moveTo(x1, y2 + line)
            painter.lineTo(x1 + ((x2 - x1) / 2f), y2 + line / 2f)
            painter.lineTo(x2, y2 + line)
            painter.closePath()
        }
    }

    fun setPaintStyle(painter: UIPainter, expectedVoice: Boolean) {
        val foreground = if (expectedVoice) color1 else color2
        if (foreground != null) painter.setForeground(foreground)
    }

    fun moveRight(): Boolean {
        if (getSelectedBeat() != null) {
            var measure = measure
            var voiceAtPosition = songManager.measureManager.getNextVoice(
                measure.beats,
                getSelectedBeat()!!,
                getVoice()
            )
            var beat = voiceAtPosition?.beat
            if (beat == null) {
                measure = songManager.trackManager.getNextMeasure(measure) as? TGMeasureImpl
                    ?: return false
                voiceAtPosition = songManager.measureManager.getFirstVoice(measure.beats, getVoice())
                beat = voiceAtPosition?.beat
                if (beat == null) {
                    beat = songManager.measureManager.getFirstBeat(measure.beats)
                }
            }
            if (beat != null) {
                moveTo(track, measure, beat, getStringNumber())
            }
        }
        return true
    }

    fun moveLeft() {
        if (getSelectedBeat() != null) {
            var measure = measure
            var voiceAtPosition = songManager.measureManager.getPreviousVoice(
                measure.beats,
                getSelectedBeat()!!,
                getVoice()
            )
            var beat = voiceAtPosition?.beat
            if (beat == null) {
                measure = songManager.trackManager.getPrevMeasure(measure) as? TGMeasureImpl
                    ?: return
                voiceAtPosition = songManager.measureManager.getLastVoice(measure.beats, getVoice())
                beat = voiceAtPosition?.beat
                if (beat == null) beat = songManager.measureManager.getFirstBeat(measure.beats)
            }
            if (beat != null) moveTo(track, measure, beat, getStringNumber())
        }
    }

    private fun updateDuration() {
        val beat = selectedBeat
        if (beat != null && !beat.getVoice(getVoice()).isRestVoice) {
            duration.copyFrom(beat.getVoice(getVoice()).duration)
        }
    }

    fun moveUp() {
        val count = track.stringCount()
        setStringNumber(((stringNumber - 2 + count) % count) + 1)
    }

    fun moveDown() {
        val count = track.stringCount()
        setStringNumber((stringNumber % count) + 1)
    }

    fun setStringNumber(number: Int) {
        stringNumber = number
        updateNote()
    }

    fun getStringNumber(): Int = stringNumber
    fun getPosition(): Long = position
    fun setSelectedDuration(selectedDuration: TGDuration) {
        this.duration = selectedDuration
    }

    private fun updatePosition() {
        position = selectedBeat!!.start
    }

    private fun updateString() {
        if (stringNumber < 1 || stringNumber > track.stringCount()) {
            stringNumber = 1
        }
        val strings: List<*> = track.strings
        val iterator = strings.iterator()
        while (iterator.hasNext()) {
            val instrumentString = iterator.next() as TGString
            if (instrumentString.number == stringNumber) {
                selectedString = instrumentString
            }
        }
    }

    fun hasChanges(): Boolean = changes
    fun setChanges(changes: Boolean) {
        this.changes = changes
    }
    fun getVelocity(): Int = velocity
    fun setVelocity(velocity: Int) {
        this.velocity = velocity
    }

    private fun updateNote() {
        selectedNote = null
        val string = selectedString
        selectedNote = songManager.measureManager.getNote(measure, getPosition(), string.number)
    }

    fun getSelectedNote(): TGNote? = selectedNote

    private fun updateBeat() {
        restBeat = selectedBeat!!.isRestBeat
    }

    fun getSelectedBeat(): TGBeatImpl? = selectedBeat as? TGBeatImpl
    private fun updateVoice() {
        selectedVoice = getSelectedBeat()!!.getVoice(getVoice())
    }

    fun getSelectedVoice(): TGVoice? = selectedVoice
    fun getVoice(): Int = voice
    fun setVoice(voice: Int) {
        this.voice = voice
        update()
    }
    fun isRestBeatSelected(): Boolean = restBeat

    fun setColor1(colorModel: UIColorModel) {
        disposeResource(color1)
        color1 = tablature.resourceFactory.createColor(colorModel)
    }

    fun setColor2(colorModel: UIColorModel) {
        disposeResource(color2)
        color2 = tablature.resourceFactory.createColor(colorModel)
    }

    fun disposeResource(resource: UIResource?) {
        if (resource != null && !resource.isDisposed) resource.dispose()
    }

    fun dispose() {
        disposeResource(color1)
        disposeResource(color2)
    }
}
