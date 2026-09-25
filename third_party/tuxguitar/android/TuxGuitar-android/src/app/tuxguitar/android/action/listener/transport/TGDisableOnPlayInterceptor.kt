package app.tuxguitar.android.action.listener.transport

import app.tuxguitar.action.*
import app.tuxguitar.android.activity.*
import app.tuxguitar.player.base.MidiPlayer
import app.tuxguitar.util.TGContext

class TGDisableOnPlayInterceptor(private val context: TGContext) : TGActionInterceptor {
 private val actionIds=mutableListOf<String>()
 fun containsActionId(id:String)=actionIds.contains(id); fun addActionId(id:String)=actionIds.add(id); fun removeActionId(id:String)=actionIds.remove(id)
 override fun intercept(id:String, actionContext:TGActionContext):Boolean { if (!containsActionId(id)) return false; val intercepted=MidiPlayer.getInstance(context).isRunning; if(intercepted) TGActivityController.getInstance(context).activity?.updateCache(true); return intercepted }
}
