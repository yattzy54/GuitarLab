package app.tuxguitar.android.graphics

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import app.tuxguitar.ui.resource.UIImage
import app.tuxguitar.ui.resource.UIPainter
import java.io.InputStream

class TGImageImpl : UIImage {
    private var handle: Bitmap?

    constructor(width: Float, height: Float) {
        handle = Bitmap.createBitmap(kotlin.math.round(width).toInt(), kotlin.math.round(height).toInt(), Bitmap.Config.ARGB_8888)
    }

    constructor(stream: InputStream) {
        handle = BitmapFactory.decodeStream(stream)
    }

    override fun dispose() {
        if (!isDisposed()) {
            handle!!.recycle()
            handle = null
        }
    }

    override fun isDisposed(): Boolean = handle == null || handle!!.isRecycled

    fun getHandle(): Bitmap = handle!!

    override fun getWidth(): Float = handle!!.width.toFloat()

    override fun getHeight(): Float = handle!!.height.toFloat()

    override fun createPainter(): UIPainter = TGPainterImpl(Canvas(handle!!))
}
