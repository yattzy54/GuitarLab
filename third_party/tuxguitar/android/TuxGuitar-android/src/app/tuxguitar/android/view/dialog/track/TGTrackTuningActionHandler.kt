package app.tuxguitar.android.view.dialog.track

import android.view.MenuItem
import android.view.View
import app.tuxguitar.android.action.TGActionProcessorListener
import app.tuxguitar.android.action.impl.gui.TGOpenCabMenuAction
import app.tuxguitar.android.action.impl.gui.TGOpenDialogAction
import app.tuxguitar.android.menu.controller.TGMenuController
import app.tuxguitar.android.view.dialog.TGDialogController
import app.tuxguitar.editor.TGEditorManager

class TGTrackTuningActionHandler(private val dialog: TGTrackTuningDialog) {
    fun createAction(actionId: String): TGActionProcessorListener =
        TGActionProcessorListener(dialog.findContext(), actionId)

    fun createOpenDialogAction(controller: TGDialogController): TGActionProcessorListener =
        createAction(TGOpenDialogAction.NAME).also {
            it.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_ACTIVITY, dialog.findActivity())
            it.setAttribute(TGOpenDialogAction.ATTRIBUTE_DIALOG_CONTROLLER, controller)
        }

    fun createOpenCabMenuAction(
        controller: TGMenuController,
        selectableView: View
    ): TGActionProcessorListener =
        createAction(TGOpenCabMenuAction.NAME).also {
            it.setAttribute(TGOpenCabMenuAction.ATTRIBUTE_MENU_ACTIVITY, dialog.findActivity())
            it.setAttribute(TGOpenCabMenuAction.ATTRIBUTE_MENU_CONTROLLER, controller)
            it.setAttribute(TGOpenCabMenuAction.ATTRIBUTE_MENU_SELECTABLE_VIEW, selectableView)
        }

    fun createTuningModelMenuAction(
        model: TGTrackTuningModel,
        selectableView: View
    ): TGActionProcessorListener =
        createOpenCabMenuAction(TGTrackTuningListItemMenu(dialog, model), selectableView)

    fun createEditTuningModelAction(model: TGTrackTuningModel): TGActionProcessorListener =
        createOpenDialogAction(TGTrackTuningModelDialogController()).also { processor ->
            processor.setAttribute(TGTrackTuningModelDialogController.ATTRIBUTE_MODEL, model)
            processor.setAttribute(
                TGTrackTuningModelDialogController.ATTRIBUTE_HANDLER,
                object : TGTrackTuningModelHandler {
                    override fun handleSelection(modifiedModel: TGTrackTuningModel) {
                        dialog.postModifyTuningModel(model, modifiedModel.value)
                    }
                }
            )
        }

    fun createAddTuningModelAction(): TGActionProcessorListener =
        createOpenDialogAction(TGTrackTuningModelDialogController()).also { processor ->
            processor.setAttribute(
                TGTrackTuningModelDialogController.ATTRIBUTE_HANDLER,
                object : TGTrackTuningModelHandler {
                    override fun handleSelection(model: TGTrackTuningModel) {
                        dialog.postAddTuningModel(model)
                    }
                }
            )
        }

    fun createRemoveTuningModelAction(model: TGTrackTuningModel): MenuItem.OnMenuItemClickListener =
        MenuItem.OnMenuItemClickListener {
            dialog.postRemoveTuningModel(model)
            TGEditorManager.getInstance(dialog.findContext()).updateSelection()
            true
        }
}
