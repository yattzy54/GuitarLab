package app.tuxguitar.android.action.listener.cache

import app.tuxguitar.action.*
import app.tuxguitar.android.action.*
import app.tuxguitar.event.*
import app.tuxguitar.util.TGAbstractContext

class TGUpdateListener(private val manager: TGActionAdapterManager) : TGEventListener {
 private val controllers = TGActionMap<TGUpdateController>()
 private val buffer = TGUpdateBuffer(manager.getContext())
 private var level = 0
 fun getBuffer() = buffer
 fun getControllers() = controllers
 fun processUpdate(actionId: String, actionContext: TGActionContext) { controllers.get(actionId)?.update(manager.getContext(), actionContext) }
 fun processUpdate(event: TGEvent) { processUpdate(event.getAttribute(TGActionPostExecutionEvent.ATTRIBUTE_ACTION_ID), event.getAttribute(TGActionPostExecutionEvent.ATTRIBUTE_SOURCE_CONTEXT)) }
 fun processPreExecution() { if (level == 0) buffer.clear(); level++ }
 fun processPostExecution(event: TGEvent) { level--; processUpdate(event); if (level == 0) buffer.apply(event.getAttribute<TGAbstractContext>(TGEvent.ATTRIBUTE_SOURCE_CONTEXT)) }
 fun processError() { level = 0; buffer.clear() }
 override fun processEvent(event: TGEvent) { when (event.eventType) { TGActionPreExecutionEvent.EVENT_TYPE -> processPreExecution(); TGActionPostExecutionEvent.EVENT_TYPE -> processPostExecution(event); TGActionErrorEvent.EVENT_TYPE -> processError() } }
}
