package app.tuxguitar.android.domain.repository

import app.tuxguitar.android.domain.model.EditorState
import kotlinx.coroutines.flow.StateFlow

interface EditorRepository {
    val state: StateFlow<EditorState>

    fun goBack(sessionId: String)
    fun dismissDialog(sessionId: String, dialogId: Long)
    fun consumeExitRequest(sessionId: String)
}
