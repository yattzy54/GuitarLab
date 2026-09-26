package app.tuxguitar.android.ui.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner

inline fun <reified VM : ViewModel> ViewModelStoreOwner.editorViewModel(noinline create: () -> VM): VM =
    ViewModelProvider(this, object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T = modelClass.cast(create())
    })[VM::class.java]

@Composable
inline fun <reified VM : ViewModel> scopedEditorViewModel(noinline create: () -> VM): VM {
    val owner = if (LocalInspectionMode.current) {
        remember { object : ViewModelStoreOwner { override val viewModelStore = ViewModelStore() } }.also {
            DisposableEffect(it) { onDispose { it.viewModelStore.clear() } }
        }
    } else {
        requireNotNull(LocalViewModelStoreOwner.current) { "Editor content needs a ViewModelStoreOwner" }
    }
    return remember(owner) { owner.editorViewModel(create) }
}

/** Binds a form input to immutable ViewModel state without retaining UI callbacks. */
@Composable
fun <S, T> EditorStateViewModel<S>.bind(
    read: (S) -> T,
    onChange: (T) -> Unit,
): MutableState<T> {
    val observed = state.collectAsStateWithLifecycle()
    return object : MutableState<T> {
        override var value: T
            get() {
                observed.value
                // Event handlers can make consecutive edits before Flow collection resumes.
                return read(state.value)
            }
            set(value) { onChange(value) }

        override fun component1(): T = value
        override fun component2(): (T) -> Unit = { value = it }
    }
}
