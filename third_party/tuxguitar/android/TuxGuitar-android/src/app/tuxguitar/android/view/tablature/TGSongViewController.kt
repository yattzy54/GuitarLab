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

class TGSongViewController(val context: TGContext) : TGController {
    companion object {
        const val EMPTY_SCALE: Float = 0f

        @JvmStatic
        fun getInstance(context: TGContext): TGSongViewController =
            TGSingletonUtil.getInstance(context, TGSongViewController::class.java.name, object : TGSingletonFactory<TGSongViewController> {
                override fun createInstance(context: TGContext): TGSongViewController = TGSongViewController(context)
            })
    }

    private var disposed = false
    val resourceFactory = TGResourceFactoryImpl()
    val layout: TGLayout = TGLayoutVertical(this, TGLayout.DISPLAY_TABLATURE or TGLayout.DISPLAY_SCORE or TGLayout.DISPLAY_COMPACT or TGLayout.HIGHLIGHT_PLAYED_BEAT)
    val songStyles = TGSongViewStyles()
    val bufferController = TGSongViewBufferController(this)
    val layoutPainter = TGSongViewLayoutPainter(this)
    val axisSelector = TGSongViewAxisSelector(this)
    val smartMenu = TGSongViewSmartMenu(this)
    val caret = TGCaret(this)
    val scroll = TGScroll()
    var songView: TGSongView? = null
    var scalePreview = EMPTY_SCALE

    init {
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
        scroll.x.reset(false, 0f, 0f, 0f)
        scroll.y.reset(true, 0f, 0f, 0f)
    }

    fun disposeUnregisteredResources() {
        TGSynchronizer.getInstance(context).executeLater {
            resourceBuffer.disposeUnregisteredResources()
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
        scroll.x.maximum = maxOf(layout.width - bounds.width, 0f)
        scroll.y.maximum = maxOf(layout.height - bounds.height, 0f)
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

    override fun getSongManager(): TGSongManager = TGDocumentManager.getInstance(context).getSongManager()

    override fun getSong(): TGSong = TGDocumentManager.getInstance(context).getSong()

    override fun getResourceFactory(): UIResourceFactory = resourceFactory

    override fun getResourceBuffer(): TGResourceBuffer = bufferController.getResourceBuffer()

    override fun getStyles(): TGLayoutStyles = songStyles

    override fun getTrackSelection(): Int {
        return if (layout.style and TGLayout.DISPLAY_MULTITRACK == 0) {
            caret.track?.number ?: -1
        } else {
            -1
        }
    }

    override fun isRunning(beat: TGBeat): Boolean = isRunning(beat.measure) && TGTransport.getInstance(context).cache.isPlaying(beat.measure, beat)

    override fun isRunning(measure: TGMeasure): Boolean = measure.track == caret.track && TGTransport.getInstance(context).cache.isPlaying(measure)

    override fun isLoopSHeader(measureHeader: TGMeasureHeader): Boolean {
        val mode = MidiPlayer.getInstance(context).mode
        return mode.isLoop && (mode.loopSHeader == measureHeader.number || (mode.loopSHeader == -1 && measureHeader.number == 1))
    }

    override fun isLoopEHeader(measureHeader: TGMeasureHeader): Boolean {
        val mode = MidiPlayer.getInstance(context).mode
        return mode.isLoop && (mode.loopEHeader == measureHeader.number || (mode.loopEHeader == -1 && measureHeader.number == measureHeader.song.countMeasureHeaders()))
    }

    fun isScaleActionAvailable(): Boolean = !TGEditorManager.getInstance(context).isLocked && !MidiPlayer.getInstance(context).isRunning

    fun isScrollActionAvailable(): Boolean = !TGEditorManager.getInstance(context).isLocked

    fun isDisposed(): Boolean = disposed

    fun dispose() {
        caret.dispose()
        layout.disposeLayout()
        resourceBuffer.disposeAllResources()
        disposed = true
    }
}
