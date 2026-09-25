package app.tuxguitar.android.view.tablature

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import app.tuxguitar.android.application.TGApplicationUtil
import app.tuxguitar.android.graphics.TGPainterImpl
import app.tuxguitar.android.transport.TGTransport
import app.tuxguitar.android.transport.TGTransportCache
import app.tuxguitar.editor.TGEditorManager
import app.tuxguitar.graphics.control.TGBeatImpl
import app.tuxguitar.graphics.control.TGMeasureImpl
import app.tuxguitar.player.base.MidiPlayer
import app.tuxguitar.ui.resource.UIPainter
import app.tuxguitar.ui.resource.UIRectangle
import app.tuxguitar.util.TGContext
import app.tuxguitar.util.TGException
import app.tuxguitar.util.error.TGErrorManager

class TGSongView : View {
    private lateinit var contextValue: TGContext
    lateinit var controller: TGSongViewController
        private set
    private lateinit var gestureDetector: TGSongViewGestureDetector
    private var bufferedBitmap: Bitmap? = null
    private var painting = false

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle)

    override fun onFinishInflate() {
        super.onFinishInflate()
        contextValue = TGApplicationUtil.findContext(this)
        controller = TGSongViewController.getInstance(contextValue)
        controller.songView = this
        controller.layout.loadStyles(defaultScale)
        controller.updateTablature()
        gestureDetector = TGSongViewGestureDetector(context, this)
    }

    val defaultScale: Float
        get() = resources.displayMetrics.density

    val minimumScale: Float
        get() = defaultScale / 2f

    val maximumScale: Float
        get() = defaultScale * 2f

    fun redraw() {
        setPainting(true)
        postInvalidate()
    }

    fun paintBuffer(canvas: Canvas) {
        try {
            val area = createClientArea(canvas)
            val painter = createBufferedPainter(area)
            paintArea(painter, area)

            if (controller.scalePreview != TGSongViewController.EMPTY_SCALE) {
                val currentScale = 1f / controller.layout.scale * controller.scalePreview
                (painter as TGPainterImpl).canvas.scale(currentScale, currentScale)
            }

            paintTablature(painter, area)
            painter.dispose()
        } catch (throwable: Throwable) {
            TGErrorManager.getInstance(contextValue).handleError(throwable)
        }
    }

    fun paintArea(painter: UIPainter, area: UIRectangle) {
        painter.setBackground(controller.resourceFactory.createColor(255, 255, 255))
        painter.initPath(UIPainter.PATH_FILL)
        painter.addRectangle(area.x, area.y, area.width, area.height)
        painter.closePath()
    }

    fun paintTablature(painter: UIPainter, area: UIRectangle) {
        if (controller.song != null) {
            controller.layoutPainter.paint(
                painter,
                area,
                -getPaintableScrollX().toFloat(),
                -getPaintableScrollY().toFloat()
            )
            controller.caret.paintCaret(controller.layout, painter)
            controller.updateScroll(area)

            if (MidiPlayer.getInstance(contextValue).isRunning) {
                paintTablaturePlayMode(painter, area)
            } else if (controller.caret.hasChanges()) {
                controller.caret.setChanges(false)
                moveScrollTo(controller.caret.measure, area)
            }
        }
    }

    fun paintTablaturePlayMode(painter: UIPainter, area: UIRectangle) {
        val transportCache: TGTransportCache = TGTransport.getInstance(contextValue).cache
        val measure: TGMeasureImpl? = transportCache.playMeasure
        val beat: TGBeatImpl? = transportCache.playBeat
        if (measure != null && measure.hasTrack(controller.caret.track.number)) {
            moveScrollTo(measure, area)
            if (!measure.isOutOfBounds) {
                controller.layout.paintPlayMode(painter, measure, beat)
            }
        }
    }

    fun moveScrollTo(measure: TGMeasureImpl?, area: UIRectangle): Boolean {
        var success = false
        if (measure != null && measure.ts != null) {
            val scrollX = getPaintableScrollX()
            val scrollY = getPaintableScrollY()
            val mX = measure.posX
            val mY = measure.posY
            val mWidth = measure.getWidth(controller.layout)
            val mHeight = measure.ts!!.size
            val marginWidth = controller.layout.firstMeasureSpacing
            val marginHeight = controller.layout.firstTrackSpacing

            if (mX < 0 || (
                    mX + mWidth > area.width &&
                        (area.width >= mWidth + marginWidth || mX > marginWidth)
                    )
            ) {
                controller.scroll.getX().setValue((scrollX + mX) - marginWidth)
                success = true
            }
            if (mY < 0 || (
                    mY + mHeight > area.height &&
                        (area.height >= mHeight + marginHeight || mY > marginHeight)
                    )
            ) {
                controller.scroll.getY().setValue((scrollY + mY) - marginHeight)
                success = true
            }
            if (success) redraw()
        }
        return success
    }

    override fun onDraw(canvas: Canvas) {
        try {
            if (!controller.isDisposed()) {
                val editor = TGEditorManager.getInstance(contextValue)
                if (editor.tryLock()) {
                    try {
                        setPainting(true)
                        paintBuffer(canvas)
                        setPainting(false)
                    } finally {
                        editor.unlock()
                    }
                } else {
                    postInvalidate()
                }
            }
            bufferedBitmap?.let { canvas.drawBitmap(it, 0f, 0f, null) }
        } catch (throwable: Throwable) {
            handleError(throwable)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector.processTouchEvent(event)
        redraw()
        return true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        controller.caret.setChanges(true)
        controller.resetScroll()
        redraw()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        recycleBuffer()
        controller.layoutPainter.dispose()
    }

    fun createPainter(canvas: Canvas): UIPainter = TGPainterImpl(canvas)

    fun createBufferedPainter(area: UIRectangle): UIPainter {
        if (bufferedBitmap == null ||
            bufferedBitmap!!.width.toFloat() != area.width ||
            bufferedBitmap!!.height.toFloat() != area.height
        ) {
            recycleBuffer()
            bufferedBitmap = Bitmap.createBitmap(
                Math.round(area.width),
                Math.round(area.height),
                Bitmap.Config.ARGB_8888
            )
        }
        return createPainter(Canvas(bufferedBitmap!!))
    }

    fun recycleBuffer() {
        bufferedBitmap?.let {
            if (!it.isRecycled) it.recycle()
            bufferedBitmap = null
        }
    }

    fun createClientArea(canvas: Canvas): UIRectangle {
        val rect: Rect = canvas.clipBounds
        return UIRectangle(
            rect.left.toFloat(),
            rect.top.toFloat(),
            rect.right.toFloat(),
            rect.bottom.toFloat()
        )
    }

    fun handleError(throwable: Throwable) {
        TGErrorManager.getInstance(contextValue).handleError(TGException(throwable))
    }

    fun getPaintableScrollX(): Int {
        return if (controller.scroll.getX().isEnabled()) {
            Math.round(controller.scroll.getX().getValue())
        } else {
            0
        }
    }

    fun getPaintableScrollY(): Int {
        return if (controller.scroll.getY().isEnabled()) {
            Math.round(controller.scroll.getY().getValue())
        } else {
            0
        }
    }

    fun isPainting(): Boolean = painting

    fun setPainting(painting: Boolean) {
        this.painting = painting
    }
}
