package app.tuxguitar.android.action.listener.gui
import app.tuxguitar.action.*
import app.tuxguitar.android.action.*
import app.tuxguitar.android.action.impl.gui.TGFinishAction
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.view.processing.TGActionProcessingController
import app.tuxguitar.event.*
import app.tuxguitar.util.TGContext
class TGActionProcessingListener(context:TGContext,activity:TGActivity):TGEventListener { private val controller=TGActionProcessingController(context,activity); private var level=0; private val pre=arrayOf(TGActionPreExecutionEvent.EVENT_TYPE,TGActionAsyncProcessStartEvent.EVENT_TYPE);private val post=arrayOf(TGActionPostExecutionEvent.EVENT_TYPE,TGActionAsyncProcessFinishEvent.EVENT_TYPE);private val errors=arrayOf(TGActionErrorEvent.EVENT_TYPE,TGActionAsyncProcessErrorEvent.EVENT_TYPE); fun resetLevel(){level=0};fun finish(){controller.finish()}; override fun processEvent(e:TGEvent){if(e.getAttribute<String>(TGActionEvent.ATTRIBUTE_ACTION_ID)==TGFinishAction.NAME){finish();return};if(!controller.isFinished()){synchronized(this){when{pre.contains(e.eventType)->{if(level==0)controller.update(true);level++};post.contains(e.eventType)||errors.contains(e.eventType)->{level--;if(level==0)controller.update(false)}}}}}}
