package app.tuxguitar.android.ui.editor

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.tuxguitar.android.domain.model.EditorMode

@Composable
fun EditorScreen(
    host: EditorHost,
    mode: EditorMode = EditorMode.EDIT,
    onFinish: () -> Unit,
    viewModel: EditorViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val currentOnFinish by rememberUpdatedState(onFinish)

    BackHandler(enabled = state.isAttached && !state.exitRequested) {
        viewModel.goBack()
    }

    host.Content(viewModel.sessionId, mode, state, viewModel::dismissDialog)

    LaunchedEffect(state.exitRequested) {
        if (state.exitRequested) {
            viewModel.consumeExitRequest()
            currentOnFinish()
        }
    }
}
