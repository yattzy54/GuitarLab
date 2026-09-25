package app.tuxguitar.android.view.dialog.harmonic

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController

class TGHarmonicDialogController : TGModalFragmentController<TGHarmonicDialog>() {
    override fun createNewInstance(): TGHarmonicDialog = TGHarmonicDialog()
}
