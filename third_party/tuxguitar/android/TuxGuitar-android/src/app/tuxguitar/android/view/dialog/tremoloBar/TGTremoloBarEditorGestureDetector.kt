package app.tuxguitar.android.view.dialog.tremoloBar

import android.content.Context
import android.view.GestureDetector
import android.view.MotionEvent
import androidx.core.view.GestureDetectorCompat

class TGTremoloBarEditorGestureDetector(
    context: Context,
    private val tremoloBarEditor: TGTremoloBarEditor
) : GestureDetector.SimpleOnGestureListener() {
    private val gestureDetector = GestureDetectorCompat(context, this)

    fun processTouchEvent(event: MotionEvent): Boolean = gestureDetector.onTouchEvent(event)

    override fun onSingleTapUp(event: MotionEvent): Boolean {
        tremoloBarEditor.editPoint(Math.round(event.x).toFloat(), Math.round(event.y).toFloat())
        return true
    }

    override fun onDown(event: MotionEvent): Boolean = true
}
