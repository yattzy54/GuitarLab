package app.tuxguitar.android.view.dialog.track

import app.tuxguitar.android.view.dialog.compose.TGComposeBottomSheetDialogController

class TGTrackTuningModelDialogController : TGComposeBottomSheetDialogController<TGTrackTuningModelDialog>() {
    override fun createNewInstance(): TGTrackTuningModelDialog = TGTrackTuningModelDialog()

    companion object {
        @JvmField
        val ATTRIBUTE_HANDLER = TGTrackTuningModelHandler::class.java.name
        @JvmField
        val ATTRIBUTE_MODEL = TGTrackTuningModel::class.java.name
    }
}
