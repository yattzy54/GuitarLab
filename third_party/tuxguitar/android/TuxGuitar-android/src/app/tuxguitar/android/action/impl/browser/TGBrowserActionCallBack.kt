package app.tuxguitar.android.action.impl.browser

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.action.TGActionException
import app.tuxguitar.action.TGActionManager
import app.tuxguitar.android.action.TGActionAsyncProcess
import app.tuxguitar.android.action.TGActionBase
import app.tuxguitar.tools.browser.base.TGBrowserCallBack

abstract class TGBrowserActionCallBack<T>(
    action: TGActionBase,
    actionContext: TGActionContext
) : TGActionAsyncProcess(action, actionContext), TGBrowserCallBack<T> {
    init { onStart() }

    override fun onSuccess(successData: T) = callRunnableActionInNewThread(successData)

    fun callRunnableActionInNewThread(successData: T) {
        Thread { callRunnableActionInCurrentThread(successData) }.start()
    }

    fun callRunnableActionInCurrentThread(successData: T) {
        try {
            getActionContext()!!.setAttribute(
                TGBrowserRunnableAction.ATTRIBUTE_RUNNABLE,
                createOnActionSuccessRunnable(successData)
            )
            TGActionManager.getInstance(getAction().getContext())
                .execute(TGBrowserRunnableAction.NAME, getActionContext()!!)
            onFinish()
        } catch (exception: TGActionException) {
            handleError(exception)
        }
    }

    fun createOnActionSuccessRunnable(successData: T): Runnable =
        Runnable { onActionSuccess(successData) }

    fun onActionSuccess(successData: T) {
        onActionSuccess(getActionContext()!!, successData)
    }

    abstract fun onActionSuccess(actionContext: TGActionContext, successData: T)
}
