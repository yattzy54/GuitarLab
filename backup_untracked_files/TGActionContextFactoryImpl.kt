package app.tuxguitar.android.action

import app.tuxguitar.action.TGActionContext
import app.tuxguitar.action.TGActionContextFactory
import app.tuxguitar.action.TGActionException
import app.tuxguitar.android.view.tablature.TGCaret
import app.tuxguitar.android.view.tablature.TGSongViewController
import app.tuxguitar.document.TGDocumentContextAttributes
import app.tuxguitar.document.TGDocumentManager
import app.tuxguitar.song.models.TGNote
import app.tuxguitar.util.TGContext
import app.tuxguitar.util.TGNoteRange

class TGActionContextFactoryImpl(
    private val context: TGContext
) : TGActionContextFactory {
    @Throws(TGActionException::class)
    override fun createActionContext(): TGActionContext {
        val actionContext = TGActionContextImpl()
        val documentManager = TGDocumentManager.getInstance(context)
        actionContext.setAttribute(
            TGDocumentContextAttributes.ATTRIBUTE_SONG_MANAGER,
            documentManager.songManager
        )
        actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_SONG, documentManager.song)

        val songView = TGSongViewController.getInstance(context)
        if (songView != null) {
            val caret: TGCaret = songView.caret
            val selectedNote: TGNote? = caret.selectedNote
            actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_TRACK, caret.track)
            actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_MEASURE, caret.measure)
            actionContext.setAttribute(
                TGDocumentContextAttributes.ATTRIBUTE_HEADER,
                caret.measure?.header
            )
            actionContext.setAttribute(
                TGDocumentContextAttributes.ATTRIBUTE_BEAT,
                caret.selectedBeat
            )
            actionContext.setAttribute(
                TGDocumentContextAttributes.ATTRIBUTE_VOICE,
                caret.selectedVoice
            )
            actionContext.setAttribute(TGDocumentContextAttributes.ATTRIBUTE_NOTE, selectedNote)
            actionContext.setAttribute(
                TGDocumentContextAttributes.ATTRIBUTE_STRING,
                caret.selectedString
            )
            actionContext.setAttribute(
                TGDocumentContextAttributes.ATTRIBUTE_DURATION,
                caret.duration
            )
            actionContext.setAttribute(
                TGDocumentContextAttributes.ATTRIBUTE_VELOCITY,
                caret.velocity
            )
            actionContext.setAttribute(
                TGDocumentContextAttributes.ATTRIBUTE_POSITION,
                caret.position
            )
            actionContext.setAttribute(
                TGDocumentContextAttributes.ATTRIBUTE_NOTE_RANGE,
                if (selectedNote == null) TGNoteRange.empty() else TGNoteRange.single(selectedNote)
            )
        }
        return actionContext
    }
}
