package app.tuxguitar.android.view.tablature

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.core.view.GestureDetectorCompat
import app.tuxguitar.android.action.impl.caret.TGMoveToAxisPositionAction
import app.tuxguitar.android.application.TGApplicationUtil
import app.tuxguitar.editor.action.TGActionProcessor

class TGSongViewGestureDetector(context: Context, private val songView: TGSongView) : GestureDetector.SimpleOnGestureListener() {

    private val gestureDetector: GestureDetectorCompat = GestureDetectorCompat(context, this)
    private val songViewScaleGestureDetector = TGSongViewScaleGestureDetector(context, songView)

    fun processTouchEvent(event: MotionEvent): Boolean {
        this.songViewScaleGestureDetector.processTouchEvent(event)
        if (!this.songViewScaleGestureDetector.isInProgress()) {
            this.gestureDetector.onTouchEvent(event)
        }
        return true
    }

    override fun onLongPress(e: MotionEvent) {
        this.moveToAxisPosition(e.x, e.y, true)
    }

    override fun onSingleTapUp(e: MotionEvent): Boolean {
        this.moveToAxisPosition(e.x, e.y, false)
        return true
    }

    override fun onScroll(e1: MotionEvent?, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
        val controller = this.songView.getController() ?: return true
        if (controller.isScrollActionAvailable()) {
            this.updateAxis(controller.scroll.x, distanceX)
            this.updateAxis(controller.scroll.y, distanceY)
        }
        return true
    }

    fun updateAxis(axis: TGScrollAxis, distance: Float) {
        if (axis.isEnabled) {
            axis.value = Math.max(Math.min(axis.value + distance, axis.maximum), axis.minimum)
        }
    }

    private fun moveToAxisPosition(x: Float, y: Float, requestSmartMenu: Boolean) {
        val tgActionProcessor = TGActionProcessor(TGApplicationUtil.findContext(this.songView), TGMoveToAxisPositionAction.NAME)
        tgActionProcessor.setAttribute(TGMoveToAxisPositionAction.ATTRIBUTE_X, x)
        tgActionProcessor.setAttribute(TGMoveToAxisPositionAction.ATTRIBUTE_Y, y)
        tgActionProcessor.setAttribute(TGMoveToAxisPositionAction.ATTRIBUTE_REQUEST_SMART_MENU, requestSmartMenu)
        tgActionProcessor.processOnNewThread()
    }
}
