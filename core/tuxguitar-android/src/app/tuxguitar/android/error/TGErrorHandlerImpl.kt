package app.tuxguitar.android.error

import android.os.Environment
import app.tuxguitar.android.R
import app.tuxguitar.android.action.impl.gui.TGOpenDialogAction
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.view.dialog.message.TGMessageDialogController
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.util.error.TGErrorHandler
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.io.StringWriter
import java.util.Date

class TGErrorHandlerImpl(private val activity: TGActivity) : TGErrorHandler {
    override fun handleError(throwable: Throwable) {
        throwable.printStackTrace()
        logError(throwable)
        showUserMessage(throwable)
    }

    fun showUserMessage(throwable: Throwable) {
        val tgActionProcessor = TGActionProcessor(activity.findContext(), TGOpenDialogAction.NAME)
        tgActionProcessor.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_ACTIVITY, activity)
        tgActionProcessor.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_CONTROLLER, TGMessageDialogController())
        tgActionProcessor.setAttribute(TGMessageDialogController.ATTRIBUTE_TITLE, MSG_TITLE)
        tgActionProcessor.setAttribute(TGMessageDialogController.ATTRIBUTE_MESSAGE, createHumanErrorMessage(throwable))
        tgActionProcessor.processOnNewThread()
    }

    fun createHumanErrorMessage(throwable: Throwable): String {
        val message = throwable.message
        return if (message == null || message.trim().isEmpty()) activity.getString(R.string.global_error_message) else message
    }

    fun createFullErrorMessage(throwable: Throwable): String {
        val message = StringBuffer()
        message.append(throwable.javaClass.name)
        message.append(EOL)
        message.append(EOL)
        if (throwable.message != null) {
            message.append(throwable.message)
            message.append(EOL)
        }
        message.append(getStackTrace(throwable))
        return message.toString()
    }

    fun getStackTrace(throwable: Throwable): String {
        val stringWriter = StringWriter()
        val printWriter = PrintWriter(stringWriter)
        throwable.printStackTrace(printWriter)
        return stringWriter.toString()
    }

    fun logError(throwable: Throwable) {
        try {
            val logFile = File(LOG_FILE)
            if (!logFile.exists()) {
                logFile.parentFile?.mkdirs()
                logFile.createNewFile()
            }

            if (logFile.exists() && logFile.canWrite()) {
                BufferedWriter(FileWriter(logFile, true)).use { buffer ->
                    buffer.append(Date().toString())
                    buffer.newLine()
                    buffer.append(createFullErrorMessage(throwable))
                    buffer.newLine()
                }
            }
        } catch (e: Throwable) {
            e.printStackTrace()
        }
    }

    companion object {
        @JvmField
        val LOG_FILE: String = Environment.getExternalStorageDirectory().absolutePath + File.separator + "TuxGuitar/log/tuxguitar.log"

        const val MSG_TITLE = "Error"
        const val EOL = "\r\n"
    }
}
