package app.tuxguitar.android.activity

import app.tuxguitar.util.TGContext
import app.tuxguitar.util.singleton.TGSingletonFactory
import app.tuxguitar.util.singleton.TGSingletonUtil

class TGActivityController {
    var activity: TGActivity? = null

    companion object {
        @JvmStatic
        fun getInstance(context: TGContext): TGActivityController =
            TGSingletonUtil.getInstance(
                context,
                TGActivityController::class.java.name,
                object : TGSingletonFactory<TGActivityController> {
                    override fun createInstance(context: TGContext) = TGActivityController()
                },
            )
    }
}
