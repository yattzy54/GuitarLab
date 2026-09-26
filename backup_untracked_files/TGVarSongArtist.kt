package app.tuxguitar.android.variables

import app.tuxguitar.document.TGDocumentManager
import app.tuxguitar.util.TGContext

class TGVarSongArtist(private val context: TGContext) {
    override fun toString(): String = TGDocumentManager.getInstance(context).song.artist
    companion object { const val NAME = "songartist" }
}
