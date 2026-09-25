package app.tuxguitar.android.navigation

import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import app.tuxguitar.android.action.impl.gui.TGOpenFragmentAction
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.fragment.TGFragmentController
import app.tuxguitar.android.fragment.TGScreen
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.event.TGEventListener
import app.tuxguitar.event.TGEventManager
import app.tuxguitar.util.TGContext

/**
 * Drives which [TGScreen] is currently shown inside [TGActivity]'s content
 * area. Previously this replaced a Fragment inside `content_frame` via a
 * `FragmentManager` transaction; now it just swaps a Compose state holder
 * that [TGActivity]'s own content composable reads to decide which screen's
 * [TGScreen.Content] to render.
 */
class TGNavigationManager(private val activity: TGActivity) {
    private val navigationFragments = mutableListOf<TGNavigationFragment>()

    var currentScreen: TGScreen? by mutableStateOf(null)
        private set

    fun initialize() {
        navigationFragments.clear()
        currentScreen = null
    }

    fun processLoadFragment(controller: TGFragmentController<*>, tagId: String?) {
        processLoadFragment(TGNavigationFragment().apply {
            this.controller = controller
            this.tagId = tagId
        })
    }

    /**
     * [app.tuxguitar.android.action.impl.gui.TGOpenFragmentAction] (like
     * most actions in this codebase) runs off the main thread by default, so
     * the actual screen swap - which touches Compose state and the Toolbar -
     * is posted to the main thread here, matching how the previous
     * `FragmentManager.commitAllowingStateLoss()` call tolerated being
     * invoked from a background thread.
     */
    fun processLoadFragment(fragment: TGNavigationFragment) {
        Handler(Looper.getMainLooper()).post { doProcessLoadFragment(fragment) }
    }

    private fun doProcessLoadFragment(fragment: TGNavigationFragment) {
        val previousScreen = currentScreen
        val newScreen = fragment.controller!!.getFragment()
        previousScreen?.onHideView()
        currentScreen = newScreen
        newScreen.ensureCreated()
        newScreen.onShowView()
        activity.rebuildOptionsMenu()

        var backFrom: TGNavigationFragment? = null
        val index = navigationFragments.indexOf(fragment)
        if (index >= 0) {
            while (navigationFragments.size > index) {
                val removed = navigationFragments.removeAt(navigationFragments.lastIndex)
                if (removed != fragment) backFrom = removed
            }
        }
        navigationFragments.add(fragment)
        fireNavigationEvent(fragment, backFrom)
    }

    fun getCurrentFragment(): TGNavigationFragment? = navigationFragments.lastOrNull()
    fun getPreviousFragment(): TGNavigationFragment? =
        if (navigationFragments.size > 1) navigationFragments[navigationFragments.size - 2] else null
    fun removeLastFragment() {
        if (navigationFragments.isNotEmpty()) navigationFragments.removeAt(navigationFragments.lastIndex)
    }
    fun hasPreviousFragment() = getPreviousFragment() != null
    fun findContext(): TGContext = activity.findContext()

    fun callOpenPreviousFragment(): Boolean {
        val previous = getPreviousFragment() ?: return false
        callOpenFragment(previous)
        return true
    }

    fun callOpenFragment(fragment: TGNavigationFragment) =
        callOpenFragment(fragment.controller!!, fragment.tagId)
    fun callOpenFragment(controller: TGFragmentController<*>) = callOpenFragment(controller, null)
    fun callOpenFragment(controller: TGFragmentController<*>, tagId: String?) {
        TGActionProcessor(findContext(), TGOpenFragmentAction.NAME).apply {
            setAttribute(TGOpenFragmentAction.ATTRIBUTE_ACTIVITY, activity)
            setAttribute(TGOpenFragmentAction.ATTRIBUTE_CONTROLLER, controller)
            setAttribute(TGOpenFragmentAction.ATTRIBUTE_TAG_ID, tagId)
            processOnNewThread()
        }
    }

    fun addNavigationListener(listener: TGEventListener) =
        TGEventManager.getInstance(findContext()).addListener(TGNavigationEvent.EVENT_TYPE, listener)
    fun removeNavigationListener(listener: TGEventListener) =
        TGEventManager.getInstance(findContext()).removeListener(TGNavigationEvent.EVENT_TYPE, listener)
    fun fireNavigationEvent(fragment: TGNavigationFragment, backFrom: TGNavigationFragment?) =
        TGEventManager.getInstance(findContext()).fireEvent(TGNavigationEvent(fragment, backFrom))
}
