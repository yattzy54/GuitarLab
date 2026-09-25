package app.tuxguitar.android.menu.controller

import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import app.tuxguitar.editor.TGEditorManager
import app.tuxguitar.editor.event.TGUpdateEvent
import app.tuxguitar.event.TGEvent
import app.tuxguitar.event.TGEventListener
import app.tuxguitar.util.TGAbstractContext
import app.tuxguitar.util.TGContext
import app.tuxguitar.util.TGSynchronizer

class TGMenuCabCallBack(
    private val context: TGContext,
    private val controller: TGMenuController,
    private val selectableView: View?
) : TGEventListener, ActionMode.Callback {
    private var actionMode: ActionMode? = null

    override fun onCreateActionMode(actionMode: ActionMode, menu: Menu): Boolean {
        this.actionMode = actionMode
        controller.inflate(menu, actionMode.menuInflater)
        selectableView?.isActivated = true
        addEventListener()
        return true
    }

    override fun onDestroyActionMode(actionMode: ActionMode) {
        selectableView?.isActivated = false
    }

    override fun onActionItemClicked(actionMode: ActionMode, menuItem: MenuItem): Boolean = false

    override fun onPrepareActionMode(actionMode: ActionMode, menu: Menu): Boolean = false

    fun finish() {
        actionMode?.let {
            it.finish()
            actionMode = null
        }
    }

    fun addEventListener() {
        TGEditorManager.getInstance(context).addUpdateListener(this)
    }

    fun removeEventListener() {
        TGEditorManager.getInstance(context).removeUpdateListener(this)
    }

    fun shouldByPassEvent(event: TGEvent): Boolean {
        if (TGUpdateEvent.EVENT_TYPE != event.eventType) {
            return true
        }
        val sourceContext = event.getAttribute(TGEvent.ATTRIBUTE_SOURCE_CONTEXT) as? TGAbstractContext
            ?: return false
        val bypassValue: Any? = sourceContext.getAttribute<Any>(ATTRIBUTE_BY_PASS_CLOSE_MENU)
        return bypassValue == true
    }

    override fun processEvent(event: TGEvent) {
        if (!shouldByPassEvent(event)) {
            removeEventListener()
            TGSynchronizer.getInstance(context).executeLater { finish() }
        }
    }

    companion object {
        const val ATTRIBUTE_BY_PASS_CLOSE_MENU =
            "app.tuxguitar.android.menu.controller.TGMenuCabCallBack-byPassCloseMenu"
    }
}
