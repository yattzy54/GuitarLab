package app.tuxguitar.android.view.tablature

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.core.view.GestureDetectorCompat
import app.tuxguitar.android.action.impl.caret.TGMoveToAxisPositionAction
import app.tuxguitar.android.application.TGApplicationUtil
import app.tuxguitar.editor.action.TGActionProcessor
import kotlin.math.max
import kotlin.math.min

class TGSongViewGestureDetector(
    context: Context,
    private val songView: TGSongView
) : GestureDetector.SimpleOnGestureListener() {
    private val gestureDetector = GestureDetectorCompat(context, this)
    private val songViewScaleGestureDetector = TGSongViewScaleGestureDetector(context, songView)

    fun processTouchEvent(event: MotionEvent): Boolean {
        songViewScaleGestureDetector.processTouchEvent(event)
        if (!songViewScaleGestureDetector.isInProgress()) {
            gestureDetector.onTouchEvent(event)
        }
        return true
    }

    override fun onLongPress(event: MotionEvent) {
        moveToAxisPosition(event.x, event.y, true)
    }

    override fun onSingleTapUp(event: MotionEvent): Boolean {
        moveToAxisPosition(event.x, event.y, false)
        return true
    }

    override fun onScroll(
        firstEvent: MotionEvent?,
        secondEvent: MotionEvent,
        distanceX: Float,
        distanceY: Float
    ): Boolean {
        if (songView.controller.isScrollActionAvailable()) {
            updateAxis(songView.controller.scroll.getX(), distanceX)
            updateAxis(songView.controller.scroll.getY(), distanceY)
        }
        return true
    }

    fun updateAxis(axis: TGScrollAxis, distance: Float) {
        if (axis.isEnabled()) {
            axis.setValue(max(min(axis.getValue() + distance, axis.getMaximum()), axis.getMinimum()))
        }
    }

    private fun moveToAxisPosition(x: Float, y: Float, requestSmartMenu: Boolean) {
        val processor = TGActionProcessor(
            TGApplicationUtil.findContext(songView),
            TGMoveToAxisPositionAction.NAME
        )
        processor.setAttribute(TGMoveToAxisPositionAction.ATTRIBUTE_X, x)
        processor.setAttribute(TGMoveToAxisPositionAction.ATTRIBUTE_Y, y)
        processor.setAttribute(TGMoveToAxisPositionAction.ATTRIBUTE_REQUEST_SMART_MENU, requestSmartMenu)
        processor.processOnNewThread()
    }
}
