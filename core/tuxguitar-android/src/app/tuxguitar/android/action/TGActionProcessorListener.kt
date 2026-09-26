package app.tuxguitar.android.action

import android.view.MenuItem
import android.view.View
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.util.TGContext

class TGActionProcessorListener(
    context: TGContext,
    actionName: String,
) : TGActionProcessor(context, actionName), View.OnClickListener, View.OnLongClickListener,
    MenuItem.OnMenuItemClickListener {

    fun processEvent(eventSource: Any, attributes: Map<String, Any>?) {
        processOnNewThread(processEventAttributes(eventSource, attributes))
    }

    fun processEventAttributes(eventSource: Any, attributes: Map<String, Any>?): Map<String, Any> =
        buildMap {
            put(PROPERTY_EVENT_SOURCE, eventSource)
            attributes?.let(::putAll)
        }

    override fun onClick(view: View) {
        @Suppress("UNCHECKED_CAST")
        processEvent(view, view.tag as? Map<String, Any>)
    }

    override fun onMenuItemClick(item: MenuItem): Boolean {
        processEvent(item, null)
        return true
    }

    override fun onLongClick(view: View): Boolean {
        @Suppress("UNCHECKED_CAST")
        processEvent(view, view.tag as? Map<String, Any>)
        return true
    }

    companion object {
        const val PROPERTY_EVENT_SOURCE = "eventSource"
    }
}
