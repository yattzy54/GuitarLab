package app.tuxguitar.android.variables

import app.tuxguitar.document.TGDocumentManager
import app.tuxguitar.util.TGContext

class TGVarSongAlbum(private val context: TGContext) {
    override fun toString(): String = TGDocumentManager.getInstance(context).song.album
    companion object { const val NAME = "songalbum" }
}
