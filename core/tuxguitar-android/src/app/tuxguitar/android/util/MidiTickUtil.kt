package app.tuxguitar.android.util

import app.tuxguitar.document.TGDocumentManager
import app.tuxguitar.player.base.MidiPlayer
import app.tuxguitar.player.base.MidiRepeatController
import app.tuxguitar.song.models.TGMeasureHeader
import app.tuxguitar.song.models.TGSong
import app.tuxguitar.util.TGContext

class MidiTickUtil private constructor() {
    companion object {
        @JvmStatic
        fun getStart(context: TGContext, tick: Long): Long {
            val startPoint = getStartPoint(context)
            var start = startPoint
            var length = 0L

            val song: TGSong = TGDocumentManager.getInstance(context).song
            val controller = MidiRepeatController(song, getSHeader(context), getEHeader(context))
            while (!controller.finished()) {
                val header: TGMeasureHeader = song.getMeasureHeader(controller.index)
                controller.process()
                if (controller.shouldPlay()) {
                    start += length
                    length = header.length
                    if (tick >= start && tick < start + length) {
                        return header.start + (tick - start)
                    }
                }
            }
            return if (tick < startPoint) startPoint else start
        }

        @JvmStatic
        fun getTick(context: TGContext, start: Long): Long {
            val startPoint = getStartPoint(context)
            var tick = startPoint
            var length = 0L

            val song: TGSong = TGDocumentManager.getInstance(context).song
            val controller = MidiRepeatController(song, getSHeader(context), getEHeader(context))
            while (!controller.finished()) {
                val header: TGMeasureHeader = song.getMeasureHeader(controller.index)
                controller.process()
                if (controller.shouldPlay()) {
                    tick += length
                    length = header.length
                    if (start >= header.start && start < header.start + length) {
                        return tick
                    }
                }
            }
            return if (start < startPoint) startPoint else tick
        }

        private fun getStartPoint(context: TGContext): Long {
            val midiPlayer = MidiPlayer.getInstance(context)
            midiPlayer.updateLoop(false)
            return midiPlayer.loopSPosition
        }

        @JvmStatic
        fun getSHeader(context: TGContext): Int = MidiPlayer.getInstance(context).loopSHeader

        @JvmStatic
        fun getEHeader(context: TGContext): Int = MidiPlayer.getInstance(context).loopEHeader
    }
}
