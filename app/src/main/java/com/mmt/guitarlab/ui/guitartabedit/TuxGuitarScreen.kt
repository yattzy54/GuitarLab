package com.mmt.guitarlab.ui.guitartabedit

import androidx.compose.runtime.Composable
import app.tuxguitar.android.domain.model.EditorMode
import app.tuxguitar.android.ui.editor.EditorHost
import app.tuxguitar.android.ui.editor.EditorScreen

@Composable
fun TuxGuitarScreen(host: EditorHost, onFinish: () -> Unit) {
    EditorScreen(host = host, mode = EditorMode.EDIT, onFinish = onFinish)
}

@Composable
fun TuxGuitarReaderScreen(host: EditorHost, onFinish: () -> Unit) {
    EditorScreen(host = host, mode = EditorMode.READ_ONLY, onFinish = onFinish)
}
