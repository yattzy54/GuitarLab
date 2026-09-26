package com.mmt.guitarlab.ui.guitartabedit

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import app.tuxguitar.android.domain.model.EditorMode
import app.tuxguitar.android.ui.editor.EditorHost
import app.tuxguitar.android.ui.editor.EditorScreen
import app.tuxguitar.android.ui.editor.EditorViewModel
import app.tuxguitar.android.ui.editor.LegacyEditorHost
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@Composable
fun TuxGuitarScreen(
    onFinish: () -> Unit,
    editorHost: EditorHost = hiltViewModel<LegacyEditorHostHolder>().editorHost,
    viewModel: EditorViewModel = hiltViewModel(),
) {
    EditorScreen(
        host = editorHost,
        mode = EditorMode.EDIT,
        onFinish = onFinish,
        viewModel = viewModel
    )
}

@HiltViewModel
class LegacyEditorHostHolder @Inject constructor(
    val editorHost: LegacyEditorHost
) : ViewModel()
