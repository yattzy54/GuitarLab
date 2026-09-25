package app.tuxguitar.android.fragment.impl

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.View
import app.tuxguitar.android.R
import app.tuxguitar.android.fragment.TGCachedFragment
import app.tuxguitar.android.menu.controller.impl.fragment.TGMainMenu

class TGMainFragment : TGCachedFragment(R.layout.view_main) {
    override fun onPostCreate(savedInstanceState: Bundle?) {
        attachInstance()
        createActionBar(true, false, R.string.app_name)
    }

    override fun onPostCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        TGMainMenu.getInstance(findContext()).inflate(menu, menuInflater)
    }

    fun findChildViewById(id: Int): View? = view?.findViewById(id)

    fun getTopView(): View? = findChildViewById(R.id.main_top)
    fun getBottomView(): View? = findChildViewById(R.id.main_bottom)
    fun getLeftView(): View? = findChildViewById(R.id.main_left)
    fun getRightView(): View? = findChildViewById(R.id.main_right)
    fun getBodyView(): View? = findChildViewById(R.id.main_body)

    fun attachInstance() {
        TGMainFragmentController.getInstance(findContext()).attachInstance(this)
    }
}
