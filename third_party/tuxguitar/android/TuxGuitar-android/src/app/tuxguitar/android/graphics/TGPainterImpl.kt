package app.tuxguitar.android.graphics

import android.graphics.Canvas
import android.graphics.DashPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Path.Direction
import android.graphics.Rect
import android.graphics.Typeface
import app.tuxguitar.ui.resource.UIColor
import app.tuxguitar.ui.resource.UIColorModel
import app.tuxguitar.ui.resource.UIFont
import app.tuxguitar.ui.resource.UIImage
import app.tuxguitar.ui.resource.UIPainter
import kotlin.math.roundToInt

class TGPainterImpl(canvas: Canvas) : UIPainter {
    private var pathEmpty = false
    private var style = 0
    private var alpha = 0xff
    private var canvasHandle: Canvas? = canvas
    private var paintHandle: Paint? = Paint()
    private var path: Path? = null
    private var foreground = TGColorImpl(UIColorModel(0, 0, 0))
    private var background = TGColorImpl(UIColorModel(255, 255, 255))

    val canvas: Canvas
        get() = canvasHandle!!

    override fun initPath(style: Int) {
        this.style = style
        path = Path()
        pathEmpty = true
        setAntialias(true)
    }

    override fun initPath() {
        initPath(UIPainter.PATH_DRAW)
    }

    override fun closePath() {
        if (!pathEmpty) {
            if ((style and UIPainter.PATH_DRAW) != 0) {
                paintHandle!!.style = Paint.Style.STROKE
                paintHandle!!.color = foreground.getHandle(alpha)
                canvasHandle!!.drawPath(path!!, paintHandle!!)
            }
            if ((style and UIPainter.PATH_FILL) != 0) {
                paintHandle!!.style = Paint.Style.FILL
                paintHandle!!.color = background.getHandle(alpha)
                canvasHandle!!.drawPath(path!!, paintHandle!!)
            }
        }

        style = 0
        path = null
        pathEmpty = true
        setAntialias(false)
    }

    override fun dispose() {
        canvasHandle = null
        paintHandle = null
    }

    override fun isDisposed(): Boolean = canvasHandle == null || paintHandle == null

    override fun addCircle(x: Float, y: Float, width: Float) {
        path!!.addCircle(x, y, width / 2f, Direction.CW)
        pathEmpty = false
    }

    override fun addRectangle(x: Float, y: Float, width: Float, height: Float) {
        path!!.addRect(x, y, x + width, y + height, Direction.CW)
        pathEmpty = false
    }

    override fun lineTo(x: Float, y: Float) {
        path!!.lineTo(x, y)
        pathEmpty = false
    }

    override fun moveTo(x: Float, y: Float) {
        path!!.moveTo(x, y)
        pathEmpty = false
    }

    override fun cubicTo(x1: Float, y1: Float, x2: Float, y2: Float, x3: Float, y3: Float) {
        path!!.cubicTo(x1, y1, x2, y2, x3, y3)
        pathEmpty = false
    }

    override fun drawImage(image: UIImage, x: Float, y: Float) {
        canvasHandle!!.drawBitmap((image as TGImageImpl).getHandle(), x, y, paintHandle!!)
    }

    override fun drawImage(image: UIImage, srcX: Float, srcY: Float, srcWidth: Float, srcHeight: Float, dstX: Float, dstY: Float, dstWidth: Float, dstHeight: Float) {
        canvasHandle!!.drawBitmap((image as TGImageImpl).getHandle(), toRect(srcX, srcY, srcX + srcWidth, srcY + srcHeight), toRect(dstX, dstY, dstX + dstWidth, dstY + dstHeight), paintHandle!!)
    }

    override fun drawString(text: String, x: Float, y: Float) {
        paintHandle!!.style = Paint.Style.FILL
        paintHandle!!.color = foreground.getHandle(alpha)
        canvasHandle!!.drawText(text, x, y, paintHandle!!)
    }

    fun setAdvanced(advanced: Boolean) {
        setAntialias(advanced)
    }

    override fun setAlpha(alpha: Int) {
        this.alpha = alpha
    }

    override fun setAntialias(antialias: Boolean) {
        paintHandle!!.isAntiAlias = antialias
    }

    override fun setFont(font: UIFont) {
        if (font is TGFontImpl) {
            paintHandle!!.typeface = Typeface.create(font.name, (if (font.isBold) Typeface.BOLD else 0) or (if (font.isItalic) Typeface.ITALIC else 0))
            paintHandle!!.textSize = font.height
        }
    }

    override fun setForeground(color: UIColor) {
        if (color is TGColorImpl) {
            foreground = color
        }
    }

    override fun setBackground(color: UIColor) {
        if (color is TGColorImpl) {
            background = color
        }
    }

    override fun setLineStyleSolid() {
        paintHandle!!.pathEffect = null
    }

    override fun setLineStyleDash() {
        paintHandle!!.pathEffect = DashPathEffect(floatArrayOf(4f, 1f), 0f)
    }

    override fun setLineStyleDashDot() {
        paintHandle!!.pathEffect = DashPathEffect(floatArrayOf(4f, 1f, 1f, 1f), 0f)
    }

    override fun setLineStyleDot() {
        paintHandle!!.pathEffect = DashPathEffect(floatArrayOf(1f, 1f), 0f)
    }

    override fun setLineWidth(width: Float) {
        paintHandle!!.strokeWidth = width
    }

    override fun getFontSize(): Float = paintHandle!!.textSize.roundToInt().toFloat()

    override fun getFMBaseLine(): Float = 0f

    override fun getFMTopLine(): Float = -((getFMAscent() / 10f) * 8f)

    override fun getFMMiddleLine(): Float = -((getFMAscent() / 10f) * 4f)

    fun getFMAscent(): Float {
        paintHandle!!.style = Paint.Style.FILL
        return paintHandle!!.fontMetrics.ascent
    }

    override fun getFMHeight(): Float = getFMTopLine() - getFMBaseLine()

    override fun getFMWidth(text: String): Float {
        val bounds = Rect()
        paintHandle!!.style = Paint.Style.FILL
        paintHandle!!.getTextBounds(text, 0, text.length, bounds)
        return bounds.width().toFloat()
    }

    fun toRect(left: Float, top: Float, right: Float, bottom: Float): Rect {
        return Rect(left.roundToInt(), top.roundToInt(), right.roundToInt(), bottom.roundToInt())
    }
}
