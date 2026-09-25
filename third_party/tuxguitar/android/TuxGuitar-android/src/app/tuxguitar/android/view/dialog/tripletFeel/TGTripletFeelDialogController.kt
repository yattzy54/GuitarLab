package app.tuxguitar.android.view.dialog.tripletFeel

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController

class TGTripletFeelDialogController : TGModalFragmentController<TGTripletFeelDialog>() {
    override fun createNewInstance(): TGTripletFeelDialog = TGTripletFeelDialog()
}
