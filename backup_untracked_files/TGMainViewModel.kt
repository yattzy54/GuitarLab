package app.tuxguitar.android.fragment.impl

import app.tuxguitar.android.ui.state.EditorStateViewModel

data class TGMainState(val readOnly: Boolean, val keyboardVisible: Boolean = !readOnly)

class TGMainViewModel(readOnly: Boolean) : EditorStateViewModel<TGMainState>(TGMainState(readOnly)) {
    fun toggleKeyboard() {
        update { it.copy(keyboardVisible = !it.readOnly && !it.keyboardVisible) }
    }
}
