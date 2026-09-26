package app.tuxguitar.android.view.keyboard

import app.tuxguitar.util.TGContext
import app.tuxguitar.util.singleton.TGSingletonFactory
import app.tuxguitar.util.singleton.TGSingletonUtil

class TGTabKeyboardController {
    private var view: TGTabKeyboard? = null

    fun getView(): TGTabKeyboard? = view

    fun setView(view: TGTabKeyboard) {
        this.view = view
    }

    fun toggleVisibility() {
        getView()?.toggleVisibility()
    }

    companion object {
        @JvmStatic
        fun getInstance(context: TGContext): TGTabKeyboardController = TGSingletonUtil.getInstance(
            context,
            TGTabKeyboardController::class.java.name,
            object : TGSingletonFactory<TGTabKeyboardController> {
                override fun createInstance(context: TGContext): TGTabKeyboardController = TGTabKeyboardController()
            },
        )
    }
}
