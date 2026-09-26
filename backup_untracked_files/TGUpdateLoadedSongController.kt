package app.tuxguitar.android.action.listener.cache.controller

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.util.TGContext
class TGUpdateLoadedSongController : TGUpdateItemsController() {
 override fun update(context: TGContext, actionContext: TGActionContext) { val player=app.tuxguitar.player.base.MidiPlayer.getInstance(context); player.reset(); player.mode.clear(); player.resetChannels(); app.tuxguitar.editor.undo.TGUndoableManager.getInstance(context).discardAllEdits(); actionContext.getAttribute<app.tuxguitar.song.managers.TGSongManager>(TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER)?.let { m -> actionContext.getAttribute<app.tuxguitar.song.models.TGSong>(TGDocumentContextAttributes.ATTRIBUTE_SONG)?.let { m.ensurePercussionChannel(it) } }; findUpdateBuffer(context).requestUpdateLoadedSong(); super.update(context,actionContext) }
}
