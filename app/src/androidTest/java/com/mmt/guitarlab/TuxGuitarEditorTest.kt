package com.mmt.guitarlab

import android.view.View
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.Visibility
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.tuxguitar.android.R as EditorR
import app.tuxguitar.android.domain.model.EditorMode
import app.tuxguitar.android.ui.editor.EditorScreen
import com.mmt.guitarlab.core.ui.theme.GuitarLabTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TuxGuitarEditorTest {
    @get:Rule
    val compose = createAndroidComposeRule<MainActivity>()

    @Test
    fun rendersEditorClosesDialogAndExitsThroughViewModel() {
        val visible = mutableStateOf(true)
        compose.runOnUiThread {
            compose.activity.setContent {
                GuitarLabTheme {
                    if (visible.value) {
                        EditorScreen(host = compose.activity.editorHost, onFinish = { visible.value = false })
                    }
                }
            }
        }
        waitForScore()
        onView(withId(EditorR.id.main_bottom)).check(matches(isDisplayed()))

        onView(withId(EditorR.id.tempo_display_item)).perform(click())
        compose.onNodeWithText(compose.activity.getString(EditorR.string.tempo_dlg_title)).assertIsDisplayed()
        pressBack()
        compose.onNodeWithText(compose.activity.getString(EditorR.string.tempo_dlg_title)).assertDoesNotExist()

        pressBack()
        compose.onNodeWithText(compose.activity.getString(EditorR.string.confirm_dlg_title)).assertIsDisplayed()
        compose.onNodeWithText(compose.activity.getString(EditorR.string.global_button_ok)).performClick()
        compose.waitUntil(10_000) { !visible.value }
        compose.waitUntil(10_000) {
            var released = false
            compose.activityRule.scenario.onActivity {
                released = it.findViewById<View>(EditorR.id.main_body) == null
            }
            released
        }
    }

    @Test
    fun switchesOwnedSessionsWithoutRestoringReaderKeyboardOrDisposingNewEditor() {
        val mode = mutableStateOf(EditorMode.EDIT)
        compose.runOnUiThread {
            compose.activity.setContent {
                GuitarLabTheme {
                    EditorScreen(
                        host = compose.activity.editorHost,
                        mode = mode.value,
                        onFinish = {},
                        viewModel = hiltViewModel(key = mode.value.name),
                    )
                }
            }
        }
        waitForScore()
        onView(withId(EditorR.id.main_bottom)).check(matches(isDisplayed()))

        compose.runOnIdle { mode.value = EditorMode.READ_ONLY }
        waitForScore()
        onView(withId(EditorR.id.main_bottom)).check(matches(withEffectiveVisibility(Visibility.GONE)))

        compose.runOnIdle { mode.value = EditorMode.EDIT }
        waitForScore()
        onView(withId(EditorR.id.main_bottom)).check(matches(isDisplayed()))
        onView(withId(EditorR.id.tempo_display_item)).perform(click())
        compose.onNodeWithText(compose.activity.getString(EditorR.string.tempo_dlg_title)).assertIsDisplayed()
        pressBack()
    }

    @Test
    fun releasingOutgoingDestinationDoesNotCloseIncomingSession() {
        val outgoingVisible = mutableStateOf(true)
        compose.runOnUiThread {
            compose.activity.setContent {
                GuitarLabTheme {
                    Box {
                        if (outgoingVisible.value) {
                            EditorScreen(
                                host = compose.activity.editorHost,
                                onFinish = {},
                                viewModel = hiltViewModel(key = "outgoing"),
                            )
                        }
                        EditorScreen(
                            host = compose.activity.editorHost,
                            mode = EditorMode.READ_ONLY,
                            onFinish = {},
                            viewModel = hiltViewModel(key = "incoming"),
                        )
                    }
                }
            }
        }
        waitForScore()
        compose.runOnIdle { outgoingVisible.value = false }

        onView(withId(EditorR.id.main_body)).check(matches(isDisplayed()))
        onView(withId(EditorR.id.main_bottom)).check(matches(withEffectiveVisibility(Visibility.GONE)))
        onView(withId(EditorR.id.tempo_display_item)).perform(click())
        compose.onNodeWithText(compose.activity.getString(EditorR.string.tempo_dlg_title)).assertIsDisplayed()
        pressBack()
    }

    private fun waitForScore() {
        compose.waitUntil(10_000) {
            var ready = false
            compose.activityRule.scenario.onActivity {
                ready = it.findViewById<View>(EditorR.id.main_body)?.isShown == true
            }
            ready
        }
    }
}
