package app.tuxguitar.android.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView

/**
 * [TGBaseFragment] variant for full-screen fragments rendered with Jetpack
 * Compose instead of an XML layout resource.
 *
 * Mirrors [TGCachedFragment]'s single-inflation-per-attach caching behaviour:
 * the [ComposeView] is created once and reused across [onCreateView] calls
 * for the same fragment instance, and [onShowView]/[onHideView] bracket the
 * view's visible lifetime the same way the old [TGCachedFragment] did for its
 * subclasses (registering/unregistering event listeners, etc.).
 */
abstract class TGComposeCachedFragment : TGBaseFragment() {
    private var cachedView: View? = null

    @Composable
    abstract fun FragmentContent()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val createdView = super.onCreateView(inflater, container, savedInstanceState)
        onShowView()
        return createdView
    }

    override fun onDestroyView() {
        onHideView()
        super.onDestroyView()
    }

    override fun onPostCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
        createdView: View?,
    ): View? {
        if (cachedView == null) {
            cachedView = ComposeView(requireContext()).apply {
                setContent {
                    MaterialTheme {
                        FragmentContent()
                    }
                }
            }
        }
        return cachedView
    }

    open fun onShowView() {
    }

    open fun onHideView() {
    }
}
