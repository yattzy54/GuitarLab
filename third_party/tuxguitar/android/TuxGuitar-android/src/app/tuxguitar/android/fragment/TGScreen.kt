package app.tuxguitar.android.fragment

import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuInflater
import androidx.compose.runtime.Composable
import app.tuxguitar.action.TGActionException
import app.tuxguitar.android.R
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.activity.TGActivityActionBarController
import app.tuxguitar.event.TGEventManager
import app.tuxguitar.util.TGContext

/**
 * Base class for the app's full-screen destinations (previously
 * `TGBaseFragment`/`TGCachedFragment`/`TGComposeCachedFragment`, all Fragment
 * subclasses). A screen is now a plain object rendered by [TGActivity]'s own
 * Compose content, swapped by [app.tuxguitar.android.navigation.TGNavigationManager]
 * instead of a `FragmentManager` transaction: there is no Android View
 * inflation/lifecycle to bridge, just a single [Content] composable that is
 * created once and shown/hidden as navigation moves between screens.
 */
abstract class TGScreen {
    var hasOptionsMenu: Boolean = false
        private set

    @Composable
    abstract fun Content()

    open fun onPostCreate() {
    }

    open fun onPostCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
    }

    open fun onShowView() {
    }

    open fun onHideView() {
    }

    /**
     * Runs [onPostCreate], mirroring the previous Fragment's `onCreate`
     * being re-invoked every time it was swapped back into `content_frame`
     * by a navigation transaction.
     */
    fun ensureCreated() {
        onPostCreate()
        fireEvent(TGFragmentEvent.ACTION_CREATED)
    }

    fun findContext(): TGContext = findActivity().findContext()

    fun findActivity(): TGActivity = TGActivity.requireCurrent()

    fun getString(resId: Int): String = findActivity().getString(resId)
    fun getString(resId: Int, vararg formatArgs: Any?): String = findActivity().getString(resId, *formatArgs)

    fun findActionBar(): TGActivityActionBarController = findActivity().getActionBarController()

    @Throws(TGActionException::class)
    fun fireEvent(action: String) {
        TGEventManager.getInstance(findContext()).fireEvent(TGFragmentEvent(this, action))
    }

    fun createActionBar(hasOptionsMenu: Boolean, showIcon: Boolean, title: String?) {
        this.hasOptionsMenu = hasOptionsMenu
        findActionBar().setDisplayUseLogoEnabled(showIcon)
        findActionBar().setDisplayShowHomeEnabled(showIcon)
        findActionBar().setDisplayShowTitleEnabled(title != null)

        if (showIcon) {
            findActionBar().setLogo(R.drawable.ic_launcher)
            findActionBar().setLogo(R.drawable.ic_launcher)
        }

        if (title != null) {
            findActionBar().setTitle(title)
        }
        findActivity().rebuildOptionsMenu()
    }

    fun createActionBar(hasOptionsMenu: Boolean, showIcon: Boolean, titleId: Int) {
        createActionBar(hasOptionsMenu, showIcon, findActivity().requireContext().getString(titleId))
    }

    fun postWhenReady(runnable: Runnable) {
        Handler(Looper.getMainLooper()).post(runnable)
    }
}
