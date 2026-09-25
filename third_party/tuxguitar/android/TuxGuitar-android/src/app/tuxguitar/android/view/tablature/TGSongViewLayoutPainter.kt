package app.tuxguitar.android.view.tablature

import app.tuxguitar.ui.resource.UIImage
import app.tuxguitar.ui.resource.UIPainter
import app.tuxguitar.ui.resource.UIPosition
import app.tuxguitar.ui.resource.UIRectangle

class TGSongViewLayoutPainter(private val controller: TGSongViewController) {
    private var buffer: UIImage? = null
    private var point: UIPosition? = null
    private var refreshBuffer = false

    fun dispose() {
        val currentBuffer = buffer
        if (currentBuffer != null && !currentBuffer.isDisposed) {
            currentBuffer.dispose()
            buffer = null
        }
    }

    fun refreshBuffer() {
        refreshBuffer = true
    }

    fun paint(target: UIPainter, clientArea: UIRectangle, fromX: Float, fromY: Float) {
        resizeBuffer(clientArea)
        updatePoint(fromX, fromY)
        if (refreshBuffer) {
            refreshBuffer = false
            val painter = buffer!!.createPainter()
            paintArea(painter, clientArea)
            paintLayout(painter, clientArea)
            painter.dispose()
        }
        target.drawImage(buffer!!, 0f, 0f)
    }

    private fun paintLayout(painter: UIPainter, area: UIRectangle) {
        val position = point!!
        controller.layout.paint(painter, area, position.x, position.y)
    }

    private fun paintArea(painter: UIPainter, area: UIRectangle) {
        painter.setBackground(controller.resourceFactory.createColor(255, 255, 255))
        painter.initPath(UIPainter.PATH_FILL)
        painter.addRectangle(
            area.x.toFloat(),
            area.y.toFloat(),
            area.width.toFloat(),
            area.height.toFloat()
        )
        painter.closePath()
    }

    private fun updatePoint(x: Float, y: Float) {
        val currentPoint = point
        if (currentPoint == null || currentPoint.x != x || currentPoint.y != y) {
            point = UIPosition(x, y)
            refreshBuffer()
        }
    }

    private fun resizeBuffer(area: UIRectangle) {
        val currentBuffer = buffer
        if (currentBuffer == null || currentBuffer.isDisposed ||
            currentBuffer.width != area.width || currentBuffer.height != area.height
        ) {
            dispose()
            buffer = controller.resourceFactory
                .createImage(area.width.toFloat(), area.height.toFloat())
            refreshBuffer()
        }
    }
}
