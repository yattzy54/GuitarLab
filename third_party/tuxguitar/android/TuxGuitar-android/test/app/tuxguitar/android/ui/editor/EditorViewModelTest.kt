package app.tuxguitar.android.ui.editor

import androidx.lifecycle.ViewModelStore
import app.tuxguitar.android.domain.model.EditorDestination
import app.tuxguitar.android.domain.model.EditorMode
import app.tuxguitar.android.domain.model.EditorState
import app.tuxguitar.android.domain.repository.EditorRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class EditorViewModelTest {
    private val dispatcher = StandardTestDispatcher()
    private val store = ViewModelStore()
    private val repository = FakeEditorRepository()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        store.clear()
        Dispatchers.resetMain()
    }

    private fun createViewModel(): EditorViewModel =
        EditorViewModel(repository).also { store.put(it.sessionId, it) }

    @Test
    fun publishesOwnedEditorStateIncludingReaderModeAndDialog() = runTest(dispatcher) {
        val viewModel = createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.state.collect {} }
        val expected = EditorState(
            sessionId = viewModel.sessionId,
            mode = EditorMode.READ_ONLY,
            destination = EditorDestination.SCORE,
            isAttached = true,
            dialogId = 42,
            revision = 2,
        )

        repository.state.value = expected
        runCurrent()

        assertEquals(expected, viewModel.state.value)
    }

    @Test
    fun ignoresAnotherDestinationsStateAndExitRequest() = runTest(dispatcher) {
        val first = createViewModel()
        val second = createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { first.state.collect {} }
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { second.state.collect {} }
        assertNotEquals(first.sessionId, second.sessionId)
        repository.state.value = EditorState(sessionId = first.sessionId, isAttached = true)
        runCurrent()
        assertEquals(first.sessionId, first.state.value.sessionId)

        val secondState = EditorState(sessionId = second.sessionId, isAttached = true, exitRequested = true)
        repository.state.value = secondState
        runCurrent()

        assertEquals(EditorState(), first.state.value)
        assertEquals(secondState, second.state.value)
    }

    @Test
    fun clearsPresentationStateWhenNativeHostDetaches() = runTest(dispatcher) {
        val viewModel = createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.state.collect {} }
        repository.state.value = EditorState(sessionId = viewModel.sessionId, isAttached = true, dialogId = 1)
        runCurrent()

        repository.state.value = EditorState()
        runCurrent()

        assertEquals(EditorState(), viewModel.state.value)
    }

    @Test
    fun forwardsBackAndDialogDismissalWithSessionIdentity() {
        val viewModel = createViewModel()

        viewModel.goBack()
        viewModel.dismissDialog(7)

        assertEquals(viewModel.sessionId, repository.backSessionId)
        assertEquals(viewModel.sessionId to 7L, repository.dismissedDialog)
    }

    @Test
    fun consumesExitRequestWithoutKeepingItForRecomposition() = runTest(dispatcher) {
        val viewModel = createViewModel()
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) { viewModel.state.collect {} }
        repository.state.value = EditorState(sessionId = viewModel.sessionId, isAttached = true, exitRequested = true)
        runCurrent()

        viewModel.consumeExitRequest()
        runCurrent()

        assertEquals(viewModel.sessionId, repository.consumedSessionId)
        assertFalse(viewModel.state.value.exitRequested)
    }

    private class FakeEditorRepository : EditorRepository {
        override val state = MutableStateFlow(EditorState())
        var backSessionId: String? = null
        var dismissedDialog: Pair<String, Long>? = null
        var consumedSessionId: String? = null

        override fun goBack(sessionId: String) {
            backSessionId = sessionId
        }

        override fun dismissDialog(sessionId: String, dialogId: Long) {
            dismissedDialog = sessionId to dialogId
        }

        override fun consumeExitRequest(sessionId: String) {
            consumedSessionId = sessionId
            state.value = state.value.copy(exitRequested = false)
        }
    }
}
