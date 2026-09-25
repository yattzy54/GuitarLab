package app.tuxguitar.android.fragment.impl

import app.tuxguitar.android.fragment.TGCachedFragmentController
import app.tuxguitar.util.TGContext
import app.tuxguitar.util.singleton.TGSingletonFactory
import app.tuxguitar.util.singleton.TGSingletonUtil

class TGChannelListFragmentController : TGCachedFragmentController<TGChannelListFragment>() {
    override fun createNewInstance(): TGChannelListFragment = TGChannelListFragment()

    companion object {
        @JvmStatic
        fun getInstance(context: TGContext): TGChannelListFragmentController =
            TGSingletonUtil.getInstance(
                context,
                TGChannelListFragmentController::class.java.name,
                object : TGSingletonFactory<TGChannelListFragmentController> {
                    override fun createInstance(context: TGContext) = TGChannelListFragmentController()
                }
            )
    }
}
