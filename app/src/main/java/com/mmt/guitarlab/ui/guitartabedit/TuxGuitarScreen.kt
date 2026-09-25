package com.mmt.guitarlab.ui.guitartabedit

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.fragment.app.FragmentActivity
import app.tuxguitar.android.activity.TGActivity
import app.tuxguitar.android.activity.TGReaderActivity

/**
 * Editor destination in the app's single-Activity NavHost. The legacy
 * TuxGuitar editor engine is embedded in-place as a plain [TGActivity]
 * instance (no Fragment involved) whose root View is created once per
 * destination visit and torn down when this Composable leaves composition.
 */
@Composable
fun TuxGuitarScreen(onFinish: () -> Unit = {}) {
    TuxGuitarHost(newInstance = { TGActivity() }, onFinish = onFinish)
}

@Composable
fun TuxGuitarReaderScreen(onFinish: () -> Unit = {}) {
    TuxGuitarHost(newInstance = { TGReaderActivity() }, onFinish = onFinish)
}

@Preview(showBackground = true, backgroundColor = 0xFF0F141C)
@Composable
private fun TuxGuitarScreenPreview() {
    // The real host needs a live FragmentActivity/TGContext graph that isn't
    // available in layout preview, so the preview only verifies that this
    // file's Composables declare and compile correctly.
}

@Composable
private fun TuxGuitarHost(
    newInstance: () -> TGActivity,
    onFinish: () -> Unit,
) {
    val hostActivity = LocalContext.current as FragmentActivity
    val tgActivity = remember { newInstance() }
    tgActivity.onFinishRequested = onFinish

    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { tgActivity.getOrCreateRootView(hostActivity) },
    )

    DisposableEffect(tgActivity) {
        onDispose { tgActivity.destroyRootView() }
    }
}
