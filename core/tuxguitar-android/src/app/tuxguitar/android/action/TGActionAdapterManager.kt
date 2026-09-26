package app.tuxguitar.android.action

import app.tuxguitar.action.TGActionContextFactory
import app.tuxguitar.action.TGActionManager
import app.tuxguitar.android.action.installer.TGActionInstaller
import app.tuxguitar.android.action.listener.cache.TGUpdateListener
import app.tuxguitar.android.action.listener.error.TGActionErrorHandler
import app.tuxguitar.android.action.listener.gui.TGActionProcessingListener
import app.tuxguitar.android.action.listener.gui.TGExitConfirmInterceptor
import app.tuxguitar.android.action.listener.gui.TGHideSoftInputListener
import app.tuxguitar.android.action.listener.lock.TGLockableActionListener
import app.tuxguitar.android.action.listener.thread.TGSyncThreadInterceptor
import app.tuxguitar.android.action.listener.transport.TGDisableOnPlayInterceptor
import app.tuxguitar.android.action.listener.transport.TGStopTransportInterceptor
import app.tuxguitar.android.action.listener.undoable.TGUndoableActionListener
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.event.TGEventListener
import app.tuxguitar.event.TGEventManager
import app.tuxguitar.util.TGContext
import app.tuxguitar.util.singleton.TGSingletonFactory
import app.tuxguitar.util.singleton.TGSingletonUtil

class TGActionAdapterManager private constructor(
    private val context: TGContext
) {
    private val actionContextFactory: TGActionContextFactory = TGActionContextFactoryImpl(context)
    private val disableOnPlayInterceptor = TGDisableOnPlayInterceptor(context)
    private val stopTransportInterceptor = TGStopTransportInterceptor(context)
    private val syncThreadInterceptor = TGSyncThreadInterceptor(context)
    private val lockableActionListener = TGLockableActionListener(context)
    private val undoableActionListener = TGUndoableActionListener(context)
    private val updatableActionListener = TGUpdateListener(this)
    private val errorHandler = TGActionErrorHandler(context)

    fun initialize(activity: TGActivity) {
        initializeHandlers(activity)
        TGActionInstaller(this).installDefaultActions()
    }

    private fun initializeHandlers(activity: TGActivity) {
        val processingListener = TGActionProcessingListener(context, activity)
        val hideSoftInputListener = TGHideSoftInputListener(context, activity)
        val actionManager = TGActionManager.getInstance(context)
        actionManager.setActionContextFactory(actionContextFactory)
        actionManager.addInterceptor(TGExitConfirmInterceptor(context, activity))
        actionManager.addInterceptor(stopTransportInterceptor)
        actionManager.addInterceptor(disableOnPlayInterceptor)
        actionManager.addInterceptor(syncThreadInterceptor)
        actionManager.addInterceptor(lockableActionListener)
        actionManager.addPreExecutionListener(processingListener)
        actionManager.addPreExecutionListener(errorHandler)
        actionManager.addPreExecutionListener(lockableActionListener)
        actionManager.addPreExecutionListener(undoableActionListener)
        actionManager.addPreExecutionListener(updatableActionListener)
        actionManager.addPostExecutionListener(updatableActionListener)
        actionManager.addPostExecutionListener(undoableActionListener)
        actionManager.addPostExecutionListener(lockableActionListener)
        actionManager.addPostExecutionListener(errorHandler)
        actionManager.addPostExecutionListener(hideSoftInputListener)
        actionManager.addPostExecutionListener(processingListener)
        actionManager.addErrorListener(updatableActionListener)
        actionManager.addErrorListener(undoableActionListener)
        actionManager.addErrorListener(lockableActionListener)
        actionManager.addErrorListener(errorHandler)
        actionManager.addErrorListener(hideSoftInputListener)
        actionManager.addErrorListener(processingListener)
        addAsyncProcessStartListener(processingListener)
        addAsyncProcessFinishListener(processingListener)
        addAsyncProcessErrorListener(processingListener)
        addAsyncProcessStartListener(errorHandler)
        addAsyncProcessFinishListener(errorHandler)
        addAsyncProcessErrorListener(errorHandler)
    }

    fun getContext(): TGContext = context
    fun getDisableOnPlayInterceptor() = disableOnPlayInterceptor
    fun getStopTransportInterceptor() = stopTransportInterceptor
    fun getSyncThreadInterceptor() = syncThreadInterceptor
    fun getLockableActionListener() = lockableActionListener
    fun getUndoableActionListener() = undoableActionListener
    fun getUpdatableActionListener() = updatableActionListener

    fun addAsyncProcessStartListener(listener: TGEventListener) {
        TGEventManager.getInstance(context).addListener(TGActionAsyncProcessStartEvent.EVENT_TYPE, listener)
    }

    fun removeAsyncProcessStartListener(listener: TGEventListener) {
        TGEventManager.getInstance(context).removeListener(TGActionAsyncProcessStartEvent.EVENT_TYPE, listener)
    }

    fun addAsyncProcessFinishListener(listener: TGEventListener) {
        TGEventManager.getInstance(context).addListener(TGActionAsyncProcessFinishEvent.EVENT_TYPE, listener)
    }

    fun removeAsyncProcessEndListener(listener: TGEventListener) {
        TGEventManager.getInstance(context).removeListener(TGActionAsyncProcessFinishEvent.EVENT_TYPE, listener)
    }

    fun addAsyncProcessErrorListener(listener: TGEventListener) {
        TGEventManager.getInstance(context).addListener(TGActionAsyncProcessErrorEvent.EVENT_TYPE, listener)
    }

    fun removeAsyncProcessErrorListener(listener: TGEventListener) {
        TGEventManager.getInstance(context).removeListener(TGActionAsyncProcessErrorEvent.EVENT_TYPE, listener)
    }

    companion object {
        @JvmStatic
        fun getInstance(context: TGContext): TGActionAdapterManager =
            TGSingletonUtil.getInstance(
                context,
                TGActionAdapterManager::class.java.name,
                object : TGSingletonFactory<TGActionAdapterManager> {
                    override fun createInstance(context: TGContext) = TGActionAdapterManager(context)
                }
            )
    }
}
