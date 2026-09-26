package app.tuxguitar.android.action.listener.undoable
import app.tuxguitar.action.*
import app.tuxguitar.android.action.TGActionMap
import app.tuxguitar.editor.undo.*
import app.tuxguitar.editor.undo.impl.TGUndoableEditBase
import app.tuxguitar.event.*
import app.tuxguitar.util.TGContext
class TGUndoableActionListener(private val context:TGContext):TGEventListener{private val controllers=TGActionMap<TGUndoableActionController>();fun getControllers()=controllers;private fun bypass(c:TGActionContext)=c.getAttribute<Boolean>(TGUndoableEditBase.ATTRIBUTE_BY_PASS_UNDOABLE)==true;override fun processEvent(e:TGEvent){val c=e.getAttribute<TGActionContext>(TGEvent.ATTRIBUTE_SOURCE_CONTEXT);if(bypass(c))return;val id=e.getAttribute<String>(TGActionPostExecutionEvent.ATTRIBUTE_ACTION_ID);val u=TGUndoableContext.getInstance(c);if(e.eventType==TGActionPreExecutionEvent.EVENT_TYPE){controllers.get(id)?.let{if(u.undoable==null)u.undoable=TGUndoableJoined(context);it.startUndoable(context,c)?.let(u::addUndoableToCurrentLevel)};u.incrementLevel()}else if(e.eventType==TGActionPostExecutionEvent.EVENT_TYPE){u.decrementLevel();u.getUndoableFromCurrentLevel()?.let{controllers.get(id)?.endUndoable(context,c,it)?.let{e2->u.undoable?.addUndoableEdit(e2)}};if(u.level==0&&u.undoable!=null&&!u.undoable!!.isEmpty()){TGUndoableManager.getInstance(context).addEdit(u.undoable!!.endUndo());u.reset()}}}}
