package app.tuxguitar.android.action.listener.gui
import android.content.Context
import android.view.inputmethod.InputMethodManager
import app.tuxguitar.action.TGActionContext
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.event.*
import app.tuxguitar.util.*
class TGHideSoftInputListener(private val context:TGContext, private val activity:TGActivity):TGEventListener {
 companion object { const val ATTRIBUTE_BY_PASS="app.tuxguitar.android.action.listener.gui.TGHideSoftInputListener-byPass"; const val ATTRIBUTE_DONE="app.tuxguitar.android.action.listener.gui.TGHideSoftInputListener-done" }
 fun hideSoftInputFromWindow(){ activity.currentFocus?.let { (activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(it.windowToken,0) } }
 fun hideSoftInputFromWindowInUiThread(){ TGSynchronizer.getInstance(context).executeLater { hideSoftInputFromWindow() } }
 private fun actionContext(e:TGEvent)=e.getAttribute<TGActionContext>(TGEvent.ATTRIBUTE_SOURCE_CONTEXT)
 override fun processEvent(e:TGEvent){ val c=actionContext(e); if(c.getAttribute<Boolean>(ATTRIBUTE_BY_PASS)!=true && c.getAttribute<Boolean>(ATTRIBUTE_DONE)!=true){hideSoftInputFromWindowInUiThread();c.setAttribute(ATTRIBUTE_DONE,true)} }
}
