package app.tuxguitar.android.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import app.tuxguitar.action.TGActionException
import app.tuxguitar.android.R
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.activity.TGActivityActionBarController
import app.tuxguitar.event.TGEventManager
import app.tuxguitar.util.TGContext

abstract class TGBaseFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onPostCreate(savedInstanceState)
        fireEvent(TGFragmentEvent.ACTION_CREATED)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        onPostCreateOptionsMenu(menu, inflater)
        fireEvent(TGFragmentEvent.ACTION_OPTIONS_MENU_CREATED)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val createdView = super.onCreateView(inflater, container, savedInstanceState)
        val view = onPostCreateView(inflater, container, savedInstanceState, createdView)
        fireEvent(TGFragmentEvent.ACTION_VIEW_CREATED)
        return view
    }

    open fun onPostCreate(savedInstanceState: Bundle?) {
    }

    open fun onPostCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
    }

    open fun onPostCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
        createdView: View?,
    ): View? = createdView

    fun findContext(): TGContext = findActivity().findContext()

    fun findActivity(): TGActivity = TGActivity.requireCurrent()

    fun findActionBar(): TGActivityActionBarController = findActivity().getActionBarController()

    fun isReady(): Boolean = view != null && isVisible

    @Throws(TGActionException::class)
    fun fireEvent(action: String) {
        TGEventManager.getInstance(findContext()).fireEvent(TGFragmentEvent(this, action))
    }

    fun createActionBar(hasOptionsMenu: Boolean, showIcon: Boolean, title: String?) {
        setHasOptionsMenu(hasOptionsMenu)
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
    }

    fun createActionBar(hasOptionsMenu: Boolean, showIcon: Boolean, titleId: Int) {
        createActionBar(hasOptionsMenu, showIcon, requireActivity().getString(titleId))
    }

    fun postWhenReady(runnable: Runnable) {
        Thread {
            if (!isReady()) {
                postWhenReady(runnable)
            } else {
                requireView().post(runnable)
            }
        }.start()
    }
}
