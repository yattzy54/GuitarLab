package app.tuxguitar.android.view.tablature

import android.content.Context
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import app.tuxguitar.android.action.impl.layout.TGSetLayoutScaleAction
import app.tuxguitar.android.application.TGApplicationUtil
import app.tuxguitar.editor.action.TGActionProcessor

class TGSongViewScaleGestureDetector(context: Context, private val songView: TGSongView) : ScaleGestureDetector.OnScaleGestureListener {

    private var scaleFactor = 0f
    private val gestureDetector = ScaleGestureDetector(context, this)

    fun processTouchEvent(event: MotionEvent): Boolean {
        val controller = this.songView.getController() ?: return false
        if (controller.isScaleActionAvailable()) {
            return this.gestureDetector.onTouchEvent(event)
        }
        return false
    }

    fun isInProgress(): Boolean {
        val controller = this.songView.getController() ?: return false
        if (controller.isScaleActionAvailable()) {
            return this.gestureDetector.isInProgress
        }
        return false
    }

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
        val controller = this.songView.getController() ?: return false
        this.scaleFactor = controller.layout.scale
        return true
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        val minScale = this.songView.getMinimumScale()
        val maxScale = this.songView.getMaximumScale()
        this.scaleFactor = Math.max(minScale, Math.min(this.scaleFactor * detector.scaleFactor, maxScale))
        this.previewScale()
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector) {
        this.applyScale()
    }

    fun previewScale() {
        val controller = this.songView.getController() ?: return
        controller.scalePreview = this.scaleFactor
    }

    fun applyScale() {
        val tgActionProcessor = TGActionProcessor(TGApplicationUtil.findContext(this.songView), TGSetLayoutScaleAction.NAME)
        tgActionProcessor.setAttribute(TGSetLayoutScaleAction.ATTRIBUTE_SCALE, this.scaleFactor)
        tgActionProcessor.processOnNewThread()
    }
}
