package app.tuxguitar.android.action.listener.error

import app.tuxguitar.action.*
import app.tuxguitar.android.action.*
import app.tuxguitar.event.*
import app.tuxguitar.util.TGContext
import app.tuxguitar.util.error.*

class TGActionErrorHandler(private val context: TGContext) : TGEventListener {
 companion object { const val ATTRIBUTE_ACTION_LEVEL = "app.tuxguitar.android.action.listener.error.TGActionErrorHandler-level"; const val ATTRIBUTE_ERROR_HANDLER = "app.tuxguitar.util.error.TGErrorHandler"; const val ATTRIBUTE_ERROR_HANDLED = "errorHandled" }
 private val pre = arrayOf(TGActionPreExecutionEvent.EVENT_TYPE, TGActionAsyncProcessStartEvent.EVENT_TYPE)
 private val post = arrayOf(TGActionPostExecutionEvent.EVENT_TYPE, TGActionAsyncProcessFinishEvent.EVENT_TYPE)
 private val errors = arrayOf(TGActionErrorEvent.EVENT_TYPE, TGActionAsyncProcessErrorEvent.EVENT_TYPE)
 private fun level(c: TGActionContext) = c.getAttribute<Int>(ATTRIBUTE_ACTION_LEVEL)
 fun incrementLevel(c: TGActionContext) { c.setAttribute(ATTRIBUTE_ACTION_LEVEL, (level(c) ?: 0) + 1) }
 fun decrementLevel(c: TGActionContext) { c.setAttribute(ATTRIBUTE_ACTION_LEVEL, (level(c) ?: 1) - 1) }
 fun findActionContext(e: TGEvent) = e.getAttribute<TGActionContext>(TGEvent.ATTRIBUTE_SOURCE_CONTEXT)
 private fun isType(e: TGEvent, types: Array<String>) = types.contains(e.eventType)
 fun processError(t: Throwable, h: TGErrorHandler?) { if (h != null) h.handleError(t) else TGErrorManager.getInstance(context).handleError(t) }
 fun processErrorEvent(e: TGEvent) { val c=findActionContext(e); if ((level(c) ?: 0)==0) { processError(e.getAttribute(TGActionErrorEvent.PROPERTY_ACTION_ERROR), c.getAttribute(ATTRIBUTE_ERROR_HANDLER)); c.setAttribute(ATTRIBUTE_ERROR_HANDLED,true) } }
 override fun processEvent(e: TGEvent) { val c=findActionContext(e); when { isType(e,pre)->incrementLevel(c); isType(e,post)->decrementLevel(c); isType(e,errors)->{decrementLevel(c);processErrorEvent(e)} } }
}
