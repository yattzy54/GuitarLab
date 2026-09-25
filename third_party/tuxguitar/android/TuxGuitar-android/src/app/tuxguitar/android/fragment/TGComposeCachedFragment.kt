package app.tuxguitar.android.fragment

import androidx.compose.runtime.Composable

/**
 * [TGScreen] variant for full-screen destinations rendered with Jetpack
 * Compose instead of an XML layout resource. Kept as a thin adapter (rather
 * than folding [FragmentContent] directly into [TGScreen]) so existing
 * subclasses (`TGMainFragment`, `TGBrowserFragment`, `TGChannelListFragment`,
 * `TGPreferencesFragment`) did not need to be renamed.
 */
abstract class TGComposeCachedFragment : TGScreen() {
    @Composable
    abstract fun FragmentContent()

    @Composable
    final override fun Content() {
        FragmentContent()
    }
}
