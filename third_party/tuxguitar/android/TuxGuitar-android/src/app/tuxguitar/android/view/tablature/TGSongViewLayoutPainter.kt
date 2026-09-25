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
        if (this.buffer != null && !this.buffer!!.isDisposed) {
            this.buffer!!.dispose()
            this.buffer = null
        }
    }

    fun refreshBuffer() {
        this.refreshBuffer = true
    }

    fun paint(target: UIPainter, clientArea: UIRectangle, fromX: Float, fromY: Float) {
        this.resizeBuffer(clientArea)
        this.updatePoint(fromX, fromY)

        if (this.refreshBuffer) {
            this.refreshBuffer = false

            val tgPainter = this.buffer!!.createPainter()
            this.paintArea(tgPainter, clientArea)
            this.paintLayout(tgPainter, clientArea)
            tgPainter.dispose()
        }
        target.drawImage(this.buffer, 0f, 0f)
    }

    private fun paintLayout(painter: UIPainter, area: UIRectangle) {
        this.controller.layout.paint(painter, area, this.point!!.x, this.point!!.y)
    }

    private fun paintArea(painter: UIPainter, area: UIRectangle) {
        painter.setBackground(this.controller.resourceFactory.createColor(255, 255, 255))
        painter.initPath(UIPainter.PATH_FILL)
        painter.addRectangle(area.x, area.y, area.width, area.height)
        painter.closePath()
    }

    private fun updatePoint(x: Float, y: Float) {
        if (this.point == null || this.point!!.x != x || this.point!!.y != y) {
            this.point = UIPosition(x, y)
            this.refreshBuffer()
        }
    }

    private fun resizeBuffer(area: UIRectangle) {
        if (this.buffer == null || this.buffer!!.isDisposed || this.buffer!!.width != area.width || this.buffer!!.height != area.height) {
            this.dispose()
            this.buffer = this.controller.resourceFactory.createImage(area.width, area.height)
            this.refreshBuffer()
        }
    }
}
