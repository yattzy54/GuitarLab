package app.tuxguitar.android.view.tablature

import app.tuxguitar.android.graphics.TGResourceFactoryImpl
import app.tuxguitar.android.transport.TGTransport
import app.tuxguitar.document.TGDocumentManager
import app.tuxguitar.editor.TGEditorManager
import app.tuxguitar.graphics.control.TGController
import app.tuxguitar.graphics.control.TGLayout
import app.tuxguitar.graphics.control.TGLayoutStyles
import app.tuxguitar.graphics.control.TGLayoutVertical
import app.tuxguitar.graphics.control.TGResourceBuffer
import app.tuxguitar.player.base.MidiPlayer
import app.tuxguitar.player.base.MidiPlayerMode
import app.tuxguitar.song.managers.TGSongManager
import app.tuxguitar.song.models.TGBeat
import app.tuxguitar.song.models.TGDuration
import app.tuxguitar.song.models.TGMeasure
import app.tuxguitar.song.models.TGMeasureHeader
import app.tuxguitar.song.models.TGSong
import app.tuxguitar.ui.resource.UIRectangle
import app.tuxguitar.ui.resource.UIResourceFactory
import app.tuxguitar.util.TGContext
import app.tuxguitar.util.TGSynchronizer
import app.tuxguitar.util.singleton.TGSingletonFactory
import app.tuxguitar.util.singleton.TGSingletonUtil
import kotlin.jvm.JvmName

class TGSongViewController(val context: TGContext) : TGController {
    var disposed = false
        private set
    @get:JvmName("resourceFactory")
    lateinit var resourceFactory: UIResourceFactory
        private set
    lateinit var layout: TGLayout
        private set
    lateinit var songStyles: TGSongViewStyles
        private set
    lateinit var bufferController: TGSongViewBufferController
        private set
    lateinit var layoutPainter: TGSongViewLayoutPainter
        private set
    lateinit var axisSelector: TGSongViewAxisSelector
        private set
    lateinit var smartMenu: TGSongViewSmartMenu
        private set
    lateinit var caret: TGCaret
        private set
    lateinit var scroll: TGScroll
        private set
    var songView: TGSongView? = null
    var scalePreview = 0f

    init {
        songStyles = TGSongViewStyles()
        resourceFactory = TGResourceFactoryImpl()
        bufferController = TGSongViewBufferController(this)
        layoutPainter = TGSongViewLayoutPainter(this)
        layout = TGLayoutVertical(
            this,
            TGLayout.DISPLAY_TABLATURE or TGLayout.DISPLAY_SCORE or
                TGLayout.DISPLAY_COMPACT or TGLayout.HIGHLIGHT_PLAYED_BEAT
        )
        caret = TGCaret(this)
        scroll = TGScroll()
        smartMenu = TGSongViewSmartMenu(this)
        axisSelector = TGSongViewAxisSelector(this)

        resetCaret()
        resetScroll()
        updateTablature()
        appendListeners()
    }

    fun appendListeners() {
        val listener = TGSongViewEventListener(this)
        TGEditorManager.getInstance(context).addRedrawListener(listener)
        TGEditorManager.getInstance(context).addUpdateListener(listener)
        TGEditorManager.getInstance(context).addDestroyListener(listener)
    }

    fun resetCaret() {
        caret.update(1, TGDuration.QUARTER_TIME, 1)
    }

    fun resetScroll() {
        scroll.getX().reset(false, 0f, 0f, 0f)
        scroll.getY().reset(true, 0f, 0f, 0f)
    }

    fun disposeUnregisteredResources() {
        TGSynchronizer.getInstance(context).executeLater {
            bufferController.getResourceBuffer().disposeUnregisteredResources()
        }
    }

    fun updateTablature() {
        layout.updateSong()
        caret.update()
        disposeUnregisteredResources()
    }

    fun updateMeasures(numbers: List<Int>) {
        layout.updateMeasureNumbers(numbers)
        caret.update()
        disposeUnregisteredResources()
    }

    fun updateScroll(bounds: UIRectangle) {
        scroll.getX().setMaximum(maxOf(layout.width - bounds.width, 0f))
        scroll.getY().setMaximum(maxOf(layout.height - bounds.height, 0f))
    }

    fun updateSelection() {
        bufferController.updateSelection()
        layoutPainter.refreshBuffer()
    }

    fun scale(scale: Float) {
        layout.loadStyles(scale)
        scalePreview = EMPTY_SCALE
    }

    fun redraw() {
        songView?.redraw()
    }

    fun redrawPlayingMode() {
        if (songView != null && !songView!!.isPainting() && MidiPlayer.getInstance(context).isRunning) {
            redraw()
        }
    }

    @get:JvmName("songManager")
    val songManager: TGSongManager
        get() = TGDocumentManager.getInstance(context).songManager

    @get:JvmName("song")
    val song: TGSong
        get() = TGDocumentManager.getInstance(context).song

    @get:JvmName("resourceBuffer")
    val resourceBuffer: TGResourceBuffer
        get() = bufferController.getResourceBuffer()

    @get:JvmName("styles")
    val styles: TGLayoutStyles
        get() = songStyles

    override fun getResourceFactory(): UIResourceFactory = resourceFactory

    override fun getResourceBuffer(): TGResourceBuffer = bufferController.getResourceBuffer()

    override fun getSongManager(): TGSongManager = songManager

    override fun getSong(): TGSong = song

    override fun getStyles(): TGLayoutStyles = songStyles

    override fun getTrackSelection(): Int {
        if ((layout.style and TGLayout.DISPLAY_MULTITRACK) == 0) {
            return caret.track.number
        }
        return -1
    }

    override fun isRunning(beat: TGBeat): Boolean {
        return isRunning(beat.measure) &&
            TGTransport.getInstance(context).cache.isPlaying(beat.measure, beat)
    }

    override fun isRunning(measure: TGMeasure): Boolean {
        return measure.track == caret.track &&
            TGTransport.getInstance(context).cache.isPlaying(measure)
    }

    override fun isLoopSHeader(measureHeader: TGMeasureHeader): Boolean {
        val mode: MidiPlayerMode = MidiPlayer.getInstance(context).mode
        return mode.isLoop && (
            mode.loopSHeader == measureHeader.number ||
                (mode.loopSHeader == -1 && measureHeader.number == 1)
            )
    }

    override fun isLoopEHeader(measureHeader: TGMeasureHeader): Boolean {
        val mode: MidiPlayerMode = MidiPlayer.getInstance(context).mode
        return mode.isLoop && (
            mode.loopEHeader == measureHeader.number ||
                (mode.loopEHeader == -1 &&
                    measureHeader.number == measureHeader.song.countMeasureHeaders())
            )
    }

    fun isScaleActionAvailable(): Boolean {
        return !TGEditorManager.getInstance(context).isLocked &&
            !MidiPlayer.getInstance(context).isRunning
    }

    fun isScrollActionAvailable(): Boolean = !TGEditorManager.getInstance(context).isLocked

    fun isDisposed(): Boolean = disposed

    fun dispose() {
        caret.dispose()
        layout.disposeLayout()
        resourceBuffer.disposeAllResources()
        disposed = true
    }

    companion object {
        const val EMPTY_SCALE = 0f

        @JvmStatic
        fun getInstance(context: TGContext): TGSongViewController {
                return TGSingletonUtil.getInstance(
                context,
                TGSongViewController::class.java.name,
                    object : TGSingletonFactory<TGSongViewController> {
                    override fun createInstance(context: TGContext): TGSongViewController {
                        return TGSongViewController(context)
                    }
                }
            )
        }
    }
}
