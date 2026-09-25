package com.mmt.guitarlab.ui.guitartabedit

import android.view.View
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.activity.TGReaderActivity

/**
 * Editor destination in the app's single-Activity NavHost. The legacy
 * TuxGuitar editor engine (previously its own `TGActivity`) is embedded
 * in-place as a Fragment hosted by [MainActivity]'s own
 * `supportFragmentManager`, rather than launched as a separate Activity.
 */
@Composable
fun TuxGuitarScreen(onFinish: () -> Unit = {}) {
    TuxGuitarFragmentHost(activityClass = TGActivity::class.java, onFinish = onFinish)
}

@Composable
fun TuxGuitarReaderScreen(onFinish: () -> Unit = {}) {
    TuxGuitarFragmentHost(activityClass = TGReaderActivity::class.java, onFinish = onFinish)
}

@Preview(showBackground = true, backgroundColor = 0xFF0F141C)
@Composable
private fun TuxGuitarScreenPreview() {
    // The real fragment needs a live FragmentActivity/TGContext graph that
    // isn't available in layout preview, so the preview only verifies that
    // this file's Composables declare and compile correctly.
}

@Composable
private fun TuxGuitarFragmentHost(
    activityClass: Class<out TGActivity>,
    onFinish: () -> Unit,
) {
    val fragmentActivity = LocalContext.current as FragmentActivity
    val fragmentTag = remember(activityClass) { "tuxguitar-editor-" + activityClass.name }

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            FragmentContainerView(context).apply {
                id = View.generateViewId()
            }
        },
        update = { containerView ->
            val fragmentManager = fragmentActivity.supportFragmentManager
            val existing = fragmentManager.findFragmentByTag(fragmentTag) as? TGActivity
            val fragment = existing ?: activityClass.getDeclaredConstructor().newInstance().also {
                fragmentManager.beginTransaction()
                    .replace(containerView.id, it, fragmentTag)
                    .commitNowAllowingStateLoss()
            }
            fragment.onFinishRequested = onFinish
        },
    )

    DisposableEffect(fragmentTag) {
        onDispose {
            val fragmentManager = fragmentActivity.supportFragmentManager
            fragmentManager.findFragmentByTag(fragmentTag)?.let { fragment ->
                if (!fragmentManager.isStateSaved) {
                    fragmentManager.beginTransaction().remove(fragment).commitNowAllowingStateLoss()
                }
            }
        }
    }
}
