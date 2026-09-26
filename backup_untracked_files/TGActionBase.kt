package app.tuxguitar.android.action

import app.tuxguitar.android.view.tablature.TGSongViewController
import app.tuxguitar.util.TGContext

abstract class TGActionBase(context: TGContext, name: String) :
    app.tuxguitar.editor.action.TGActionBase(context, name) {
    fun getEditor(): TGSongViewController = TGSongViewController.getInstance(context)
}
