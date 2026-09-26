package app.tuxguitar.android.domain.model

enum class EditorMode { EDIT, READ_ONLY }

enum class EditorDestination { SCORE, BROWSER, CHANNELS, PREFERENCES }

data class EditorState(
    val sessionId: String? = null,
    val mode: EditorMode = EditorMode.EDIT,
    val destination: EditorDestination? = null,
    val isAttached: Boolean = false,
    val dialogId: Long? = null,
    val exitRequested: Boolean = false,
    val revision: Long = 0,
)
