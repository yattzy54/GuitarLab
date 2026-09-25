package app.tuxguitar.android.view.tablature

import android.content.Context
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import app.tuxguitar.android.action.impl.layout.TGSetLayoutScaleAction
import app.tuxguitar.android.application.TGApplicationUtil
import app.tuxguitar.editor.action.TGActionProcessor
import kotlin.math.max
import kotlin.math.min

class TGSongViewScaleGestureDetector(
    context: Context,
    private val songView: TGSongView
) : ScaleGestureDetector.OnScaleGestureListener {
    private var scaleFactor = 0f
    private val gestureDetector = ScaleGestureDetector(context, this)

    fun processTouchEvent(event: MotionEvent): Boolean =
        if (songView.controller.isScaleActionAvailable()) gestureDetector.onTouchEvent(event) else false

    fun isInProgress(): Boolean =
        songView.controller.isScaleActionAvailable() && gestureDetector.isInProgress

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
        scaleFactor = songView.controller.layout.scale
        return true
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        scaleFactor = max(
            songView.minimumScale,
            min(scaleFactor * detector.scaleFactor, songView.maximumScale)
        )
        previewScale()
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector) {
        applyScale()
    }

    fun previewScale() {
        songView.controller.scalePreview = scaleFactor
    }

    fun applyScale() {
        val processor = TGActionProcessor(
            TGApplicationUtil.findContext(songView),
            TGSetLayoutScaleAction.NAME
        )
        processor.setAttribute(TGSetLayoutScaleAction.ATTRIBUTE_SCALE, scaleFactor)
        processor.processOnNewThread()
    }
}
