package app.tuxguitar.android.variables

import app.tuxguitar.document.TGDocumentManager
import app.tuxguitar.util.TGContext

class TGVarSongName(private val context: TGContext) {
    override fun toString(): String = TGDocumentManager.getInstance(context).song.name
    companion object { const val NAME = "songname" }
}
