package app.tuxguitar.android.action

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.action.TGActionException
import app.tuxguitar.event.TGEventManager
import app.tuxguitar.util.error.TGErrorHandler

class TGActionAsyncProcess(
    private val action: TGActionBase,
    private val actionContext: TGActionContext,
) : TGErrorHandler {
    fun getAction(): TGActionBase = action
    fun getActionContext(): TGActionContext? = actionContext
    private fun eventManager(): TGEventManager = TGEventManager.getInstance(action.context)

    fun onStart() {
        eventManager().fireEvent(TGActionAsyncProcessStartEvent(action.name, actionContext))
    }

    fun onFinish() {
        eventManager().fireEvent(TGActionAsyncProcessFinishEvent(action.name, actionContext))
    }

    override fun handleError(throwable: Throwable) {
        eventManager().fireEvent(TGActionAsyncProcessErrorEvent(action.name, actionContext, throwable))
        val handled = actionContext.getAttribute<Boolean>(ATTRIBUTE_ERROR_HANDLED) ?: false
        if (!handled) throw TGActionException(throwable)
    }

    companion object {
        const val ATTRIBUTE_ERROR_HANDLED = "errorHandled"
    }
}
