package app.tuxguitar.android.view.dialog.tripletFeel

import app.tuxguitar.android.view.dialog.compose.TGComposeBottomSheetDialogController

class TGTripletFeelDialogController : TGComposeBottomSheetDialogController<TGTripletFeelDialog>() {
    override fun createNewInstance(): TGTripletFeelDialog = TGTripletFeelDialog()
}
