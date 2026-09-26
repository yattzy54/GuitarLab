package app.tuxguitar.android.ui.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import app.tuxguitar.android.domain.model.EditorState
import app.tuxguitar.android.domain.repository.EditorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

@HiltViewModel
class EditorViewModel @Inject constructor(
    private val repository: EditorRepository,
) : ViewModel() {
    val sessionId: String = UUID.randomUUID().toString()

    val state = repository.state
        .map { if (it.sessionId == sessionId) it else EditorState() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), EditorState())

    fun goBack() = repository.goBack(sessionId)

    fun dismissDialog(dialogId: Long) = repository.dismissDialog(sessionId, dialogId)

    fun consumeExitRequest() = repository.consumeExitRequest(sessionId)
}
