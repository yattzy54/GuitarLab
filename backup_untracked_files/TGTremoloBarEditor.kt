package app.tuxguitar.android.view.dialog.tremoloBar

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import app.tuxguitar.android.graphics.TGColorImpl
import app.tuxguitar.android.graphics.TGPainterImpl
import app.tuxguitar.song.factory.TGFactory
import app.tuxguitar.song.models.effects.TGEffectTremoloBar
import app.tuxguitar.ui.resource.UIColorModel
import app.tuxguitar.ui.resource.UIPainter
import app.tuxguitar.ui.resource.UIPosition
import kotlin.math.abs
import kotlin.math.roundToInt

class TGTremoloBarEditor(context: Context, attrs: AttributeSet?) : View(context, attrs) {
    private var xSpacing = 0f
    private var ySpacing = 0f
    private val x = FloatArray(X_LENGTH)
    private val y = FloatArray(Y_LENGTH)
    private val points = mutableListOf<UIPosition>()
    private var listener: TGTremoloBarEditorListener? = null
    private val gestureDetector = TGTremoloBarEditorGestureDetector(context, this)

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = MeasureSpec.getSize(widthMeasureSpec).toFloat()
        updateDimensions(width)
        val measuredHeight = paddingTop + (ySpacing * Y_LENGTH) + paddingBottom
        setMeasuredDimension(width.roundToInt(), measuredHeight.roundToInt())
    }

    override fun onDraw(canvas: Canvas) {
        createPainter(canvas).apply {
            paintEditor(this)
            dispose()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean =
        gestureDetector.processTouchEvent(event) || super.onTouchEvent(event)

    fun updateDimensions(expectedWidth: Float) {
        xSpacing = expectedWidth / X_LENGTH
        ySpacing = xSpacing / 2
        val xMargin = xSpacing / 2
        val yMargin = ySpacing / 2
        x.indices.forEach { x[it] = xMargin + it * xSpacing }
        y.indices.forEach { y[it] = yMargin + it * ySpacing }
    }

    fun createPainter(canvas: Canvas): UIPainter = TGPainterImpl(canvas)

    fun paintEditor(painter: UIPainter) {
        x.indices.forEach { index ->
            setStyleX(painter, index)
            painter.initPath()
            painter.setAntialias(false)
            painter.moveTo(x[index], y[0])
            painter.lineTo(x[index], y[Y_LENGTH - 1])
            painter.closePath()
        }
        y.indices.forEach { index ->
            setStyleY(painter, index)
            painter.initPath()
            painter.setAntialias(false)
            painter.moveTo(x[0], y[index])
            painter.lineTo(x[X_LENGTH - 1], y[index])
            painter.closePath()
        }

        painter.setLineStyleSolid()
        painter.setLineWidth(2f)
        painter.setForeground(TGColorImpl(UIColorModel(0x99, 0x99, 0x99)))
        points.zipWithNext().forEach { (previous, point) ->
            painter.initPath()
            painter.moveTo(previous.getX(), previous.getY())
            painter.lineTo(point.getX(), point.getY())
            painter.closePath()
        }

        painter.setLineWidth(5f)
        painter.setForeground(TGColorImpl(UIColorModel(0, 0, 0)))
        points.forEach { point ->
            painter.initPath()
            painter.setAntialias(false)
            painter.addRectangle(point.getX() - 2, point.getY() - 2, 5f, 5f)
            painter.closePath()
        }
        painter.setLineWidth(1f)
    }

    fun setStyleX(painter: UIPainter, index: Int) {
        painter.setLineStyleSolid()
        if (index == 0 || index == X_LENGTH - 1) {
            painter.setForeground(TGColorImpl(UIColorModel(0, 0, 0)))
        } else {
            painter.setForeground(TGColorImpl(UIColorModel(0, 0, 0xff)))
            if (index % 3 > 0) painter.setLineStyleDot()
        }
    }

    private fun setStyleY(painter: UIPainter, index: Int) {
        painter.setLineStyleSolid()
        when {
            index == 0 || index == Y_LENGTH - 1 ||
                index == TGEffectTremoloBar.MAX_VALUE_LENGTH ->
                painter.setForeground(TGColorImpl(UIColorModel(0, 0, 0)))
            else -> {
                painter.setForeground(TGColorImpl(UIColorModel(0xff, 0, 0)))
                if (index % 2 > 0) {
                    painter.setLineStyleDot()
                    painter.setForeground(TGColorImpl(UIColorModel(0x99, 0x99, 0x99)))
                }
            }
        }
    }

    fun checkPoint(pointX: Float, pointY: Float) {
        val point = UIPosition(getX(pointX), getY(pointY))
        if (!removePoint(point)) {
            removePointsAtXLine(point.getX())
            addPoint(point)
            orderPoints()
        }
    }

    fun removePoint(point: UIPosition): Boolean {
        val pointToRemove = points.firstOrNull {
            it.getX() == point.getX() && it.getY() == point.getY()
        } ?: return false
        points.remove(pointToRemove)
        return true
    }

    fun orderPoints() {
        points.sortBy { it.getX() }
    }

    fun removePointsAtXLine(pointX: Float) {
        points.removeAll { it.getX() == pointX }
    }

    fun addPoint(point: UIPosition) {
        points.add(point)
    }

    fun getX(pointX: Float): Float = findClosest(x, pointX)

    fun getY(pointY: Float): Float = findClosest(y, pointY)

    private fun findClosest(coordinates: FloatArray, point: Float): Float {
        var closest = -1f
        coordinates.forEach { coordinate ->
            if (closest < 0 || abs(point - coordinate) < abs(point - closest)) {
                closest = coordinate
            }
        }
        return closest
    }

    fun isEmpty(): Boolean = points.isEmpty()

    fun addPointFromTremoloBarPoint(point: TGEffectTremoloBar.TremoloBarPoint) {
        val indexX = point.getPosition()
        val indexY = y.size - TGEffectTremoloBar.MAX_VALUE_LENGTH - point.getValue() - 1
        if (indexX in x.indices && indexY in y.indices) {
            points.add(UIPosition(x[indexX], y[indexY]))
        }
    }

    fun addTremoloBarPointFromPoint(effect: TGEffectTremoloBar, point: UIPosition) {
        val position = x.indexOfFirst { point.getX() == it }.takeIf { it >= 0 } ?: 0
        val indexY = y.indexOfFirst { point.getY() == it }
        val value = if (indexY >= 0) TGEffectTremoloBar.MAX_VALUE_LENGTH - indexY else 0
        effect.addPoint(position, value)
    }

    fun loadTremoloBar(effect: TGEffectTremoloBar) {
        points.clear()
        effect.getPoints().forEach { point ->
            addPointFromTremoloBarPoint(point as TGEffectTremoloBar.TremoloBarPoint)
        }
        postInvalidate()
    }

    fun createTremoloBar(factory: TGFactory): TGEffectTremoloBar? {
        if (points.isEmpty()) return null
        return factory.newEffectTremoloBar().also { effect ->
            points.forEach { point -> addTremoloBarPointFromPoint(effect, point) }
        }
    }

    fun editPoint(pointX: Float, pointY: Float) {
        checkPoint(pointX, pointY)
        notifyChanged()
        postInvalidate()
    }

    fun notifyChanged() {
        listener?.onChange()
    }

    fun getListener(): TGTremoloBarEditorListener? = listener

    fun setListener(listener: TGTremoloBarEditorListener?) {
        this.listener = listener
    }

    private companion object {
        const val X_LENGTH = TGEffectTremoloBar.MAX_POSITION_LENGTH + 1
        const val Y_LENGTH = TGEffectTremoloBar.MAX_VALUE_LENGTH * 2 + 1
    }
}
