package app.tuxguitar.android.menu.controller.impl.fragment

import android.view.Menu
import android.view.MenuInflater
import app.tuxguitar.android.R
import app.tuxguitar.android.action.TGActionProcessorListener
import app.tuxguitar.android.action.impl.browser.TGBrowserCdRootAction
import app.tuxguitar.android.action.impl.browser.TGBrowserCdUpAction
import app.tuxguitar.android.action.impl.browser.TGBrowserRefreshAction
import app.tuxguitar.android.action.impl.gui.TGOpenDialogAction
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.activity.TGActivityController
import app.tuxguitar.android.browser.TGBrowserManager
import app.tuxguitar.android.browser.model.TGBrowserSession
import app.tuxguitar.android.menu.controller.TGMenuController
import app.tuxguitar.android.view.dialog.TGDialogController
import app.tuxguitar.android.view.dialog.browser.collection.TGBrowserCollectionsDialogController
import app.tuxguitar.util.TGContext
import app.tuxguitar.util.singleton.TGSingletonFactory
import app.tuxguitar.util.singleton.TGSingletonUtil

class TGBrowserMenu private constructor(private val context: TGContext) : TGMenuController {
    override fun inflate(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_browser, menu)
        initializeItems(menu)
    }

    fun initializeItems(menu: Menu) {
        menu.findItem(R.id.action_browser_settings)!!
            .setOnMenuItemClickListener(createOpenDialogAction(TGBrowserCollectionsDialogController()))
        menu.findItem(R.id.action_browser_root)!!
            .setOnMenuItemClickListener(createBrowserAction(TGBrowserCdRootAction.NAME))
        menu.findItem(R.id.action_browser_back)!!
            .setOnMenuItemClickListener(createBrowserAction(TGBrowserCdUpAction.NAME))
        menu.findItem(R.id.action_browser_refresh)!!
            .setOnMenuItemClickListener(createBrowserAction(TGBrowserRefreshAction.NAME))
    }

    fun createActionProcessor(actionId: String): TGActionProcessorListener =
        TGActionProcessorListener(context, actionId)

    fun createBrowserAction(actionId: String): TGActionProcessorListener =
        createActionProcessor(actionId).apply {
            setAttribute(
                TGBrowserSession::class.java.name,
                TGBrowserManager.getInstance(context).session
            )
        }

    fun createOpenDialogAction(controller: TGDialogController): TGActionProcessorListener =
        createActionProcessor(TGOpenDialogAction.NAME).apply {
            setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_ACTIVITY, getActivity())
            setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_CONTROLLER, controller)
        }

    fun getContext(): TGContext = context

    fun getActivity(): TGActivity = TGActivityController.getInstance(context).activity!!

    companion object {
        @JvmStatic
        fun getInstance(context: TGContext): TGBrowserMenu =
            TGSingletonUtil.getInstance(
                context,
                TGBrowserMenu::class.java.name,
                object : TGSingletonFactory<TGBrowserMenu> {
                    override fun createInstance(context: TGContext) = TGBrowserMenu(context)
                }
            )
    }
}
