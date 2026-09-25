package app.tuxguitar.android.fragment.impl

import app.tuxguitar.android.fragment.TGCachedFragmentController
import app.tuxguitar.util.TGContext
import app.tuxguitar.util.singleton.TGSingletonFactory
import app.tuxguitar.util.singleton.TGSingletonUtil

class TGBrowserFragmentController : TGCachedFragmentController<TGBrowserFragment>() {
    override fun createNewInstance(): TGBrowserFragment = TGBrowserFragment()

    companion object {
        @JvmStatic
        fun getInstance(context: TGContext): TGBrowserFragmentController =
            TGSingletonUtil.getInstance(context, TGBrowserFragmentController::class.java.name, object : TGSingletonFactory<TGBrowserFragmentController> {
                override fun createInstance(context: TGContext): TGBrowserFragmentController = TGBrowserFragmentController()
            })
    }
}
