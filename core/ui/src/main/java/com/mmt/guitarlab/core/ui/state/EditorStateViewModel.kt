package com.mmt.guitarlab.core.ui.state

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

abstract class EditorStateViewModel<S>(initial: S) : ViewModel() {
    private val mutableState = MutableStateFlow(initial)
    val state = mutableState.asStateFlow()

    protected fun update(transform: (S) -> S) {
        mutableState.update(transform)
    }
}
