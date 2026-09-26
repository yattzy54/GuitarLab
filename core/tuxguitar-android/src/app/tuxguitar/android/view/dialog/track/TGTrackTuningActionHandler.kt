package app.tuxguitar.android.view.dialog.track

import app.tuxguitar.android.action.TGActionProcessorListener
import app.tuxguitar.android.action.impl.gui.TGOpenDialogAction
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

    fun openEditTuningModelDialog(model: TGTrackTuningModel) {
        createEditTuningModelAction(model).process()
    }

    fun openAddTuningModelDialog() {
        createAddTuningModelAction().process()
    }

    fun removeTuningModel(model: TGTrackTuningModel) {
        dialog.postRemoveTuningModel(model)
        TGEditorManager.getInstance(dialog.findContext()).updateSelection()
    }

    private fun createEditTuningModelAction(model: TGTrackTuningModel): TGActionProcessorListener =
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

    private fun createAddTuningModelAction(): TGActionProcessorListener =
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
}
