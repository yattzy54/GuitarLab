package app.tuxguitar.android.graphics

import android.graphics.Color
import app.tuxguitar.ui.resource.UIColor
import app.tuxguitar.ui.resource.UIColorModel

class TGColorImpl(model: UIColorModel) : UIColor {
    private var model: UIColorModel? = model

    override fun dispose() {
        model = null
    }

    override fun isDisposed(): Boolean = model == null

    override fun getBlue(): Int = model!!.blue

    override fun getGreen(): Int = model!!.green

    override fun getRed(): Int = model!!.red

    fun getHandle(alpha: Int): Int = Color.argb(alpha, model!!.red, model!!.green, model!!.blue)
}
