package app.tuxguitar.android.transport

import app.tuxguitar.android.activity.TGActivityController
import app.tuxguitar.android.util.MidiTickUtil
import app.tuxguitar.android.view.tablature.TGSongViewController
import app.tuxguitar.document.TGDocumentManager
import app.tuxguitar.player.base.MidiPlayer
import app.tuxguitar.player.base.MidiPlayerException
import app.tuxguitar.song.managers.TGSongManager
import app.tuxguitar.song.models.TGMeasure
import app.tuxguitar.song.models.TGMeasureHeader
import app.tuxguitar.song.models.TGSong
import app.tuxguitar.util.TGContext
import app.tuxguitar.util.error.TGErrorManager
import app.tuxguitar.util.singleton.TGSingletonFactory
import app.tuxguitar.util.singleton.TGSingletonUtil

class TGTransport(private val context: TGContext) {
    val cache = TGTransportCache(context)

    fun getSongManager(): TGSongManager = TGDocumentManager.getInstance(context).songManager

    fun getSong(): TGSong = TGDocumentManager.getInstance(context).song

    fun gotoFirst() {
        gotoMeasure(getSongManager().getFirstMeasureHeader(getSong()), true)
    }

    fun gotoLast() {
        gotoMeasure(getSongManager().getLastMeasureHeader(getSong()), true)
    }

    fun gotoNext() {
        val player = MidiPlayer.getInstance(context)
        val header = getSongManager().getMeasureHeaderAt(getSong(), MidiTickUtil.getStart(context, player.tickPosition))
        if (header != null) {
            gotoMeasure(getSongManager().getNextMeasureHeader(getSong(), header), true)
        }
    }

    fun gotoPrevious() {
        val player = MidiPlayer.getInstance(context)
        val header = getSongManager().getMeasureHeaderAt(getSong(), MidiTickUtil.getStart(context, player.tickPosition))
        if (header != null) {
            gotoMeasure(getSongManager().getPrevMeasureHeader(getSong(), header), true)
        }
    }

    fun gotoMeasure(header: TGMeasureHeader?) {
        gotoMeasure(header, false)
    }

    fun gotoCaretPosition() {
        gotoMeasure(TGSongViewController.getInstance(context).caret.measure!!.header, false)
    }

    fun gotoMeasure(header: TGMeasureHeader?, moveCaret: Boolean) {
        if (header != null) {
            var playingMeasure: TGMeasure? = null
            val player = MidiPlayer.getInstance(context)
            if (player.isRunning) {
                cache.updatePlayMode()
                playingMeasure = cache.playMeasure
            }
            if (playingMeasure == null || playingMeasure!!.header!!.number != header.number) {
                player.tickPosition = MidiTickUtil.getTick(context, header.start)
                if (moveCaret) {
                    goToTickPosition()
                }
            }
        }
    }

    fun gotoPlayerPosition() {
        val player = MidiPlayer.getInstance(context)
        val header = getSongManager().getMeasureHeaderAt(getSong(), MidiTickUtil.getStart(context, player.tickPosition))
        if (header != null) {
            player.tickPosition = MidiTickUtil.getTick(context, header.start)
        }

        goToTickPosition()
    }

    fun goToTickPosition() {
        TGSongViewController.getInstance(context).caret.goToTickPosition()
        TGActivityController.getInstance(context).activity?.updateCache(true)
    }

    fun play() {
        val player = MidiPlayer.getInstance(context)
        if (!player.isRunning) {
            try {
                gotoCaretPosition()
                player.mode.reset()
                player.play()
            } catch (exception: MidiPlayerException) {
                TGErrorManager.getInstance(context).handleError(exception)
            }
        } else {
            player.pause()
        }
    }

    fun stop() {
        val player = MidiPlayer.getInstance(context)
        if (!player.isRunning) {
            player.reset()
            gotoPlayerPosition()
        } else {
            player.reset()
        }
    }

    companion object {
        @JvmStatic
        fun getInstance(context: TGContext): TGTransport {
            return TGSingletonUtil.getInstance(context, TGTransport::class.java.name, object : TGSingletonFactory<TGTransport> {
                override fun createInstance(context: TGContext): TGTransport = TGTransport(context)
            })
        }
    }
}
