package app.tuxguitar.android.action.listener.lock
import app.tuxguitar.action.*
import app.tuxguitar.android.action.listener.thread.TGSyncThreadAction
import app.tuxguitar.editor.TGEditorManager
import app.tuxguitar.event.*
import app.tuxguitar.util.TGContext
class TGLockableActionListener(context:TGContext):TGSyncThreadAction(context),TGActionInterceptor,TGEventListener { private val ids=mutableListOf<String>();fun containsActionId(id:String)=ids.contains(id);fun addActionId(id:String)=ids.add(id);fun removeActionId(id:String)=ids.remove(id); override fun intercept(id:String,c:TGActionContext)=if(containsActionId(id)&&isUiThread()){runInUiThread(id,c);true}else false; override fun processEvent(e:TGEvent){val id=e.getAttribute<String>(TGActionEvent.ATTRIBUTE_ACTION_ID);if(e.eventType==TGActionPreExecutionEvent.EVENT_TYPE&&containsActionId(id))TGEditorManager.getInstance(getContext()).lock();if((e.eventType==TGActionPostExecutionEvent.EVENT_TYPE||e.eventType==TGActionErrorEvent.EVENT_TYPE)&&containsActionId(id))TGEditorManager.getInstance(getContext()).unlock()}}
