package app.tuxguitar.android.view.dialog.info

import app.tuxguitar.android.view.dialog.fragment.TGModalFragmentController

class TGSongInfoDialogController : TGModalFragmentController<TGSongInfoDialog>() {
    override fun createNewInstance(): TGSongInfoDialog = TGSongInfoDialog()
}
