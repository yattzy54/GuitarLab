package app.tuxguitar.android.fragment.impl

import app.tuxguitar.android.fragment.TGCachedFragmentController
import app.tuxguitar.util.TGContext
import app.tuxguitar.util.singleton.TGSingletonFactory
import app.tuxguitar.util.singleton.TGSingletonUtil

class TGMainFragmentController : TGCachedFragmentController<TGMainFragment>() {
    override fun createNewInstance(): TGMainFragment = TGMainFragment()

    companion object {
        @JvmStatic
        fun getInstance(context: TGContext): TGMainFragmentController =
            TGSingletonUtil.getInstance(
                context,
                TGMainFragmentController::class.java.name,
                object : TGSingletonFactory<TGMainFragmentController> {
                    override fun createInstance(context: TGContext) = TGMainFragmentController()
                }
            )
    }
}
