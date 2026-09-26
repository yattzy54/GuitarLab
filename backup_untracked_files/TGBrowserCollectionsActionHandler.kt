package app.tuxguitar.android.view.dialog.browser.collection

import app.tuxguitar.android.action.TGActionProcessorListener
import app.tuxguitar.android.action.impl.browser.TGBrowserAddCollectionAction
import app.tuxguitar.android.action.impl.browser.TGBrowserRemoveCollectionAction
import app.tuxguitar.android.action.impl.gui.TGOpenDialogAction
import app.tuxguitar.android.action.impl.gui.TGOpenMenuAction
import app.tuxguitar.android.menu.controller.TGMenuController
import app.tuxguitar.android.view.dialog.TGDialogController
import app.tuxguitar.tools.browser.TGBrowserCollection

class TGBrowserCollectionsActionHandler(private val view: TGBrowserCollectionsDialog) {
    fun createAction(actionId: String): TGActionProcessorListener =
        TGActionProcessorListener(view.findContext(), actionId)

    fun createAddCollectionAction(collection: TGBrowserCollection): TGActionProcessorListener =
        createAction(TGBrowserAddCollectionAction.NAME).also {
            it.setAttribute(TGBrowserAddCollectionAction.ATTRIBUTE_COLLECTION, collection)
        }

    fun createRemoveCollectionAction(collection: TGBrowserCollection): TGActionProcessorListener =
        createAction(TGBrowserRemoveCollectionAction.NAME).also {
            it.setAttribute(TGBrowserRemoveCollectionAction.ATTRIBUTE_COLLECTION, collection)
        }

    fun createOpenDialogAction(controller: TGDialogController): TGActionProcessorListener =
        createAction(TGOpenDialogAction.NAME).also {
            it.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_ACTIVITY, view.findActivity())
            it.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_CONTROLLER, controller)
        }

    fun createOpenMenuAction(controller: TGMenuController): TGActionProcessorListener =
        createAction(TGOpenMenuAction.NAME).also {
            it.setAttribute(TGOpenMenuAction.ATTRIBUTE_MENU_ACTIVITY, view.findActivity())
            it.setAttribute(TGOpenMenuAction.ATTRIBUTE_MENU_CONTROLLER, controller)
        }
}
