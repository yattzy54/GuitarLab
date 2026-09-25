package app.tuxguitar.android.fragment.impl

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import app.tuxguitar.android.R
import app.tuxguitar.android.fragment.TGCachedFragment
import app.tuxguitar.android.menu.controller.impl.fragment.TGBrowserMenu

class TGBrowserFragment : TGCachedFragment(R.layout.view_browser) {
    override fun onPostCreate(savedInstanceState: Bundle?) {
        attachInstance()
        createActionBar(true, false, null)
    }

    override fun onPostCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        TGBrowserMenu.getInstance(findContext()).inflate(menu, menuInflater)
    }

    fun attachInstance() {
        TGBrowserFragmentController.getInstance(findContext()).attachInstance(this)
    }
}
