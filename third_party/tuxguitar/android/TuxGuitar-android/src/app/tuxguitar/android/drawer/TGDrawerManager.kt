package app.tuxguitar.android.drawer

import android.content.res.Configuration
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import app.tuxguitar.action.TGActionManager
import app.tuxguitar.android.R
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.fragment.TGFragmentController
import app.tuxguitar.util.TGContext
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout

class TGDrawerManager(private val activity: TGActivity) {
    private var drawerBuilder: TGDrawerViewBuilder? = null
    private var drawerView: ViewGroup? = null
    private var drawerLayout: DrawerLayout? = null
    private var drawerToggle: ActionBarDrawerToggle? = null
    private var open = false

    fun initialize() {
        drawerView = activity.findViewById(R.id.left_drawer)
        drawerLayout = activity.findViewById(R.id.root_layout)
        drawerToggle = object : ActionBarDrawerToggle(
            activity,
            drawerLayout!!,
            R.string.app_name,
            R.string.app_name
        ) {
            override fun onDrawerClosed(view: View) {
                onVisibilityChanged()
            }

            override fun onDrawerOpened(drawerView: View) {
                onVisibilityChanged()
            }
        }.also { drawerLayout!!.addDrawerListener(it) }
        appendListeners()
    }

    fun appendListeners() {
        val drawerListener = TGDrawerEventListener(this)
        val drawerInterceptor = TGDrawerActionInterceptor(this)
        val actionManager = TGActionManager.getInstance(findContext())
        actionManager.addPostExecutionListener(drawerListener)
        actionManager.addInterceptor(drawerInterceptor)
        activity.getNavigationManager().addNavigationListener(drawerListener)
    }

    fun syncState() {
        drawerToggle?.syncState()
    }

    fun closeDrawer() {
        drawerLayout?.closeDrawer(drawerView!!)
    }

    fun onConfigurationChanged(configuration: Configuration) {
        drawerToggle?.onConfigurationChanged(configuration)
    }

    fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            val toggle = drawerToggle
            if (toggle != null && toggle.isDrawerIndicatorEnabled) {
                return toggle.onOptionsItemSelected(item)
            }
            return activity.getNavigationManager().callOpenPreviousFragment()
        }
        return false
    }

    fun onOpenFragment(controller: TGFragmentController<*>) {
        val view = drawerView!!
        if (view.childCount > 0) {
            view.removeAllViews()
        }
        drawerBuilder?.onOpenFragment(controller, view)
        val available = view.childCount > 0
        drawerToggle?.isDrawerIndicatorEnabled = available
        drawerLayout?.setDrawerLockMode(
            if (available) DrawerLayout.LOCK_MODE_UNLOCKED else DrawerLayout.LOCK_MODE_LOCKED_CLOSED
        )
    }

    fun onVisibilityChanged() {
        open = drawerLayout?.isDrawerOpen(drawerView!!) == true
        if (open) {
            activity.updateCache(true)
        }
    }

    fun setDrawerBuilder(drawerBuilder: TGDrawerViewBuilder?) {
        this.drawerBuilder = drawerBuilder
    }

    fun findContext(): TGContext = activity.findContext()

    fun isOpen(): Boolean = open
}
