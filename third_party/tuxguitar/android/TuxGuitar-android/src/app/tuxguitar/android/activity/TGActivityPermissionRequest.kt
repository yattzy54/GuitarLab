package app.tuxguitar.android.activity

import android.content.pm.PackageManager
import android.os.AsyncTask
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import app.tuxguitar.android.action.impl.gui.TGOpenDialogAction
import app.tuxguitar.android.action.impl.gui.TGRequestPermissionsAction
import app.tuxguitar.android.view.dialog.confirm.TGConfirmDialogController
import app.tuxguitar.editor.action.TGActionProcessor

class TGActivityPermissionRequest(
    private val activity: TGActivity,
    private val permissions: Array<String>,
    private val permissionRationale: String?,
    private val onPermissionGranted: Runnable?,
    private val onPermissionDenied: Runnable?
) {
    private val requestCode = activity.getPermissionResultManager().createRequestCode()
    private val resultHandler = object : TGActivityPermissionResultHandler {
        override fun onRequestPermissionsResult(permissions: Array<String>, grantResults: IntArray) {
            processPermissionRequestResult(permissions, grantResults)
        }
    }

    fun process() {
        addResultHandlers()
        checkPermissionsAsyncTask(false).execute(null)
    }

    private fun addResultHandlers() = activity.getPermissionResultManager().addHandler(requestCode, resultHandler)
    private fun removeResultHandlers() = activity.getPermissionResultManager().removeHandler(requestCode, resultHandler)

    private fun onPermissionGranted() {
        removeResultHandlers()
        onPermissionGranted?.let { createThreadRunnable(it).run() }
    }

    private fun onPermissionDenied() {
        removeResultHandlers()
        onPermissionDenied?.let { createThreadRunnable(it).run() }
    }

    private fun checkPermissions(ignoreRationale: Boolean) {
        val missingPermissions = permissions.filter {
            ContextCompat.checkSelfPermission(activity, it) != PackageManager.PERMISSION_GRANTED
        }
        if (missingPermissions.isEmpty()) {
            onPermissionGranted()
        } else {
            val requiredPermissions = missingPermissions.toTypedArray()
            if (ignoreRationale || !isShowingRequestPermissionRationale(requiredPermissions)) {
                callRequestPermissions(requiredPermissions)
            }
        }
    }

    private fun processPermissionRequestResult(permissions: Array<String>, grantResults: IntArray) {
        if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            onPermissionGranted()
        } else if (!isShowingRequestPermissionRationale(permissions)) {
            onPermissionDenied()
        }
    }

    private fun isShowingRequestPermissionRationale(permissions: Array<String>): Boolean {
        if (permissionRationale != null) {
            for (permission in permissions) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    showRequestPermissionRationale()
                    return true
                }
            }
        }
        return false
    }

    private fun callRequestPermissions(requiredPermissions: Array<String>) {
        TGActionProcessor(activity.findContext(), TGRequestPermissionsAction.NAME).apply {
            setAttribute(TGRequestPermissionsAction.ATTRIBUTE_ACTIVITY, activity)
            setAttribute(TGRequestPermissionsAction.ATTRIBUTE_PERMISSIONS, requiredPermissions)
            setAttribute(TGRequestPermissionsAction.ATTRIBUTE_REQUEST_CODE, requestCode)
            process()
        }
    }

    private fun showRequestPermissionRationale() {
        TGActionProcessor(activity.findContext(), TGOpenDialogAction.NAME).apply {
            setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_CONTROLLER, TGConfirmDialogController())
            setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_ACTIVITY, activity)
            setAttribute(TGConfirmDialogController.ATTRIBUTE_MESSAGE, permissionRationale)
            setAttribute(TGConfirmDialogController.ATTRIBUTE_RUNNABLE, Runnable {
                checkPermissionsAsyncTask(true).execute(null)
            })
            setAttribute(TGConfirmDialogController.ATTRIBUTE_CANCEL_RUNNABLE, Runnable {
                onPermissionDenied()
            })
            process()
        }
    }

    fun createThreadRunnable(target: Runnable): Runnable = Runnable {
        Thread(target).start()
    }

    @Suppress("DEPRECATION")
    private fun checkPermissionsAsyncTask(ignoreRationale: Boolean): AsyncTask<Void, Void, Void> =
        object : AsyncTask<Void, Void, Void>() {
            override fun doInBackground(vararg params: Void?): Void? {
                checkPermissions(ignoreRationale)
                return null
            }
        }
}
