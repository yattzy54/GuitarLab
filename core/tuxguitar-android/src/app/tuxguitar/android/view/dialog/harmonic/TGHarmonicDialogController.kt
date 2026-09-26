package app.tuxguitar.android.view.dialog.harmonic

import app.tuxguitar.android.view.dialog.compose.TGComposeBottomSheetDialogController

class TGHarmonicDialogController : TGComposeBottomSheetDialogController<TGHarmonicDialog>() {
    override fun createNewInstance(): TGHarmonicDialog = TGHarmonicDialog()
}
