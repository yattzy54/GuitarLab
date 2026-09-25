package app.tuxguitar.android.action.impl.gui

import android.app.Activity
import androidx.core.app.ActivityCompat
import app.tuxguitar.action.TGActionContext
import app.tuxguitar.android.action.TGActionBase
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.util.TGContext

class TGRequestPermissionsAction(context: TGContext) : TGActionBase(context, NAME) {
    override fun processAction(actionContext: TGActionContext) {
        val activity = actionContext.getAttribute<Activity>(ATTRIBUTE_ACTIVITY)
        val permissions = actionContext.getAttribute<Array<String>>(ATTRIBUTE_PERMISSIONS)
        val requestCode = actionContext.getAttribute<Int>(ATTRIBUTE_REQUEST_CODE)
        ActivityCompat.requestPermissions(activity, permissions, requestCode)
    }

    companion object {
        const val NAME = "action.gui.request-permissions"
        const val ATTRIBUTE_ACTIVITY = "app.tuxguitar.android.activity.TGActivity"
        const val ATTRIBUTE_PERMISSIONS = "permissions"
        const val ATTRIBUTE_REQUEST_CODE = "requestCode"
    }
}
