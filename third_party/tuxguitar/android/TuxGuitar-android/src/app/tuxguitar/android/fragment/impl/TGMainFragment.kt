package app.tuxguitar.android.fragment.impl

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import app.tuxguitar.android.R
import app.tuxguitar.android.fragment.TGComposeCachedFragment
import app.tuxguitar.android.menu.controller.impl.fragment.TGMainMenu

/**
 * Hosts the tablature editor (song rendering canvas + tab keyboard).
 *
 * [app.tuxguitar.android.view.tablature.TGSongView] and
 * [app.tuxguitar.android.view.keyboard.TGTabKeyboard] are hand-rolled
 * `Canvas`-based rendering engines shared with the desktop editor via the
 * platform-agnostic `UIPainter` abstraction; rewriting that engine on top of
 * Compose's drawing APIs is out of scope. Instead this fragment hosts the
 * existing `view_main.xml` hierarchy unchanged through a single [AndroidView],
 * so the surrounding fragment/navigation layer can still be Compose-based.
 */
class TGMainFragment : TGComposeCachedFragment() {
    private var inflatedRoot: View? = null

    override fun onPostCreate() {
        attachInstance()
        createActionBar(true, false, R.string.app_name)
    }

    override fun onPostCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        TGMainMenu.getInstance(findContext()).inflate(menu, menuInflater)
    }

    fun findChildViewById(id: Int): View? = inflatedRoot?.findViewById(id)

    fun getTopView(): View? = findChildViewById(R.id.main_top)
    fun getBottomView(): View? = findChildViewById(R.id.main_bottom)
    fun getLeftView(): View? = findChildViewById(R.id.main_left)
    fun getRightView(): View? = findChildViewById(R.id.main_right)
    fun getBodyView(): View? = findChildViewById(R.id.main_body)

    fun attachInstance() {
        TGMainFragmentController.getInstance(findContext()).attachInstance(this)
    }

    @Composable
    override fun FragmentContent() {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                LayoutInflater.from(context).inflate(R.layout.view_main, null, false).also {
                    inflatedRoot = it
                }
            },
        )
    }
}
