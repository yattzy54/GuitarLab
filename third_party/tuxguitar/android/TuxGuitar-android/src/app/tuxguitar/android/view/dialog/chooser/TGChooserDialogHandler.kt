package app.tuxguitar.android.view.dialog.chooser

interface TGChooserDialogHandler<T> {
    fun onChoose(value: T?)
}
