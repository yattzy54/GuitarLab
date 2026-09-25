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

class TGSongView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : View(context, attrs, defStyle) {
    private var contextValue: TGContext? = null
    private var controller: TGSongViewController? = null
    private var gestureDetector: TGSongViewGestureDetector? = null
    private var bufferedBitmap: Bitmap? = null
    private var painting = false
    private val defaultScale: Float = resources.displayMetrics.density

    val paintableScrollX: Int
        get() = if (controller!!.scroll.x.isEnabled) Math.round(controller!!.scroll.x.value) else 0

    val paintableScrollY: Int
        get() = if (controller!!.scroll.y.isEnabled) Math.round(controller!!.scroll.y.value) else 0

    override fun onFinishInflate() {
        super.onFinishInflate()
        contextValue = TGApplicationUtil.findContext(this)
        controller = TGSongViewController.getInstance(contextValue!!)
        controller!!.songView = this
        controller!!.layout.loadStyles(defaultScale)
        controller!!.updateTablature()
        gestureDetector = TGSongViewGestureDetector(context, this)
    }

    fun getDefaultScale(): Float = defaultScale

    fun getMinimumScale(): Float = defaultScale / 2f

    fun getMaximumScale(): Float = defaultScale * 2f

    fun redraw() {
        painting = true
        postInvalidate()
    }

    fun paintBuffer(canvas: Canvas) {
        try {
            val area = createClientArea(canvas)
            val painter = createBufferedPainter(area)
            paintArea(painter, area)
            if (controller!!.scalePreview != TGSongViewController.EMPTY_SCALE) {
                val currentScale = 1 / (controller!!.layout.scale) * controller!!.scalePreview
                (painter as TGPainterImpl).canvas.scale(currentScale, currentScale)
            }
            paintTablature(painter, area)
            painter.dispose()
        } catch (throwable: Throwable) {
            TGErrorManager.getInstance(contextValue!!).handleError(throwable)
        }
    }

    fun paintArea(painter: UIPainter, area: UIRectangle) {
        painter.setBackground(controller!!.resourceFactory.createColor(255, 255, 255))
        painter.initPath(UIPainter.PATH_FILL)
        painter.addRectangle(area.x, area.y, area.width, area.height)
        painter.closePath()
    }

    fun paintTablature(painter: UIPainter, area: UIRectangle) {
        if (controller!!.song != null) {
            controller!!.layoutPainter.paint(painter, area, -paintableScrollX.toFloat(), -paintableScrollY.toFloat())
            controller!!.caret.paintCaret(controller!!.layout, painter)
            controller!!.updateScroll(area)
            if (MidiPlayer.getInstance(contextValue!!).isRunning) {
                paintTablaturePlayMode(painter, area)
            } else if (controller!!.caret.hasChanges()) {
                controller!!.caret.setChanges(false)
                moveScrollTo(controller!!.caret.measure, area)
            }
        }
    }

    fun paintTablaturePlayMode(painter: UIPainter, area: UIRectangle) {
        val transportCache: TGTransportCache = TGTransport.getInstance(contextValue!!).cache
        val measure: TGMeasureImpl? = transportCache.playMeasure
        val beat: TGBeatImpl? = transportCache.playBeat
        if (measure != null && measure.hasTrack(controller!!.caret.track!!.number)) {
            moveScrollTo(measure, area)
            if (!measure.isOutOfBounds) {
                controller!!.layout.paintPlayMode(painter, measure, beat)
            }
        }
    }

    fun moveScrollTo(measure: TGMeasureImpl?, area: UIRectangle): Boolean {
        if (measure == null || measure.ts == null) {
            return false
        }
        var success = false
        val scrollX = paintableScrollX
        val scrollY = paintableScrollY
        val mX = measure.posX
        val mY = measure.posY
        val mWidth = measure.getWidth(controller!!.layout)
        val mHeight = measure.ts.size
        val marginWidth = controller!!.layout.firstMeasureSpacing
        val marginHeight = controller!!.layout.firstTrackSpacing
        if (mX < 0 || (mX + mWidth > area.width && (area.width >= mWidth + marginWidth || mX > marginWidth))) {
            controller!!.scroll.x.value = (scrollX + mX) - marginWidth
            success = true
        }
        if (mY < 0 || (mY + mHeight > area.height && (area.height >= mHeight + marginHeight || mY > marginHeight))) {
            controller!!.scroll.y.value = (scrollY + mY) - marginHeight
            success = true
        }
        if (success) {
            redraw()
        }
        return success
    }

    override fun onDraw(canvas: Canvas) {
        try {
            if (!controller!!.isDisposed()) {
                val editor = TGEditorManager.getInstance(contextValue!!)
                if (editor.tryLock()) {
                    try {
                        painting = true
                        paintBuffer(canvas)
                        painting = false
                    } finally {
                        editor.unlock()
                    }
                } else {
                    postInvalidate()
                }
            }
            if (bufferedBitmap != null) {
                canvas.drawBitmap(bufferedBitmap!!, 0f, 0f, null)
            }
        } catch (throwable: Throwable) {
            handleError(throwable)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        gestureDetector!!.processTouchEvent(event)
        redraw()
        return true
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        controller!!.caret.setChanges(true)
        controller!!.resetScroll()
        redraw()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        recycleBuffer()
        controller!!.layoutPainter.dispose()
    }

    fun createPainter(canvas: Canvas): UIPainter = TGPainterImpl(canvas)

    fun createBufferedPainter(area: UIRectangle): UIPainter {
        if (bufferedBitmap == null || bufferedBitmap!!.width != area.width.toInt() || bufferedBitmap!!.height != area.height.toInt()) {
            recycleBuffer()
            bufferedBitmap = Bitmap.createBitmap(Math.round(area.width), Math.round(area.height), Bitmap.Config.ARGB_8888)
        }
        return createPainter(Canvas(bufferedBitmap!!))
    }

    fun recycleBuffer() {
        if (bufferedBitmap != null && !bufferedBitmap!!.isRecycled) {
            bufferedBitmap!!.recycle()
            bufferedBitmap = null
        }
    }

    fun createClientArea(canvas: Canvas): UIRectangle {
        val rect: Rect = canvas.clipBounds
        return UIRectangle(rect.left.toFloat(), rect.top.toFloat(), rect.right.toFloat(), rect.bottom.toFloat())
    }

    fun handleError(throwable: Throwable) {
        TGErrorManager.getInstance(contextValue!!).handleError(TGException(throwable))
    }

    fun getController(): TGSongViewController? = controller

    fun isPainting(): Boolean = painting

    fun setPainting(painting: Boolean) {
        this.painting = painting
    }
}
