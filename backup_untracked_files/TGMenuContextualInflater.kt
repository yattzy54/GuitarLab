package app.tuxguitar.android.menu.controller

import android.view.Menu
import android.view.MenuInflater
import app.tuxguitar.util.TGContext
import app.tuxguitar.util.singleton.TGSingletonFactory
import app.tuxguitar.util.singleton.TGSingletonUtil

class TGMenuContextualInflater private constructor() {
    private var controller: TGMenuController? = null

    fun setController(controller: TGMenuController?) {
        this.controller = controller
    }

    fun inflate(menu: Menu, inflater: MenuInflater) {
        controller?.let {
            it.inflate(menu, inflater)
            controller = null
        }
    }

    companion object {
        @JvmStatic
        fun getInstance(context: TGContext): TGMenuContextualInflater =
            TGSingletonUtil.getInstance(
                context,
                TGMenuContextualInflater::class.java.name,
                object : TGSingletonFactory<TGMenuContextualInflater> {
                    override fun createInstance(context: TGContext) = TGMenuContextualInflater()
                }
            )
    }
}
