package app.tuxguitar.android.transport

import app.tuxguitar.android.util.MidiTickUtil
import app.tuxguitar.android.view.tablature.TGCaret
import app.tuxguitar.android.view.tablature.TGSongViewController
import app.tuxguitar.document.TGDocumentManager
import app.tuxguitar.graphics.control.TGBeatImpl
import app.tuxguitar.graphics.control.TGMeasureImpl
import app.tuxguitar.player.base.MidiPlayer
import app.tuxguitar.song.managers.TGSongManager
import app.tuxguitar.song.models.TGBeat
import app.tuxguitar.song.models.TGDuration
import app.tuxguitar.song.models.TGMeasure
import app.tuxguitar.song.models.TGTrack
import app.tuxguitar.util.TGContext

class TGTransportCache(private val context: TGContext) {
    private var playTrack = 0
    private var playTick = 0L
    private var playStart = 0L
    private var playBeatEnd = 0L
    private var playChanges = false
    private var playUpdate = false
    private var currentPlayBeat: TGBeatImpl? = null
    private var currentPlayMeasure: TGMeasureImpl? = null

    @get:JvmName("getPlayBeatProperty")
    val playBeat: TGBeatImpl?
        get() = currentPlayBeat

    @get:JvmName("getPlayMeasureProperty")
    val playMeasure: TGMeasureImpl?
        get() = currentPlayMeasure

    init {
        reset()
    }

    fun reset() {
        currentPlayBeat = null
        currentPlayMeasure = null
        playUpdate = false
        playChanges = false
        playTrack = 0
        playTick = 0
        playStart = 0
        playBeatEnd = 0
    }

    fun updatePlayMode() {
        playUpdate = true
        getPlayBeat()
    }

    fun getPlayBeat(): TGBeatImpl? {
        if (playUpdate) {
            playChanges = false

            val player = MidiPlayer.getInstance(context)
            val manager: TGSongManager = TGDocumentManager.getInstance(context).songManager
            if (isPlaying()) {
                val caret: TGCaret = TGSongViewController.getInstance(context).caret
                val track: TGTrack = caret.track!!

                val tick = player.tickPosition
                var start = playStart + (tick - playTick)
                if (currentPlayMeasure == null || start < currentPlayMeasure!!.start || start > currentPlayMeasure!!.start + currentPlayMeasure!!.length) {
                    currentPlayMeasure = null
                    start = MidiTickUtil.getStart(context, tick)
                }

                if (currentPlayMeasure == null || playBeatEnd == 0L || start > playBeatEnd || start < playStart || track.number != playTrack) {
                    currentPlayBeat = null
                    playBeatEnd = 0
                    playChanges = true

                    if (currentPlayMeasure == null || !currentPlayMeasure!!.hasTrack(track.number) || !isPlaying(currentPlayMeasure)) {
                        currentPlayMeasure = manager.trackManager.getMeasureAt(track, start) as TGMeasureImpl?
                    }
                    if (currentPlayMeasure != null && !isPlayingCountDown()) {
                        currentPlayBeat = manager.measureManager.getBeatIn(currentPlayMeasure, start) as TGBeatImpl?
                        if (currentPlayBeat != null) {
                            val next = manager.measureManager.getNextBeat(currentPlayMeasure!!.beats, currentPlayBeat)
                            playBeatEnd = if (next != null) {
                                next.start
                            } else {
                                val duration: TGDuration = manager.measureManager.getMinimumDuration(currentPlayBeat)
                                currentPlayBeat!!.start + duration.time
                            }
                        }
                    }
                }
                playTrack = track.number
                playTick = tick
                playStart = start
            }
            playUpdate = false
        }
        return currentPlayBeat
    }

    fun getPlayTick(): Long = playTick

    fun getPlayStart(): Long = playStart

    fun shouldRedraw(): Boolean = playChanges

    fun isPlaying(): Boolean = MidiPlayer.getInstance(context).isRunning

    fun isPlayingCountDown(): Boolean = MidiPlayer.getInstance(context).countDown.isRunning

    fun isPlaying(measure: TGMeasure?): Boolean = isPlaying() && currentPlayMeasure != null && measure == currentPlayMeasure

    fun isPlaying(measure: TGMeasure?, b: TGBeat?): Boolean = isPlaying(measure) && currentPlayBeat != null && currentPlayBeat!!.start == b?.start
}
