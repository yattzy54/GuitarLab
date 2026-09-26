package app.tuxguitar.android.fragment.impl

import app.tuxguitar.android.fragment.TGCachedFragmentController
import app.tuxguitar.util.TGContext
import app.tuxguitar.util.singleton.TGSingletonFactory
import app.tuxguitar.util.singleton.TGSingletonUtil

class TGPreferencesFragmentController : TGCachedFragmentController<TGPreferencesFragment>() {
    override fun createNewInstance(): TGPreferencesFragment = TGPreferencesFragment()

    companion object {
        @JvmStatic
        fun getInstance(context: TGContext): TGPreferencesFragmentController =
            TGSingletonUtil.getInstance(context, TGPreferencesFragmentController::class.java.name, object : TGSingletonFactory<TGPreferencesFragmentController> {
                override fun createInstance(context: TGContext): TGPreferencesFragmentController = TGPreferencesFragmentController()
            })
    }
}
