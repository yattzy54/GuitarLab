package app.tuxguitar.android.menu.util

import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.view.Menu
import android.view.MenuItem
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.view.util.TGProcess
import app.tuxguitar.android.view.util.TGSyncProcessLocked
import app.tuxguitar.editor.TGEditorManager
import app.tuxguitar.editor.event.TGUpdateEvent
import app.tuxguitar.event.TGEvent
import app.tuxguitar.event.TGEventListener
import app.tuxguitar.util.TGContext

class TGToggleStyledIconHelper(private val context: TGContext) : TGEventListener {
    private var activity: TGActivity? = null
    private var menu: Menu? = null
    private val styledIcons = mutableMapOf<Int, Drawable?>()
    private val handlers = mutableListOf<TGToggleStyledIconHandler>()
    private lateinit var updateIcons: TGProcess

    init {
        createSyncProcesses()
        appendListeners()
    }

    fun initialize(activity: TGActivity, menu: Menu) {
        this.activity = activity
        this.menu = menu
        updateIcons.process()
    }

    fun addHandler(handler: TGToggleStyledIconHandler) {
        handlers.add(handler)
    }

    fun appendListeners() {
        TGEditorManager.getInstance(context).addUpdateListener(this)
    }

    fun findStyledDrawable(style: Int): Drawable? {
        if (styledIcons.containsKey(style)) {
            return styledIcons[style]
        }
        val currentActivity = activity ?: return null
        val typedArray: TypedArray = currentActivity.requireContext().obtainStyledAttributes(
            style,
            intArrayOf(android.R.attr.src)
        )
        val drawable = typedArray.getDrawable(0)
        typedArray.recycle()
        styledIcons[style] = drawable
        return drawable
    }

    fun updateIcon(handler: TGToggleStyledIconHandler?) {
        val currentMenu = menu ?: return
        val menuItemId = handler?.getMenuItemId() ?: return
        val style = handler.resolveStyle() ?: return
        val menuItem: MenuItem = currentMenu.findItem(menuItemId) ?: return
        val drawable = findStyledDrawable(style) ?: return
        if (menuItem.icon == null || menuItem.icon != drawable) {
            menuItem.icon = drawable
        }
    }

    fun updateIcons() {
        if (menu != null) {
            handlers.forEach(::updateIcon)
        }
    }

    fun createSyncProcesses() {
        updateIcons = TGSyncProcessLocked(context, Runnable { updateIcons() })
    }

    fun processUpdateEvent(event: TGEvent) {
        val type = event.getAttribute(TGUpdateEvent.PROPERTY_UPDATE_MODE) as Int
        if (type == TGUpdateEvent.SELECTION) {
            updateIcons.process()
        }
    }

    override fun processEvent(event: TGEvent) {
        if (TGUpdateEvent.EVENT_TYPE == event.eventType) {
            processUpdateEvent(event)
        }
    }
}
