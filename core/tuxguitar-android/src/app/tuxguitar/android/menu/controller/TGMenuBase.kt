package app.tuxguitar.android.menu.controller

import android.view.Menu
import android.view.MenuItem
import app.tuxguitar.android.action.TGActionProcessorListener
import app.tuxguitar.android.action.impl.gui.TGOpenDialogAction
import app.tuxguitar.android.action.impl.gui.TGOpenMenuAction
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.application.TGApplicationUtil
import app.tuxguitar.android.view.dialog.TGDialogController
import app.tuxguitar.android.view.dialog.confirm.TGConfirmDialogController
import app.tuxguitar.editor.action.TGActionProcessor
import app.tuxguitar.util.TGContext

abstract class TGMenuBase(private val activity: TGActivity) : TGMenuController {
    fun getActivity(): TGActivity = activity

    fun findContext(): TGContext = TGApplicationUtil.findContext(activity)

    fun initializeItem(
        menu: Menu,
        id: Int,
        listener: MenuItem.OnMenuItemClickListener,
        enabled: Boolean,
        checked: Boolean
    ) {
        menu.findItem(id)!!.apply {
            setOnMenuItemClickListener(listener)
            isEnabled = enabled
            isChecked = checked
            isVisible = true
        }
    }

    fun initializeItem(menu: Menu, id: Int, listener: MenuItem.OnMenuItemClickListener, enabled: Boolean) {
        initializeItem(menu, id, listener, enabled, false)
    }

    fun initializeItem(menu: Menu, id: Int, dialogController: TGDialogController, enabled: Boolean) {
        initializeItem(menu, id, createDialogActionProcessor(dialogController), enabled)
    }

    fun initializeItem(menu: Menu, id: Int, contextMenuController: TGMenuController, enabled: Boolean) {
        initializeItem(menu, id, createContextMenuActionProcessor(contextMenuController), enabled)
    }

    fun createActionProcessor(actionId: String): TGActionProcessorListener =
        TGActionProcessorListener(findContext(), actionId)

    fun createDialogActionProcessor(controller: TGDialogController): TGActionProcessorListener =
        createActionProcessor(TGOpenDialogAction.NAME).apply {
            setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_CONTROLLER, controller)
            setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_ACTIVITY, activity)
        }

    fun createContextMenuActionProcessor(controller: TGMenuController): TGActionProcessorListener =
        createActionProcessor(TGOpenMenuAction.NAME).apply {
            setAttribute(TGOpenMenuAction.ATTRIBUTE_MENU_CONTROLLER, controller)
            setAttribute(TGOpenMenuAction.ATTRIBUTE_MENU_ACTIVITY, activity)
        }

    fun createConfirmableActionProcessor(
        actionProcessor: TGActionProcessor,
        confirmMessage: String
    ): TGActionProcessorListener =
        createDialogActionProcessor(TGConfirmDialogController()).apply {
            setAttribute(TGConfirmDialogController.ATTRIBUTE_MESSAGE, confirmMessage)
            setAttribute(TGConfirmDialogController.ATTRIBUTE_RUNNABLE, Runnable { actionProcessor.process() })
        }
}
