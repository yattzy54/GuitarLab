package app.tuxguitar.android.fragment.impl

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import app.tuxguitar.android.R
import app.tuxguitar.android.fragment.TGCachedFragment
import app.tuxguitar.android.menu.controller.impl.fragment.TGChannelListMenu

class TGChannelListFragment : TGCachedFragment(R.layout.view_channel_list) {
    override fun onPostCreate(savedInstanceState: Bundle?) {
        attachInstance()
        createActionBar(true, false, R.string.channel_list)
    }

    override fun onPostCreateOptionsMenu(menu: Menu, menuInflater: MenuInflater) {
        TGChannelListMenu.getInstance(findContext()).inflate(menu, menuInflater)
    }

    fun attachInstance() {
        TGChannelListFragmentController.getInstance(findContext()).attachInstance(this)
    }
}
