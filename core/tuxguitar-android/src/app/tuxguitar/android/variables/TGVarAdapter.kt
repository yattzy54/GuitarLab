package app.tuxguitar.android.variables

import app.tuxguitar.util.TGContext
import app.tuxguitar.util.TGExpressionResolver

object TGVarAdapter {
    @JvmStatic
    fun initialize(context: TGContext) {
        val variables = mapOf<String, Any>(
            TGVarAppName.NAME to TGVarAppName(),
            TGVarAppVersion.NAME to TGVarAppVersion(),
            TGVarSongName.NAME to TGVarSongName(context),
            TGVarSongAuthor.NAME to TGVarSongAuthor(context),
            TGVarSongAlbum.NAME to TGVarSongAlbum(context),
            TGVarSongArtist.NAME to TGVarSongArtist(context)
        )
        TGExpressionResolver.getInstance(context)
            .addResolver(TGExpressionResolver.MapPropertyResolver(variables))
    }
}
