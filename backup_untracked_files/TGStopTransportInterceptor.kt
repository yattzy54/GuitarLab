package app.tuxguitar.android.action.listener.transport

import app.tuxguitar.action.*
import app.tuxguitar.android.action.impl.transport.TGTransportStopAction
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.player.base.MidiPlayer
import app.tuxguitar.util.TGContext

class TGStopTransportInterceptor(private val context: TGContext) : TGActionInterceptor {
 private val actionIds=mutableListOf<String>()
 fun containsActionId(id:String)=actionIds.contains(id); fun addActionId(id:String)=actionIds.add(id); fun removeActionId(id:String)=actionIds.remove(id)
 override fun intercept(id:String, actionContext:TGActionContext):Boolean { if(containsActionId(id)&&MidiPlayer.getInstance(context).isRunning){ TGActionProcessor(context,TGTransportStopAction.NAME).also { it.setOnFinish(executeInterceptedActionThread(id,actionContext));it.process() }; return true };return false }
 fun executeInterceptedActionThread(id:String,c:TGActionContext)=Runnable { Thread { executeInterceptedAction(id,c) }.start() }
 fun executeInterceptedAction(id:String,c:TGActionContext){try{TGActionManager.getInstance(context).execute(id,c)}catch(e:TGActionException){e.printStackTrace()}}
}
