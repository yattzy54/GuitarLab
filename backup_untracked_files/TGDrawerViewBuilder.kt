package app.tuxguitar.android.drawer

import android.view.ViewGroup
import app.tuxguitar.android.fragment.TGFragmentController

interface TGDrawerViewBuilder {
    fun onOpenFragment(controller: TGFragmentController<*>, drawerView: ViewGroup)
}
