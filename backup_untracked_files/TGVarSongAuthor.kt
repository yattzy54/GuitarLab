package app.tuxguitar.android.variables

import app.tuxguitar.document.TGDocumentManager
import app.tuxguitar.util.TGContext

class TGVarSongAuthor(private val context: TGContext) {
    override fun toString(): String = TGDocumentManager.getInstance(context).song.author
    companion object { const val NAME = "songauthor" }
}
